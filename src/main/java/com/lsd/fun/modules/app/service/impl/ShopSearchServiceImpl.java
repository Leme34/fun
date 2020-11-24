package com.lsd.fun.modules.app.service.impl;

import com.google.common.collect.Lists;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.ShopSortUtil;
import com.lsd.fun.modules.app.dto.LocationDto;
import com.lsd.fun.modules.app.dto.SearchResultDto;
import com.lsd.fun.modules.app.dto.ShopIndexKey;
import com.lsd.fun.modules.app.query.MapSearchQuery;
import com.lsd.fun.modules.app.query.ShopSearchQuery;
import com.lsd.fun.modules.app.service.ShopSearchService;
import com.lsd.fun.modules.app.vo.ShopBucketByArea;
import com.lsd.fun.modules.app.vo.ShopSearchResult;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.dto.ShopSuggest;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import com.lsd.fun.modules.cms.service.ShopService;
import com.lsd.fun.modules.cms.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lsd
 * 2020-04-03 10:23
 */
@Slf4j
@Service
public class ShopSearchServiceImpl implements ShopSearchService {
    //人工标注得到的categoryId与相关搜索词的映射
    private final static Map<Integer, List<String>> categoryId2WordMap = new HashMap<>();

    @PostConstruct
    public void iniCategoryId2WordMap() {
        final List<String> foods = Lists.newArrayList("鸡", "吃饭", "晚饭", "美食", "食物", "吃");
        final List<String> hotels = Lists.newArrayList("休息", "睡觉", "住宿", "住", "民宿", "酒店");
        categoryId2WordMap.put(1, foods);
        categoryId2WordMap.put(2, hotels);
    }

    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient rhlClient;
    @Autowired
    private BaiduLBSService baiduLBSService;
    @Autowired
    private ShopService shopService;
    @Value("${fun.elasticsearch.max-suggest:5}")
    private Integer maxSuggest;

