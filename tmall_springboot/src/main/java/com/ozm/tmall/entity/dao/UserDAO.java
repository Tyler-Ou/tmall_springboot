package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    //用于注册时检验用户是否存在
    User findByName(String name);
    //登录时通过账号和密码获取用户
    User getByNameAndPassword(String name, String password);
}
