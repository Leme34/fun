package com.lsd.fun.modules.sys.vo;

import lombok.Data;
import com.lsd.fun.modules.sys.entity.SysDictionaryManageEntity;

import java.util.List;

/**
 * Created by lsd
 * 2019-12-05 14:53
 */
@Data
public class TDataDictionaryVo {
    // 数据字典id
    private Long dataDictionaryId;
    // 数据字典名称
    private String dataDictionaryName;

    private List<SysDictionaryManageEntity> dictionaryManageList;

}
