package com.lsd.fun.config;

import com.lsd.fun.modules.cos.config.QiNiuProperties;
import lombok.Data;
import com.lsd.fun.config.properties.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 自定义配置封装类
 * <p>
 * Created by lsd
 * 2019-11-15 14:06
 */
@ConfigurationProperties("fun")
@Configuration
@Data
public class FunConfig {
    private AuthProperties auth = new AuthProperties();
    private JwtProperties jwt = new JwtProperties();
    private ShiroProperties shiro = new ShiroProperties();
    private RedisProperties redis = new RedisProperties();
    private SwaggerProperties swagger = new SwaggerProperties();
    private QiNiuProperties qiniu = new QiNiuProperties();
}
