package com.lsd.fun.modules.cos.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.modules.cos.config.QiNiuProperties;
import com.lsd.fun.modules.cos.service.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import lombok.extern.slf4j.Slf4j;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.ShiroUtils;
import com.lsd.fun.common.utils.SpringContextUtils;
import com.lsd.fun.modules.cos.dao.TFileDao;
import com.lsd.fun.modules.cos.entity.TFileEntity;
import com.lsd.fun.modules.cos.service.TFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by lsd
 * 2019-12-03 10:14
 */
@Slf4j
@Service
public class TFileServiceImpl extends ServiceImpl<TFileDao, TFileEntity> implements TFileService {

    @Value("#{funConfig.qiniu}")
    private QiNiuProperties qiNiuProperties;

    // Map<fileType,relativePath>
    public static Map<Integer, String> relativePathMap;

    @PostConstruct
    public void initRelativePathMap() {
        relativePathMap = Maps.newHashMap();
        relativePathMap.put(0, qiNiuProperties.getOtherPath());
        relativePathMap.put(1, qiNiuProperties.getAvatarPath());
        relativePathMap.put(2, qiNiuProperties.getImagePath());
        relativePathMap.put(3, qiNiuProperties.getVideoPath());
    }

    @Transactional
    @Override
    public TFileEntity upload(MultipartFile file, Integer fileType) {
        QiNiuService qiNiuService = getQiniuCOSService();

        final String originalFilename = file.getOriginalFilename();
        final String extensionName = "." + StringUtils.substringAfterLast(originalFilename, ".");
        final String relativePath = relativePathMap.get(fileType) == null ? qiNiuProperties.getOtherPath() : relativePathMap.get(fileType);
        // 云端路径
        String cosFilePath = relativePath + genePath(null, extensionName);
        // 上传云端
        Response response;
        try {
            response = qiNiuService.uploadFile(file.getInputStream(), cosFilePath);
            if (!response.isOK()) {
                log.error("文件上传失败,info={}", response.getInfo());
            }
        } catch (Exception e) {
            log.error("文件读入失败", e);
            throw new RRException("文件上传失败");
        }
        // 获取上传用户信息
        Long uploaderId = null;
        try {
            uploaderId = ShiroUtils.getUserId();
        } catch (NullPointerException ignored) {
        }
        // 入文件表
        final TFileEntity tFileEntity = new TFileEntity()
                .setOriginalFilename(originalFilename)
                .setUploaderId(uploaderId)
                .setMimeType(file.getContentType())
                .setPath(cosFilePath)
                .setSize(file.getSize());
        try {
            this.save(tFileEntity);
        } catch (Exception e) {
            // 删除云对象存储
            this.delete(cosFilePath);
            log.error("上传文件入库失败", e);
            throw new RRException("上传失败");
        }
        return tFileEntity.setPath(qiNiuProperties.getHostPrefix() + cosFilePath);
    }

    @Transactional
    @Override
    public void deleteById(Integer tFileId) {
        Optional<TFileEntity> oneOpt = this.lambdaQuery().select(TFileEntity::getPath, TFileEntity::getIsCrawl)
                .eq(TFileEntity::getId, tFileId)
                .oneOpt();
        if (!oneOpt.isPresent()) {
            return;
        }
        TFileEntity tFile = oneOpt.get();
        if (Constant.TRUE.equals(tFile.getIsCrawl())) {
            return;
        }
        this.delete(tFile.getPath());
        // 更新数据库
        this.lambdaUpdate()
                .set(TFileEntity::getDeletedAt, LocalDateTime.now())
                .eq(TFileEntity::getId, tFileId)
                .update();
    }

    @Override
    public void delete(String cosPath) {
        QiNiuService qCloudCOSService = getQiniuCOSService();
        try {
            Response response = qCloudCOSService.delete(cosPath);
            if (!response.isOK()) {
                log.error("文件删除失败,info={}", response.getInfo());
                throw new RRException("文件删除失败");
            }
        } catch (QiniuException e) {
            log.error("文件删除失败", e);
            throw new RRException("文件删除失败");
        }
    }


    /**
     * 若配置了 fun.qiniu.enable = true 才返回 QiNiuService 的 Bean 对象
     */
    private QiNiuService getQiniuCOSService() {
        return Optional.ofNullable(SpringContextUtils.getBean(QiNiuService.class))
                .orElseThrow(() -> new RRException("COS服务未启用"));
    }

}
