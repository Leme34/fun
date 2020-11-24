package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.CommodityEntity;
import com.lsd.fun.modules.cms.service.CommodityService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 商品表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/commodity")
public class CommodityController {
    @Autowired
    private CommodityService commodityService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:commodity:list")
    public R list(BaseQuery query){
        PageUtils page = commodityService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:commodity:info")
    public R info(@PathVariable("id") Integer id){
		CommodityEntity commodity = commodityService.getById(id);

        return R.ok().put("commodity", commodity);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:commodity:save")
    public R save(@RequestBody CommodityEntity commodity){
		commodityService.save(commodity);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:commodity:update")
    public R update(@RequestBody CommodityEntity commodity){
		commodityService.updateById(commodity);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:commodity:delete")
    public R delete(@RequestBody Integer[] ids){
		commodityService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
