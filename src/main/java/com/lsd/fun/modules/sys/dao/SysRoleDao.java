package com.lsd.fun.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsd.fun.modules.sys.entity.SysRoleEntity;

import java.util.List;

/**
 * 角色管理
 *
 */
@Mapper
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {

	/**
	 * 查询用户创建的角色ID列表
	 */
	List<Long> queryRoleIdList(Long createUserId);

	List<SysRoleEntity> queryRoleListByUserId(long userId);
}
