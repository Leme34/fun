package com.lsd.fun.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.modules.sys.dao.SysUserDao;
import com.lsd.fun.modules.sys.entity.SysRoleEntity;
import com.lsd.fun.modules.sys.entity.SysUserEntity;
import com.lsd.fun.modules.sys.query.SysUserQuery;
import com.lsd.fun.modules.sys.service.SysRoleService;
import com.lsd.fun.modules.sys.service.SysUserRoleService;
import com.lsd.fun.modules.sys.service.SysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 系统用户
 *
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserDao sysUserDao;

    @Override
    public PageUtils queryPage(SysUserQuery queryForm) {
        String username = queryForm.getUsername();
        Long createUserId = queryForm.getCreateUserId();

        IPage<SysUserEntity> page = this.page(
                new Query<SysUserEntity>().getPage(queryForm),
                new QueryWrapper<SysUserEntity>()
                        .like(StringUtils.isNotBlank(username), "username", username)
                        .eq(createUserId != null, "create_user_id", createUserId)
        );

        return new PageUtils(page);
    }


    @Override
    public List<String> queryAllPerms(Long userId) {
        return baseMapper.queryAllPerms(userId);
    }

    @Override
    public List<String> queryAllRoles(Long userId) {
        final QueryWrapper wrapper = new QueryWrapper<>().eq("u.user_id", userId);
        return baseMapper.queryAllRoles(wrapper);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    public SysUserEntity queryByUserName(String username) {
        return baseMapper.queryByUserName(username);
    }

    @Override
    @Transactional
    public void saveUser(SysUserEntity user) {
        SysUserEntity currentUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        user.setCreateUserId(currentUser.getUserId());
        user.setCreateTime(LocalDateTime.now());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        user.setSalt(salt);
        this.save(user);

        //若不是超级管理员，检查角色是否越权
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(currentUser.getUserId());
        if (roleList.stream().noneMatch(role -> StringUtils.equals(role.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            checkRole(user);
        }

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Override
    @Transactional
    public void update(SysUserEntity user) {
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(null);
        } else {
            user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
        }
        this.updateById(user);

        //若不是超级管理员，检查角色是否越权
        SysUserEntity currentUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(currentUser.getUserId());
        if (roleList.stream().noneMatch(role -> StringUtils.equals(role.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            checkRole(user);
        }

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Override
    public void deleteBatch(Long[] userId) {
        this.removeByIds(Arrays.asList(userId));
    }

    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
    }

    @Override
    public Long saveReturnId(SysUserEntity sysUserEntity) {
        sysUserDao.insert(sysUserEntity);
        return sysUserEntity.getUserId();
    }

    @Override
    public void deleteSysUserByIds(Long[] userIds) {
        Collection<SysUserEntity> sysUsers = this.listByIds(Arrays.asList(userIds));
        if (CollectionUtils.isEmpty(sysUsers)) {
            return;
        }
        sysUsers.forEach(sysUser -> sysUser.setStatus(Constant.FALSE));
        this.updateBatchById(sysUsers);
    }

    @Override
    public List<String> listByRole(String roleName) {
        return sysUserDao.listByRole(roleName);
    }

    /**
     * 检查角色是否越权，需要判断用户的角色是否自己创建
     */
    private void checkRole(SysUserEntity user) {
        if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
            return;
        }
        //查询用户创建的角色列表
        List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());

        //判断是否越权
        if (!roleIdList.containsAll(user.getRoleIdList())) {
            throw new RRException("新增用户所选角色，不是本人创建");
        }
    }
}
