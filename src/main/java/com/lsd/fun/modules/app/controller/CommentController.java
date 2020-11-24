package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.annotation.AppLogin;
import com.lsd.fun.modules.app.annotation.AppLoginUser;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.entity.CommentEntity;
import com.lsd.fun.modules.app.query.CommentQuery;
import com.lsd.fun.modules.app.service.CommentService;
import com.lsd.fun.modules.app.service.CommentVoteService;
import com.lsd.fun.modules.app.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


/**
 * 评论表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-15 18:30:06
 */
@Api("店铺评论")
@RestController
@RequestMapping("app/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private JwtUtils jwtUtils;

    @ApiOperation(value = "分页查询此店铺的评论")
    @GetMapping("/page")
    public R page(CommentQuery query, HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader(jwtUtils.getHeader())).orElse(request.getParameter(jwtUtils.getHeader()));
        if (StringUtils.isNotBlank(token)) {
            final Claims claims = jwtUtils.getClaimByToken(token);
            // 已登录
            if (claims != null && !jwtUtils.isTokenExpired(claims.getExpiration())) {
                query.setUserId(new Integer(claims.getSubject()));
            }
        }
        PageUtils page = commentService.queryPage(query);
        return R.ok().put("data", page).put("more", page.getTotalCount() > (query.getPage() + query.getLimit()));
    }


//    @ApiOperation(value = "根据id查询")
//    @GetMapping("/{id}")
//    public R info(@PathVariable("id") Integer id) {
//        CommentEntity comment = commentService.getById(id);
//
//        return R.ok().put("comment", comment);
//    }


    @ApiOperation("提交评论")
    @AppLogin
    @PostMapping
    public R save(@RequestBody CommentEntity comment, @AppLoginUser UserRoleDto userRoleDto) {
        comment.setUserId(userRoleDto.getUserId());
        commentService.save(comment);
        return R.ok();
    }

//    @ApiOperation("修改")
//    @PutMapping("/{id}")
//    public R update(@RequestBody CommentEntity comment) {
//        commentService.updateById(comment);
//
//        return R.ok();
//    }

    @ApiOperation("删除")
    @AppLogin
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Integer id, @AppLoginUser UserRoleDto userRoleDto) {
        boolean success = commentService.lambdaUpdate()
                .eq(CommentEntity::getId, id)
                .eq(CommentEntity::getUserId, userRoleDto.getUserId())
                .remove();
        return success ? R.ok() : R.error(HttpStatus.SC_FORBIDDEN, "权限不足");
    }


}
