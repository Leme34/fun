package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.annotation.AppLogin;
import com.lsd.fun.modules.app.annotation.AppLoginUser;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.form.AppMemberForm;
import com.lsd.fun.modules.app.form.AppRegisterForm;
import com.lsd.fun.modules.app.vo.MemberVo;
import com.lsd.fun.modules.cms.entity.MemberEntity;
import com.lsd.fun.modules.cms.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by lsd
 * 2020-04-01 21:39
 */
@Api(tags = "会员")
@RequestMapping("/app/member")
@RestController
public class AppMemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation("会员信息")
    @AppLogin
    @GetMapping
    public R getUserInfo(@AppLoginUser UserRoleDto dto) {
        Optional<MemberVo> memberOpt = memberService.queryById(dto.getUserId());
        if (memberOpt.isPresent()) {
            return R.ok().put("data", memberOpt.get());
        }
        return R.error(HttpStatus.SC_NOT_FOUND, "用户不存在或被禁用");
    }

    @ApiOperation("修改信息")
    @AppLogin
    @PutMapping
    public R updateUserInfo(@RequestBody AppMemberForm form, @AppLoginUser UserRoleDto dto) {
        form.setId(dto.getUserId());
        memberService.updateUserInfo(form);
        return R.ok();
    }

}
