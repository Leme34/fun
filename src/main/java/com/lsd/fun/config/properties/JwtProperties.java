package com.lsd.fun.config.properties;

import lombok.Data;

/**
 * Created by lsd
 * 2019-11-15 14:08
 */
@Data
public class JwtProperties {
    // 加密秘钥
    private String secret = "f4e2e52034348f86b67cde581c0f9eb5[net.qunzhi.rest]";
    // token有效时长，7天，单位秒
    private Integer expire = 604800;
    // 请求header名称
    private String header = "token";
}
