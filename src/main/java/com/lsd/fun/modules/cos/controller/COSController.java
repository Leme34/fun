package com.lsd.fun.modules.cos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.cos.service.TFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件云对象存储相关接口
 * <p>
 * Created by lsd
 * 2019-11-28 18:14
 */
@Slf4j
@Api(tags = "文件相关接口")
@RequestMapping("/cos")
@RestController
public class COSController {

    @Autowired
    private TFileService tFileService;

    @ApiOperation("通用上传文件接口")
    @PostMapping("/save")
    public R uploadFile(MultipartFile file,
                        @ApiParam("文件类型，0：其他文件，1：头像文件，2：图片，3：音频") @RequestParam(required = false, defaultValue = "0") Integer fileType
    ) {
        return R.ok().put("data", tFileService.upload(file, fileType));
    }

    @ApiOperation("删除文件接口")
    @PostMapping("/delete")
    public R deleteFile(@ApiParam(value = "需要删除的文件id") @RequestParam Integer tFileId) {
        tFileService.deleteById(tFileId);
        return R.ok();
    }

}
