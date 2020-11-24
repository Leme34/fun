package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.CouponDao;
import com.lsd.fun.modules.cms.entity.CouponEntity;
import com.lsd.fun.modules.cms.service.CouponService;


@Service
public class CouponServiceImpl extends ServiceImpl<CouponDao, CouponEntity> implements CouponService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<CouponEntity> page = this.page(
                new Query<CouponEntity>().getPage(query),
                new QueryWrapper<CouponEntity>()
        );

        return new PageUtils(page);
    }

}
