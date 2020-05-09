package cn.runic.blog.blog.dao;

import cn.runic.blog.blog.pojo.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {


     /*
            这个方法是要返回所有的顶级评论
            即，这些对象的父评论属性为 null
      */
     List<Comment> findByBlogIdAndParentCommentNull  (Long blogId, Sort sort);
}


