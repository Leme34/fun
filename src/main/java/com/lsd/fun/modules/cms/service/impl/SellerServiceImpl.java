package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.SellerDao;
import com.lsd.fun.modules.cms.entity.SellerEntity;
import com.lsd.fun.modules.cms.service.SellerService;


@Service
public class SellerServiceImpl extends ServiceImpl<SellerDao, SellerEntity> implements SellerService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<SellerEntity> page = this.page(
                new Query<SellerEntity>().getPage(query),
                new QueryWrapper<SellerEntity>()
                        .lambda()
                        .eq(SellerEntity::getDisabledFlag, 0)
        );

        return new PageUtils(page);
    }

}
