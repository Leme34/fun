package com.lsd.fun.modules.app.vo;

import com.lsd.fun.modules.app.entity.CommentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by lsd
 * 2020-04-15 18:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentVo extends CommentEntity {

    private String userName; //发评论者用户名
    private String userAvatar; //发评论者头像
    private String replyUserName; //被回复者用户名
    private List<CommentVo> commentList;  //此评论的所有子评论
    private boolean isVoted;  //此用户是否有给此评论点赞
    private boolean isMine;  //此用户是否发评论者
    private Integer voteNum;  //此评论被点赞总数

    private Integer currentUserId;  //用于暂存传入的userId传入子查询中
    private String hostPrefix;  //用于暂存传入的userId传入子查询中

}
