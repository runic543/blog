package cn.runic.blog.blog.service;

import cn.runic.blog.blog.dao.CommentRepository;
import cn.runic.blog.blog.pojo.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class CommentServiceIMPL implements CommentService {



    @Autowired
    private CommentRepository commentRepository;











    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {

        // ASC 正序 （前端页面显示效果： 最新评论在最下面）
        Sort sort =  Sort.by(Sort.Direction.ASC,"createTime");

        // 返回所有顶级的数据（即父评论）
        List<Comment> comments = commentRepository.findByBlogIdAndParentCommentNull(blogId,sort);

        //调用抄来的方法
       return eachComment(comments);
    }


    /**
     * 循环每个顶级的评论节点
     * @param comments
     * @return
     */
    private List<Comment> eachComment(List<Comment> comments) {

        List<Comment> commentsView = new ArrayList<>();

        for (Comment comment : comments) {

            Comment c = new Comment();

            BeanUtils.copyProperties(comment,c);

            commentsView.add(c);
        }

        //合并评论的各层子代到第一级子代集合中
        combineChildren(commentsView);

        return commentsView; //返回所有父级评论，以及父级评论的所有子级评论，且所有子级评论都在同一(list)层级
    }

    /**
     *
     * @param comments root根节点，blog不为空的对象集合
     * @return
     */
    private void combineChildren(List<Comment> comments) {

        for (Comment comment : comments) {

            //拿到子评论对象
            List<Comment> replys1 = comment.getReplyComments();

            for(Comment reply1 : replys1) {
                //循环迭代，找出子代，存放在tempReplys中
                recursively(reply1);
            }

            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReplyComments(tempReplys);

            //清除临时存放区
            tempReplys = new ArrayList<>();
        }
    }



    //存放迭代找出的所有子代的集合
    private List<Comment> tempReplys = new ArrayList<>();



    /**
     * 递归迭代，剥洋葱
     * @param comment 被迭代的对象
     * @return
     */
    private void recursively(Comment comment) {


        tempReplys.add(comment);//顶节点添加到临时存放集合

        if (comment.getReplyComments().size()>0) {

            List<Comment> replys = comment.getReplyComments();

            for (Comment reply : replys) {

                tempReplys.add(reply);

                if (reply.getReplyComments().size()>0) {

                    recursively(reply);
                }
            }
        }
    }











    @Transactional
    @Override
    public Comment saveComment(Comment comment) {


        Long parentCommentId = comment.getParentComment().getId();

        if (parentCommentId != -1) {
            comment.setParentComment(commentRepository.findById(parentCommentId).get());

        } else {

            comment.setParentComment(null);
        }

        comment.setCreateTime(new Date());


        return commentRepository.save(comment);
    }



}
