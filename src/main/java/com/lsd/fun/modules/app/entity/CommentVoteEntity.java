package com.lsd.fun.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 评论点赞表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-15 18:30:06
 */
@Accessors(chain = true)
@Data
@TableName("comment_vote")
public class CommentVoteEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private Integer userId;
	/**
	 *
	 */
	private Integer commentId;

}
