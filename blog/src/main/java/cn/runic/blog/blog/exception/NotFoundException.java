package cn.runic.blog.blog.exception;

/*
    这是自定义的异常，就是用来测试 场景二 的
    当资源找不到时，抛出该异常并日志记录异常，打印输出异常到控制台

    然后进行页面跳转 @ResponseStatus(HttpStatus.NOT_FOUND)

 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



@ResponseStatus(HttpStatus.NOT_FOUND) // 此注解是出现异常后页面跳转的关键，表示当抛出 NotFoundException 的时候
                                      //        SpringBoot会把它当做资源找不到的状态，然后实行跳转到 404.html

//                                      继承 RunTimeException 才能进行异常捕获
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException() {

    }

    public NotFoundException(String message) {
        super(message);
    }


    /*  注意，此时写完之后，在 IndexController 类中进行场景二 的测试时
                还不能完成跳转

        因： 之前写了异常拦截处理器  ControllerExceptionHandler
            它把所有的异常都拦截了，并最终 把 error.html 进行返回

        解决 ： 到 ControllerExceptionHandler 里设置过滤，
                即 当有些异常 标识了状态码，就不进行处理

     */

}
