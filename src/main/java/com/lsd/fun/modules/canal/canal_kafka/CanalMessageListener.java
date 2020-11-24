package com.lsd.fun.modules.canal.canal_kafka;

import cn.hutool.json.JSONUtil;
import com.alibaba.otter.canal.protocol.Message;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.modules.app.dto.ShopIndexKey;
import com.lsd.fun.modules.app.service.ShopSearchService;
import com.lsd.fun.modules.cms.dao.ShopDao;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.dto.ShopSuggest;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.otter.canal.protocol.CanalEntry.*;

/**
 * 由 Canal 监听 MySQL 并发消息到 Kafka，由此监听器消费消息并更新到 ES 或 Hive
 * canal.mq.flatMessage = false的情况，canal-server拉取到多少Binlog就把多少变更行写到一个Kafka消息中，效率较高
 * <p>
 * Created by lsd
 * 2020-04-03 18:22
 */
@Slf4j
@Component
public class CanalMessageListener {

    private static final String CANAL_TOPIC = "example";
    private static final String DATABASE = "fun";

    @Autowired
    private ShopDao shopDao;
    @Autowired
    private BaiduLBSService baiduLBSService;
    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient rhlClient;
    @Autowired
    private ShopSearchService shopSearchService;
    @Autowired
    private Gson gson;

    /**
     * canal.mq.flatMessage = false的情况，手动解析Canal二进制形式（protobuf格式）的Message
     */
    @KafkaListener(topics = CANAL_TOPIC, properties = {
            "key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer=com.alibaba.otter.canal.client.kafka.MessageDeserializer"
    })
    public void handlerMessage(Message content) throws Exception {
        final long start = System.currentTimeMillis();
        List<Entry> entries = content.getEntries();
        // 收集所有变更数据行的表名和id
        ArrayListMultimap<String, Integer> upsertRecordIdsMap = ArrayListMultimap.create(); //数据变更队列
        List<Integer> deletedShopIdList = new ArrayList<>();//shop表数据(逻辑)删除队列
        // 若有数据Row更新，则解析处理Binlog，获取所在表和id
        for (Entry entry : entries) {
            this.parseCanalEvent(entry, upsertRecordIdsMap, deletedShopIdList);
        }
        // 去对应的table批量查询所有待更新数据
        List<Map<String, Object>> needIndexDataList = new ArrayList<>();
        // upsert队列去对应table批量查询
        for (String table : upsertRecordIdsMap.keySet()) {
            List<Integer> ids = upsertRecordIdsMap.get(table);
            needIndexDataList.addAll(this.queryLatestDataBatch(table, ids));
        }
        // 批量同步到ES
        this.bulkIndex2ES(needIndexDataList, deletedShopIdList);
        // 批量同步到百度LBS云
//        this.bulkIndex2LBS(needIndexDataList, deletedShopIdList);
        log.info("本次同步耗时：{}s", (System.currentTimeMillis() - start) / 1000);
    }


