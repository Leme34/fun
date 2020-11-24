package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.entity.CouponMemberEntity;
import com.lsd.fun.common.utils.BaseQuery;
import java.util.Map;

/**
 * 会员-抵扣券中间表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
public interface CouponMemberService extends IService<CouponMemberEntity> {

    PageUtils queryPage(BaseQuery query);
}

