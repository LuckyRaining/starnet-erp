package net.starnet.erp.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring Security 安全配置类
 * 用于配置 Web 安全相关的规则，如请求授权和 CSRF 防护。
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置 HTTP 安全策略
     * 定义哪些请求需要认证，哪些可以公开访问，以及是否启用 CSRF 防护。
     *
     * @param http HttpSecurity 对象，用于配置 Web 安全选项
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 允许所有路径的请求公开访问（不进行身份验证）
                .antMatchers("/**").permitAll()
                // 其他任何请求都需要经过身份验证
                .anyRequest().authenticated()
                .and()
                // 禁用 CSRF（跨站请求伪造）防护，通常在使用 JWT 等无状态认证时关闭
                .csrf().disable();
    }


}
