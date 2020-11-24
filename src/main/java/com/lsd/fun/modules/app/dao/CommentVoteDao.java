package com.lsd.fun.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsd.fun.modules.app.entity.CommentVoteEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论点赞表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-15 18:30:06
 */
@Mapper
public interface CommentVoteDao extends BaseMapper<CommentVoteEntity> {

}
