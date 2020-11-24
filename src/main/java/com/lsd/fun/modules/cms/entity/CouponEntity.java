package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 抵扣券
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("coupon")
public class CouponEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 券名称
	 */
	private String couponName;
	/**
	 * 券面金额（用于抵扣订单金额）
	 */
	private BigDecimal couponPrice;
	/**
	 * 创建人（后台用户ID）
	 */
	private Integer createUserId;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;
	/**
	 * 更新时间
	 */
	private LocalDateTime updatedAt;

}
