package com.lsd.fun.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsd.fun.modules.sys.entity.SysCaptchaEntity;

/**
 * 验证码
 *
 */
@Mapper
public interface SysCaptchaDao extends BaseMapper<SysCaptchaEntity> {

}
