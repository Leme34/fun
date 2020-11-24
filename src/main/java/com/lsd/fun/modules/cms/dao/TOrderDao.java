package com.lsd.fun.modules.cms.dao;

import com.lsd.fun.modules.cms.entity.TOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-08 12:28:39
 */
@Mapper
public interface TOrderDao extends BaseMapper<TOrderEntity> {

    List<Integer> listBoughtShopByUserId(Integer userId);
}
