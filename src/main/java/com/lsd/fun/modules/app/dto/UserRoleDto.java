package com.lsd.fun.modules.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by lsd
 * 2020-01-13 17:57
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("app登录接口参数")
@Data
public class UserRoleDto {

    @ApiModelProperty(hidden = true)
    private Integer userId;

    @ApiModelProperty(hidden = true)
    private List<String> roleList;

}
