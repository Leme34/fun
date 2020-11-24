package com.lsd.fun.modules.sys.query;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.lsd.fun.common.utils.BaseQuery;

/**
 * 系统角色查询参数
 * Created by lsd
 * 2019-08-07 16:49
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "系统角色查询参数")
@Data
public class SysRoleQuery extends BaseQuery {

    @ApiModelProperty(position = 5, required = false, value = "创建者")
    private Long createUserId;

    @ApiModelProperty(position = 6, required = false, value = "角色名称")
    private String roleName;

}
