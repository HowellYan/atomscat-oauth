package com.atomscat.config.security.jwt;

import com.atomscat.common.exception.LoginFailLimitException;
import com.atomscat.common.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Howell Yang
 */
@Slf4j
@Component
public class AuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${Atomscat.loginTimeLimit}")
    private Integer loginTimeLimit;

    @Value("${Atomscat.loginAfterTime}")
    private Integer loginAfterTime;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
            String username = request.getParameter("username");
            recordLoginTime(username);
            String key = "loginTimeLimit:" + username;

            int restLoginTime = loginTimeLimit;
            log.info("用户" + username + "登录失败，还有" + restLoginTime + "次机会");
            if (restLoginTime <= 3 && restLoginTime > 0) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "用户名或密码错误，还有" + restLoginTime + "次尝试机会"));
            } else if (restLoginTime <= 0) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "登录错误次数超过限制，请" + loginAfterTime + "分钟后再试"));
            } else {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "用户名或密码错误"));
            }
        } else if (e instanceof DisabledException) {
            ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "账户被禁用，请联系管理员"));
        } else if (e instanceof LoginFailLimitException) {
            ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, ((LoginFailLimitException) e).getMsg()));
        } else {
            ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "登录失败，其他内部错误"));
        }
    }

    /**
     * 判断用户登陆错误次数
     */
    public boolean recordLoginTime(String username) {

        String key = "loginTimeLimit:" + username;
        String flagKey = "loginFailFlag:" + username;

        return true;
    }
}
