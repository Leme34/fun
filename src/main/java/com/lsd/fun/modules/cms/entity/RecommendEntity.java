package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户-推荐商铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("recommend")
public class RecommendEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会员id
	 */
	@TableId
	private Integer memberId;
	/**
	 * 以“,”分隔的推荐商铺id字符串数组
	 */
	private String shopIds;

}
