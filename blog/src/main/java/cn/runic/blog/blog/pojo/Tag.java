package cn.runic.blog.blog.pojo;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_tag")
public class Tag {

    @Id
    @GeneratedValue
    private Long id;


    @NotBlank(message = "标签名称不能为空")  // 和 Type 类的 name 一样，具体可以看那边
    private String name;






    @ManyToMany(mappedBy = "tags")    //和 Blog 类进行 多对多 关系映射
                                         // 选择 Tag 为被维护的 端
    private List<Blog> blogs = new ArrayList<>();






    public Tag() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }



    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}