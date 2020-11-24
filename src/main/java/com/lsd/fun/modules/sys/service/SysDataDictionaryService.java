package com.lsd.fun.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.sys.entity.SysDataDictionaryEntity;
import com.lsd.fun.modules.sys.vo.TDataDictionaryVo;

import java.util.List;

/**
 * 数据字典
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-15 11:03:15
 */
public interface SysDataDictionaryService extends IService<SysDataDictionaryEntity> {

    PageUtils queryPage(BaseQuery query);

    List<TDataDictionaryVo> queryList();

}

