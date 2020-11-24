package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 会员点赞店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("member_shop_like")
public class MemberShopLikeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 会员id
	 */
	private Integer memberId;
	/**
	 * 店铺id
	 */
	private Integer shopId;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;

}
