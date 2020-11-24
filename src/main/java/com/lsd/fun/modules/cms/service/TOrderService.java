package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.form.TOrderForm;
import com.lsd.fun.modules.cms.entity.TOrderEntity;
import com.lsd.fun.common.utils.BaseQuery;

import java.util.List;
import java.util.Map;

/**
 * 订单表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-08 12:28:39
 */
public interface TOrderService extends IService<TOrderEntity> {

    PageUtils queryPage(BaseQuery query);

    /**
     * 下单
     */
    void creatOrder(UserRoleDto userRoleDto, TOrderForm form);

    /**
     * 用户下过单的店铺
     */
    List<Integer> listBoughtShopByUserId(Integer userId);
}

