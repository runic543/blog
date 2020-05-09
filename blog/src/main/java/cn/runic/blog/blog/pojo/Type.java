package cn.runic.blog.blog.pojo;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_type")
public class Type {

    @Id
    @GeneratedValue
    private Long id;


    @NotBlank(message = "分类名称不能为空") // 虽然前端页面已经做好了 非空校验 (types-input.html 中使用js 实现的 )
    private String name;                   //   可是很容易就可以绕过，因此后端这里也要做 非空校验
                                           //  在输入数据库的时候进行，因此在 实体类 这里写非空校验
    /*                                     要注意的是 ： 这样写后，错误信息只会在 后端控制台输出，
     *                                                  如果想要让错误信息在前端页面展示 ，就要去 控制器 那里修改 */



    //在 Blog 类中写好了 和 Type 类的 多对一关系映射后
    //在这里就要写和 Blog 的 一对多 映射关系

    //   使用 mappedBy = type 进行标记 ，表明 这一端是 “ 关系被维护端 ”
    //       注意 ： 这个 "type"  的意思是， 通过 Blog 类中的 type 属性 进行关联
    @OneToMany(mappedBy = "type")
    private List<Blog> blogs = new ArrayList<>();

    /*
    mappedBy的意思就是“被映射”，即mappedBy这方不用管关联关系，关联关系交给另一方处理

    规律：凡是双向关联，mapped必设，因为根本都没必要在2个表中都存在一个外键关联，在数据库中只要定义一边就可以了

            a) 只有OneToOne,OneToMany,ManyToMany上才有mappedBy属性，ManyToOne不存在该属性；

            b) mappedBy标签一定是定义在the owned side(被拥有方的)，他指向the owning side(拥有方)；

            c) mappedBy的含义，应该理解为，拥有方能够自动维护 跟被拥有方的关系；
                当然，如果从被拥有方，通过手工强行来维护拥有方的关系也是可以做到的。

            d) mappedBy跟JoinColumn/JoinTable总是处于互斥的一方，可以理解为正是由于拥有方的关联被拥有方的字段存在，
                拥有方才拥有了被 拥有方。mappedBy这方定义的JoinColumn/JoinTable总是失效的，不会建立对应的字段或者表
     */







    public Type() {
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
        return "Type{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
