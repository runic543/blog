package cn.runic.blog.blog.dao;

import cn.runic.blog.blog.pojo.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {

    Tag findByName(String name);

    /*
         为了 index.html 中以顺序显示 tag 属性而写的方法
         与 type 的类似
     */
    @Query("select t from Tag t")
    List<Tag> findTop(Pageable pageable);
}
