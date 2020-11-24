package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 商家表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("seller")
public class SellerEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Integer id;
	/**
	 * 商家名称
	 */
	private String name;
	/**
	 * 自我介绍
	 */
	private String description;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;
	/**
	 * 更新时间
	 */
	private LocalDateTime updatedAt;
	/**
	 * 商家评分
	 */
	private BigDecimal remarkScore;
	/**
	 * 是否禁用
	 */
	private Integer disabledFlag;

}
