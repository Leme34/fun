package com.lsd.fun.modules.sys.loader;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.config.FunConfig;
import com.lsd.fun.modules.sys.entity.SysUserEntity;
import com.lsd.fun.modules.sys.entity.SysUserRoleEntity;
import com.lsd.fun.modules.sys.service.SysDictionaryManageService;
import com.lsd.fun.modules.sys.service.SysRoleService;
import com.lsd.fun.modules.sys.service.SysUserRoleService;
import com.lsd.fun.modules.sys.service.SysUserService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 容器首次启动完成后，初始化鉴权数据
 * Created by lsd
 * 2019-08-08 14:11
 */
@Component
public class InitialApplicationLoader implements ApplicationListener<ContextRefreshedEvent> {

    // 是否已启动
    private boolean alreadySetup = false;
    private final FunConfig config;
    private final SysRoleService sysRoleService;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final SysDictionaryManageService sysDictionaryManageService;
    private final StringRedisTemplate redisTemplate;
    private final Gson gson;

    public InitialApplicationLoader(FunConfig config, SysRoleService sysRoleService, SysUserService sysUserService, SysUserRoleService sysUserRoleService, SysDictionaryManageService sysDictionaryManageService, StringRedisTemplate redisTemplate, Gson gson) {
        this.config = config;
        this.sysRoleService = sysRoleService;
        this.sysUserService = sysUserService;
        this.sysUserRoleService = sysUserRoleService;
        this.sysDictionaryManageService = sysDictionaryManageService;
        this.redisTemplate = redisTemplate;
        this.gson = gson;
    }

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        // 1 加载字典表放入redis
        sysDictionaryManageService.list().forEach(dic ->
                redisTemplate.opsForValue().set(config.getRedis().getKeyPrefix().getDictionary() + ":" + dic.getId(), gson.toJson(dic))
        );
        // 2 加载角色
        loadUserAndRoles();

        // 已启动
        alreadySetup = true;
    }

    /**
     * 获取数据字典的所有角色，没有则插入系统角色表
     */
    private void loadUserAndRoles() {
        // 1 获取最高权限的角色，没有则创建
        Long superRoleId = sysRoleService.saveIfNotExists(Constant.RoleName.SUPER_ADMIN_ROLENAME.getRoleName(), "拥有最高权限");
        // 2 创建默认管理员账号
        String username = config.getAuth().getAdmin().getUsername();
        String salt = config.getAuth().getAdmin().getSalt();
        SysUserEntity adminUser = sysUserService.getOne(new QueryWrapper<SysUserEntity>().lambda().eq(SysUserEntity::getUsername, username));
        boolean isAdminNotExists = adminUser == null;
        Long adminUserId = isAdminNotExists ? null : adminUser.getUserId();  // 若已有admin则取得其userId
        if (config.getAuth().getAdmin().isEnabled() && isAdminNotExists) {
            SysUserEntity admin = SysUserEntity.builder()
                    .username(username)
                    .password(new Sha256Hash(config.getAuth().getAdmin().getDefaultPassword(), salt).toHex())  //sha256加密
                    .salt(salt)
                    .status(Constant.TRUE)
                    .createTime(LocalDateTime.now())
                    .build();
            adminUserId = sysUserService.saveReturnId(admin);
        }
        // 3 关联管理员角色
        final LambdaQueryWrapper<SysUserRoleEntity> qw = new QueryWrapper<SysUserRoleEntity>()
                .lambda()
                .eq(SysUserRoleEntity::getUserId, adminUserId)
                .eq(SysUserRoleEntity::getRoleId, superRoleId);
        if (sysUserRoleService.getOne(qw) == null) {
            SysUserRoleEntity sysUserRole = SysUserRoleEntity.builder().roleId(superRoleId).userId(adminUserId).build();
            sysUserRoleService.save(sysUserRole);
        }
    }
}
