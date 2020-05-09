package cn.runic.blog.blog.dao;

import cn.runic.blog.blog.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 *   这里就使用到了 SpringBoot Data JPA
 *
 *   继承 JpaRepository<参数 1 , 参数 2>
 *
 *     参数 1 ： 要操作的 实体类对象是谁
 *     参数 2 ： 主键类型
 *
 *  这样继承之后，使用者不用关心细节，就可以直接操作数据库
 */
public interface UserRepository extends JpaRepository<User,Long> {

    // 登录校验功能， 方法名写对之后就行了，他会自动根据用户名和密码去查询数据库
    User findByUsernameAndAndPassword(String username, String password);
}
