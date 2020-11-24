package com.lsd.fun.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.lsd.fun.common.exception.RRException;

import static java.time.temporal.ChronoUnit.MINUTES;

import com.lsd.fun.modules.sys.dao.SysCaptchaDao;
import com.lsd.fun.modules.sys.entity.SysCaptchaEntity;
import com.lsd.fun.modules.sys.service.SysCaptchaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * 验证码
 *
 */
@Service("sysCaptchaService")
public class SysCaptchaServiceImpl extends ServiceImpl<SysCaptchaDao, SysCaptchaEntity> implements SysCaptchaService {
    @Autowired
    private Producer producer;

    @Override
    public BufferedImage getCaptcha(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new RRException("uuid不能为空");
        }
        //生成文字验证码
        String code = producer.createText();

        SysCaptchaEntity captchaEntity = new SysCaptchaEntity();
        captchaEntity.setUuid(uuid);
        captchaEntity.setCode(code);
        //5分钟后过期
        captchaEntity.setExpireTime(LocalDateTime.now().plus(5, MINUTES));
        this.save(captchaEntity);

        return producer.createImage(code);
    }

    @Override
    public boolean validate(String uuid, String code) {
        SysCaptchaEntity captchaEntity = this.getOne(new QueryWrapper<SysCaptchaEntity>().eq("uuid", uuid));
        if (captchaEntity == null) {
            return false;
        }

        //删除验证码
        this.removeById(uuid);

        final boolean ge = captchaEntity.getExpireTime().isAfter(LocalDateTime.now()) || captchaEntity.getExpireTime().isEqual(LocalDateTime.now());
        if (captchaEntity.getCode().equalsIgnoreCase(code) && ge) {
            return true;
        }

        return false;
    }
}
