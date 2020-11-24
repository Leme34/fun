package com.lsd.fun.modules.sys.controller;

import com.lsd.fun.config.FunConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.lsd.fun.common.annotation.SysLog;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.common.validator.Assert;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.common.validator.group.AddGroup;
import com.lsd.fun.common.validator.group.UpdateGroup;
import com.lsd.fun.modules.sys.entity.SysRoleEntity;
import com.lsd.fun.modules.sys.entity.SysUserEntity;
import com.lsd.fun.modules.sys.form.PasswordForm;
import com.lsd.fun.modules.sys.query.SysUserQuery;
import com.lsd.fun.modules.sys.service.SysRoleService;
import com.lsd.fun.modules.sys.service.SysUserRoleService;
import com.lsd.fun.modules.sys.service.SysUserService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户
 */
@Api(tags = "系统用户表相关")
@RestController("adminUserController")
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private FunConfig config;

    /**
     * 所有用户列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:user:list")
    @ApiOperation(value = "所有用户列表分页", notes = "所有用户列表分页")
    public R list(SysUserQuery queryForm) {
        //只有超级管理员，才能查看所有管理员列表
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(getUserId());
        if (roleList.stream().noneMatch(role -> StringUtils.equals(role.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            queryForm.setCreateUserId(getUserId());
        }
        PageUtils page = sysUserService.queryPage(queryForm);
        return R.ok().put("page", page);
    }

    /**
     * 获取登录的用户信息
     */
    @ApiOperation(value = "获取登录的用户信息")
    @GetMapping("/info")
    public R info() {
        return R.ok().put("user", getUser());
    }

    @SysLog("修改密码")
    @PostMapping("/password")
    public R password(@RequestBody PasswordForm form) {
        Assert.isBlank(form.getNewPassword(), "新密码不为能空");

        //sha256加密
        String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
        //sha256加密
        String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();

        //更新密码
        boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
        if (!flag) {
            return R.error("原密码不正确");
        }

        return R.ok();
    }

    @ApiOperation(value = "用户详情")
    @GetMapping("/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public R info(@PathVariable("userId") Long userId) {
        SysUserEntity user = sysUserService.getById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);

        return R.ok().put("user", user);
    }

    @SysLog("保存用户")
    @PostMapping("/save")
    @RequiresPermissions("sys:user:save")
    public R save(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, AddGroup.class);

        sysUserService.saveUser(user);

        return R.ok();
    }

    @SysLog("修改用户")
    @PostMapping("/update")
    @RequiresPermissions("sys:user:update")
    public R update(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);
        user.setCreateUserId(getUserId());
        sysUserService.update(user);
        return R.ok();
    }

    @SysLog("删除用户")
    @PostMapping("/delete")
    @RequiresPermissions("sys:user:delete")
    public R delete(@RequestBody Long[] userIds) {
        final SysUserEntity adminUser = sysUserService.queryByUserName(config.getAuth().getAdmin().getUsername());
        if (adminUser != null && ArrayUtils.contains(userIds, adminUser.getUserId())) {
            return R.error("系统管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, getUserId())) {
            return R.error("当前用户不能删除");
        }

//        sysUserService.deleteBatch(userIds);
        sysUserService.deleteSysUserByIds(userIds);

        return R.ok();
    }
}
