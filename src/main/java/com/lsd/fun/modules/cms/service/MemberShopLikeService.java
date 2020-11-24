package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.entity.MemberShopLikeEntity;
import com.lsd.fun.common.utils.BaseQuery;
import java.util.Map;

/**
 * 会员点赞店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
public interface MemberShopLikeService extends IService<MemberShopLikeEntity> {

    PageUtils queryPage(BaseQuery query);
}

