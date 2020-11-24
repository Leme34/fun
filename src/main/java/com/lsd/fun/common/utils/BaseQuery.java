package com.lsd.fun.common.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通用查询参数
 * Created by lsd
 * 2019-08-07 15:18
 */
@ApiModel(description = "通用查询参数")
@Data
public class BaseQuery {

    @ApiModelProperty(position = 1, required = false, value = "当前页码")
    public Integer page = 1;

    @ApiModelProperty(position = 2, required = false, value = "每页显示记录数")
    public Integer limit = 10;

    @ApiModelProperty(position = 3, required = false, value = "排序字段")
    public String order_field = null;

    @ApiModelProperty(position = 4, required = false, value = "排序方式，默认升序")
    public String order = "asc";

}
