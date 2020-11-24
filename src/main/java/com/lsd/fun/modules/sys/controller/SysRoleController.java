package com.lsd.fun.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.lsd.fun.common.annotation.SysLog;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.modules.sys.entity.SysRoleEntity;
import com.lsd.fun.modules.sys.query.SysRoleQuery;
import com.lsd.fun.modules.sys.service.SysRoleMenuService;
import com.lsd.fun.modules.sys.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 */
@Api(tags = "角色相关")
@RestController("adminRoleController")
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 角色列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:role:list")
    @ApiOperation(value = "角色列表", notes = "查询所有角色")
    public R list(SysRoleQuery queryForm) {
        //如果不是超级管理员，则只查询自己创建的角色列表
        Long userId = getUserId();
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(userId);
        if (roleList.stream().noneMatch(role-> StringUtils.equals(role.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            queryForm.setCreateUserId(userId);
        }

        PageUtils page = sysRoleService.queryPage(queryForm);

        return R.ok().put("page", page);
    }

    /**
     * 角色列表
     */
    @GetMapping("/select")
    @RequiresPermissions("sys:role:select")
    public R select() {
        Map<String, Object> map = new HashMap<>();

        //如果不是超级管理员，则只查询自己所拥有的角色列表
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(getUserId());
        if (roleList.stream().noneMatch(role -> org.apache.commons.lang.StringUtils.equals(role.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            map.put("create_user_id", getUserId());
        }
        List<SysRoleEntity> list = (List<SysRoleEntity>) sysRoleService.listByMap(map);

        return R.ok().put("list", list);
    }

    /**
     * 角色信息
     */
    @GetMapping("/info/{roleId}")
    @RequiresPermissions("sys:role:info")
    public R info(@PathVariable("roleId") Long roleId) {
        SysRoleEntity role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        return R.ok().put("role", role);
    }

    /**
     * 保存角色
     */
    @SysLog("保存角色")
    @PostMapping("/save")
    @RequiresPermissions("sys:role:save")
    public R save(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);

        sysRoleService.saveRole(role);

        return R.ok();
    }

    /**
     * 修改角色
     */
    @SysLog("修改角色")
    @PostMapping("/update")
    @RequiresPermissions("sys:role:update")
    public R update(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);

        role.setCreateUserId(getUserId());
        sysRoleService.update(role);

        return R.ok();
    }

    /**
     * 删除角色
     */
    @SysLog("删除角色")
    @PostMapping("/delete")
    @RequiresPermissions("sys:role:delete")
    public R delete(@RequestBody Long[] roleIds) {
        sysRoleService.deleteBatch(roleIds);

        return R.ok();
    }

    /**
     * 下拉框
     */
    @ApiOperation("角色下拉框")
    @GetMapping("/listCombo")
    public R listCombo(){
        List<Map<String,Object>> list = sysRoleService.listMaps(
                new QueryWrapper<SysRoleEntity>().select("role_id as id","role_name as name"));
        return R.ok().put("page", list);
    }
}
