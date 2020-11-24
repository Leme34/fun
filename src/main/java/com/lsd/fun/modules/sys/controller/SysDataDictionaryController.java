package com.lsd.fun.modules.sys.controller;

import com.lsd.fun.modules.sys.entity.SysDataDictionaryEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.sys.service.SysDataDictionaryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 数据字典
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-15 11:03:15
 */
@Api(tags = "数据字典目录接口")
@RestController
@RequestMapping("sys/sysDataDictionary")
public class SysDataDictionaryController {
    @Autowired
    private SysDataDictionaryService sysDataDictionaryService;

    @ApiOperation("数据字典目录列表")
    @GetMapping("/list")
    @RequiresPermissions("sys:sysDataDictionary:list")
    public R list(){
        return R.ok().put("data", sysDataDictionaryService.list());
    }


    @ApiOperation("根据id查询数据字典目录信息")
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:sysDataDictionary:info")
    public R info(@PathVariable("id") Integer id){
		SysDataDictionaryEntity tDataDictionary = sysDataDictionaryService.getById(id);

        return R.ok().put("tDataDictionary", tDataDictionary);
    }


}
