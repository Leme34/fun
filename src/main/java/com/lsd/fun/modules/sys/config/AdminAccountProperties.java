package com.lsd.fun.modules.sys.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lsd
 * 2019-08-08 14:19
 */
@Data
@Configuration
public class AdminAccountProperties {
    private String username = "admin";
    private String defaultPassword = "admin";
    private String salt = "YzcmCZNvbXocrsz9dm8e";
    private boolean enabled = true;
}
