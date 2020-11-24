package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.app.form.AppMemberForm;
import com.lsd.fun.modules.app.vo.MemberVo;
import com.lsd.fun.modules.cms.entity.MemberEntity;
import com.lsd.fun.common.utils.BaseQuery;

import java.util.Map;
import java.util.Optional;

/**
 * 会员表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(BaseQuery query);

    Optional<MemberVo> queryById(Integer userId);

    void checkEmail(String email);

    void checkMobile(String phone);

    void checkUsername(String username);

    void updateUserInfo(AppMemberForm form);
}

