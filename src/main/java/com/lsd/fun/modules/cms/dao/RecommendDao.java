package com.lsd.fun.modules.cms.dao;

import com.lsd.fun.modules.cms.entity.RecommendEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsd.fun.modules.cms.vo.ShopVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户-推荐商铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Mapper
public interface RecommendDao extends BaseMapper<RecommendEntity> {

}
