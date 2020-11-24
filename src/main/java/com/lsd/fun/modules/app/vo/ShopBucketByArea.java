package com.lsd.fun.modules.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据地区聚合商铺信息DTO
 *
 * Created by lsd
 * 2020-04-03 10:15
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopBucketByArea {

    @ApiModelProperty("聚合bucket的key,此处是区级行政单位")
    private String key;

    @ApiModelProperty("聚合结果值,此处是此区级行政单位下的商铺数量")
    private long count;

    @ApiModelProperty("此区级行政单位百度地图经度")
    private double lng;
    @ApiModelProperty("此区级行政单位百度地图纬度")
    private double lat;

}
