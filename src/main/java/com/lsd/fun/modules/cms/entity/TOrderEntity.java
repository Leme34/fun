package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 订单表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-08 12:28:39
 */
@Accessors(chain = true)
@Data
@TableName("t_order")
public class TOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单ID
	 */
	@TableId(type = IdType.INPUT)
	private Long id;
	/**
	 * 会员ID
	 */
	private Integer memberId;
	/**
	 * 订单原价
	 */
	private BigDecimal originPrice;
	/**
	 * 订单实付
	 */
	private BigDecimal payPrice;
	/**
	 * 订单状态（1:进行中 2:已完成 3:已取消）
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
