package cn.runic.blog.blog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 *    这是用来实现 日志处理 的类
 *    而且实现方式是 使用 SpringBoot 的 AOP 进行切面处理
 *
 */
@Aspect//    标识该类是 可以进行切面操作的
@Component //开启组件扫描
public class LogAspect {

    //先获取一个 日志对象
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    //通过这个注解 定义这个方法是一个切面
    @Pointcut("execution(* cn.runic.blog.blog.controller.*.*(..))")    //execution来定义该切面拦截哪些类
                //意思是，controller 包下的所有的类的所有方法都拦截
    public void log(){
        //这个方法中可以啥也不做
    }






    @Before("log()")// 表示该方法 在 上面的切面 方法之前执行
    public void doBefore(JoinPoint joinPoint){
        /**
         *  定义完最下面的 内部类之后，要在 doBefore() 中写 获取这些个信息的代码
         *
         *  1. url 和 ip 可以通过 HttpServletRequest 对象来获取，
         *      可以使用 ServletRequestAttribute 来获取 HttpServletRequest

            2. 调用的类名和方法名 classMethod 可以用 切面对象 JoinPoint 来拿到
                 要现在方法的参数中加上 这个对象

            3. 请求参数 也是通过 JoinPoint 得到
         */

        //1. url 和 ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        String url = request.getRequestURL().toString();

        String ip = request.getRemoteAddr();



        //2. 调用的类名和方法名 classMethod
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
                                    // 类名                                                 方法名


        //3. 请求参数
        Object[] args = joinPoint.getArgs();


        //4. 都获取到了之后, 把这些封装进 内部类 RequestLog
        RequestLog requestLog = new RequestLog(url,ip,classMethod,args);


        //5. 放进日志对象中， 最终就会显示 Request : { RequestLog 类对象的 toString()打印 }
        logger.info("Request : {}",requestLog);



//      这个就可以注掉了
//      logger.info("-----------doBefore-----");
    }

    /*
        注意，类似 doBefore() 这样的方法会在 切面方法 log() 之前( 之后 ) 执行
         但是由于 log() 中啥也没加
         所以 log() 方法本身不是重点

         重点是 log() 方法拦截了 controller 包中的 所有方法，成为了一个 切面
          那么 controller 包中的方法在执行前，就会执行 log()方法
          而 log() 执行之前 就会执行 doBefore() 这些方法
     */





     @After("log()")
     public void doAfter(){
         logger.info("------------doAfter--------------");
     }





     //有时候需要记录 方法执行完之后返回信息 进 日志, 可以使用 doAfterReturn
     @AfterReturning(returning = "result",pointcut = "log()")  //参数 1 ：说明返回的对象是谁
                                                               //参数 2 : 切入点是谁
     public void doAfterReturn(Object result){
                            // 参数 ：返回的对象

         logger.info( "Result : {} ",result );
     }






    /**
     *  设置这个类 实际上是为了 记录日志 ( 只不过是通过 AOP 的方式实现，这里体会到 AOP 的好处了吧 )
     *   要记录的有
     *     1. 请求 URL
     *     2. 访问者 ip
     *     3. 调用的类名和方法名 classMethod
     *     4. 请求参数 args
     *     5. 返回内容
     *
     *   可以使用一个类 来 封装 这些数据
     *    这里就偷懒 使用 内部类
     */

    private class RequestLog{
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args; // 请求参数，对象数组，什么类型都可以接收

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "RequestLog{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
