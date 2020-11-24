package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.TOrderEntity;
import com.lsd.fun.modules.cms.service.TOrderService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 订单表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-08 12:28:39
 */
@RestController
@RequestMapping("cms/tOrder")
public class TOrderController {
    @Autowired
    private TOrderService tOrderService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:tOrder:list")
    public R list(BaseQuery query){
        PageUtils page = tOrderService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:tOrder:info")
    public R info(@PathVariable("id") Integer id){
		TOrderEntity tOrder = tOrderService.getById(id);

        return R.ok().put("tOrder", tOrder);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:tOrder:save")
    public R save(@RequestBody TOrderEntity tOrder){
		tOrderService.save(tOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:tOrder:update")
    public R update(@RequestBody TOrderEntity tOrder){
		tOrderService.updateById(tOrder);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:tOrder:delete")
    public R delete(@RequestBody Integer[] ids){
		tOrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
