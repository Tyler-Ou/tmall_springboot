package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.pojo.OrderItem;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer> {
    //一个订单可能包含多个商品
    List<OrderItem> findByOrderOrderByIdDesc(Order order);

    //通过产品找对应的订单数 一个product主键id对应多个Pid
    List<OrderItem> findByProduct(Product product);

    //基于用户对象user，查询没有生成订单的订单项集合
    List<OrderItem> findByUserAndOrderIsNull(User user);


}
