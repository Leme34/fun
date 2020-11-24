package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.TOrderCommodityEntity;
import com.lsd.fun.modules.cms.service.TOrderCommodityService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 订单详情表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-08 12:28:39
 */
@RestController
@RequestMapping("cms/tOrderCommodity")
public class TOrderCommodityController {
    @Autowired
    private TOrderCommodityService tOrderCommodityService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:tOrderCommodity:list")
    public R list(BaseQuery query){
        PageUtils page = tOrderCommodityService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:tOrderCommodity:info")
    public R info(@PathVariable("id") Integer id){
		TOrderCommodityEntity tOrderCommodity = tOrderCommodityService.getById(id);

        return R.ok().put("tOrderCommodity", tOrderCommodity);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:tOrderCommodity:save")
    public R save(@RequestBody TOrderCommodityEntity tOrderCommodity){
		tOrderCommodityService.save(tOrderCommodity);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:tOrderCommodity:update")
    public R update(@RequestBody TOrderCommodityEntity tOrderCommodity){
		tOrderCommodityService.updateById(tOrderCommodity);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:tOrderCommodity:delete")
    public R delete(@RequestBody Integer[] ids){
		tOrderCommodityService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
