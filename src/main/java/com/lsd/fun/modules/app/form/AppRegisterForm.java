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
@EqualsAndHashCode(callSuper = true)
@Api(tags = "App用户注册表单")
@Data
public class AppRegisterForm extends AppLoginForm {

    private Integer avatar;

    @NotNull(message = "请输入邮箱")
    private String email;

    @NotNull(message = "请输入验证码")
    private String captcha;

}
