package cn.runic.blog.blog.service;

import cn.runic.blog.blog.dao.UserRepository;
import cn.runic.blog.blog.pojo.User;
import cn.runic.blog.blog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIMPL implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {

        /**
         *   这里使用了 MD5 加密工具类
         *
         *   解释 :  为了让传输密码的时候更加安全，需要在 2 个地方对密码进行加密
         *           1. 前端页面 提交表单时 ，对密码进行加密
         *           2. 后端 接收到密码时 ，再次对其进行加密，并保存到 数据库中
         *
         *           由于这部分 并不是特别专业，也不知道有没有更好的 加密方式
         *           所以，就暂时只做 后端 拿到密码并保存到 数据库 这一过程的 加密
         *              前端的加密要是有 JS 代码做，之后再补充
         *
         *   注意  : 如果密码是 123 ，此时数据库就要保存为 加密后的 202cb962ac59075b964b07152d234b70
         *              这样才能对应得上
         */
        User user = userRepository.findByUsernameAndAndPassword(username, MD5Utils.code(password));

        return user;
    }
}
