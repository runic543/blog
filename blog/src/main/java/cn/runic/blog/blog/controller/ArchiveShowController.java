package cn.runic.blog.blog.controller;

import cn.runic.blog.blog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchiveShowController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/archives")
    public String archives(Model model) {

        // 返回按年份分类的 Blog 对象，该方法返回的是一个 map 集合
        model.addAttribute("archiveMap", blogService.archiveBlog());


        model.addAttribute("blogCount", blogService.countBlog()); // 返回总记录数

        return "archives";

    }
}