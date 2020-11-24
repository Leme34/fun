package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.entity.CategoryEntity;
import com.lsd.fun.common.utils.BaseQuery;
import java.util.Map;

/**
 * 商铺类别表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(BaseQuery query);
}

