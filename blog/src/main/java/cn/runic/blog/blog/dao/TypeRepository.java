package cn.runic.blog.blog.dao;

import cn.runic.blog.blog.pojo.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// 这里的 接口 继承JpaRepository<Type, Long> 就相当于 加了 @Repository 注解了，Springboot 能扫描它
public interface TypeRepository extends JpaRepository<Type, Long> {


    /**
     *      因为 Springboot data jpa 没有提供根据名称查询的方法
     *      因此要来自己写
     */
    Type findByName(String name);


    /**
     *
     *  1 .
     *      这个方法是为了 index.html 中分类栏而写
     *      Service 层调用这个方法，这个方法应该返回 按照规定顺序查询出来的 分类信息
     *      具体看 Service 层，联系起来就会知道这个方法的作用了
     *
     *
     *  2 .
     *      由于 jpa 没有自带这种功能(按照顺序查询出信息)，所以需要我们自己定义 查询语句
     *      @ Query( "select t from Type t")
     *
     *      这里使用 JPQL 语句( 类似 sql 语句 )
            具体语法上网看

            select t from Type t
             Type t 表示给 Type 起别名
             select 后面跟的是要返回的实体类对象，因为起了别名，所以 JPA 会知道去返回 Type 类型实体

             这个 JPQL 的意思是，找到装 Type 对象的表，查询出信息并封装进 Type 中，最后返回


        3 .
            为什么要使用 Pageable 做形参
            解释 ：
                 Pageable 中带有每一页的大小
                 以及以什么进行排序的信息

        4 .
            把 @Quert() 与 findTop(Pageable pageable)放在一起，
                JPA 会自己根据 pageable 中传过来的 size，sort，按顺序地查询
                并返回 实体类 Type对象
     */
    @Query("select t from Type t")
    List<Type> findTop(Pageable pageable);


}

