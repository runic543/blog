package cn.runic.blog.blog.interceptor;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *      这个类 是配合 LoginInterceptor 这个拦截器来实现 登录拦截校验 功能的
 *
 *      目的是让 Springboot 知道要拦截什么 访问路径
 */

//记得加上配置类 注解
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     *  addInterceptors()
     *  把刚才写好的 拦截器 加进来
     *  然后设置拦截访问路径
     *  设置过滤的路径
     *
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
                //加入拦截器
        registry.addInterceptor (new LoginInterceptor())
                //设置需要拦截的路径
                .addPathPatterns("/admin/**")
                //设置过滤的访问路径 ( 即这些路径不用拦截 )
                .excludePathPatterns("/admin","/admin/login","/js/**","/css/**","/images/**","/lib/**");

    }
}
