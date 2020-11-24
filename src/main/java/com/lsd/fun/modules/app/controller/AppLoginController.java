package com.lsd.fun.modules.app.controller;

import com.google.common.collect.Lists;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.form.AppLoginForm;
import com.lsd.fun.modules.app.form.AppRegisterForm;
import com.lsd.fun.modules.app.service.MailService;
import com.lsd.fun.modules.app.utils.JwtUtils;
import com.lsd.fun.modules.cms.entity.MemberEntity;
import com.lsd.fun.modules.cms.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by lsd
 * 2020-01-13 15:38
 */
@Slf4j
@Api(tags = "App登录接口")
@RequestMapping("/app")
@RestController
public class AppLoginController {

    private final JwtUtils jwtUtils;
    private final MemberService memberService;
    private final MailService mailService;
    private final StringRedisTemplate redisTemplate;
    @Value("#{funConfig.redis.keyPrefix.mailCaptcha}")
    private String keyPrefix;

    public AppLoginController(JwtUtils jwtUtils, MemberService memberService, MailService mailService, StringRedisTemplate redisTemplate) {
        this.jwtUtils = jwtUtils;
        this.memberService = memberService;
        this.mailService = mailService;
        this.redisTemplate = redisTemplate;
    }

    @ApiOperation("移动端用户注册")
    @PostMapping("/register")
    public R register(@RequestBody AppRegisterForm form) {
        ValidatorUtils.validateEntity(form);
        // 验证邮箱
        final String key = keyPrefix + ":" + form.getEmail();
        final String captchaInRedis = redisTemplate.opsForValue().get(key);
        if (!StringUtils.equals(form.getCaptcha(), captchaInRedis)) {
            throw new RRException("验证码错误");
        }
        String salt = RandomStringUtils.randomAlphanumeric(20);
        final MemberEntity familyUserEntity = new MemberEntity()
                .setAvatar(form.getAvatar())
                .setUsername(form.getUsername())
                .setPassword(new Sha256Hash(form.getPassword(), salt).toHex()) //sha256加密
                .setSalt(salt)
                .setEmail(form.getEmail());
        try {
            memberService.save(familyUserEntity);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new RRException("用户名或邮箱已被注册");
        }
        // 消费验证码
        redisTemplate.delete(key);
        return R.ok();
    }

    @ApiOperation("移动端登录")
    @PostMapping("/login")
    public R login(@RequestBody AppLoginForm form) {
        ValidatorUtils.validateEntity(form);
        final MemberEntity member = Optional.ofNullable(memberService.lambdaQuery().eq(MemberEntity::getUsername, form.getUsername()).one())
                .orElseThrow(() -> new RRException("用户名密码错误", HttpStatus.UNAUTHORIZED.value()));
        if (!StringUtils.equals(member.getPassword(), new Sha256Hash(form.getPassword(), member.getSalt()).toHex())) {
            throw new RRException("用户名密码错误", HttpStatus.UNAUTHORIZED.value());
        }
        if (member.getStatus().equals(Constant.FALSE)) {
            throw new RRException("用户被禁用", HttpStatus.FORBIDDEN.value());
        }
        // 模拟查询用户权限
        List<String> roleList = Lists.newArrayList("user");
        // 发放token
        return R.ok().put("token", jwtUtils.generateToken(new UserRoleDto(member.getId(), roleList)));
    }

    @ApiOperation(value = "获取邮箱验证码", notes = "发送验证码邮件到此邮箱")
    @GetMapping("/send-email")
    public R sendEmail(String username, String email) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(email)) {
            return R.error("请输入用户名和邮箱");
        }
        memberService.checkUsername(username);
        memberService.checkEmail(email);
        //发送邮件
        mailService.sendMail(email);
        return R.ok("邮件发送成功");
    }


}
