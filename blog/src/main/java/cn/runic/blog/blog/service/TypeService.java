package cn.runic.blog.blog.service;

import cn.runic.blog.blog.pojo.Type;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {

    //保存
    Type saveType(Type type);

    //通过 ID 获取类型信息
    Type getType(Long id);

    //通过 名称 获取查询 类型信息
    Type getTypeByName(String name);

    //分页查询
    Page<Type> listType(Pageable pageable);

    //查询所有 但是不像分页查询那样，需要传递 Pageable 实现分页功能,也不需传任何参数
    // 只需要把所有的结果查询出来即可
    List<Type> listType();

    //这个方法特别为 首页分类栏 写的
    List<Type> listTypeTop(Integer size);

    //修改
    Type updateType(Long id,Type type);

    //删除
    void deleteType(Long id);
}
