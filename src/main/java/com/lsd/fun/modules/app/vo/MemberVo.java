package com.lsd.fun.modules.app.vo;

import com.lsd.fun.modules.cms.entity.MemberEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by lsd
 * 2020-04-01 21:43
 */
@Data
public class MemberVo {

    private String username;

    private Integer sex;

    private String phone;

    private String email;

    private LocalDate birth;

    private Integer avatar;
    private String avatarUrl;

    private Integer addressDefaultId;

    private LocalDateTime createdAt;

}
