package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 商品表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("commodity")
public class CommodityEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 商品名
	 */
	private String name;
	/**
	 * 商品金额
	 */
	private BigDecimal price;
	/**
	 * 商品分类id
	 */
	private Integer commodityCategoryId;
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
