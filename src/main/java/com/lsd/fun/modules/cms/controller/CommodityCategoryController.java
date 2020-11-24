package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.CommodityCategoryEntity;
import com.lsd.fun.modules.cms.service.CommodityCategoryService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 商品类别表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/commodityCategory")
public class CommodityCategoryController {
    @Autowired
    private CommodityCategoryService commodityCategoryService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:commodityCategory:list")
    public R list(BaseQuery query){
        PageUtils page = commodityCategoryService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:commodityCategory:info")
    public R info(@PathVariable("id") Integer id){
		CommodityCategoryEntity commodityCategory = commodityCategoryService.getById(id);

        return R.ok().put("commodityCategory", commodityCategory);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:commodityCategory:save")
    public R save(@RequestBody CommodityCategoryEntity commodityCategory){
		commodityCategoryService.save(commodityCategory);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:commodityCategory:update")
    public R update(@RequestBody CommodityCategoryEntity commodityCategory){
		commodityCategoryService.updateById(commodityCategory);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:commodityCategory:delete")
    public R delete(@RequestBody Integer[] ids){
		commodityCategoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
