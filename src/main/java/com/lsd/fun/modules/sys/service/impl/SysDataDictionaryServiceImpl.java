package com.lsd.fun.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.modules.sys.dao.SysDataDictionaryDao;
import com.lsd.fun.modules.sys.entity.SysDataDictionaryEntity;
import com.lsd.fun.modules.sys.service.SysDataDictionaryService;
import com.lsd.fun.modules.sys.vo.TDataDictionaryVo;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SysDataDictionaryServiceImpl extends ServiceImpl<SysDataDictionaryDao, SysDataDictionaryEntity> implements SysDataDictionaryService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<SysDataDictionaryEntity> page = this.page(
                new Query<SysDataDictionaryEntity>().getPage(query),
                new QueryWrapper<SysDataDictionaryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<TDataDictionaryVo> queryList() {
        return this.baseMapper.queryList();
    }
}
