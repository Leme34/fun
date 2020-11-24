package com.lsd.fun.modules.cos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 读取七牛云自定义配置
 */
@Data
public class QiNiuProperties {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String cdnPrefix;
    // 外链默认域名
    private String hostPrefix;
    // 允许上传的文件类型
    private List<String> allowTypes;

    private Boolean enable = false;
    // 用户头像目录
    private String avatarPath = "";
    // 图片目录
    private String imagePath = "";
    // 音频目录
    private String videoPath = "";
    // 其他文件目录
    private String otherPath = "";

}
