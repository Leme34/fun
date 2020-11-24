package com.lsd.fun.modules.cos.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * 七牛云服务
 */
public interface QiNiuService {

    /**
     * 上传文件，有文件类型校验逻辑
     *
     * @param file 需要上传的文件
     * @param key  保存到云端的文件名，若key为null则根据时间生成随机文件名
     */
    Response uploadFile(File file, String key) throws QiniuException;

    /**
     * 上传文件，无文件类型校验逻辑
     *
     * @param inputStream 需要上传的文件is
     * @param key  保存到云端的文件名，若key为null则七牛云会自动生成文件名
     */
    Response uploadFile(InputStream inputStream, String key) throws QiniuException;

    /**
     * 删除
     *
     * @param key 需要删除的文件文件名称
     */
    Response delete(String key) throws QiniuException;
}
