package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 订单详情表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-08 12:28:39
 */
@Accessors(chain = true)
@Data
@TableName("t_order_commodity")
public class TOrderCommodityEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Integer id;
	/**
	 * 订单ID
	 */
	private Long orderId;
	/**
	 * 商品ID
	 */
	private Integer shopId;
	/**
	 * 商品名称
	 */
	private String shopName;
	/**
	 * 商品数量
	 */
	private Integer num;
	/**
	 * 商品金额
	 */
	private BigDecimal price;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;
	/**
	 * 更新时间
	 */
	private LocalDateTime updatedAt;

}
