package cn.runic.blog.blog.interceptor;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  这个类是一个自定义 的拦截器类 ，要继承 HandlerInterceptorAdapter  类
 *
 *  目的 :
 *          用来 拦截 所有访问 /admin 下的页面的   ( 要设置拦截路径还需要去 写一个 WebConfig 类,待会介绍 )
 *
 *          防止有人直接输入网址就能登陆 后台博客管理页面
 */

public class LoginInterceptor extends HandlerInterceptorAdapter {

    // preHandle 表示预处理
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (request.getSession().getAttribute("user") == null ){

            response.sendRedirect("/admin");

            return false;
        }

        return true;

    }


    /**
     *      写好了拦截器要做的事之后，还要写一个 WebConfig 配置类
     *      告诉 SpringBoot 要拦截什么访问路径
     */
}
