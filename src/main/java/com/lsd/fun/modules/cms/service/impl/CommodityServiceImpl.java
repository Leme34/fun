package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.CommodityDao;
import com.lsd.fun.modules.cms.entity.CommodityEntity;
import com.lsd.fun.modules.cms.service.CommodityService;


@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityDao, CommodityEntity> implements CommodityService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<CommodityEntity> page = this.page(
                new Query<CommodityEntity>().getPage(query),
                new QueryWrapper<CommodityEntity>()
        );

        return new PageUtils(page);
    }

}
