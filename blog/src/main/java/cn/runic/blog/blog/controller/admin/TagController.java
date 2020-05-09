package cn.runic.blog.blog.controller.admin;

import cn.runic.blog.blog.pojo.Tag;
import cn.runic.blog.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TagController {

    //注入 Service 层的 Tag 对象
    @Autowired
    private TagService tagService;





    /**
     *  分页列表展示功能
     */
    @GetMapping("/tags")
    //@PageableDefault此注解的作用是指定 Pageable 的参数 : 一页多少条数据，根据什么排序，根据倒序还是正序排序
    public String tags(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model ){                                               // 这个 Pageable 会根据前端代码 自动把 前端数据封装进 Pageable 类对象中

        //调用 tagService 的分页查询方法,拿到 Page 类对象,并把对象存进 model 中
        model.addAttribute("page",tagService.listTag(pageable)) ;


        return "admin/tags";

    }












    /**
     *  tags 页面点击 新增 按钮所调用的方法
     */
    @GetMapping("/tags/input")
    public String input(Model model){

        model.addAttribute("tag",new Tag()); // 写这行代码主要是为了配合 tags-input.html 中的
        // <form action="#" method="post" th:action="@{/admin/tags} " th:object="${tag}"  class="ui form">
        //   需要后端把 Tag 对象传过去


        return "admin/tags-input";
    }









    /**
     *   和 TypeController 的方法一样，所有的注释都去那个类中看
     */
    @PostMapping("/tags")
    public String post(@Valid Tag tag, BindingResult result, RedirectAttributes attributes){


        Tag tag1 = tagService.getTagByName(tag.getName());


        if( tag1 != null){
            result.rejectValue("name","nameError","不能重复添加分类");
        }


//-----------------------------------------------------------------------------------------

        if(result.hasErrors()){     // 假如 后端非空校验结果有错误 就返回

            return "admin/tags-input";
        }


//-----------------------------------------------------------------------------------------

        Tag t = tagService.saveTag(tag);

        if(t == null){
            //如果返回 null 就表示保存失败
            attributes.addFlashAttribute("message","新增操作失败");

        }else{
            //不为空就保存成功
            attributes.addFlashAttribute("message","新增操作成功");
        }

        return "redirect:/admin/tags";
    }









    /**
     *    编辑标签信息 功能 1
     *
     *   首先要实现，根据 id 查找到要编辑的 标签信息 的功能
     *
     *   这个方法是 tags.html 中点击 编辑 按钮之后访问的
     *
     *
     */
    @GetMapping("/tags/{id}/input")  //这个路径要和 tags.html 中的 编辑按钮 的 href 对应
    public String editInput (@PathVariable Long id, Model model){

        //根据ID 把查询出来的标签信息，存到 model 中，并带到 tags-input.html 中
        // 主要目的是为了在 tags-input.html 的输入框中 显示出 分类名称
        model.addAttribute("tag",tagService.getTag(id));

        return "admin/tags-input";   // 为什么要到这个页，因为偷懒，想把新增和编辑这 2 个功能共用同一个页面

    }



    /**
     *   编辑分类信息 功能  2
     *
     *  查询成功之后，在输入框修改 标签名称 后
     *
     *  要点击 提交 的按钮，然后把表单提交给后端
     *
     *   ( 这个方法和 上上面的方法差不多，只不过加了 Long id 参数，然后调用的是 tagService.updateTag 方法
     *
     */
    @PostMapping("/tags/{id}")
    public String editPost( @Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes){

        Tag tag1 = tagService.getTagByName(tag.getName());

        if( tag1 != null){
            result.rejectValue("name","nameError","不能重复添加分类");
        }

        if(result.hasErrors()){
            return "admin/tags-input";
        }

        //这里调用的是更新方法，不是保存方法
        Tag t = tagService.updateTag(id,tag);

        if(t == null){
            attributes.addFlashAttribute("message","编辑操作失败");

        }else{
            attributes.addFlashAttribute("message","编辑操作成功");
        }

        return "redirect:/admin/tags";

    }









    /**
     *  删除标签信息 功能
     *
     *  在 tags.html 中， 删除按钮点击之后，访问这个方法
     */
    @GetMapping("/tags/{id}/delete")
    public String deleteInput (@PathVariable Long id,RedirectAttributes attributes) {

        tagService.deleteTag(id);

        attributes.addFlashAttribute("message","删除操作成功");

        return "redirect:/admin/tags";
    }






}
