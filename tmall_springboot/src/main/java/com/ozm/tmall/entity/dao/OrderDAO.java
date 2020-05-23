package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order,Integer>{
    //通过用户和订单状态获取对应的订单列表，但是状态又不是 "delete" 的订单。 "delete" 是作为状态调用的时候传进来的
    //获取到的订单列表用于显示在我的订单中
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
