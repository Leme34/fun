package com.lsd.fun.modules.sys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.lsd.fun.common.annotation.SysLog;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.common.validator.group.AddGroup;
import com.lsd.fun.common.validator.group.UpdateGroup;
import com.lsd.fun.modules.sys.entity.SysDictionaryManageEntity;
import com.lsd.fun.modules.sys.query.TDictionaryQuery;
import com.lsd.fun.modules.sys.service.SysDictionaryManageService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 字典管理
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-15 11:03:14
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("sys/sysDictionaryManage")
public class SysDictionaryManageController {
    @Autowired
    private SysDictionaryManageService sysDictionaryManageService;

    @ApiOperation("分页查询此数据字典id下的字典列表")
    @GetMapping("/list")
    @RequiresPermissions("sys:sysDictionaryManage:list")
    public R list(TDictionaryQuery query){
        PageUtils page = sysDictionaryManageService.queryPage(query);

        return R.ok().put("page", page);
    }


    @ApiOperation("根据字典编号查询")
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:sysDictionaryManage:info")
    public R info(@PathVariable("id") Integer id){
        return R.ok().put("sysDictionaryManage", sysDictionaryManageService.queryById(id));
    }

    @SysLog("保存数据字典")
    @ApiOperation("保存数据字典")
    @PostMapping("/save")
    @RequiresPermissions("sys:sysDictionaryManage:save")
    public R save(@RequestBody SysDictionaryManageEntity sysDictionaryManage){
        ValidatorUtils.validateEntity(sysDictionaryManage, AddGroup.class);
		sysDictionaryManageService.saveOrUpdateWithCheck(sysDictionaryManage);

        return R.ok();
    }

    @SysLog("修改数据字典")
    @ApiOperation("修改数据字典")
    @PostMapping("/update")
    @RequiresPermissions("sys:sysDictionaryManage:update")
    public R update(@RequestBody SysDictionaryManageEntity sysDictionaryManage){
        ValidatorUtils.validateEntity(sysDictionaryManage, UpdateGroup.class);
        sysDictionaryManageService.saveOrUpdateWithCheck(sysDictionaryManage);
        return R.ok();
    }

    @SysLog("删除数据字典")
    @ApiOperation("删除数据字典")
    @PostMapping("/delete")
    @RequiresPermissions("sys:sysDictionaryManage:delete")
    public R delete(@RequestBody Integer[] ids){
		sysDictionaryManageService.remove(ids);

        return R.ok();
    }

}
