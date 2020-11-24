package com.lsd.fun.modules.app.dto;

import lombok.Data;

/**
 * 索引字段常量
 * <p>
 * Created by lsd
 * 2020-04-03 20:07
 */
@Data
public class ShopIndexKey {

    public final static String ID = "id";
    public final static String TITLE = "title";
    public final static String TAGS = "tags";
    public final static String PROVINCE = "province";
    public final static String CITY = "city";
    public final static String REGION = "region";
    public final static String ADDRESS = "address";
    public final static String LOCATION = "location";
    public final static String REMARK_SCORE = "remark_score";
    public final static String PRICE_PER_MAN = "price_per_man";
    public final static String CATEGORY_ID = "category_id";
    public final static String CATEGORY_NAME = "category_name";
    public final static String SELLER_ID = "seller_id";
    public final static String SELLER_NAME = "seller_name";
    public final static String SELLER_REMARK_SCORE = "seller_remark_score";
    public final static String DISABLED_FLAG = "disabled_flag";
    // 自动补全提示依赖字段
    public static final String SUGGESTION = "suggest";

    public final static String INDEX_NAME = "shop";
    public final static String AGG_REGION = "agg_region";
    public final static String AGG_TAGS = "agg_tags";

    public final static String DISTANCE = "distance";  //script_fields字段

}
