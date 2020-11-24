package com.lsd.fun.modules.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.interceptor.AuthorizationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt工具类
 */
@ConfigurationProperties(prefix = "fun.jwt")
@Component
@Data
public class JwtUtils {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String secret;
    private long expire;
    private String header;

    /**
     * 生成jwt token
     */
    public String generateToken(UserRoleDto userRoleDto) {
        Date nowDate = new Date();
        // 过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        // 创建payload的私有声明，存放当前请求用户的角色列表
        Map<String,Object> claims = new HashMap<>();
        String roleListStr = String.join(",", userRoleDto.getRoleList());
        claims.put(AuthorizationInterceptor.USER_ROLE_KEY,roleListStr);

        return Jwts.builder()
                // 角色列表放入payload的私有声明
                .setClaims(claims)
                .setHeaderParam("typ", "JWT")
                .setSubject(userRoleDto.getUserId().toString())
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            logger.debug("validate is token error ", e);
            return null;
        }
    }

    /**
     * token是否过期
     * @return  true：过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

}
