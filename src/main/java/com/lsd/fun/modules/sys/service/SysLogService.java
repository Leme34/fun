package com.lsd.fun.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.sys.entity.SysLogEntity;
import com.lsd.fun.modules.sys.query.SysLogQuery;

/**
 * 系统日志
 *
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(SysLogQuery queryForm);

}
