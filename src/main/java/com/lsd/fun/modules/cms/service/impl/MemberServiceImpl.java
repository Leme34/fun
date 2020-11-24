package com.lsd.fun.modules.cms.service.impl;

import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.modules.app.form.AppMemberForm;
import com.lsd.fun.modules.app.vo.MemberVo;
import com.lsd.fun.modules.cos.config.QiNiuProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.MemberDao;
import com.lsd.fun.modules.cms.entity.MemberEntity;
import com.lsd.fun.modules.cms.service.MemberService;


@Service
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Value("#{funConfig.qiniu}")
    private QiNiuProperties qiNiuProperties;

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(query),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Optional<MemberVo> queryById(Integer userId) {
        Optional<MemberVo> opt = Optional.ofNullable(this.baseMapper.queryById(userId));
        if (opt.isPresent()) {
            MemberVo vo = opt.get();
            if (vo.getAvatar() != null) {
                vo.setAvatarUrl(qiNiuProperties.getHostPrefix() + vo.getAvatarUrl());
            }
        }
        return opt;
    }


    public void checkEmail(String email) {
        if (email == null) {
            return;
        }
        if (this.lambdaQuery().eq(MemberEntity::getEmail, email).oneOpt().isPresent()) {
            throw new RRException("邮件已被注册");
        }
    }

    public void checkMobile(String phone) {
        if (phone == null) {
            return;
        }
        if (this.lambdaQuery().eq(MemberEntity::getPhone, phone).oneOpt().isPresent()) {
            throw new RRException("手机号已被注册");
        }
    }

    public void checkUsername(String username) {
        if (this.lambdaQuery().eq(MemberEntity::getUsername, username).oneOpt().isPresent()) {
            throw new RRException("用户名已被注册");
        }
    }

    @Override
    public void updateUserInfo(AppMemberForm form) {
        this.checkMobile(form.getPhone());
        this.checkEmail(form.getEmail());
        MemberEntity member = new MemberEntity();
        BeanUtils.copyProperties(form, member);
        this.updateById(member.setId(form.getId()));
    }

}
