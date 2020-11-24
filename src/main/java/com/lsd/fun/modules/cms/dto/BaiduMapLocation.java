package com.lsd.fun.modules.cms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 百度地图经纬度结构
 * Created by lsd
 * 2020-02-09 18:35
 */
@AllArgsConstructor
@Data
public class BaiduMapLocation {

    // 经度
    @JsonProperty("lon")
    private double longitude;

    // 纬度
    @JsonProperty("lat")
    private double latitude;

}
