package com.tairanchina.csp.avm.interceptor;

import java.io.IOException;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.entity.LoginInfo;
import com.tairanchina.csp.avm.entity.User;
import com.tairanchina.csp.avm.mapper.UserMapper;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员拦截器
 * Created by hzlizx on 2018/4/25 0025
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LoginInfo loginInfo = ThreadLocalUtils.USER_THREAD_LOCAL.get();
        String userId = loginInfo.getUserId();
        User user = new User();
        user.setUserId(userId);
        User user1 = userMapper.selectOne(user).orElse(null);
        if(user1!=null && user1.getIsAdmin() == 1){
            return true;
        }else{
            this.print(response, ServiceResultConstants.NO_ADMIN.toString());
            return false;
        }
    }


    private void print(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            response.getWriter().print(json);
        } catch (IOException e) {
            logger.error("打印出错", e);
        }
    }
}
