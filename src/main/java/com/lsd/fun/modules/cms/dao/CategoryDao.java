package com.lsd.fun.modules.cms.dao;

import com.lsd.fun.modules.cms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商铺类别表
 * 
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
