package com.lsd.fun.modules.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 发送验证码邮件的服务,生成的验证码存入Redis
 */
@Slf4j
@Service
public class MailService {

    @Autowired
    private JavaMailSenderImpl sender;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("#{funConfig.redis.keyPrefix.mailCaptcha}")
    private String keyPrefix;

    public void sendMail(String email) {
        //生成4位随机数作为验证码
        Random random = new Random();
        int captcha = random.nextInt(9000) + 1000;
        //设置邮件标题和内容
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("欢迎注册Fun");
        mailMessage.setText("您的验证码是：" + captcha+"，有效期5分钟");
        //设置源和发送邮箱地址
        mailMessage.setFrom(sender.getUsername());
        mailMessage.setTo(email);
        //发送邮件
        sender.send(mailMessage);
        // 放入redis
        redisTemplate.opsForValue().set(keyPrefix + ":" + email, Integer.toString(captcha), 5, TimeUnit.MINUTES);
        log.debug("生成的验证码为:" + captcha);
    }

}
