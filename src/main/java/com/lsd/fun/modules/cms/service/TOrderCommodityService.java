package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.entity.TOrderCommodityEntity;
import com.lsd.fun.common.utils.BaseQuery;
import java.util.Map;

/**
 * 订单详情表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-08 12:28:39
 */
public interface TOrderCommodityService extends IService<TOrderCommodityEntity> {

    PageUtils queryPage(BaseQuery query);
}

