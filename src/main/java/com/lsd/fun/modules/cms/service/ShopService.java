package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.query.ShopQuery;
import com.lsd.fun.modules.cms.dto.ShopExcelDTO;
import com.lsd.fun.modules.cms.vo.ShopVO;
import com.lsd.fun.modules.cms.entity.ShopEntity;

import java.util.Collection;
import java.util.List;

/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 01:29:43
 */
public interface ShopService extends IService<ShopEntity> {

    PageUtils queryPage(ShopQuery query);

    /**
     * ShopExcelDTO -> ShopEntity 批量新增或更新
     *
     * @param parsedResult 从Excel数据解析出来的数据
     */
    void saveFromExcelParsedResult(List<ShopExcelDTO> parsedResult, Integer isUpdate);

    List<ShopVO> queryList(Wrapper wrapper);

    ShopVO queryById(Integer id);

    void update(ShopEntity shop);

    void removeLogic(List<Integer> idList);

    /**
     * order by field #{keySet}
     */
    List<ShopVO> listOrderByField(Collection<Integer> keySet);
}

