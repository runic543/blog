package cn.runic.blog.blog.controller;


import cn.runic.blog.blog.pojo.Blog;
import cn.runic.blog.blog.pojo.Type;
import cn.runic.blog.blog.service.BlogService;
import cn.runic.blog.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TypeShowController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id,
                        Model model) {

        //这里size要设置得无限大，这样前端页面就能完全显示出来
        List<Type> types = typeService.listTypeTop(10000);



        if (id == -1) {
            //赋值集合中第一个 type 的 ID
            id = types.get(0).getId();
        }
        model.addAttribute("types", types);






        Blog blog = new Blog();
        Type type = new Type();
        type.setId(id);
        blog.setType(type);

        model.addAttribute("page", blogService.listBlog(pageable, blog));






        // 此代码要结合前端的 activeTypeId ，主要作用就是让选中的标签变 teal色
        model.addAttribute("activeTypeId", id);


        return "types";
    }

}


