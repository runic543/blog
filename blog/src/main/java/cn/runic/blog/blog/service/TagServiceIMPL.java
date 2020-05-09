package cn.runic.blog.blog.service;

import cn.runic.blog.blog.dao.TagRepository;
import cn.runic.blog.blog.exception.NotFoundException;
import cn.runic.blog.blog.pojo.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceIMPL implements TagService {

    //注入 TagRepository 类对象
    @Autowired
    private TagRepository tagRepository;




    /**
     *    保存功能
     */
    @Transactional
    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }



    /**
     *   查询功能， 这个就不用加上事务注解了
     */
    @Override
    public Tag getTag(Long id) {
        return tagRepository.findById(id).get();
    }


    /**
     *
     *   根据 名字 查询 标签信息
     */
    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }


    /**
     *    分页查询功能  ????????????没搞明白
     */
    @Transactional
    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }




    /**
            查询所有的标签信息
     */
    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }





    /**
     *
     *     通过 ID 查询所有的标签
     *
     *     从前端 blog-input.html 页面的  <input type="hidden" name="tagIds" value="1,2,3">
     *         回传过来的数据是 1,2,3 这样包含逗号的字符串类型数据
     *
     *     所以，要先把 1,2,3 这样包含逗号的 String 数据通过逗号分开，
     *          转换成 Long 类型的数据，
     *          最后放进 list 集合
     *         这里就用到了下面的方法 converToList()
     *
     *     最后执行 findAllById() ，注意这个方法的参数 是可以传 迭代器的
     *          就是，传一个 list 集合进，它会把 list 集合中的数据一个个取出来
     *          迭代地拿到每一个 Long 类数据
     *          并返回 list 集合里所有 id 对应的 tag
     *          最终以 list 集合的方式返回
     *
     */
    @Override
    public List<Tag> listTag(String ids) {  //

        return tagRepository.findAllById(convertToList(ids));

    }

    /**
     *      这个方法是辅助上面的方法的
     *
     *      可以把包含逗号的 String 类型的 1,2,3 数据
     *      转化成 Long 类型的 list 集合
     */
    private List<Long> convertToList(String ids) {

        List<Long> list = new ArrayList<>();

        if (!"".equals(ids) && ids != null) {

            String[] idarray = ids.split(",");


            for (int i=0; i < idarray.length;i++) {


                //情况 1 : 字符串中不包含字母的，只包含数值
                //      判断是否是 id ，如果是则。。。
                //      如果不是。。。

                if( idarray[i].matches("[0-9]+")  ) {     // 全为数字 返回true

                    //先把字符串取出来，放进 str 中
                    String str = idarray[i];
                    //把字符串转换为 Long 型数据
                    Long num = new Long(str);



                    try{
                        //如果查的出对象，说明前端传过来的 数字，是tag 的 id值
                        Tag t = tagRepository.findById(num).get();

                        // 那么就把这个 id值 放进 list中
                        list.add(new Long(idarray[i]));

                    }catch (Exception e){

                        //如果上面出现异常，就表查不出来，那就说明 前端传过来的数字，不是 tag 的 id
                        //  而是 tag 的 name值

                        //那么就根据 name值 创建一个新的 tag,并放进数据库中
                        String name = idarray[i];
                        Tag t1 = new Tag();
                        t1.setName(name);
                        Tag t2 = tagRepository.save(t1);

                        //执行保存方法之后会返回一个 Tag 类对象   获取到它的 id值，并放进 list集合
                        Long id = t2.getId();
                        list.add(id);
                    }   /*
                            因为 tag 的 id 是会自动增长的，所以不推荐用 数字 作为 标签的名字，除非是一连串长数字
                         */




                }else {
                  //情况 2 : 字符串中包含字母的  （即不完全是数字）

                          //这种就肯定是 tag 的 name值
                          //跟上面差不多


                        String name = idarray[i];
                        Tag t1 = new Tag();
                        t1.setName(name);
                        Tag t2 = tagRepository.save(t1);

                        Long id = t2.getId();
                        list.add(id);
                }

            }
        }
        return list;
    }















    /**
     *   主要是实现 index.html 中 顺序显示 tag 信息

         与 typeServiceIMPL 的一样
     */


    @Override
    public List<Tag> listTagTop(Integer size) {

        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size");

        Pageable pageable = PageRequest.of(0,size,sort);


        return tagRepository.findTop(pageable);
    }






    /**  更新功能    */
    @Transactional
    @Override
    public Tag updateTag(Long id, Tag tag) {

        //先找到这个要更新的对象
        Tag t = tagRepository.getOne(id);

        if(t == null){
            throw  new NotFoundException("不存在该标签"); //这利用了自定义异常
        }


        //找到之后，替换
        BeanUtils.copyProperties(tag,t);

        return tagRepository.save(t);// 替换完成后会返回一个替换后的 Type 对象
    }


    /**
     *    删除
     */
    @Transactional
    @Override
    public void deleteTag(Long id) {
             tagRepository.deleteById(id);
    }
}
