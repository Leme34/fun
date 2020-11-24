package com.lsd.fun.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lsd.fun.modules.app.dao.CommentDao;
import com.lsd.fun.modules.app.entity.CommentEntity;
import com.lsd.fun.modules.app.query.CommentQuery;
import com.lsd.fun.modules.app.service.CommentService;
import com.lsd.fun.modules.app.vo.CommentVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {

    @Value("#{funConfig.qiniu.hostPrefix}")
    private String hostPrefix;

    @Override
    public PageUtils queryPage(CommentQuery query) {
        IPage<Object> page = new Query<>().getPage(query);
        IPage<CommentVo> voList = this.baseMapper.queryPage(page, query.getUserId(), query.getShopId(),hostPrefix);
        return new PageUtils(voList);
    }

}
