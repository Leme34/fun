package com.lsd.fun.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lsd
 * 2020-02-03 23:01
 */
@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeSpecialFloatingPointValues() //序列化特殊浮点值，防止Float.POSITIVE_INFINITY序列化报错
                .create();
    }

}
