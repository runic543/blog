package cn.runic.blog.blog.controller.admin;


import cn.runic.blog.blog.pojo.Type;
import cn.runic.blog.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController {


    //注入 Service 层的类
    @Autowired
    private TypeService typeService;







    /**
     *  分页列表展示功能
     */
    @GetMapping("/types")
                      //@PageableDefault此注解的作用是指定 Pageable 的参数 : 一页多少条数据，根据什么排序，根据倒序还是正序排序
    public String types(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model ){                                               // 这个 Pageable 会根据前端代码 自动把 前端数据封装进 Pageable 类对象中

                                  //调用 typeService 的分页查询方法,拿到 Page 类对象,并把对象存进 model 中
       model.addAttribute("page",typeService.listType(pageable)) ;



        return "admin/types";

        /*
                 把 Page 对象传给 admin.types.html 之后，就要去这个前端写获取 数据的代码了
                 具体看 admin.types.html
         */
    }















    /**
     *  新增博客分类信息   功能
     */
    @GetMapping("/types/input")
    public String input(Model model){

        model.addAttribute("type",new Type()); // 写这行代码主要是为了配合 types-input.html 中的
                        // <form action="#" method="post" th:action="@{/admin/types} " th:object="${type}"  class="ui form">
                        //   需要后端把 Type 对象传过去

        System.out.println("有人访问 types/input");


        return "admin/types-input";
    }














    /**
     *  前端输完分类信息，点击提交按钮时 所访问的控制器方法
     *
     *  虽然都是 types, 但是和之前的方法提交方式不一样，
     *  之前是 get 方式，此处是 post 方式
     */
    @PostMapping("/types")
                      //参数 type : 使用 Type 实体类进行接收.Springboot会自动封装前端数据
                     // 参数 attributes : 由于 return 使用重定向 跳转，为了在跳转后的页面也能获取到我们存储的信息
                     //         所以要使用 RedirectAttributes 来完成
                    /*
                         @Valid 和 参数 BindingResult ：
                                @Valid 表示该参数是需要校验的，
                                在 types-input.html 中写完前端的非空校验之后，在后端的 Type 实体类上也写了非空校验 注解之后，
                                为什么在这里也要写非空校验呢 ？ 因为在 Type 实体类上写的非空校验，只能在控制台显示信息
                                为了让 校验结果 能在 前端页面 显示 校验结果，就必须 使用这两个东西
                                ( 注意 ： 这里的校验结果的 错误提示信息  还是 Type 类上写的那些 )

                                BindingResult 用来接收校验之后的结果

                                重点  ！！！ @Valid 和 BinddingResult 一定要写在一起，不然校验无效果
                     */
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes){



        //这是实现 类型名称不可重复的 校验功能 ， 先根据名称查询出数据库中是否已存在一样名字的 类型对象
        Type type1 = typeService.getTypeByName(type.getName());

        if( type1 != null){  //如果 type1 不为空，就表示已经存在这个 分类信息 了

             /*     如何把校验信息返回给前端呢 ？

                    还是使用 BindingResult 类对象，
                    其方法 rejectVaule( @Valid 下的对象的要验证的属性名称，验证结果的键名(可自定义 ) ，要返回的字符串 )

                    该方法会 加一条验证结果信息 返回给前端
              */
            result.rejectValue("name","nameError","不能重复添加分类");
        }






        if(result.hasErrors()){     // 假如 后端非空校验结果有错误 就返回

            return "admin/types-input";
        }







        Type t = typeService.saveType(type);// 调用这个方法会返回一个 Type 类型对象，保存之后的 Type 对象

        if(t == null){
            //如果返回 null 就表示保存失败
            attributes.addFlashAttribute("message","新增操作失败");


        }else{
            //不为空就保存成功
            attributes.addFlashAttribute("message","新增操作成功");
        }

        return "redirect:/admin/types";
    }













    /**
     *    编辑分类信息 功能 1
     *
     *   首先要实现，根据 id 查找到要编辑的 分类信息 的功能
     *
     *   这个方法是 types.html 中点击 编辑 按钮之后访问的
     *
     *
     */
    @GetMapping("/types/{id}/input")  //这个路径要和 types.html 中的 编辑按钮 的 href 对应
    public String editInput (@PathVariable Long id,Model model){

        //根据ID 把查询出来的分类信息，存到 model 中，并带到 types-input.html 中
        // 主要目的是为了在 types-input.html 的输入框中 显示出 分类名称
        model.addAttribute("type",typeService.getType(id));

        return "admin/types-input";   // 为什么要到这个页，因为偷懒，想把新增和编辑这 2 个功能共用同一个页面

     }


    /**
     *   编辑分类信息 功能  2
     *
     *  查询成功之后，在输入框修改 分类名称 后
     *
     *  要点击 提交 的按钮，然后把表单提交给后端
     *
     *   ( 这个方法和 上上面的方法差不多，只不过加了 Long id 参数，然后调用的是 typeService.updateType 方法
     *
     */
    @PostMapping("/types/{id}")
    public String editPost( @Valid Type type, BindingResult result, @PathVariable Long id, RedirectAttributes attributes){

        Type type1 = typeService.getTypeByName(type.getName());

        if( type1 != null){
            result.rejectValue("name","nameError","不能重复添加分类");
        }

        if(result.hasErrors()){
            return "admin/types-input";
        }

        //这里调用的是更新方法，不是保存方法
        Type t = typeService.updateType(id,type);

        if(t == null){
            attributes.addFlashAttribute("message","编辑操作失败");

        }else{
            attributes.addFlashAttribute("message","编辑操作成功");
        }

        return "redirect:/admin/types";

    }


    /**
     *  删除分类信息 功能
     *
     *  在 types.html 中， 删除按钮点击之后，访问这个方法
     */
    @GetMapping("/types/{id}/delete")
    public String deleteInput (@PathVariable Long id,RedirectAttributes attributes) {

        typeService.deleteType(id);

        attributes.addFlashAttribute("message","删除操作成功");

        return "redirect:/admin/types";
    }

}
