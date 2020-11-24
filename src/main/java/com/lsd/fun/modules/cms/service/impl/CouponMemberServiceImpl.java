package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.CouponMemberDao;
import com.lsd.fun.modules.cms.entity.CouponMemberEntity;
import com.lsd.fun.modules.cms.service.CouponMemberService;


@Service
public class CouponMemberServiceImpl extends ServiceImpl<CouponMemberDao, CouponMemberEntity> implements CouponMemberService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<CouponMemberEntity> page = this.page(
                new Query<CouponMemberEntity>().getPage(query),
                new QueryWrapper<CouponMemberEntity>()
        );

        return new PageUtils(page);
    }

}
