package com.lsd.fun.modules.cos.service.impl;

import com.google.gson.Gson;
import com.lsd.fun.modules.cos.config.QiNiuProperties;
import com.lsd.fun.modules.cos.service.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;

@ConditionalOnProperty(value = "fun.qiniu.enable", havingValue = "true")
@Service
public class QiNiuServiceImpl implements QiNiuService, InitializingBean {
    @Autowired
    private UploadManager uploadManager;
    @Autowired
    private BucketManager bucketManager;
    @Autowired
    private Auth auth;
    @Value("#{funConfig.qiniu}")
    private QiNiuProperties qiNiuProperties;
    @Autowired
    private Gson gson;
    private StringMap putPolicy;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    @Override
    public Response uploadFile(File file, String key) throws QiniuException {
        key = validateFileTypeAndKey(file, key);
        Response response = this.uploadManager.put(file, key, getUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, key, getUploadToken());
            retry++;
        }
        if (!response.isOK()) {
            throw new RuntimeException("文件上传失败！Cause By：" + response.error);
        }
        return response;
    }


    @Override
    public Response uploadFile(InputStream inputStream, String key) throws QiniuException {
        // 空串需要手动替换为null七牛云才会使用hash
        if (StringUtils.isBlank(key)) {
            key = null;
        }
        Response response = this.uploadManager.put(inputStream, key, getUploadToken(), null, null);
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(inputStream, key, getUploadToken(), null, null);
            retry++;
        }
        if (!response.isOK()) {
            throw new RuntimeException("文件上传失败！Cause By：" + response.error);
        }
        return response;
    }

    @Override
    public Response delete(String key) throws QiniuException {
        Response response = bucketManager.delete(qiNiuProperties.getBucket(), key);
        int retry = 0;
        while (response.needRetry() && retry++ < 3) {
            response = bucketManager.delete(qiNiuProperties.getBucket(), key);
        }
        return response;
    }

    /**
     * 获取上传凭证
     *
     * @return
     */
    private String getUploadToken() {
        return this.auth.uploadToken(qiNiuProperties.getBucket(), null, 3600, putPolicy);
    }

    /**
     * 文件名生成规则：年/月/日/当前时间戳/随机数.扩展名
     *
     * @param file 用户上传的文件
     * @return 生成的随机文件名
     */
    private String geneRandomFileName(File file) {
        LocalDateTime now = LocalDateTime.now();
        String key = now.getYear() + "/" + now.getMonthValue() + "/" +
                now.getDayOfMonth() + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(100, 9999) + "." +
                StringUtils.substringAfterLast(file.getName(), ".");
        return key;
    }

    /**
     * 若文件格式不在配置的允许列表中抛出异常
     * 若key为空串，生成随机key
     *
     * @param file 用户上传的文件
     * @param key  用户指定的云端文件名
     * @return 若key为空串，返回生成随机的key
     */
    private String validateFileTypeAndKey(File file, String key) {
        // 校验文件格式
        boolean isLegal = qiNiuProperties.getAllowTypes().stream()
                .anyMatch(type -> StringUtils.endsWithIgnoreCase(file.getName(), type));
        if (!isLegal) {
            throw new RuntimeException("上传文件格式不合法！");
        }
        // 若文件名为空串，则根据时间生成文件名
        if (StringUtils.isBlank(key)) {
            key = geneRandomFileName(file);
        }
        return key;
    }

}
