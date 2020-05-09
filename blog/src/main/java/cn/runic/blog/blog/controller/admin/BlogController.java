package cn.runic.blog.blog.controller.admin;

import cn.runic.blog.blog.pojo.Blog;
import cn.runic.blog.blog.pojo.Type;
import cn.runic.blog.blog.pojo.User;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import static java.lang.Long.parseLong;

@Controller
@RequestMapping("/admin")
public class BlogController {


    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;  // 注入这个主要是为了获取 Type 分类信息，然后把它们显示在 adminBlog.html 中的搜索框中

    @Autowired
    private TagService tagService; //  注入这个主要是为了获取 Tag 标签信息，然后把它们显示在 blogs-input.html 中的选择框中

    /*
            定义这些主要是为了 return 时写的路径更加 可视化，不用也行
     */
    private static final String INPUT = "admin/blog-input";
    private static final String LIST = "admin/adminBlog";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";










    /**
     *   要访问 adminBlog.html ，就得访问这个方法
     *
     */
    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 2,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Blog blog, Model model){


        model.addAttribute("page",blogService.listBlog(pageable,blog));


        //这一步是把查询出来的数据库中的 types 分类信息，传给 adminBlog.html 中的搜索框中的 分类下拉框 进行显示
        model.addAttribute("types",typeService.listType());

        return "admin/adminBlog";
    }


    /**
     *    和上面的方法差不多，
     *    区别在于：
     *          1. 这个方法是在 adminBlog.html 中点击 上一页/下一页/搜索 按钮时才访问的
     *
     *          2. 因为前端使用 ajax 提交表格数据，因此是 PostMapping 注解
     *
     *          3. 为了让前端页面只刷新   中间列表部分
     *              所以 return 时只返回一个 片段
     *
     *    目的是为了 在点击这 3 个按钮之后，实现分页显示功能，但是又不影响 搜索框里的内容
     *    和 ajax 相关，具体要结合 adminBlog.html
     */
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 2,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Blog blog, Model model,String typeId){
        /*  注意这里参数还加了一个 String typeId

            解释 ： 因为 adminBlog.html 中，点 上一页/下一页/搜索 按钮之后，
                    都会传过来 3 个参数，其中 title 和 recommend 都是 Blog 的基础类型属性，
                    而 typeId 是 Blog 对象的 Type 类型属性 的 属性值

                    因此，仅靠一个 Blog 参数是不能完全接收 3 个属性值

            解决 :  所以就要写一个 名字和前端属性值一样的 参数
                    typeId 来接收
                    并把它转换为 Long ，放进 Blog 里
         */

        if(typeId != null && "" != typeId) {    //要先进行非空判断，因为有时候前端页面不加分类信息，就点击搜索 .这种情况就不用运行 if 里的代码


            Long typeIdLong = parseLong(typeId);

            Type BlogType = new Type();
            BlogType.setId(typeIdLong);

            blog.setType(BlogType);
        }


        model.addAttribute("page",blogService.listBlog(pageable,blog));

        //  意思是 返回一个 admin下的 adminBlog 页面的叫作 blogList  片段
        return "admin/adminBlog :: blogList";

        /**
         *  如何定义这个片段呢？
         *      就要到 adminBlog.html 中，找到要选择的部分，即 <table> 标签
         *      加上 <table th:fragment="blogList">
         *      那么这样之后，每次调用该方法，Thymeleaf 模板引擎都只会渲染更新这一部分内容
         */
    }















    /**
     *      跳转到 admin/blogs-input, html
     */
    @GetMapping("/blogs/input")
    public String input(Model model){


        //把查询出来的数据库中的 types 分类信息，传给 前端页面的 分类下拉框 进行显示
        model.addAttribute("types",typeService.listType());


        //把查询出来的数据库中的 tags 分类信息，传给 前端页面的 分类下拉框 进行显示
        model.addAttribute("tags",tagService.listTag());


        //这一步是为了在访问 blogs-input.html 时，让前端能拿到一些值，不至于报错 而写的
        model.addAttribute("blog",new Blog());


        return INPUT ; //用到了上面定义 final 变量
    }








    /**
     *   blog 新增和修改共用同一个方法
     */
    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes,HttpSession session){

        //先在 session 中拿到 user 数据
        blog.setUser((User) session.getAttribute("user"));



        // 由于前端的 type.id 值会自动封装进 blog 的 Type 属性的 id 中
        // 所以要先根据 blog 中的   type.id 获取到 Type 对象
        // 然后把 Type 对象 放进 blog 中
        /*
             这样并非多此一举，因为前端只传过来一个 type.id ,意味着 Type 对象只有 id 属性有值
             为了让 Type 对象的 name 属性也有值，所以才这么做
         */
        blog.setType(typeService.getType(blog.getType().getId()));



        // 和上面差不多，但由于前端传过来的 tagIds 值是 String 类型的 "1,2,3,4,5.."
        // 且 Blog 对象的 tag 属性为一个集合
        // 这样就得去 Service 层为 tagService 写一个 listTag( String ids ) 方法 ( 注意带有迭代器 ，让字符串转化成 Long )
        // 详细代码看 tagServiceIMPL
        blog.setTags(tagService.listTag(blog.getTagIds()));






        //最后保存 Blog 类对象进数据库
        Blog b ;

        /*
            这个代码是为了修复 编辑博客 的时候，

         */
        if (blog.getId() == null) {

            b =  blogService.saveBlog(blog);

        } else {

            b = blogService.updateBlog(blog.getId(), blog);
        }








        /*
            注意，这里就不像写 TypeController 前后端都实现严格的 非空校验了，就简单校验一下
         */
        if(b == null){
           attributes.addFlashAttribute("message","操作失败");
        } else {
            attributes.addFlashAttribute("message","操作成功");
        }









        return REDIRECT_LIST;   //有参数 RedirectAttributes，才能写 return " redirect:...."
                                // 且 redirect: 跟着的不是页面的名字，是 Controller类中要执行的方法名称
    }









    /**
     *   编辑博客时访问的方法
     *
     *   拿到从前端传过来的 id，判断是哪一个 Blog 对象要编辑
     *   然后跳转到 编辑页面 ( 编辑和新增是共用一个页面的 　)
     */
    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model){

        // 由于最终还是要跳转到  blog-input.html ，因此还是要往前端带 标签和分类 数据
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());



        Blog blog = blogService.getBlog(id);

        //调用该方法，可以让 Blog 对象中的 tagIds 属性进行赋值
        //  这样前端接收到 Blog 类对象就好拿到 tagIds
        //详细看 Blog.java
        blog.init();

        //
        model.addAttribute("blog",blog);

        return INPUT ;
    }






    //点击页面 删除 按钮对应的方法
    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes) {

        blogService.deleteBlog(id);


        attributes.addFlashAttribute("message", "删除成功");

        return REDIRECT_LIST;

    }

}
