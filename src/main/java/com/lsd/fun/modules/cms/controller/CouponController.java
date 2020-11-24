package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.CouponEntity;
import com.lsd.fun.modules.cms.service.CouponService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 抵扣券
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:coupon:list")
    public R list(BaseQuery query){
        PageUtils page = couponService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:coupon:info")
    public R info(@PathVariable("id") Integer id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:coupon:save")
    public R save(@RequestBody CouponEntity coupon){
		couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:coupon:update")
    public R update(@RequestBody CouponEntity coupon){
		couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:coupon:delete")
    public R delete(@RequestBody Integer[] ids){
		couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
