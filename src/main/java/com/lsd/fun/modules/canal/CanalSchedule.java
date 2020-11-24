package com.lsd.fun.modules.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.alibaba.otter.canal.protocol.CanalEntry.*;

/**
 * Canal-ES同步调度器
 * 负责解析MySQL的Binglog，并导入ES中
 *
 * Created by lsd
 * 2020-03-20 18:05
 */
//@Component
@Deprecated  //转用 Canal+ Kafka 实现，弃用此方案
public class CanalSchedule implements Runnable {

    private final static String INDEX_NAME = "shop";

    @Autowired
    private CanalConnector canalConnector;
    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient rhlClient;

    /**
     * 每秒同步一次
     */
    @Scheduled(fixedDelay = 100)
    @Override
    public void run() {
        long batchId = -1;
        try {
            int batchSize = 1000; //每次同步1k条数据
            // 不ack地拉取消息
            Message message = canalConnector.getWithoutAck(batchSize);
            batchId = message.getId(); //这批消息的id
            List<Entry> entries = message.getEntries();
            // 若有数据Row更新，则解析处理Binlog
            if (batchId != -1 && entries.size() > 0) {
                for (Entry entry : entries) {
                    this.publishCanalEvent(entry);
                }
            }
            // ack
            canalConnector.ack(batchId);
        } catch (Exception e) {
            e.printStackTrace();// print logs
            // 出异常回滚
            canalConnector.rollback(batchId);
        }
    }

    /**
     * 解析处理Binlog
     */
    private void publishCanalEvent(Entry entry) throws IOException {
        Header header = entry.getHeader();
        EventType eventType = header.getEventType();
        String database = header.getSchemaName();
        String table = header.getTableName();
        RowChange rowChange;
        try {
            rowChange = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();// print logs
            throw e;
        }
        for (RowData rowData : rowChange.getRowDatasList()) {
            // 列信息转换为Map结构
            List<Column> columns = rowData.getAfterColumnsList();
            Map<String, String> columnDataMap = columns.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Column::getName, Column::getValue, (oldKey, newKey) -> newKey));
            // 获取主键列
//            String primaryKeyName = "id";
//            Column idColumn = columns.stream()
//                    .filter(col -> col.getIsKey() &&
//                            StringUtils.equals(col.getName(), primaryKeyName))
//                    .findFirst()
//                    .orElse(null);
            this.indexEs(columnDataMap, database, table);
        }
    }

    /**
     * 导入ES
     *
     * @param columnDataMap 数据Row 字段名 与 字段值 的映射
     * @param database      数据库名称
     * @param table         数据表名
     */
    private void indexEs(Map<String, String> columnDataMap, String database, String table) throws IOException {
        // 若不是我们目标数据库的变更则返回
        if (!StringUtils.equals(database, "dianpingdb")) {
            return;
        }
        // 根据Binlog的不同表名去对应表查询待同步到ES的数据
        List<Map<String, Object>> needIndexDataList = new ArrayList<>();
        switch (table) {
            case "seller":
//                needIndexDataList = shopModelMapper.needIndexQuery(new Integer(columnDataMap.get("id")), null, null);
                break;
            case "category":
//                needIndexDataList = shopModelMapper.needIndexQuery(null, new Integer(columnDataMap.get("id")), null);
                break;
            case "shop":
//                needIndexDataList = shopModelMapper.needIndexQuery(null, null, new Integer(columnDataMap.get("id")));
                break;
            default:
                break;
        }
        // 所有变更数据同步到ES
        for (Map<String, Object> map : needIndexDataList) {
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .id(String.valueOf(map.get("id")))
                    .source(map);
            try {
                rhlClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();  //print logs
                throw e;
            }
        }
    }

}
