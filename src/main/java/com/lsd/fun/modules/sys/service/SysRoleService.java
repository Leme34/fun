package com.lsd.fun.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.sys.entity.SysRoleEntity;
import com.lsd.fun.modules.sys.query.SysRoleQuery;

import java.util.List;

/**
 * 角色
 *
 */
public interface SysRoleService extends IService<SysRoleEntity> {

	PageUtils queryPage(SysRoleQuery queryForm);

	void saveRole(SysRoleEntity sysRoleEntity);

	void update(SysRoleEntity role);

	void deleteBatch(Long[] roleIds);


	/**
	 * 查询用户创建的角色ID列表
	 */
	List<Long> queryRoleIdList(Long createUserId);

	/**
	 * 若无该名称角色则创建
	 * @return 本次插入的 sys_role 的id，若已有则返回其 sys_role_id
	 */
	Long saveIfNotExists(String roleName, String remark);

	/**
	 * 插入
	 * @param sysRoleEntity 返回 自增的id 到 sysRoleEntity 中
	 * @return 返回自增的id
	 */
	long saveReturnId(SysRoleEntity sysRoleEntity);

	/**
	 * 根据用户ID，获取角色列表
	 */
	List<SysRoleEntity> queryRoleListByUserId(Long userId);

}
