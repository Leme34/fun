package com.lsd.fun.modules.cms.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by lsd
 * 2020-03-28 01:03
 */
@Data
public class AreaQuery {

    @ApiModelProperty("父级id")
    private Integer pid;
    @ApiModelProperty("父级地区名称")
    private String pName;

    @ApiModelProperty("层级(0:省份/直辖市,1:市级单位,2:区级单位)")
    private Integer level;

}
