package com.lsd.fun.config.baidu_map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 百度地图API相关配置
 *
 * Created by lsd
 * 2020-02-09 21:05
 */
@Configuration
@ConfigurationProperties("baidu-map")
@Data
public class BaiduMapProperties {

    // 访问应用（AK）服务端
    private String apiKey;
    // 地理编码服务Web API接口url前缀
    private String geocoderApiPrefix;
    // LBS.云服务 - 位置数据（poi）管理相关
    private LbsPoi poi;

}
