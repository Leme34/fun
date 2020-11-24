package com.lsd.fun.modules.cms.controller;

import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.lsd.fun.modules.cms.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * 地区表,直辖市在level=0能够找到，在level=1也能找到
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 02:06:00
 */
@Api(tags = "地区")
@RestController
@RequestMapping("cms/area")
public class AreaController {
    @Autowired
    private AreaService areaService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:area:list")
    public R list(BaseQuery query){
        PageUtils page = areaService.queryPage(query);

        return R.ok().put("page", page);
    }

    @ApiOperation(value = "地区树",responseContainer = "List",response = AreaEntity.class)
    @GetMapping("/listTree")
    @RequiresPermissions("cms:area:list")
    public R listTree(){
        List<AreaEntity> list = areaService.listTree();
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:area:info")
    public R info(@PathVariable("id") Long id){
		AreaEntity area = areaService.getById(id);

        return R.ok().put("area", area);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:area:save")
    public R save(@RequestBody AreaEntity area){
		areaService.save(area);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:area:update")
    public R update(@RequestBody AreaEntity area){
		areaService.updateById(area);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:area:delete")
    public R delete(@RequestBody Long[] ids){
		areaService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
