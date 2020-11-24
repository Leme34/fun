package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.CouponOrderEntity;
import com.lsd.fun.modules.cms.service.CouponOrderService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 订单-抵扣券
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/couponOrder")
public class CouponOrderController {
    @Autowired
    private CouponOrderService couponOrderService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:couponOrder:list")
    public R list(BaseQuery query){
        PageUtils page = couponOrderService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:couponOrder:info")
    public R info(@PathVariable("id") Integer id){
		CouponOrderEntity couponOrder = couponOrderService.getById(id);

        return R.ok().put("couponOrder", couponOrder);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:couponOrder:save")
    public R save(@RequestBody CouponOrderEntity couponOrder){
		couponOrderService.save(couponOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:couponOrder:update")
    public R update(@RequestBody CouponOrderEntity couponOrder){
		couponOrderService.updateById(couponOrder);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:couponOrder:delete")
    public R delete(@RequestBody Integer[] ids){
		couponOrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
