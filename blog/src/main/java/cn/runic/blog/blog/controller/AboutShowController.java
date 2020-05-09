package cn.runic.blog.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutShowController {


    @GetMapping("/about")
    public String about(){

        return "aboutme";
    }
}
