package com.lsd.fun.modules.cos.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.DateUtils;
import com.lsd.fun.modules.cos.entity.TFileEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件对象表
 * <p>
 * Created by lsd
 * 2019-11-29 10:14
 */
public interface TFileService extends IService<TFileEntity> {

    /**
     * 上传 COS 云存储
     * 会根据 fileType 选择不同的云端相对路径
     *
     * @param file      文件对象
     * @param fileType  上传文件类型
     * @return 数据库中的文件对象实体类
     */
    TFileEntity upload(MultipartFile file, Integer fileType);


    /**
     * 仅删除 COS 云存储的文件对象
     *
     * @param cosPath 云端文件对象路径
     */
    void delete(String cosPath);

    /**
     * 删除 COS 云存储的文件对象 和 数据库
     *
     * @param tFileId
     */
    void deleteById(Integer tFileId);


    /**
     * 生成文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀，即 ".扩展名"
     * @return 返回以 "/" 开头的路径
     */
    default String genePath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;
        // prefix 不为空字符串才拼接
        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }
        // 保证 以 "/" 开头
        if (!StringUtils.startsWith(path, "/")) {
            path = "/" + path;
        }
        return path + suffix;
    }
}

