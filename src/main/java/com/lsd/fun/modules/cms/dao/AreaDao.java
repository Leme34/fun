package com.lsd.fun.modules.cms.dao;

import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地区表,直辖市在level=0能够找到，在level=1也能找到
 * 
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 02:06:00
 */
@Mapper
public interface AreaDao extends BaseMapper<AreaEntity> {
	
}
