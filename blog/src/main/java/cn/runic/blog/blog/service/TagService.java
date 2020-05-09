package cn.runic.blog.blog.service;

import cn.runic.blog.blog.pojo.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    //保存
    Tag saveTag(Tag tag);

    //通过 id 获取标签信息
    Tag getTag(Long id);

    //通过 名称 查询标签信息
    Tag getTagByName(String name );

    //分页查询
    Page<Tag> listTag(Pageable pageable);

    //查询所有 但是不像分页查询那样，需要传递 Pageable 实现分页功能,也不需传任何参数
    // 只需要把所有的结果查询出来即可
    List<Tag> listTag();

    // 通过 ID 查询所有的标签
    List<Tag>  listTag(String ids);


    // 为了 index.html 的标签栏按顺序显示 标签 而写
    List<Tag>  listTagTop(Integer size);

    //修改
    Tag updateTag(Long id , Tag tag);

    //删除
    void deleteTag(Long id);
}
