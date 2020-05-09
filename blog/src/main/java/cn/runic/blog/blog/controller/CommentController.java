package cn.runic.blog.blog.controller;

import cn.runic.blog.blog.pojo.Comment;
import cn.runic.blog.blog.pojo.User;
import cn.runic.blog.blog.service.BlogService;
import cn.runic.blog.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;


@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;



    //springboot 取值语法，从 application.yaml 中取值
    // 这个头像图片作为 访问用户 的头像
    @Value("${comment.avatar}")
    private String avatar;




    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {

        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));

        return "blog :: commentList";
    }


    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session) {


        Long blogId = comment.getBlog().getId();

        comment.setBlog(blogService.getBlog(blogId));


        User user = (User) session.getAttribute("user");

        if (user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        } else {
            comment.setAvatar(avatar);
        }

        commentService.saveComment(comment);

        return "redirect:/comments/" + blogId;
    }



}