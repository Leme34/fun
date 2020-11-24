package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.TOrderCommodityDao;
import com.lsd.fun.modules.cms.entity.TOrderCommodityEntity;
import com.lsd.fun.modules.cms.service.TOrderCommodityService;


@Service
public class TOrderCommodityServiceImpl extends ServiceImpl<TOrderCommodityDao, TOrderCommodityEntity> implements TOrderCommodityService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<TOrderCommodityEntity> page = this.page(
                new Query<TOrderCommodityEntity>().getPage(query),
                new QueryWrapper<TOrderCommodityEntity>()
        );

        return new PageUtils(page);
    }

}
