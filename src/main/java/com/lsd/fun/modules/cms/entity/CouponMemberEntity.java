package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 会员-抵扣券中间表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("coupon_member")
public class CouponMemberEntity implements Serializable {
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
	 * 领取券的渠道（1:用户购买 2:公司发放）
	 */
	private Integer couponChannel;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;
	/**
	 * 更新时间
	 */
	private LocalDateTime updatedAt;

}
