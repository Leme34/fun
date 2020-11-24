package com.lsd.fun.modules.app.resolver;

import lombok.extern.slf4j.Slf4j;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.modules.app.annotation.AppLoginUser;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.interceptor.AuthorizationInterceptor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户信息
 * <p>
 * Created by lsd
 * 2020-01-13 17:29
 */
@Slf4j
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(UserRoleDto.class) && parameter.hasParameterAnnotation(AppLoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                  NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
        //用户ID
        final Integer userId = (Integer) Optional.ofNullable(request.getAttribute(AuthorizationInterceptor.USER_KEY, RequestAttributes.SCOPE_REQUEST))
                .orElseThrow(() -> {
                    log.error("用户ID不能为空");
                    return new RRException("登录失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
                });
        //用户角色
        final String userRoleStr = (String) Optional.ofNullable(request.getAttribute(AuthorizationInterceptor.USER_ROLE_KEY, RequestAttributes.SCOPE_REQUEST))
                .orElseThrow(() -> {
                    log.error("用户角色不能为空");
                    return new RRException("登录失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
                });
        final List<String> roles = Arrays.stream(userRoleStr.split(",")).collect(Collectors.toList());
        return new UserRoleDto(userId, roles);
    }
}
