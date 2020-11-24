package com.lsd.fun.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.modules.sys.dao.SysRoleDao;
import com.lsd.fun.modules.sys.entity.SysRoleEntity;
import com.lsd.fun.modules.sys.entity.SysUserEntity;
import com.lsd.fun.modules.sys.query.SysRoleQuery;
import com.lsd.fun.modules.sys.service.SysRoleMenuService;
import com.lsd.fun.modules.sys.service.SysRoleService;
import com.lsd.fun.modules.sys.service.SysUserRoleService;
import com.lsd.fun.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 角色
 *
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public PageUtils queryPage(SysRoleQuery queryForm) {

        String roleName = queryForm.getRoleName();
        Long createUserId = queryForm.getCreateUserId();

        QueryWrapper<SysRoleEntity> qw = new QueryWrapper<SysRoleEntity>()
                .like(StringUtils.isNotBlank(roleName), "role_name", roleName)
                .eq(createUserId != null, "create_user_id", createUserId);

        IPage<SysRoleEntity> page = this.page(new Query<SysRoleEntity>().getPage(queryForm), qw);

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(SysRoleEntity sysRoleEntity) {
        SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        sysRoleEntity.setCreateUserId(userId);
        sysRoleEntity.setCreateTime(LocalDateTime.now());
        this.save(sysRoleEntity);

        //若不是超级管理员，需要判断新增的角色的权限是否超过自己角色拥有的权限
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(userId);
        if (roleList.stream().noneMatch(role -> StringUtils.equals(role.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            checkPrems(sysRoleEntity);
        }

        //保存角色与菜单关系
        sysRoleMenuService.saveOrUpdate(sysRoleEntity.getRoleId(), sysRoleEntity.getMenuIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleEntity role) {
        this.updateById(role);

        //若不是超级管理员，检查权限是否越权
        SysUserEntity currentUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        List<SysRoleEntity> roleList = sysRoleService.queryRoleListByUserId(currentUser.getUserId());
        if (roleList.stream().noneMatch(r -> StringUtils.equals(r.getRoleName(), Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName()))) {
            checkPrems(role);
        }

        //更新角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] roleIds) {
        //删除角色
        this.removeByIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        sysRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        sysUserRoleService.deleteBatch(roleIds);
    }


    @Override
    public List<Long> queryRoleIdList(Long createUserId) {
        return baseMapper.queryRoleIdList(createUserId);
    }

    @Override
    public Long saveIfNotExists(String roleName, String remark) {
        SysRoleEntity role = this.getOne(
                new QueryWrapper<SysRoleEntity>().eq(StringUtils.isNotBlank(roleName), "role_name", roleName)
        );
        if (role == null) {
            SysRoleEntity sysRole = SysRoleEntity.builder()
                    .roleName(roleName)
                    .remark(remark)
                    .createTime(LocalDateTime.now())
                    .build();
            return this.saveReturnId(sysRole);
        }
        return role.getRoleId();
    }

    @Override
    public long saveReturnId(SysRoleEntity sysRoleEntity) {
        sysRoleDao.insert(sysRoleEntity);
        return sysRoleEntity.getRoleId();
    }

    @Override
    public List<SysRoleEntity> queryRoleListByUserId(Long userId) {
        return sysRoleDao.queryRoleListByUserId(userId);
    }

    /**
     * 检查权限是否越权
     */
    private void checkPrems(SysRoleEntity role) {
        //查询用户所拥有的菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(role.getCreateUserId());

        //判断是否越权
        if (!menuIdList.containsAll(role.getMenuIdList())) {
            throw new RRException("新增角色的权限，已超出你的权限范围");
        }
    }
}
