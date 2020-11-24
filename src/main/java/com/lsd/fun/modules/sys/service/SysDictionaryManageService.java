package com.lsd.fun.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.sys.entity.SysDictionaryManageEntity;
import com.lsd.fun.modules.sys.query.TDictionaryQuery;

/**
 * 字典管理
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2019-11-15 11:03:14
 */
public interface SysDictionaryManageService extends IService<SysDictionaryManageEntity> {

    /**
     * 分页查询此数据字典id下的字典列表
     */
    PageUtils queryPage(TDictionaryQuery query);


    /**
     * 根据数据字典id查询
     */
    SysDictionaryManageEntity queryById(Integer id);

    /**
     * 批量逻辑删除数据字典
     */
    void remove(Integer[] ids);

    /**
     * 检查：若字典项在同一个字典目录里边已存在，则把delete_at置为空
     */
    void saveOrUpdateWithCheck(SysDictionaryManageEntity sysDictionaryManage);

}

