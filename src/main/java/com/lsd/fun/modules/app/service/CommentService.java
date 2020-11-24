package com.lsd.fun.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.app.entity.CommentEntity;
import com.lsd.fun.modules.app.query.CommentQuery;

/**
 * 评论表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-15 18:30:06
 */
public interface CommentService extends IService<CommentEntity> {

    PageUtils queryPage(CommentQuery query);
}

