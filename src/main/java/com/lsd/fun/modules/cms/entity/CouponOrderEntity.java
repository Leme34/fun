package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 订单-抵扣券
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("coupon_order")
public class CouponOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Integer id;
	/**
	 * 抵扣券id
	 */
	private Integer couponId;
	/**
	 * 会员id
	 */
	private Integer memberId;
	/**
	 * 商品订单ID
	 */
	private Long orderId;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;

}
