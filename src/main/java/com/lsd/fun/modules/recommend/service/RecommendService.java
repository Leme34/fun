package com.lsd.fun.modules.recommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.entity.RecommendEntity;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.vo.ShopVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 查询离线在Spark平台使用ALS算法（粗排）推荐的商铺
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
public interface RecommendService extends IService<RecommendEntity> {

    /**
     * ALS算法召回（推荐）商铺
     *
     * @param userId 推荐目标用户
     * @return 推荐商铺id列表
     */
    List<Integer> recall(Integer userId);


    /**
     * LR算法实现推荐数据的排序
     *
     * @param shopIdList （粗排后）待精排的推荐商铺
     * @param userId     推荐目标用户
     * @return 推荐商铺id列表
     */
//    List<Integer> sort(List<Integer> shopIdList, Integer userId);

}

