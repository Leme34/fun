package com.lsd.fun.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 评论表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-15 18:30:06
 */
@Data
@TableName("comment")
public class CommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Integer id;
    /**
     * 一级子评论和二级子评论的pid都=顶层父评论的id，换而言之，所有子评论的pid都是同1个顶层id
     */
    private Integer pid;
    /**
     *
     */
    private Integer userId;
    /**
     * 二级子评论被回复者id,一级子评论为null
     */
    private Integer replyUserId;
    /**
     * 店铺id
     */
    private Integer shopId;
    /**
     *
     */
    private String content;
    /**
     *
     */
    private LocalDateTime createdAt;

}
