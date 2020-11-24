package com.lsd.fun.modules.app.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lsd.fun.modules.app.entity.CommentEntity;
import com.lsd.fun.modules.app.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-04-15 18:30:06
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentEntity> {

    IPage<CommentVo> queryPage(IPage<Object> page, @Param("userId") Integer userId,
                               @Param("shopId") Integer shopId, @Param("hostPrefix") String hostPrefix);
}
