package net.starnet.erp.interceptor;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域资源共享（CORS）过滤器
 * 用于处理前端请求的跨域问题，设置响应头以允许跨域访问。
 */
@Component
public class CorsFilter implements Filter {

    /**
     * 执行过滤逻辑
     * 在请求到达目标资源之前，设置 CORS 相关的响应头，并处理预检请求（OPTIONS）。
     *
     * @param request  Servlet 请求对象
     * @param response Servlet 响应对象
     * @param chain    过滤器链，用于将请求传递给下一个过滤器或目标资源
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        // 允许客户端携带凭证（如 Cookie、Authorization 头等）
        res.addHeader("Access-Control-Allow-Credentials", "true");
        // 允许所有来源的跨域请求
        res.addHeader("Access-Control-Allow-Origin", "*");
        // 允许的 HTTP 请求方法
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        // 允许的请求头字段，包括自定义的 Token 头
        res.addHeader("Access-Control-Allow-Headers", "Content-Type,X-CAF-Authorization-Token,Authorization,X-TOKEN");

        // 处理预检请求（OPTIONS），直接返回成功响应，不再继续向后执行
        if (((HttpServletRequest) request).getMethod().equals("OPTIONS")) {
            response.getWriter().println("ok");
            return;
        }
        // 将请求传递给过滤器链中的下一个环节
        chain.doFilter(request, response);
    }

    /**
     * 初始化过滤器时调用的方法
     * 在过滤器实例创建后、投入服务前调用，当前实现为空。
     *
     * @param filterConfig 过滤器配置对象
     * @throws ServletException Servlet 异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    /**
     * 销毁过滤器时调用的方法
     * 用于释放过滤器占用的资源，当前实现为空。
     */
    @Override
    public void destroy() {
    }

}