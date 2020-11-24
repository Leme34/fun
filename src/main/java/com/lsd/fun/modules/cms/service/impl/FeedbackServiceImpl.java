package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.FeedbackDao;
import com.lsd.fun.modules.cms.entity.FeedbackEntity;
import com.lsd.fun.modules.cms.service.FeedbackService;


@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackDao, FeedbackEntity> implements FeedbackService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<FeedbackEntity> page = this.page(
                new Query<FeedbackEntity>().getPage(query),
                new QueryWrapper<FeedbackEntity>()
        );

        return new PageUtils(page);
    }

}
