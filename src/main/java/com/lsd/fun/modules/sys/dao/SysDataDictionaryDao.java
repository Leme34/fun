package com.lsd.fun.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsd.fun.modules.sys.entity.SysDataDictionaryEntity;
import com.lsd.fun.modules.sys.vo.TDataDictionaryVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据字典
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-15 11:03:15
 */
@Mapper
public interface SysDataDictionaryDao extends BaseMapper<SysDataDictionaryEntity> {

    List<TDataDictionaryVo> queryList();
}
