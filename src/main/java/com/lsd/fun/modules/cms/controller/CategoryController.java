package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.CategoryEntity;
import com.lsd.fun.modules.cms.service.CategoryService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;


/**
 * 商铺类别表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:category:list")
    public R list(BaseQuery query) {
        PageUtils page = categoryService.queryPage(query);

        return R.ok().put("page", page);
    }

    @GetMapping("/listAll")
    @RequiresPermissions("cms:category:list")
    public R listAll() {
        return R.ok().put("data", categoryService.list());
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:category:info")
    public R info(@PathVariable("id") Integer id) {
        CategoryEntity category = categoryService.getById(id);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:category:save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:category:update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:category:delete")
    public R delete(@RequestBody Integer[] ids) {
        categoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
