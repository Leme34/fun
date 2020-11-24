package com.lsd.fun.modules.sys.service.impl;


import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.modules.sys.dao.SysMenuDao;
import com.lsd.fun.modules.sys.dao.SysUserDao;
import com.lsd.fun.modules.sys.dao.SysUserTokenDao;
import com.lsd.fun.modules.sys.entity.SysMenuEntity;
import com.lsd.fun.modules.sys.entity.SysRoleEntity;
import com.lsd.fun.modules.sys.entity.SysUserEntity;
import com.lsd.fun.modules.sys.entity.SysUserTokenEntity;
import com.lsd.fun.modules.sys.service.ShiroService;
import com.lsd.fun.modules.sys.service.SysRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShiroServiceImpl implements ShiroService {
    private final SysMenuDao sysMenuDao;
    private final SysUserDao sysUserDao;
    private final SysUserTokenDao sysUserTokenDao;
    private final SysRoleService sysRoleService;

    public ShiroServiceImpl(SysMenuDao sysMenuDao, SysUserDao sysUserDao, SysUserTokenDao sysUserTokenDao, SysRoleService sysRoleService) {
        this.sysMenuDao = sysMenuDao;
        this.sysUserDao = sysUserDao;
        this.sysUserTokenDao = sysUserTokenDao;
        this.sysRoleService = sysRoleService;
    }

    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;
        //是否有系统管理员角色，是则拥有最高权限
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(userId);
        if (roleList.stream().anyMatch(role -> StringUtils.equals(role.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            List<SysMenuEntity> menuList = sysMenuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for(SysMenuEntity menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = sysUserDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysUserTokenEntity queryByToken(String token) {
        return sysUserTokenDao.queryByToken(token);
    }

    @Override
    public SysUserEntity queryUser(Long userId) {
        return sysUserDao.selectById(userId);
    }
}
