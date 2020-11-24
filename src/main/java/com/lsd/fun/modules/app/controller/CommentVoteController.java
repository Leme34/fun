package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.annotation.AppLogin;
import com.lsd.fun.modules.app.annotation.AppLoginUser;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.entity.CommentEntity;
import com.lsd.fun.modules.app.entity.CommentVoteEntity;
import com.lsd.fun.modules.app.service.CommentVoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评论点赞
 * Created by lsd
 * 2020-04-15 19:56
 */
@Api("评论点赞")
@RestController
@RequestMapping("app/vote")
public class CommentVoteController {

    @Autowired
    private CommentVoteService commentVoteService;

    @ApiOperation(value = "点赞")
    @AppLogin
    @PostMapping("/{commentId}")
    public R vote(@PathVariable Integer commentId, @AppLoginUser UserRoleDto userRoleDto) {
        commentVoteService.save(new CommentVoteEntity().setCommentId(commentId).setUserId(userRoleDto.getUserId()));
        return R.ok();
    }

    @ApiOperation(value = "取消点赞")
    @AppLogin
    @DeleteMapping("/{commentId}")
    public R cancel(@PathVariable Integer commentId, @AppLoginUser UserRoleDto userRoleDto) {
        boolean success = commentVoteService.lambdaUpdate()
                .eq(CommentVoteEntity::getCommentId, commentId)
                .eq(CommentVoteEntity::getUserId, userRoleDto.getUserId())
                .remove();
        return success ? R.ok() : R.error(HttpStatus.SC_FORBIDDEN, "权限不足");
    }


}
