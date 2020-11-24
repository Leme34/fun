package com.lsd.fun.modules.cms.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lsd.fun.modules.cms.vo.ShopVO;
import com.lsd.fun.modules.cms.entity.ShopEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 01:29:43
 */
@Mapper
public interface ShopDao extends BaseMapper<ShopEntity> {

    List<ShopVO> queryPage(@Param(Constants.WRAPPER) Wrapper wrapper);

    IPage<ShopVO> queryPage(IPage page, @Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 查询这几个字段变更了的数据（待同步索引的数据）
     *
     * @param wrapper
     * @return 一条数据Row是一个Map，返回这种结构方便 ES API 直接使用
     */
    List<Map<String, Object>> queryNeedIndexRow(@Param(Constants.WRAPPER) Wrapper wrapper);

    List<ShopVO> listOrderByField(@Param("ids") Collection<Integer> ids);
}