    /**
     * 解析处理Binlog，把待同步数据暂存在 upsert队列 和 deleted队列 中
     *
     * @param upsertRecordIdsMap 插入或更新记录队列
     * @param deletedShopIdList  shop表(逻辑)删除记录队列
     */
    private void parseCanalEvent(Entry entry,
                                 Multimap<String, Integer> upsertRecordIdsMap,
                                 List<Integer> deletedShopIdList) throws IOException {
        Header header = entry.getHeader();
//        EventType eventType = header.getEventType(); //若非逻辑删除应判断EventType
        String database = header.getSchemaName();
        if (!StringUtils.equals(database, DATABASE)) {
            return;
        }
        String table = header.getTableName();
        RowChange rowChange;
        try {
            rowChange = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            log.error("Binlog索引同步失败，原因：publishCanalEvent failed", e);
            throw e;
        }
        for (RowData rowData : rowChange.getRowDatasList()) {
            List<Column> columns = rowData.getAfterColumnsList();
            // 此行的所有列值转换为Map结构
            Map<String, String> columnDataMap = columns.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Column::getName, Column::getValue, (oldKey, newKey) -> newKey));
            // 获取此行记录的主键列
            final Integer id = new Integer(columnDataMap.get("id"));
            // 若此行是逻辑删除shop则加入删除队列
            if (StringUtils.equals(table, "shop") && Constant.TRUE.equals(new Integer(columnDataMap.get("disabled_flag")))) {
                deletedShopIdList.add(id);
            } else { //否则加入更新队列
                upsertRecordIdsMap.put(table, id);
            }
        }
    }


    /**
     * 根据Binlog的变更数据行信息，去对应表中查询待同步到ES的数据
     *
     * @param table      变更记录行所在数据表
     * @param idsOfTable 数据表中变更记录行的id列表
     */
    private List<Map<String, Object>> queryLatestDataBatch(String table, List<Integer> idsOfTable) {
        if (CollectionUtils.isEmpty(idsOfTable)) {
            return Collections.emptyList();
        }
        QueryWrapper<Object> wrapper = Wrappers.query().eq("shop.disabled_flag", 0);
        switch (table) {
            case "seller":
                wrapper.in("seller.id", idsOfTable);
                break;
            case "category":
                wrapper.in("category.id", idsOfTable);
                break;
            case "shop":
                wrapper.in("shop.id", idsOfTable);
                break;
            default:
                return Collections.emptyList();
        }
        // 一条数据Row是一个Map，返回这种结构方便 ES API 直接使用
        List<Map<String, Object>> needIndexDataList = shopDao.queryNeedIndexRow(wrapper);
        // 调用百度LBS把地址"address"字段替换为经纬度"location"
        for (Map<String, Object> shopVoRow : needIndexDataList) {
            try {
                BaiduMapLocation location = baiduLBSService.parseAddress2Location(shopVoRow.get("address").toString());
                shopVoRow.put("location", location.getLatitude() + "," + location.getLongitude());
            } catch (Exception e) {
                shopVoRow.put("location", "0,0");
            }
            // 索引中存储的自动补全关键词列表
            List<ShopSuggest> shopSuggests = shopSearchService.analyzeSuggestion(shopVoRow);
            shopVoRow.put(ShopIndexKey.SUGGESTION, JSONUtil.parseArray(shopSuggests));
        }
        return needIndexDataList;
    }

    /**
     * Sync to ES
     * 优化效果：不使用bulk的话306条记录同步需要>1分钟，优化后只需要？秒
     *
     * @param needIndexDataList 当前Kafka所有Binlog消息解析后得到的待更新数据
     * @param deletedShopIdList 待删除的索引数据
     */
    private void bulkIndex2ES(List<Map<String, Object>> needIndexDataList, List<Integer> deletedShopIdList) throws IOException {
        // 应使用 Bulk Api 批量更新提高效率
        BulkRequest bulkReq = new BulkRequest()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);  //写入完成立即刷新;
        for (Map<String, Object> map : needIndexDataList) {
            IndexRequest request = new IndexRequest(ShopIndexKey.INDEX_NAME)
                    .id(String.valueOf(map.get("id")))
                    .source(map);
            bulkReq.add(request);
        }
        for (Integer id : deletedShopIdList) {
            DeleteRequest request = new DeleteRequest(ShopIndexKey.INDEX_NAME).id(id.toString());
            bulkReq.add(request);
        }
        if (bulkReq.numberOfActions() == 0) {
            return;
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
            log.error("Binlog索引同步失败，原因：bulkIndex2ES failed", e);
            throw e;
        }
    }


    /**
     * Sync to 百度LBS.云POI数据
     *
     * @param needIndexDataList 当前Kafka所有Binlog消息解析后得到的待更新数据
     * @param deletedShopIdList 待删除的索引数据
     * @deprecated 同步成功率过低，弃用
     */
    @Deprecated
    private void bulkIndex2LBS(List<Map<String, Object>> needIndexDataList, List<Integer> deletedShopIdList) {
        // 上传
        for (Map<String, Object> shopVo : needIndexDataList) {
            Integer id = new Integer(shopVo.get("id").toString());
            Integer price = new Integer(shopVo.get("price_per_man").toString());
            String title = shopVo.get("title").toString();
            String address = shopVo.get("address").toString();
            String[] lng_lat = shopVo.get("location").toString().split(",");
            BaiduMapLocation location = new BaiduMapLocation(new Double(lng_lat[0]), new Double(lng_lat[1]));
            baiduLBSService.upload(location, title, address, id, price, 0);
        }
        // 删除
        for (Integer id : deletedShopIdList) {
            baiduLBSService.remove(id);
        }
    }

}
