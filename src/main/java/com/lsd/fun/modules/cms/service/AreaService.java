package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.lsd.fun.modules.cms.query.AreaQuery;

import java.util.List;

/**
 * 地区表,直辖市在level=0能够找到，在level=1也能找到
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 02:06:00
 */
public interface AreaService extends IService<AreaEntity> {

    PageUtils queryPage(BaseQuery query);

    List<AreaEntity> listTree();

    List<AreaEntity> listSubArea(AreaQuery query);
}

