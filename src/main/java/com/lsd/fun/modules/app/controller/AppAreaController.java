package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.lsd.fun.modules.cms.query.AreaQuery;
import com.lsd.fun.modules.cms.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lsd
 * 2020-04-02 17:19
 */
@Api(tags = "App地区")
@RestController
@RequestMapping("app/area")
public class AppAreaController {

    @Autowired
    private AreaService areaService;

    @ApiOperation(value = "根据父级地区id/名称查询对应级别的行政地区", response = AreaEntity.class, responseContainer = "List")
    @GetMapping("/list")
    public R list(AreaQuery query) {
        return R.ok().put("data", areaService.listSubArea(query));
    }

}
