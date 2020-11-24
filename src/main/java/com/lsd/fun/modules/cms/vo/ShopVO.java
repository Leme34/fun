package com.lsd.fun.modules.cms.vo;

import com.lsd.fun.modules.cms.entity.ShopEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Created by lsd
 * 2020-03-26 11:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopVO extends ShopEntity {

    // 商铺类别名称
    private String category;
    // 商家名称
    private String seller;
    // 封面url
    private String coverUrl;
    // 封面是否爬取的
    private Integer isCrawl;

    // 地图搜房页面的与用户距离(m)
    private Integer distance;
    private double lat;
    private double lng;
}
