package net.starnet.erp.config;

import net.starnet.erp.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 拦截器配置类
 * 用于注册和管理系统中的拦截器，如登录校验拦截器。
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 添加拦截器配置
     * 将登录拦截器注册到系统中，并指定拦截路径和排除路径。
     *
     * @param registry 拦截器注册表
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器，拦截所有请求，但排除登录接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/user/login/**"); // 排除登录相关接口，避免循环拦截

        super.addInterceptors(registry);
    }
}
