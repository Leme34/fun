package com.lsd.fun.config.properties;

import lombok.Data;
import com.lsd.fun.modules.sys.config.AdminAccountProperties;

/**
 * Created by lsd
 * 2019-11-15 14:08
 */
@Data
public class AuthProperties {
    private AdminAccountProperties admin = new AdminAccountProperties();
}
