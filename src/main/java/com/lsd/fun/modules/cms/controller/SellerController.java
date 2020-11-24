package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.SellerEntity;
import com.lsd.fun.modules.cms.service.SellerService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;


/**
 * 商家表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/seller")
public class SellerController {
    @Autowired
    private SellerService sellerService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:seller:list")
    public R list(BaseQuery query) {
        PageUtils page = sellerService.queryPage(query);

        return R.ok().put("page", page);
    }


    @GetMapping("/listAll")
    @RequiresPermissions("cms:seller:list")
    public R listAll() {
        return R.ok().put("data", sellerService.lambdaQuery().eq(SellerEntity::getDisabledFlag, 0).list());
    }

    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:seller:info")
    public R info(@PathVariable("id") Integer id) {
        SellerEntity seller = sellerService.getById(id);

        return R.ok().put("seller", seller);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:seller:save")
    public R save(@RequestBody SellerEntity seller) {
        sellerService.save(seller);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:seller:update")
    public R update(@RequestBody SellerEntity seller) {
        sellerService.updateById(seller);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:seller:delete")
    public R delete(@RequestBody Integer[] ids) {
        sellerService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
