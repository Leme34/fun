package com.lsd.fun.modules.app.interceptor;

import io.jsonwebtoken.Claims;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.modules.app.annotation.AppLogin;
import com.lsd.fun.modules.app.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * app登录认证拦截器
 * <p>
 * Created by lsd
 * 2020-01-13 17:29
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    public static final String USER_ROLE_KEY = "roles";
    public static final String USER_KEY = "userId";
    private final JwtUtils jwtUtils;

    public AuthorizationInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            // 若没有@AppLogin注解
            final AppLogin annotation = ((HandlerMethod) handler).getMethodAnnotation(AppLogin.class);
            if (annotation == null) {
                return true;
            }
            // 对用户携带的jwt进行校验
            final String token = Optional.ofNullable(request.getHeader(jwtUtils.getHeader()))
                    .orElse(request.getParameter(jwtUtils.getHeader()));
            if (StringUtils.isBlank(token)) {
                throw new RRException(jwtUtils.getHeader() + "不能为空", HttpStatus.UNAUTHORIZED.value());
            }
            //解析jwt
            final Claims claims = jwtUtils.getClaimByToken(token);
            if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
                throw new RRException(jwtUtils.getHeader() + "失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
            }
            //设置userId、用户角色列表等到request里，往下传递后续使用
            request.setAttribute(USER_KEY, new Integer(claims.getSubject()));
            request.setAttribute(USER_ROLE_KEY, claims.get(USER_ROLE_KEY, String.class));
        }
        return true;
    }
}
