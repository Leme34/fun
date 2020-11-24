package com.lsd.fun.modules.app.service.impl;

import com.lsd.fun.modules.app.dao.CommentVoteDao;
import com.lsd.fun.modules.app.entity.CommentVoteEntity;
import com.lsd.fun.modules.app.service.CommentVoteService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;


@Service
public class CommentVoteServiceImpl extends ServiceImpl<CommentVoteDao, CommentVoteEntity> implements CommentVoteService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<CommentVoteEntity> page = this.page(
                new Query<CommentVoteEntity>().getPage(query),
                new QueryWrapper<CommentVoteEntity>()
        );

        return new PageUtils(page);
    }

}
