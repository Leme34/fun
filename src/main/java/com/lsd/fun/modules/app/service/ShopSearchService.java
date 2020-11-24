package com.lsd.fun.modules.app.service;

import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.app.query.MapSearchQuery;
import com.lsd.fun.modules.app.query.ShopSearchQuery;
import com.lsd.fun.modules.app.vo.ShopBucketByArea;
import com.lsd.fun.modules.app.vo.ShopSearchResult;
import com.lsd.fun.modules.cms.dto.ShopSuggest;
import com.lsd.fun.modules.cms.vo.ShopVO;

import java.util.List;
import java.util.Map;

/**
 * Created by lsd
 * 2020-04-03 10:22
 */
public interface ShopSearchService {
    /**
     * 根据选定城市聚合子地区商铺信息
     */
    List<ShopBucketByArea> aggBySubArea(String cityName);

    /**
     * 地图查询整个城市的房源
     */
    PageUtils mapSearchByCity(MapSearchQuery query);

    /**
     * 地图查询当前地图视野边界范围内的房源
     */
    PageUtils mapSearchByBound(MapSearchQuery query);

    /**
     * 语义相关性搜索服务
     * 相关性不能既影响召回又影响排序，一起使用会一起加分，导致召回的文档排序混乱不符合需求。
     * 比如召回打分高达0.9但排序打分只有0.1，那再把两个评分相加就会破坏打分标杆。
     * 最佳实践：一般我们不会选择作用于召回规则（防止语义理解错误导致过多无关的结果），而是使用排序策略来提高搜索结果的相关性
     * 但是语义相关性只影响排序情况下只适用于默认排序，当用户选择了排序字段则不可以使用语义相关性排序
     */
    ShopSearchResult search(ShopSearchQuery query);

    /**
     * 根据输入内容获取补全建议关键词
     * @param prefix 用户输入的内容
     */
    List<String> searchAsUType(String prefix);

    /**
     * 使用 ik_smart Tokenizer + 自定义同义词TokenFilter 对索引数据进行词条分析(分词)，并把全部词条(term)加入到索引的 补全建议关键词 列表
     * <p>
     * 过滤器构造参照:https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-analyze.html
     * 过滤器类型参照:https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-tokenfilters.html
     */
    List<ShopSuggest> analyzeSuggestion(Map<String, Object> shopVoRow);  //一条shop数据Row是一个Map
}
