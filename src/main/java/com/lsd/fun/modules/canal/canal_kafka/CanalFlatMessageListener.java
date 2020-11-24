package com.lsd.fun.modules.canal.canal_kafka;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lsd.fun.modules.app.dto.ShopIndexKey;
import com.lsd.fun.modules.cms.dao.ShopDao;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.*;

/**
 * 由 Canal 监听 MySQL 并发消息到 Kafka，由此监听器消费消息并更新到 ES 或 Hive
 * canal.mq.flatMessage = true的情况，效率不高
 * <p>
 * Created by lsd
 * 2020-03-31 10:49
 */
@Deprecated
@Slf4j
//@Component
public class CanalFlatMessageListener {

    public final static String CANAL_TOPIC = "example";

    @Autowired
    private Gson gson;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private BaiduLBSService baiduLBSService;
    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient rhlClient;

    /**
     * canal.mq.flatMessage = true的情况，效率不高
     * @param content
     */
    @KafkaListener(topics = CANAL_TOPIC)
    public void handlerMessage(String content) {
        // parse message
        JsonObject msgObject = gson.fromJson(content, JsonObject.class);
        // 若不是我们目标数据库的变更则返回
        if (!StringUtils.equals(msgObject.get("database").getAsString(), "fun")) {
            return;
        }
        log.debug("收到消息：{}", msgObject);
        // 获取所有更新行
        JsonArray rowChangeList = msgObject.getAsJsonArray("data");
        // 所有Binlog行的待更新数据
        List<Map<String, Object>> needIndexDataList = new ArrayList<>();
        for (JsonElement rowData : rowChangeList) {
            if (rowData == null) {
                return;
            }
            String table = msgObject.get("table").getAsString();
            int idOfTable = rowData.getAsJsonObject().get("id").getAsInt();
            // 获取此行Binlog的待更新数据
            List<Map<String, Object>> needIndexDataList4OneRow = processOneBinlogRow(table, idOfTable);
            needIndexDataList.addAll(needIndexDataList4OneRow);
        }
        // 批量同步到ES
        this.bulkIndex2ES(needIndexDataList);
    }


    /**
     * 根据Binlog的变更数据行信息，去对应表中查询待同步到ES的数据
     * TODO 一条条的查数据库可以改为switch不同的表收集3个不同的待查id集合,批量查询
     *
     * @param table     变更记录行所在数据表
     * @param idOfTable 数据表中变更记录行的id
     */
    private List<Map<String, Object>> processOneBinlogRow(String table, int idOfTable) {
        QueryWrapper<Object> wrapper = Wrappers.query();
        switch (table) {
            case "seller":
                wrapper.eq("seller.id", idOfTable);
                break;
            case "category":
                wrapper.eq("category.id", idOfTable);
                break;
            case "shop":
                wrapper.eq("shop.id", idOfTable);
                break;
            default:
                return Collections.emptyList();
        }
        List<Map<String, Object>> needIndexDataList = shopDao.queryNeedIndexRow(wrapper);
        for (Map<String, Object> row : needIndexDataList) {
            // 调用百度LBS把地址"address"字段替换为经纬度"location"
            BaiduMapLocation location = baiduLBSService.parseAddress2Location(row.get("address").toString());
            row.put("location", location.getLatitude() + "," + location.getLongitude());
            row.remove("address");
        }
        return needIndexDataList;
    }

    /**
     * Sync to ES
     * 优化效果：不使用bulk的话306条记录同步需要>1分钟，优化后只需要?s
     *
     * @param needIndexDataList 当前Kafka所有Binlog消息解析后得到的待更新数据
     */
    private void bulkIndex2ES(List<Map<String, Object>> needIndexDataList) {
        // 应使用 Bulk Api 批量更新提高效率
        BulkRequest bulkReq = new BulkRequest()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);  //写入完成立即刷新;
        for (Map<String, Object> map : needIndexDataList) {
            IndexRequest indexRequest = new IndexRequest(ShopIndexKey.INDEX_NAME)
                    .id(String.valueOf(map.get("id")))
                    .source(map);
            bulkReq.add(indexRequest);
        }
        try {
            BulkResponse responses = rhlClient.bulk(bulkReq, RequestOptions.DEFAULT);
            int total = responses.getItems().length;
            int failed = 0;
            for (BulkItemResponse response : responses) {
                if (response.isFailed()) {
                    failed++;
                }
            }
            log.info("Binlog索引同步完成，共{}条记录，成功{}条，失败{}条", total, total - failed, failed);
        } catch (Exception e) {
            log.error("Binlog索引同步失败", e);
        }
    }

}
