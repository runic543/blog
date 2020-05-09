package cn.runic.blog.blog.service;

import cn.runic.blog.blog.dao.BlogRepository;
import cn.runic.blog.blog.exception.NotFoundException;
import cn.runic.blog.blog.pojo.Blog;
import cn.runic.blog.blog.pojo.Type;
import cn.runic.blog.blog.util.MarkdownUtils;
import cn.runic.blog.blog.util.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;


@Service
public class BlogServiceIMPL implements BlogService {



    @Autowired
    private BlogRepository blogRepository;







    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).get();
    }






    /**  获取 Blog 并把其中的 content 属性内容 转换为 md 格式
     *
     *      主要是为了前端 博客页面，显示博客正文的时候，使其排版符合 md 格式
     */
    @Transactional
    @Override
    public Blog getAndConvert(Long id) {

        Blog blog = blogRepository.findById(id).get();

        if (blog == null) {
            throw new NotFoundException("该博客不存在");
        }

        Blog b = new Blog();

        BeanUtils.copyProperties(blog,b);

        String content = b.getContent();

        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

        //更新一下浏览次数
        blogRepository.updateViews(id);

        return b;
    }








    /**
     *
     *  由于前端页面的要求是：  动态组合搜索 ，
     *      所以就要使用 BlogRepository继承的JpaSpecificationExecutor<> 中的方法 来实现 动态组合搜索
     *       和 Pageable 类实现 分页显示
     *
     *   步骤分析 :
     *      1. 由于 dao层的接口，继承了 JpaSpecificationExecutor<>
     *           所以 blogRepository 的 findAll() 方法就多出了一个 重载 : findAll(   Specification<Blog> specification, Pageable pageable )
     *
     *      2. 由于之前没有创建出 Specification<Blog> 类对象，所以要在参数中 直接 new 出来，
     *           就是在 new 中完成 类似 sql 语句拼接，实现 动态组合搜索
     *
     *      3. new 中重写的方法 :
     *          @Override
                public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder)

                    参数  :

                        Root<>: 表示要查询的对象是谁，这里为 Blog 对象
                                (把我们要查询的 Blog 对象映射为 Root对象 ，从中可以获取表的字段,属性名..)
                        CriteriaQuery: 放查询条件的容器，并且可以执行 查询条件
                        CriteriaBuilder: 设置具体某一个条件的表达式
     */
    @Transactional
    @Override
    public Page<Blog> listBlog(Pageable pageable, Blog blog) {



        return blogRepository.findAll(
                // 参数 1 ：
                new Specification<Blog>() {
                    @Override
                    public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                        // 新建一个 Predicate 类型的集合，用来放置 我们要组合的条件 ( 就是要包含 title ，type 与否 )
                        List<Predicate> predicates = new ArrayList<>();


                        // 组合条件一 : 是否包含 Blog 对象的 title 属性值
                        if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {

                            //要是存在 title 值 , 就把这个组合条件 加进 predicates 集合里
                            // 用 predicates.add( Predicate p )方法   Predicate 可看做是 条件类

                            //下一步是关键 :
                             //         由于前端的要求是 模糊查询 title 值，所以可以
                            //          使用 criteriaBuilder.like() 方法，方法返回的是一个 Predicate 条件对象，可把它作为实参传给 add()
                            //
                            //          那么在什么地方写像 查询语句 的 “条件” 呢？
                            //           就是在 like(Expression<String> expression , String s) 中
                            //
                            //              参数 1 :
                            //                  忽略掉 Expression ,直接就理解为 属性的名字

                            //                  好比模糊查询 sql 中
                            //                  SELECT * FROM [user] WHERE u_name LIKE '%三%'  的 u_name
                            //                  通过 Blog 的映射对象 root 拿到 属性的名字

                            //              参数 2 :
                            //                   相当于 sql 语句中 like 后面要加的 字符串 ( 所以得加上 2 个 % )
                            predicates.add(criteriaBuilder.like(root.<String>get("title"), "%"+blog.getTitle()+"%"));
                        }                                           //String 表示指明 title 属性的类型






                        // 组合条件二 : 是否包含Blog对象 的 Type 类属性 的 id属性值
                        /**
                         *  问题 :
                         *      原来这里是这么写的  if ( null != blog.getType().getId() ) { ...}
                         *       但是当页面输入 localhost:....admin/blogs
                         *       报了 Type属性 空指针异常,页面无法成功跳转
                         *
                         *  分析原因 :
                         *      当我们在地址栏输入 .....admin/blogs 后
                         *      会访问后端控制器类 中相对应的方法
                         *        public String blogs( ... ,Blog blog, ..  )
                         *
                         *      发现前端并没有传递一个 Blog 对象给后端控制器，
                         *      因此，SpringBoot( mvc ) 就会自动构造一个 Blog 对象，且所有属性都是 null
                         *
                         *      当控制器类的 blogs() 方法中的代码调用到 Service 层的这个 BlogServiceIMPL 类
                         *      的时候，自然就会传一个 什么属性都为 null 的 Blog 类对象
                         *
                         *      那么,为什么只有 Type 属性报了 空指针异常 呢?
                         *      因为只有此处是 获取 Blog 中的 Type 的 id
                         *      既然 传过来的 Blog 的属性都为 null,Type 也一定是 null
                         *      null .getId() 就必然报异常
                         *
                         *
                         *  解决 :
                         *      首先要知道，啥情况下 会用到 BlogServiceIMPL 中这分页查询方法
                         *       1. 当请求 URL 为 localhost:....admin/blogs 时，会调用控制器类中的 public String blogs()
                         *          然后调用该方法.
                         *          ( 注意这种情况，从前端传过来的 Blog 对象的所有属性都是 null  )
                         *
                         *       2. 当在 adminBlog.html 中搜索框里写下内容，并点击搜索时，
                         *          以及，点击上一页/下一页 按钮时，
                         *          会调用控制器类中的 public String search()
                         *          然后调用该方法。
                         *          ( 注意这种情况，从前端传过来的 Blog 对象的 3 个属性是有值的，
                         *                       Blog.title, Blog.recommend,Blog.Type.id  )
                         *
                         *       搞清楚有什么情况后，就可以分情况解决
                         *          情况 1 : 那就好办 ，if语句直接改成 : if ( null != blog.getType() ){ 。。。 }
                         *                  那么就不会出现 null.getId() 这样的异常
                         *
                         *          情况 2 : 与情况 1 不矛盾，有 3 个属性值传过来自然就可以执行 if 体中的代码
                         *                  主要是如何从前端拿到 Type 的 id 值
                         *                  这里详细解释在 BlogController 的 public String search() 中
                         *
                         *
                         */
                        if ( null != blog.getType() ) {


                            //因为前端不需要模糊查询，所以使用 equal()
                            predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"), blog.getType().getId()));
                        }






                        //  组合条件三 : 前端传过来的 Blog 对象的 recommend 属性是否为 true
                        if (blog.isRecommend()) {
                            predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                        }






                        // 把所有组合条件都写好后并放进 predicates 集合
                        //然后就要使用 CriteriaQuery 进行查询，把 predicates 集合放进 CriteriaQuery 这个容器中

                        // CriteriaQuery.where() 方法就可按放进来的 组合条件 进行查询
                        //  where 就相当于 sql 语句中的 where 关键字
                        //   参数要放一个数组，因此要把 predicates 转换为数组
                        //                  predicates.toArray(new Predicate[predicates.size()])
                        //                   意思是 把 predicates 集合转换为 Predicate 类型的数组，且数组长度为 原来集合的大小
                        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

                        return null;
                    }
                }    ,

                //参数 2 :
                pageable);

    }







    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }





    /**
     *     index.html 中最新博客推荐栏 所要调用的方法
     */
    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {


        Sort sort =  Sort.by(Sort.Direction.DESC,"updateTime");

        Pageable pageable = PageRequest.of(0,size,sort);

        return blogRepository.findTop(pageable);
    }


    /**
     *
     *
     *   全局搜索功能，不具体解释了
     */
    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }


    /**
     *

            这个方法是 新增博客和 编辑博客 都会用到的办法
     */
    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {

        if( blog.getId() == null ){                     //如果传进来的 blog 没有 id，表示是新增博客

            //对 Blog 对象的一些属性进行赋值
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);

        }else {                                     // 传进来的 blog 有 id 即 编辑博客
            blog.setUpdateTime(new Date());         // 因此就只用对 更新时间 做一个修改
        }



        return blogRepository.save(blog);
    }






    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {

        Blog b = blogRepository.findById(id).get();

        if(b == null){
            throw new NotFoundException("该博客不存在");
        }


        /*
            参数 1 : 数据源对象，就是要被复制的对象
            参数 2 : 要对其赋值的对象

                ( 这 2 个参数的意思是，把传过来的 blog 赋值给 之前查询出来的对象 b


            参数 3 : String 类型的数组对象，放要忽略的属性
                    这里用了一个自己写的 工具类

                ( 表示，过滤掉 blog 对象中属性值为 null 的属性


                这段代码结合起来的意思就是，把 blog 中的所有有值的属性都赋值给 b
         */
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));


        b.setUpdateTime(new Date());

        return blogRepository.save(b);
    }





    @Transactional
    @Override
    public void deleteBlog(Long id) {


        blogRepository.deleteById(id);
    }




    /*
        TagShowController 中调用到的方法
        ,目的是按条件获取 tag

     */
    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {


        return blogRepository.findAll( new Specification<Blog>() {

            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                //通过 Blog 类中的 tags 属性，把标签表关联起来
                Join join = root.join("tags");

                //把表和表关联后，就可以构建查询条件

                /*
                    从关联对象中获取的 id，假如和 传进来的 tagId 一样的话
                    就会自行查询数据库，返回一个 Page 对象，里面包含 Blog 的所有数据
                 */
                return criteriaBuilder.equal(join.get("id"),tagId);


            }
        }
        ,pageable);
    }


    /*
     *
     *      这个是归档页 调用的方法
     *
     *      思路是，先查出所有的 Blog 对象的 更新时间 的年份都有啥 ( 2020,2019...)
     *              然后 按照这些年份查询出相对应的  Blog
     */
    @Override
    public Map<String, List<Blog>> archiveBlog() {

        //获取所有年份信息，按顺序的
        List<String> years = blogRepository.findGroupYear();

        //下一步就是 以查询出来的 年份信息，查询出对应的 Blog 对象
        // 把它们放进 map 中返回
        Map<String,List<Blog>> map = new HashMap<>();

        for ( String year : years ){
            map.put(year,blogRepository.findByYear(year));
        }

        return map;


    }


    /*
        归档页 调用的另一个方法

        返回总记录数

     */
    @Override
    public Object countBlog() {

        return blogRepository.count();
    }
}
