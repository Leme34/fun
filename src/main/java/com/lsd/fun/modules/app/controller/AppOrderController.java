package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.annotation.AppLogin;
import com.lsd.fun.modules.app.annotation.AppLoginUser;
import com.lsd.fun.modules.app.dto.CartDto;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.form.TOrderForm;
import com.lsd.fun.modules.app.service.CartService;
import com.lsd.fun.modules.cms.service.TOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "App订单")
@RequestMapping("app/order")
@RestController
public class AppOrderController {

    @Autowired
    private TOrderService tOrderService;

    @AppLogin
    @ApiOperation("点餐（创建点餐订单）")
    @PostMapping("/save")
    public R save(@RequestBody TOrderForm form, @AppLoginUser UserRoleDto userRoleDto) {
        tOrderService.creatOrder(userRoleDto, form);
        return R.ok();
    }

}
