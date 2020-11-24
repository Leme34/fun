package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.CommodityCategoryDao;
import com.lsd.fun.modules.cms.entity.CommodityCategoryEntity;
import com.lsd.fun.modules.cms.service.CommodityCategoryService;


@Service
public class CommodityCategoryServiceImpl extends ServiceImpl<CommodityCategoryDao, CommodityCategoryEntity> implements CommodityCategoryService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<CommodityCategoryEntity> page = this.page(
                new Query<CommodityCategoryEntity>().getPage(query),
                new QueryWrapper<CommodityCategoryEntity>()
        );

        return new PageUtils(page);
    }

}
