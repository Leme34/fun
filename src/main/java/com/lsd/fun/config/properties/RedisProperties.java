package com.lsd.fun.config.properties;

import lombok.Data;

/**
 * Created by lsd
 * 2019-11-15 14:08
 */
@Data
public class RedisProperties {

    @Data
    public static class KeyPrefix {
        // 数据字典
        private String dictionary = "";
        // 邮箱验证码
        private String mailCaptcha = "";
        // 购物车的Key
        private String cart = "";
        // etl数据的Key
        private String etl = "";
    }

    private KeyPrefix keyPrefix = new KeyPrefix();

}
