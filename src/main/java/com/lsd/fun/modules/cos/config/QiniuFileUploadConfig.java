package com.lsd.fun.modules.cos.config;

import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

/**
 * 上传文件配置类
 *
 * @ConditionalOnClass 这些类必须都存在，否则不解析该@Configuration的配置类；
 * @ConditionalOnProperty name用来从application.properties中读取某个属性值,
 * 如果没有配置,则返回matchIfMissing的值,否则返回配置值,根据其值决定此@Configuration是否生效
 * @EnableConfigurationProperties 为带有@ConfigurationProperties注解的Bean提供有效的支持。
 * 这个注解可以提供一种方便的方式来将带有@ConfigurationProperties注解的类注入为Spring容器的Bean。
 * 此处指定注入MultipartProperties的配置信息
 */
@Configuration
@ConditionalOnClass({Servlet.class, StandardServletMultipartResolver.class, MultipartConfigElement.class})
@ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(MultipartProperties.class)
public class QiniuFileUploadConfig {

    /**
     * 七牛云配置
     */
    @Value("#{funConfig.qiniu}")
    private QiNiuProperties qiNiuProperties;

    private final MultipartProperties multipartProperties;

    public QiniuFileUploadConfig(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    /**
     * 若容器中无Spring文件上传配置bean，则注册
     */
    @Bean
    @ConditionalOnMissingBean
    public MultipartConfigElement multipartConfigElement() {
        // 使用用户配置创建
        return this.multipartProperties.createMultipartConfig();
    }

    /**
     * 若容器中无MultipartResolver解析器，则注册
     */
    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    @ConditionalOnMissingBean(MultipartResolver.class)
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        multipartResolver.setResolveLazily(this.multipartProperties.isResolveLazily());
        return multipartResolver;
    }


    /**
     * 华南机房配置
     */
    @Bean
    public com.qiniu.storage.Configuration qiniuConfig() {
        return new com.qiniu.storage.Configuration(Zone.zone2());
    }

    /**
     * 注册一个七牛上传工具实例
     */
    @Bean
    public UploadManager uploadManager() {
        return new UploadManager(qiniuConfig());
    }


    /**
     * 注册认证信息实例Auth
     */
    @Bean
    public Auth auth() {
        return Auth.create(qiNiuProperties.getAccessKey(),
                qiNiuProperties.getSecretKey());
    }

    /**
     * 注册七牛空间管理实例
     */
    @Bean
    public BucketManager bucketManager() {
        return new BucketManager(auth(), qiniuConfig());
    }

}
