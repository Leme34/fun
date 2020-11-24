package com.lsd.fun.modules.cms.dao;

import com.lsd.fun.modules.cms.entity.CouponMemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员-抵扣券中间表
 * 
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Mapper
public interface CouponMemberDao extends BaseMapper<CouponMemberEntity> {
	
}
