package com.lsd.fun.config.properties;

import lombok.Data;

/**
 * Created by lsd
 * 2019-11-15 14:21
 */
@Data
public class SwaggerProperties {
    private Boolean enable = true;
    private String host = "localhost:8080";

}
