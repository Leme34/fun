package com.lsd.fun.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.sys.entity.SysUserEntity;
import com.lsd.fun.modules.sys.query.SysUserQuery;

import java.util.List;

/**
 * 系统用户
 *
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(SysUserQuery queryForm);

	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(Long userId);

	List<String> queryAllRoles(Long userId);

	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);

	/**
	 * 根据用户名，查询系统用户
	 */
	SysUserEntity queryByUserName(String username);

	/**
	 * 保存用户
	 */
	void saveUser(SysUserEntity user);

	/**
	 * 修改用户
	 */
	void update(SysUserEntity user);

	/**
	 * 删除用户
	 */
	void deleteBatch(Long[] userIds);

	/**
	 * 修改密码
	 * @param userId       用户ID
	 * @param password     原密码
	 * @param newPassword  新密码
	 */
	boolean updatePassword(Long userId, String password, String newPassword);

	Long saveReturnId(SysUserEntity sysUserEntity);

	void deleteSysUserByIds(Long[] userIds);

	/**
	 * 查询拥有此角色的所有用户名
	 */
	List<String> listByRole(String roleName);
}
