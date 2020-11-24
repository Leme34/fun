package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.cms.entity.CategoryEntity;
import com.lsd.fun.modules.cms.service.CategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


/**
 * 商铺类别表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("app/category")
public class AppCategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAll")
    public R listAll() {
        return R.ok().put("data", categoryService.list());
    }

}
