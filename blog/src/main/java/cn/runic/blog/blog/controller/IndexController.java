package cn.runic.blog.blog.controller;

import cn.runic.blog.blog.exception.NotFoundException;
import cn.runic.blog.blog.service.BlogService;
import cn.runic.blog.blog.service.TagService;
import cn.runic.blog.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {



    /**
     *   这个类主要是用来 做 博客前期 测试的
     */
//    @GetMapping("/")
//    public String index(){
//        System.out.println("访问到了");
//
//        //场景一 : 服务器端 故意出现异常，用来测试404页面
//        //   int i = 9/0;
//
//        //场景二 : 访问的资源不存在了，测试出现这种异常时，能否调到自己写的 404.html
//        String blog = null;
//        if(blog == null) {
//            //在出现异常时，“博客不存在”会被记录进日志 和 控制台
//            throw new NotFoundException("博客不存在");
//
//            //抛出该异常之后，根据我们自定义异常上的注解，最终会跳转到 404.html ( 详细看 NotFoundException )
//        }
//
//        return "index1";
//    }


    /**
     *  也是博客前期测试方法， 主要用来测试 日志 AOP 处理
     *
     *   {id}/{name} 是模拟前端传过来的参数,
     *      此处只是模拟一下，主要是测试 日志处理 获取请求参数，唯一值得说的 就是 复习如何获取 请求URI 的参数 @PathVariable
     */
//    @GetMapping("/a/{id}/{name}")
//    public String index2(@PathVariable Integer id,@PathVariable String name){
//                        //注意，因为是在 请求 URI 中接收数据，所以要使用 @ PathVariable
//
//        System.out.println("------------index---------");
//
//        return "index1";
//    }


    /**
     *   当导入静态页面之后，就要正式搭建博客
     *
     *   注意 ： 由于 SpringBoot 使用 Thymeleaf 模板引擎，而之前写的页面都是没有引入的
     *          所以每个前端页面 都要 引入 Thymeleaf 的文件头
     *          资源引入也要记得加上 th:   ( 网络资源，比如图片可以不用加 th，加了也行 。
     *                                      主要是项目中自带的资源 加 th )
     *          才会正常显示
     */
//    @GetMapping("/index.html")
//    public String index3(){
//        return "index";
//    }





    /**
     *   要把每个之前写的 前端页面 都进行引入是 相当麻烦的
     *
     *   于是就使用了 Thymeleaf 的 fragment 功能，抽取出重用的内容
     *
     *   然后在每个前端页面进行引入 就行
     *
     *   现在只把 blog.html  引入了
     *   那么测试一下
     *
         测试成功之后要把所有的 静态页面 都去引入
     */

//     @GetMapping("/blog")
//     public String blog(){
//         return "blog";
//     }










    @Autowired
    private BlogService blogService ;


    @Autowired
    private TypeService typeService;


    @Autowired
    private TagService tagService;


    /**
     *
     *
     *      页面展示访问的方法
     */
    @GetMapping("/")
    public String index(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){

        // 前端页面要展示的 博客列表 部分 的数据
        model.addAttribute("page",blogService.listBlog(pageable));

        // 分类栏 数据
        model.addAttribute("types",typeService.listTypeTop(6));

        // 标签栏 数据
        model.addAttribute("tags",tagService.listTagTop(10));


        //  最新推荐博客
        model.addAttribute("recommendBlogs",blogService.listRecommendBlogTop(8));

        return "index"   ;
    }


    /**
     *
     *   全局搜索按钮访问的方法
     */
    @PostMapping("/search")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model) {

        //注意加 % %,表示模糊搜索
        model.addAttribute("page", blogService.listBlog("%"+query+"%", pageable));


        model.addAttribute("query", query);

        return "search";
    }


    /**
     *
     *
     *    博客详情功能
     *
     */
    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id,Model model) {

        /*
            这里调用的 getAndConvert(Long id)
            目的是把 blog对象中的 content 博客正文，
            转为 md 语法格式，前端页面效果才能以 markdown 格式展示
         */
        model.addAttribute("blog", blogService.getAndConvert(id));



        return "blog";
    }


    /**
     *  这个方法是为了在 _fragment.html 中的 底部 footer 中
     *  动态显示最新博客而写的
     *
     */
    @GetMapping("/footer/newblog")
    public String newblogs(Model model) {
        model.addAttribute("newblogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }








}

