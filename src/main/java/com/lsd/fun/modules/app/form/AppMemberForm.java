package com.lsd.fun.modules.app.form;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Created by lsd
 * 2020-01-15 14:45
 */
@Api(tags = "App用户信息表单")
@Data
public class AppMemberForm{

    @ApiModelProperty(hidden = true)
    private Integer id;

    private String phone;

    private Integer avatar;

    private String email;

    private String captcha;

    @ApiModelProperty("性别(-1:未知 1:男 2:女)")
    private Integer sex;

    private LocalDate birth;

}
