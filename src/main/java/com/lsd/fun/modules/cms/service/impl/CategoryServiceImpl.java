package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.CategoryDao;
import com.lsd.fun.modules.cms.entity.CategoryEntity;
import com.lsd.fun.modules.cms.service.CategoryService;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(query),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

}
