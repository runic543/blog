package cn.runic.blog.blog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

    // 为了把异常记录起来，首先要 获取日志对象
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 这个注解是表示这方法 可以做 异常处理
    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request,Exception e) throws Exception {
                                        //参数 1 希望知道访问哪个路径出现异常，所以参数有 request
                                        //参数 2 要处理异常肯定要把异常对象传进方法中，才能进行处理

        /*  有了 日志对象 ，传递过来的 URL，以及异常对象之后
            就要把 URL和 异常对象 记录进 日志对象 中

            使用 logger.error( ) 方法
            格式是固定的
         */
        logger.error("Request URL : {} ,Exception : {}",request.getRequestURL(),e);


        /**
         *   这里补充一下，这是为了让 NotFoundException 可以跳转到它原本该跳转到的 404.html
         *   而不是 error.html
         *   所以要加一个逻辑判断，让带有 状态码的 异常不跳转到 error.html
         *   详细看 NotFoundException
         *
         *   利用了 注解工具类
         */
                                                               //意思是 ，如果状态码 存在
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;  // 马上抛出异常即可
        }



        // 记录完毕后，还要把 URL 和 异常信息 放进视图对象 并返回给前端
        ModelAndView mv = new ModelAndView();
        mv.addObject("url",request.getRequestURL());
        mv.addObject("exception",e);

        //返回给 error目录下的 error.html
        mv.setViewName("error/error");


        return mv;
    }
}
