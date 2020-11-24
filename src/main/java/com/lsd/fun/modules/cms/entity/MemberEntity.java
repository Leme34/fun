package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Accessors(chain = true)
@Data
@TableName("member")
public class MemberEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Integer id;
	/**
	 * 会员名
	 */
	private String username;
	/**
	 * 登录密码
	 */
	private String password;
	/**
	 * 盐
	 */
	private String salt;
	/**
	 * 性别（-1:未知 1:男 2:女）
	 */
	private Integer sex;
	/**
	 *
	 */
	private String phone;
	/**
	 *
	 */
	private String email;
	/**
	 * 出生日期
	 */
	private LocalDate birth;
	/**
	 * 头像
	 */
	private Integer avatar;
	/**
	 * 默认地址ID
	 */
	private Integer addressDefaultId;
	/**
	 * 会员注册渠道（1:IOS 2:android 3:微信小程序 4:微信公众号 5:h5）
	 */
	private Integer memberChannel;
	/**
	 * 微信公众号openId
	 */
	private String mpOpenId;
	/**
	 * 状态（0:禁用 1:启用）
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;
	/**
	 * 更新时间
	 */
	private LocalDateTime updatedAt;

}
