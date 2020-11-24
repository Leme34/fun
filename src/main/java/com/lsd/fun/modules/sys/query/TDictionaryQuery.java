package com.lsd.fun.modules.sys.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.lsd.fun.common.utils.BaseQuery;

import javax.validation.constraints.NotNull;

/**
 * Created by lsd
 * 2019-12-05 15:04
 */
@ApiModel("数据字典查询参数")
@Data
public class TDictionaryQuery extends BaseQuery {

    @ApiModelProperty("字典key")
    private String keyword;

    @NotNull(message = "字典key不能为空")
    @ApiModelProperty("数据字典id")
    private Integer dataDictionaryId;

}
