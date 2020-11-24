package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.MemberShopLikeEntity;
import com.lsd.fun.modules.cms.service.MemberShopLikeService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 会员点赞店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/memberShopLike")
public class MemberShopLikeController {
    @Autowired
    private MemberShopLikeService memberShopLikeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:memberShopLike:list")
    public R list(BaseQuery query){
        PageUtils page = memberShopLikeService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:memberShopLike:info")
    public R info(@PathVariable("id") Integer id){
		MemberShopLikeEntity memberShopLike = memberShopLikeService.getById(id);

        return R.ok().put("memberShopLike", memberShopLike);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:memberShopLike:save")
    public R save(@RequestBody MemberShopLikeEntity memberShopLike){
		memberShopLikeService.save(memberShopLike);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:memberShopLike:update")
    public R update(@RequestBody MemberShopLikeEntity memberShopLike){
		memberShopLikeService.updateById(memberShopLike);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:memberShopLike:delete")
    public R delete(@RequestBody Integer[] ids){
		memberShopLikeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
