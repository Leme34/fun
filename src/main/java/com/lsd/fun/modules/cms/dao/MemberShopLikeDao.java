package com.lsd.fun.modules.cms.dao;

import com.lsd.fun.modules.cms.entity.MemberShopLikeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员点赞店铺表
 * 
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Mapper
public interface MemberShopLikeDao extends BaseMapper<MemberShopLikeEntity> {
	
}
