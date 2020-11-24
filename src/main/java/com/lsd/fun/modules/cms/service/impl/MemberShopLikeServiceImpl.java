package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.MemberShopLikeDao;
import com.lsd.fun.modules.cms.entity.MemberShopLikeEntity;
import com.lsd.fun.modules.cms.service.MemberShopLikeService;


@Service
public class MemberShopLikeServiceImpl extends ServiceImpl<MemberShopLikeDao, MemberShopLikeEntity> implements MemberShopLikeService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<MemberShopLikeEntity> page = this.page(
                new Query<MemberShopLikeEntity>().getPage(query),
                new QueryWrapper<MemberShopLikeEntity>()
        );

        return new PageUtils(page);
    }

}
