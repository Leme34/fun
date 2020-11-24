package com.lsd.fun.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.app.entity.CommentVoteEntity;

import java.util.Map;

/**
 * 评论点赞表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-15 18:30:06
 */
public interface CommentVoteService extends IService<CommentVoteEntity> {

    PageUtils queryPage(BaseQuery query);
}

