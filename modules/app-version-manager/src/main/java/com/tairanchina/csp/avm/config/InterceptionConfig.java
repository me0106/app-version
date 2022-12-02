package com.tairanchina.csp.avm.config;

import com.tairanchina.csp.avm.interceptor.AccessControlAllowInterception;
import com.tairanchina.csp.avm.interceptor.AdminInterceptor;
import com.tairanchina.csp.avm.interceptor.AppInterceptor;
import com.tairanchina.csp.avm.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by hzlizx on 2018/5/10 0010
 */
@Configuration
public class InterceptionConfig implements WebMvcConfigurer {

    @Autowired
    private AccessControlAllowInterception accessControlAllowInterception;

    @Autowired
    private UserInterceptor userInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private AppInterceptor appInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessControlAllowInterception)
            .addPathPatterns("/*")
            .addPathPatterns("/*/**");
        registry.addInterceptor(userInterceptor).addPathPatterns().excludePathPatterns(
            "/swagger-ui.html",
            "/swagger-resources",
            "/v2/api-docs",
            "/swagger-resources/configuration/ui",
            "/user/register",
            "/user/login"
        );
        registry.addInterceptor(adminInterceptor).addPathPatterns(
            "/admin/*",
            "/admin/*/**",
            "/log/*",
            "/log/*/**",
            "/chatbot/*",
            "/chatbot/*/**"
        ).excludePathPatterns(
            "/admin/isAdmin"
        );
        registry.addInterceptor(appInterceptor).addPathPatterns(
            "/ios",
            "/ios/*",
            "/ios/*/**",
            "/android",
            "/android/*",
            "/android/*/**",
            "/channel",
            "/channel/*",
            "/channel/*/**",
            "/route",
            "/route/*",
            "/route/*/**",
            "/package",
            "/package/*",
            "/package/*/**",
            "/capi",
            "/capi/*",
            "/capi/*/**",
            "/white",
            "/white/*",
            "/white/*/**"
        );
    }

    @Configuration
    public static class CorsConfig {

        // 当前跨域请求最大有效时长。这里默认1天
        private static final long MAX_AGE = 24 * 60 * 60;

        @Bean
        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.addAllowedOrigin("*"); // 1 设置访问源地址
            corsConfiguration.addAllowedHeader("*"); // 2 设置访问源请求头
            corsConfiguration.addAllowedMethod("*"); // 3 设置访问源请求方法
            corsConfiguration.setMaxAge(MAX_AGE);
            source.registerCorsConfiguration("/**", corsConfiguration); // 4 对接口配置跨域设置
            return new CorsFilter(source);
        }
    }
}
