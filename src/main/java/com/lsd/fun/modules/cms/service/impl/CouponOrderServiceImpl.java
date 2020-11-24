package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.CouponOrderDao;
import com.lsd.fun.modules.cms.entity.CouponOrderEntity;
import com.lsd.fun.modules.cms.service.CouponOrderService;


@Service
public class CouponOrderServiceImpl extends ServiceImpl<CouponOrderDao, CouponOrderEntity> implements CouponOrderService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<CouponOrderEntity> page = this.page(
                new Query<CouponOrderEntity>().getPage(query),
                new QueryWrapper<CouponOrderEntity>()
        );

        return new PageUtils(page);
    }

}