    @Override
    public List<ShopBucketByArea> aggBySubArea(String cityName) {
        // 先根据城市filter再聚合
        BoolQueryBuilder qb = QueryBuilders.boolQuery().filter(
                QueryBuilders.termQuery(ShopIndexKey.CITY, cityName)
        );
        SearchSourceBuilder sb = new SearchSourceBuilder()
                .query(qb)
                .aggregation(
                        AggregationBuilders.terms(ShopIndexKey.AGG_REGION)     //聚合名称
                                .field(ShopIndexKey.REGION)            //根据区级行政区聚合
                );
        SearchRequest searchReq = new SearchRequest(ShopIndexKey.INDEX_NAME).source(sb);
        log.debug(searchReq.toString());
        try {
            final SearchResponse response = rhlClient.search(searchReq, RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK) {
                log.error("地图页面城市信息聚合失败,searchRequest = {}", searchReq.toString());
                throw new RRException("获取地区聚合信息失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            // 聚合信息转为DTO返回
            Terms terms = response.getAggregations().get(ShopIndexKey.AGG_REGION);
            return terms.getBuckets().stream().map(buck -> {
                // 请求百度地图获取地区经纬度
                String region = buck.getKeyAsString();
                BaiduMapLocation location = baiduLBSService.parseAddress2Location(region);
                return new ShopBucketByArea(region, buck.getDocCount(), location.getLongitude(), location.getLatitude());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取地区聚合信息失败", e);
            throw new RRException("获取地区聚合信息失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PageUtils mapSearchByCity(MapSearchQuery query) {
        // ES搜索出此城市的所有房源id
        QueryBuilder boolQB = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ShopIndexKey.CATEGORY_ID, 2))
                .filter(QueryBuilders.termQuery(ShopIndexKey.CITY, query.getCity()));
        final SearchResultDto searchResult = this.searchES(query, boolQB);
        return this.searchDB(query, searchResult);
    }

    @Override
    public PageUtils mapSearchByBound(MapSearchQuery query) {
        // ES搜索出当前地图视野边界范围的所有房源id
        GeoBoundingBoxQueryBuilder boundingBoxQB = QueryBuilders.geoBoundingBoxQuery(ShopIndexKey.LOCATION)
                .setCorners(
                        new GeoPoint(query.getLeftLatitude(), query.getLeftLongitude()),
                        new GeoPoint(query.getRightLatitude(), query.getRightLongitude())
                );
        BoolQueryBuilder boolQB = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ShopIndexKey.CATEGORY_ID, 2))
                .filter(QueryBuilders.termQuery(ShopIndexKey.CITY, query.getCity()))
                .filter(boundingBoxQB);
        final SearchResultDto searchResult = this.searchES(query, boolQB);
        return this.searchDB(query, searchResult);
    }


    /**
     * 查询数据库
     *
     * @param searchResult ES搜索结果
     */
    private PageUtils searchDB(BaseQuery query, SearchResultDto searchResult) {
        final Map<Integer, LocationDto> id2LocationMap = searchResult.getResultMap();
        final long total = searchResult.getTotal();
        if (total == 0 || MapUtils.isEmpty(id2LocationMap)) {
            return new PageUtils(Collections.emptyList(), query.getPage(), query.getLimit(), 0, false);
        }
        // 查询数据库
        List<ShopVO> shopVOS = shopService.listOrderByField(id2LocationMap.keySet());
        for (ShopVO vo : shopVOS) {
            LocationDto dto = id2LocationMap.get(vo.getId());
            vo.setDistance(dto.getDistance());
            vo.setLat(dto.getLat());
            vo.setLng(dto.getLng());
        }
        return new PageUtils(shopVOS, query.getPage(), query.getLimit(), (int) total, false);
    }


    /**
     * 搜索ES
     *
     * @param qb 搜索条件构造器
     * @return Map<ID, 与用户的距离>
     */
    private SearchResultDto searchES(MapSearchQuery query, QueryBuilder qb) {
        Map<Integer, LocationDto> id2LocationMap = new LinkedHashMap<>();
        boolean isSortByDistance = StringUtils.equals("distance", query.getOrder_field());
        //传入script脚本的2个入参
        Map<String, Object> params = new HashMap<>();
        params.put("lon", query.getLng());
        params.put("lat", query.getLat());
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(qb)
                .scriptField("distance",
                        new Script(ScriptType.INLINE, "expression", "haversin(lat,lon,doc['location'].lat,doc['location'].lon)", params)
                )
                .fetchSource(new String[]{ShopIndexKey.ID, ShopIndexKey.LOCATION}, null)  //只查出houseId，避免其他无用数据浪费性能
                .from(query.getPage() - 1)  //ES的from从0开始
                .size(query.getLimit());
        if (isSortByDistance) {
            sourceBuilder.sort(
                    SortBuilders.geoDistanceSort(ShopIndexKey.LOCATION, query.getLat(), query.getLng()) //地理位置距离类型的排序，排序字段：location，基于用户当前地理位置计算距离
                            .unit(DistanceUnit.KILOMETERS) //距离单位
                            .geoDistance(GeoDistance.ARC)  //距离类型：球形（默认的）
                            .order(SortOrder.fromString(query.getOrder()))
            );
        } else {
            sourceBuilder.sort(ShopSortUtil.getSortKey(query.getOrder_field()), SortOrder.fromString(query.getOrder()));
        }
        final SearchRequest searchRequest = new SearchRequest(ShopIndexKey.INDEX_NAME).source(sourceBuilder);
        log.debug(searchRequest.toString());
        try {
            final SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK) {
                log.error("房源搜索出错，searchRequest = {}", searchRequest.toString());
                throw new RRException("获取房源信息失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            for (SearchHit hit : response.getHits().getHits()) {
                int id = new Integer(hit.getSourceAsMap().get(ShopIndexKey.ID).toString());
                String location = hit.getSourceAsMap().get(ShopIndexKey.LOCATION).toString();
                final String[] lat_lng = location.split(",");
                BigDecimal distanceKm = new BigDecimal(hit.getFields().get(ShopIndexKey.DISTANCE).getValue().toString());
                // km -> m 然后取整
                int distanceM = distanceKm.multiply(BigDecimal.valueOf(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue();
                id2LocationMap.put(id, new LocationDto(distanceM, new Double(lat_lng[0]), new Double(lat_lng[1])));
            }
            final long total = response.getHits().getTotalHits().value;
            return new SearchResultDto(total, id2LocationMap);
        } catch (IOException e) {
            log.error("房源搜索出错", e);
            throw new RRException("获取房源信息失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ShopSearchResult search(ShopSearchQuery query) {
        ShopSearchResult result = new ShopSearchResult();
        FunctionScoreQueryBuilder functionScore;

        // =======================  function_score的query(召回) ===========================
        BoolQueryBuilder functionScoreQuery = QueryBuilders.boolQuery();
        List<QueryBuilder> must = functionScoreQuery.must();
        if (query.getCategoryId() != null) {  //若用户选中了类目
            must.add(QueryBuilders.termQuery(ShopIndexKey.CATEGORY_ID, query.getCategoryId()));
        }
        if (StringUtils.isNotBlank(query.getTags())) {  //若用户选中了以空格分隔的多个标签
            must.add(QueryBuilders.matchQuery(ShopIndexKey.TAGS, query.getTags()).operator(Operator.AND));
        }
        // 根据搜索词去匹配人工标注的语义相关的多个分类id
        Map<String, Integer> relativeCategory4KeywordMap = StringUtils.isBlank(query.getKeyword()) ? null :
                this.analyzeCategoryByKeyWord(query.getKeyword());
        // 相关性不能既使用在召回策略又使用在排序（打分）策略，否则会互相影响分值导致召回的文档排序混乱
        // 假如一个文档在召回策略中得分占比高达0.9而排序策略中只有0.1，而另一个文档在排序策略中得分占比高达0.9而召回策略中只有0.1，那么最后相加起来的得分是没有排序意义的
        // 最佳实践：一般我们不会把语义相关性作用于召回规则（防止语义理解错误导致过多无关的结果），而是使用在排序策略来提高搜索结果的相关性
        // 综上默认情况下，语义相关性只影响排序(打分)策略更加精确
        boolean isStrict = Constant.TRUE.equals(query.getIsStrict());
        if (!isStrict && !MapUtils.isEmpty(relativeCategory4KeywordMap)) {  // 语义相关性影响召回策略
            List<QueryBuilder> should = functionScoreQuery.should();
            should.add(QueryBuilders.matchQuery(ShopIndexKey.TITLE, query.getKeyword()).boost(0.1F));
            should.add(QueryBuilders.matchQuery(ShopIndexKey.SELLER_NAME, query.getKeyword()).boost(0.1F));
            // 把所有搜索分词语义相关的类目id全部增加到should条件中，负责文档召回
            relativeCategory4KeywordMap.forEach((keywordToken, relativeCategoryId) ->
                    // 相关category只影响召回，不影响打分（排序）
                    should.add(QueryBuilders.termQuery(ShopIndexKey.CATEGORY_ID, relativeCategoryId).boost(0))
            );
        } else if (StringUtils.isNotBlank(query.getKeyword())) {   // 语义相关性不影响召回，只影响排序(打分)策略(再下边的function_score中再通过filter对相关的category统一进行打分)
            List<QueryBuilder> should = functionScoreQuery.should();
            should.add(QueryBuilders.matchQuery(ShopIndexKey.TITLE, query.getKeyword()).boost(0.1F));
            should.add(QueryBuilders.matchQuery(ShopIndexKey.SELLER_NAME, query.getKeyword()));
            should.add(QueryBuilders.matchQuery(ShopIndexKey.ADDRESS, query.getKeyword()));
        }

        // =======================  function_score的functions(打分) =======================
        SortBuilder sortBuilder;
        // 当用户没有选择排序字段时使用默认打分策略排序
        if (StringUtils.isBlank(query.getOrder_field())) {
            // 自定义字段打分权重
            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> functionsList = Lists.newArrayList(
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(   //距离使用高斯衰减函数打分
                            ScoreFunctionBuilders.gaussDecayFunction(
                                    ShopIndexKey.LOCATION,
                                    query.getLat() + "," + query.getLng(),
                                    "50km", "0km", 0.5
                            ).setWeight(9)
                    ),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(   //加入商铺评分影响
                            ScoreFunctionBuilders.fieldValueFactorFunction(ShopIndexKey.REMARK_SCORE)
                                    .setWeight(0.2F)
                    ),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(   //加入商家评分影响
                            ScoreFunctionBuilders.fieldValueFactorFunction(ShopIndexKey.SELLER_REMARK_SCORE)
                                    .setWeight(0.1F)
                    )
            );
            // 若相关性影响排序,还要为所有搜索分词相关的分类id使用filter统一打分
            if (isStrict && !MapUtils.isEmpty(relativeCategory4KeywordMap)) {
                relativeCategory4KeywordMap.forEach((keywordToken, relativeCategoryId) ->
                        functionsList.add(
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        QueryBuilders.termQuery(ShopIndexKey.CATEGORY_ID, relativeCategoryId),
                                        ScoreFunctionBuilders.weightFactorFunction(3)
                                )
                        )
                );
            }
            // 指定分数计算模式，构造最终的query
            FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = functionsList.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[0]);
            functionScore = QueryBuilders.functionScoreQuery(functionScoreQuery, functions)
                    .scoreMode(FunctionScoreQuery.ScoreMode.SUM)    //以上functions之间的得分相加
                    .boostMode(CombineFunction.SUM);                //functions的总得分与query得分相加
            // 根据分数排序
            sortBuilder = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
        } else {  //非默认排序，直接根据用户选择的字段排序，不需要functions去计算分数 (另一种方案是把用户指定的排序字段放入field_value_factor,统一根据_score排序)
            boolean isSortByDistance = StringUtils.equals("distance", query.getOrder_field());
            if (isSortByDistance) {
                sortBuilder = SortBuilders.geoDistanceSort(ShopIndexKey.LOCATION, query.getLat(), query.getLng()) //地理位置距离类型的排序，排序字段：location，基于用户当前地理位置计算距离
                        .unit(DistanceUnit.KILOMETERS) //距离单位
                        .geoDistance(GeoDistance.ARC)  //距离类型：球形（默认的）
                        .order(SortOrder.fromString(query.getOrder()));
            } else {  //按其他文档字段排序
                sortBuilder = SortBuilders.fieldSort(ShopSortUtil.getSortKey(query.getOrder_field()))
                        .order(SortOrder.fromString(query.getOrder()));
            }
            // 指定分数计算模式，构造最终的query
            functionScore = QueryBuilders.functionScoreQuery(functionScoreQuery);
//                    .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
//                    .boostMode(CombineFunction.REPLACE);   //functions的总得分替换掉function_score中的query的得分
        }

        // =======================  搜索 =======================
        //传入script脚本的2个入参
        Map<String, Object> params = new HashMap<>();
        params.put("lon", query.getLng());
        params.put("lat", query.getLat());
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(functionScore)
                .scriptField("distance",
                        new Script(ScriptType.INLINE, "expression", "haversin(lat,lon,doc['location'].lat,doc['location'].lon)", params)
                )
                .fetchSource(new String[]{ShopIndexKey.ID, ShopIndexKey.LOCATION}, null)  //只查出houseId，避免其他无用数据浪费性能
                .aggregation(AggregationBuilders.terms(ShopIndexKey.AGG_TAGS).field(ShopIndexKey.TAGS))
                .sort(sortBuilder)  //排序规则
                .from(query.getPage() - 1)    //ES的from从0开始
                .size(query.getLimit());
        SearchRequest searchRequest = new SearchRequest(ShopIndexKey.INDEX_NAME).source(sourceBuilder);
        log.debug(searchRequest.toString());
        try {
            final SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK) {
                log.error("搜索出错，searchRequest = {}", searchRequest.toString());
                throw new RRException("搜索失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            Map<Integer, LocationDto> id2LocationMap = new LinkedHashMap<>();
            for (SearchHit hit : response.getHits().getHits()) {
                int id = new Integer(hit.getSourceAsMap().get(ShopIndexKey.ID).toString());
                String location = hit.getSourceAsMap().get(ShopIndexKey.LOCATION).toString();
                final String[] lat_lng = location.split(",");
                BigDecimal distanceKm = new BigDecimal(hit.getFields().get(ShopIndexKey.DISTANCE).getValue().toString());
                // km -> m 然后取整
                int distanceM = distanceKm.multiply(BigDecimal.valueOf(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue();
                id2LocationMap.put(id, new LocationDto(distanceM, new Double(lat_lng[0]), new Double(lat_lng[1])));
            }
            final long total = response.getHits().getTotalHits().value;
            List shopVos = this.searchDB(query, new SearchResultDto(total, id2LocationMap)).getList();
            // 聚合标签字段，返回给各个标签给用户进一步筛选
            Aggregations aggregations = response.getAggregations();
            Terms buckets = aggregations.get(ShopIndexKey.AGG_TAGS);
            List<Map<String, Object>> tagBuckets = buckets.getBuckets()
                    .stream()
                    .map(bucket -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("tags", bucket.getKeyAsString());
                        map.put("num", bucket.getDocCount());
                        return map;
                    }).collect(Collectors.toList());
            return result.setShopList(shopVos).setTagsAggs(tagBuckets).setTotal(total);
        } catch (IOException e) {
            log.error("搜索出错", e);
            throw new RRException("搜索失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<String> searchAsUType(String prefix) {
        //参照:https://www.elastic.co/guide/en/elasticsearch/reference/6.6/search-suggesters-completion.html
        CompletionSuggestionBuilder completionSuggestion = SuggestBuilders.completionSuggestion(ShopIndexKey.SUGGESTION)
                .analyzer("ik_syno")    //使用自定义的同义词分词器
                .prefix(prefix)         //提供给suggest analyzer分析的需要补全的前缀
                .size(maxSuggest)       //每个建议文本项最多可返回的建议词个数，默认值是5
                .skipDuplicates(true); //是否从结果中过滤掉来自不同文档的重复建议词，开启后会减慢搜索速度，因为需要遍历更多的建议词选出topN，下边已使用set去重，由于索引中重复太多需要开启
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().suggest(
                new SuggestBuilder().addSuggestion("autocomplete", completionSuggestion)
        );
        SearchRequest searchRequest = new SearchRequest(ShopIndexKey.INDEX_NAME).source(sourceBuilder);
        log.debug(searchRequest.toString());
        try {
            final SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
            // 最终获取5个补全建议关键字结果（做去重处理）
            final Set<String> suggestionSet = new HashSet<>();
            response.getSuggest().getSuggestion("autocomplete")
                    .getEntries()
                    .stream()
                    .filter(entry ->
                            entry instanceof CompletionSuggestion.Entry && !entry.getOptions().isEmpty()
                    ).map(entry ->
                    ((CompletionSuggestion.Entry) entry).getOptions()
            ).forEach(options -> {
                if (suggestionSet.size() > maxSuggest) {
                    return;
                }
                for (CompletionSuggestion.Entry.Option option : options) {
                    if (suggestionSet.size() > maxSuggest) {
                        break;
                    }
                    suggestionSet.add(option.getText().string());
                }
            });
            return Lists.newArrayList(suggestionSet.toArray(new String[0]));
        } catch (Exception e) {
            log.error("获取补全建议关键词失败", e);
            return new ArrayList<>();
        }
    }

    public List<ShopSuggest> analyzeSuggestion(Map<String, Object> shopVoRow) {
        String shopId = shopVoRow.get("id").toString();
        //构造数字类型term的过滤器，使分析返回的token不包含数字类型的term
//        final Map<String, Object> numericFilter = new HashMap<>();
//        numericFilter.put("type", "keep_types");
//        numericFilter.put("types", new String[]{"<NUM>"});
//        numericFilter.put("mode", "exclude");
//        //构造term长度过滤器，使分析返回的token长度>=2，这样补全提示才有意义
//        final Map<String, Object> lengthFilter = new HashMap<>();
//        lengthFilter.put("type", "length");
//        lengthFilter.put("min", 2);
//        // 用户输入的搜索词与这些域（底层存储的倒排索引词条）匹配
//        AnalyzeRequest analyzeReq = AnalyzeRequest
//                .buildCustomAnalyzer("ik_max_word")                //ik分词器分词
//                .addTokenFilter(numericFilter)                  //过滤器，we can change term or add/remove term
//                .addTokenFilter(lengthFilter)
//                .build(                                         //被分析的内容
//                        shopVoRow.get("title") + "",
//                        shopVoRow.get("description") + "",
//                        shopVoRow.get("address") + "",
//                        shopVoRow.get("seller") + ""
//                );
        // 改用自定义的max同义词分词器分词
        AnalyzeRequest analyzeReq = AnalyzeRequest
                .withIndexAnalyzer(ShopIndexKey.INDEX_NAME, "ik_syno_max4suggest",
                        shopVoRow.get("title") + "",
                        shopVoRow.get("address") + "",
                        shopVoRow.get("seller") + "");
        try {
            final AnalyzeResponse response = rhlClient.indices().analyze(analyzeReq, RequestOptions.DEFAULT);
            // 获取词条分析结果 The token is the actual term that will be stored in the index
            List<AnalyzeResponse.AnalyzeToken> analyzeTokenList = response.getTokens();
            if (CollectionUtils.isEmpty(analyzeTokenList)) {
                log.warn("词条分析结果解析失败: shopId = {}", shopId);
            }
            return analyzeTokenList.stream()
                    .map(token -> new ShopSuggest(token.getTerm()))
                    .collect(Collectors.toList());
            // 非analyze字段的直接加入补全建议关键词列表，小区名...等
//            suggestList.add(new ShopSuggest(shopVO.getRegion()));
        } catch (Exception e) {
            log.error("词条分析失败: shopId = " + shopId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据词汇匹配类目id
     *
     * @param token 搜索词的一个分词
     */
    private Integer getCategoryIdByToken(String token) {
        for (Map.Entry<Integer, List<String>> categoryId2Word : categoryId2WordMap.entrySet()) {
            if (categoryId2Word.getValue().contains(token)) {
                return categoryId2Word.getKey();
            }
        }
        return null;
    }

    /**
     * 根据用户搜索词，找到相关的类目id
     * 具体实现：先使用"title"字段的分词器分析用户输入的搜索词，得到每个词条，然后去人工标注得到的 categoryId与相关搜索词的映射 中找到它们对应的categoryId
     *
     * @return Map<用户搜索词分词token, categoryId>
     */
    private Map<String, Integer> analyzeCategoryByKeyWord(String keyword) {
        Map<String, Integer> resMap = new HashMap<>();
        //用shop索引的name的分析器(已配置ik自定义词库和同义词词库)去得到分词结果
        AnalyzeRequest analyzeReq = AnalyzeRequest
                .withField(ShopIndexKey.INDEX_NAME, "title", keyword);
        System.out.println(analyzeReq);
        try {
            final AnalyzeResponse response = rhlClient.indices().analyze(analyzeReq, RequestOptions.DEFAULT);
            // 获取词条分析结果 The token is the actual term that will be stored in the index
            List<AnalyzeResponse.AnalyzeToken> analyzeTokenList = response.getTokens();
            for (AnalyzeResponse.AnalyzeToken token : analyzeTokenList) {
                String tokenTerm = token.getTerm();
                Integer categoryId = this.getCategoryIdByToken(tokenTerm);
                if (categoryId != null) {
                    resMap.put(tokenTerm, categoryId);
                }
            }
        } catch (Exception e) {
            log.error("相关性搜索--输入词分析失败", e);
        }
        return resMap;
    }


}
