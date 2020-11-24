package com.lsd.fun.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.modules.sys.dao.SysLogDao;
import com.lsd.fun.modules.sys.entity.SysLogEntity;
import com.lsd.fun.modules.sys.query.SysLogQuery;
import com.lsd.fun.modules.sys.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Override
    public PageUtils queryPage(SysLogQuery queryForm) {
        String key = queryForm.getKey();

        IPage<SysLogEntity> page = this.page(
                new Query<SysLogEntity>().getPage(queryForm),
                new QueryWrapper<SysLogEntity>().lambda()
                        .like(StringUtils.isNotBlank(key),SysLogEntity::getUsername, key)
                        .or()
                        .like(StringUtils.isNotBlank(key),SysLogEntity::getOperation, key)
        );

        return new PageUtils(page);
    }
}
