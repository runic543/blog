package cn.runic.blog.blog.dao;

import cn.runic.blog.blog.pojo.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog,Long>, JpaSpecificationExecutor<Blog> {
//                                                          JpaSpecificationExecutor<> 中的一些方法可以帮使用者 动态地 进行组合查询
//                                                               意思就是查询数据库的时候，有时候所有参数都有，有时候只输入部分参数
//                                                                该类方法使用的是类似 拼 SQL 语句的形式， 做到动态组合查询
//                                                                   具体代码在 ServiceIMPL看


    /**
     *  为了 index.html 中最新博客推荐栏 所写的，详细看 IndexController 中的 index() 方法
     *
     *  与 Type(Tag)Repository 中一样，
     *  唯一的区别是这次的 JPQL 要加上 where 字段
     *   因为只查询 带推荐标记 博客
     */
    @Query("select b from Blog b where b.recommend = true")
    List<Blog> findTop(Pageable pageable);


    /**     实现全局搜索的功能
     *
     *     详细的就不细讲le
     *     不难，把所有类放一起就能清楚
     *
     *       JPQL 里 ?1 表示第一个参数
     */
    @Query("select b from Blog b where b.title like ?1 or b.content like ?1")
    Page<Blog> findByQuery(String query,Pageable pageable);





    @Transactional
    @Modifying
    @Query("update Blog b set b.views = b.views+1 where b.id=  ?1")
    int updateViews(Long id);








    /**
     *
     *      归档页 所调用的方法
     *
     *      思路是，先查出所有的 Blog 对象的 更新时间 的年份都有啥 ( 2020,2019...)
     *              然后按照这些年份查询出相对应的  Blog
     *
     *      这个方法主要是先完成第一步功能，先查出所有的年份
     *      在 sql 语句中是这么写的
     *      SELECT date_format(b.update_time,'%Y') as year from t_blog b GROUP BY year ORDER BY  year DESC ;
     *
     *      可以根据 sql 来写自定义的 JPQL
     *      这部分内容先按照 sql 来理解就行，具体如何用 jpql 来实现，等之后去学完 jqpl 再回来补充
     */

    @Query("select function('date_format',b.updateTime,'%Y') as year from Blog b group by function('date_format',b.updateTime,'%Y') order by year")
    List<String> findGroupYear();


    /**
     *
     *      该方法接上面的方法，完成第二步
     *      以这些查出来的年份，查询出所有对应的 Blog 对象
     *
     */
    @Query("select b from Blog b where function('date_format',b.updateTime,'%Y') = ?1 ")
    List<Blog> findByYear(String year);

}
