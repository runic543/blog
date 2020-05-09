package cn.runic.blog.blog.service;

import cn.runic.blog.blog.dao.TypeRepository;
import cn.runic.blog.blog.exception.NotFoundException;
import cn.runic.blog.blog.pojo.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class TypeServiceIMPL implements TypeService {

    //注入 TypeRepository 对象
    @Autowired
    private TypeRepository typeRepository ;




    /**
     *    保存功能
     */
    @Transactional // 增删改查一定要加上 事务 注解，这样才能执行成功 ( 应该是可以放在 dao 的接口上的，放在这里也行 )
    @Override
    public Type saveType(Type type) {
        return typeRepository.save(type);
    }





    /**
     *   查询功能， 这个就不用加上事务注解了
     */
    @Override
    public Type getType(Long id) {

        return typeRepository.findById(id).get();
    }





    /**
     *
     *   根据 名字 查询 类型信息
     */
    @Override
    public Type getTypeByName(String name) {
        return typeRepository.findByName(name);
    }






    /**
     *    分页查询功能  ????????????没搞明白
     */
    @Transactional
    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }





    /**
     *      查询所有数据，且不需要实现分页效果
     */
    @Override
    public List<Type> listType() {
        return typeRepository.findAll();
    }








    /**
     *
     *
     *
     *  这个方法是为了 index.html 中 分类栏 而写
     *
     *  传进来一个参数 size，表示要显示多少条分类信息
     *  调用该方法后，会返回 以顺序排好的 分类信息
     *
     *  ( 单这个方法解释不全，需联系 前端，控制器类，Service ，dao 这些才能完全清楚
     *
     */
    @Override
    public List<Type> listTypeTop(Integer size) {

        // 定义顺序 sort.by(Direction direction,String... properties)

        //  Direction direction : 这个参数是指定排序的方向
        //                          Sort.Direction.DESC,倒序

        // String... properties : 按什么字段排序
        //                           "blogs.size" , 意思是按照 Type 类中的 blogs 这个集合属性的 size 大小来进行排序

        Sort sort =  Sort.by(Sort.Direction.DESC,"blogs.size");

        //意思是，拿第一页，页面大小为 size，以 sort 的顺序来排序
        Pageable pageable = PageRequest.of(0,size,sort);



        return typeRepository.findTop(pageable);
    }









    /**  更新功能    */
    @Transactional
    @Override
    public Type updateType(Long id, Type type) {

        //先找到这个要更新的对象
        Type t= typeRepository.getOne(id);

        if( t == null ){
            throw new NotFoundException("不存在该类型"); //这利用了自定义异常
        }

        //找到之后，替换
        BeanUtils.copyProperties(type,t);

        return typeRepository.save(t);   // 替换完成后会返回一个替换后的 Type 对象
    }











    /**
     *    删除
     */
    @Transactional
    @Override
    public void deleteType(Long id) {

        typeRepository.deleteById(id);
    }



}
