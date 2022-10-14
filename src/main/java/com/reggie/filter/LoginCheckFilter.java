package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @ClassName LoginCheckFiler
 * @Date 2022/10/6 10:49
 * 检查用户是否已经登陆
 */

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符（**）
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public static final Logger logger = LoggerFactory.getLogger(LoginCheckFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();
        Long empId = (Long)request.getSession().getAttribute("employee");

        logger.info("拦截到请求：" + requestURI);
        //定义不需要处理的资源请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断哪些请求需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，则直接放行
        if (check) {
            logger.info("本次请求无需处理：" + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4-1.如果需要处理，首先判读登录状态，如果已登录则直接放行
        if (empId != null) {
            logger.info("用户已登录，用户id为：" + empId);
//            long id = Thread.currentThread().getId();
//            String name = Thread.currentThread().getName();
//            logger.info("当前线程id为{}，线程名称为{}", id, name);
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //4-2.如果需要处理，首先判读登录状态，如果已登录则直接放行（判断移动端用户登录状态）
        if (request.getSession().getAttribute("user") != null) {
            logger.info("用户已登录，用户id为：" + request.getSession().getAttribute("user"));
//            long id = Thread.currentThread().getId();
//            String name = Thread.currentThread().getName();
//            logger.info("当前线程id为{}，线程名称为{}", id, name);
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("用户尚未登录");
        //5.如果未登录则返回登录结果，通过输出流方式像客户端页面响应数据
        //res.data.code === 0 && res.data.msg === 'NOTLOGIN'
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }


    /**
     * @date: 2022/10/6 11:20
     * @remark: 检查本次请求是否需要放行
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
