package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 商品类别表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("commodity_category")
public class CommodityCategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 类目ID
	 */
	private String name;
	/**
	 * 父类别ID（一级类目为0）
	 */
	private Integer pid;
	/**
	 * 创建人（后台用户ID）
	 */
	private Integer createUserId;
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
