package com.lsd.fun.modules.itag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lsd.fun.modules.app.dto.TagDto;
import com.lsd.fun.modules.app.vo.MemberTag;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by lsd
 * 2020-03-06 20:53
 */
@Slf4j
@Service
public class ETLEsService {

    private static final String INDEX_NAME = "fun_member";

    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient rhlClient;
    @Autowired
    private Gson gson;
    @Autowired
    private ObjectMapper objectMapper;

    public List<MemberTag> query(List<TagDto> tags) {
        BoolQueryBuilder boolQB = QueryBuilders.boolQuery();
        // 遍历用户选择的标签，根据标签查询类型构造查询条件
        for (TagDto tag : tags) {
            String name = tag.getName();
            String value = tag.getValue();
            String type = tag.getType();
            switch (type) {
                case "match":
                    boolQB.should(QueryBuilders.matchQuery(name, value));
                    break;
                case "notMatch":
                    boolQB.mustNot(QueryBuilders.matchQuery(name, value));
                    break;
                case "rangeBoth":
                    String[] split = value.split("-");
                    boolQB.should(QueryBuilders.rangeQuery(name).gte(split[0]).lte(split[1]));
                    break;
                case "rangeLte":
                    boolQB.should(QueryBuilders.rangeQuery(name).lte(value));
                    break;
                case "rangeGte":
                    boolQB.should(QueryBuilders.rangeQuery(name).gte(value));
                    break;
                case "exists":
                    boolQB.should(QueryBuilders.existsQuery(name));
                    break;
            }
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(boolQB)
                .fetchSource(new String[]{"memberId", "phone"}, null)  //只查会员id和手机号
                .from(0)
                .size(1000);
        SearchRequest searchRequest = new SearchRequest()
                .indices(INDEX_NAME)
                .source(sourceBuilder);
        log.debug(sourceBuilder.toString());

        try {
            SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
            List<MemberTag> resultList = new ArrayList<>();
            for (SearchHit documentFields : response.getHits().getHits()) {
                String document = documentFields.getSourceAsString();
                MemberTag memberTag = gson.fromJson(document, MemberTag.class);
                resultList.add(memberTag);
            }
            return resultList;
        } catch (IOException e) {
            log.error("查询出错", e);
        }
        return Collections.emptyList();
    }

    /**
     * 批量索引到ES
     */
    public void saveToEs(List<MemberTag> memberTags) throws IOException {
        BulkRequest bulkReq = new BulkRequest()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);  //写入完成立即刷新
        try {
            for (MemberTag tag : memberTags) {
                Map<String, Object> sourceMap = objectMapper.readValue(objectMapper.writeValueAsString(tag), new TypeReference<Map<String, Object>>() {
                });
                bulkReq.add(
                        new IndexRequest(INDEX_NAME).source(sourceMap).opType(DocWriteRequest.OpType.CREATE)
                );
            }
            BulkResponse responses = rhlClient.bulk(bulkReq, RequestOptions.DEFAULT);
            int total = responses.getItems().length;
            int failed = 0;
            for (BulkItemResponse response : responses) {
                if (response.isFailed()) {
                    failed++;
                }
            }
            log.info("会员用户ETL数据批量索引完成，共{}条记录，成功{}条，失败{}条", total, total - failed, failed);
        } catch (Exception e) {
            log.error("会员用户ETL数据批量索引出错");
            throw e;
        }
    }


}
