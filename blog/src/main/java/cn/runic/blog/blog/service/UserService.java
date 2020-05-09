package cn.runic.blog.blog.service;

import cn.runic.blog.blog.pojo.User;

public interface UserService  {

    //用户登录de 校验
    User checkUser(String username,String password);


}
