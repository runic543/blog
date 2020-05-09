package cn.runic.blog.blog.controller.admin;

import cn.runic.blog.blog.pojo.User;
import cn.runic.blog.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

        @Autowired
        private UserService userService;


    /**
     *   访问后台管理登录页面 功能
     */
    @GetMapping
    public String loginPage() {
            return "admin/login";
        }


    /**
     *   登录页面的 登录共能
     */
        @PostMapping("/login")
        public String login(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes attributes){

//            System.out.println("username = "+username+"password = "+password);

            User user = userService.checkUser(username,password);


            if(user != null)  {

                // 注意 ： 这一步校验结束，就把密码设置为 空，不然有泄漏的风险
                user.setPassword(null);

                session.setAttribute("user",user);

                return "admin/loginIndex";

            }else {

                attributes.addFlashAttribute("message","用户名和密码错误");

                /*
                        注意 ： 这里要使用 redirect 重定向的方式
                                 不可以使用直接 return 请求转发的方式

                        解释 ： 虽然可以成功跳转到登录页面，但是再次登录的时候 路径会有问题

                        而且，由于使用的是 重定向，所以上面的提示信息也不可以使用 ModelAndView 对象来传递，
                           这是因为 mv对象 的作用域 不足以包含重定向之后的
                 */
                return "redirect:/admin";

            }
        }


    /**
     *  注销
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");

        return "redirect:/admin";
    }

}
