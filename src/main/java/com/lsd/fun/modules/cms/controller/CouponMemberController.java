package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.CouponMemberEntity;
import com.lsd.fun.modules.cms.service.CouponMemberService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 会员-抵扣券中间表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/couponMember")
public class CouponMemberController {
    @Autowired
    private CouponMemberService couponMemberService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:couponMember:list")
    public R list(BaseQuery query){
        PageUtils page = couponMemberService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:couponMember:info")
    public R info(@PathVariable("id") Integer id){
		CouponMemberEntity couponMember = couponMemberService.getById(id);

        return R.ok().put("couponMember", couponMember);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:couponMember:save")
    public R save(@RequestBody CouponMemberEntity couponMember){
		couponMemberService.save(couponMember);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:couponMember:update")
    public R update(@RequestBody CouponMemberEntity couponMember){
		couponMemberService.updateById(couponMember);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:couponMember:delete")
    public R delete(@RequestBody Integer[] ids){
		couponMemberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
