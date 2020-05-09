package cn.runic.blog.blog.service;

import cn.runic.blog.blog.pojo.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService    {


    Blog getBlog(Long id);

    // 获取 Blog 并把其中的 content 属性内容 转换为 md 格式
    Blog getAndConvert(Long id);

    Page<Blog> listBlog(Pageable pageable,Blog blog);

    Page<Blog> listBlog(Pageable pageable);

    //为 index.html 中的最新推荐博客栏 而写的，和 TypeService ，TagService 中的类似
    // 详细参 IndexController 的 index()
    List<Blog> listRecommendBlogTop(Integer size);

    // 全局搜索要用到的方法
    Page<Blog> listBlog(String query,Pageable pageable);

    Blog saveBlog(Blog blog);

    Blog updateBlog(Long id,Blog blog);

    void deleteBlog(Long id);

    //这个是 TagShowController 中调用的方法,目的是按条件获取 tag
    Page<Blog> listBlog(Long tagId,Pageable pageable );


    //  归档页 调用的方法，返回按年份分类的 Blog对象
    Map<String,List<Blog>> archiveBlog();

    //  归档页 调用的另一个方法，返回总记录数
    Object countBlog();
}
