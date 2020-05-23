package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.OrderDAO;
import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.pojo.OrderItem;
import com.ozm.tmall.entity.pojo.User;
import com.ozm.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orders")
public class OrderService {

    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDAO orderDAO;
    @Autowired
    OrderItemService orderItemService;

    @Cacheable(key="'orders-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Order> list(int start,int size,int navigatePages){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page pageFromJpa = orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa,navigatePages);

    }

    //removeOrderFromOrderItem
    public void removeOrderFromOrderItem(List<Order> orders){
        //从列表中取出每个order，获得每个order下的orderItem，再将OrderItem下的Order删除
        for(Order order:orders){
            removeOrderFromOrderItem(order);
        }
    }

    public void removeOrderFromOrderItem(Order order){
        List<OrderItem> orderItems = order.getOrderItems();
        //将list中的OrderItem中的每个Order设置为空
        for (OrderItem orderItem:orderItems){
            orderItem.setOrder(null);
        }
    }

    @Cacheable(key="'orders-one-'+ #p0")
    //获得单个数据
    public Order get(int id){
        return orderDAO.findOne(id);
    }

    @CacheEvict(allEntries=true)
    //如果发货就更新数据
    public void update(Order bean) {
        orderDAO.save(bean);
    }

    //添加为每个订单项添加订单
    @CacheEvict(allEntries=true)
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public float add(Order order, List<OrderItem> ois) {
        float total = 0;
        //添加订单到数据库
        add(order);
        if(false)
            throw new RuntimeException();

        for (OrderItem oi: ois) {
            oi.setOrder(order);
            orderItemService.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return total;
    }

    @CacheEvict(allEntries=true)
    //添加订单
    public void add(Order order){
        orderDAO.save(order);
    }


    //根据用户和订单的对应状态获取商品（状态非delete）
    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order>  listByUserWithoutDelete(User user){
        List<Order> orders =listByUserAndNotDeleted(user);
        //将订单项的数据填充到对应的订单中，然后返回全部信息= 订单项+订单信息
        //order.setTotal(total);
        //order.setOrderItems(orderItems);
        //rder.setTotalNumber(totalNumber);
        orderItemService.fill(orders);
        return orders;
    }

    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order> listByUserAndNotDeleted(User user) {
        //获取状态非delete的订单list
        return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }

    public void cacl(Order o) {
        List<OrderItem> orderItems = o.getOrderItems();
        float total = 0;
        for (OrderItem orderItem : orderItems) {
            total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        o.setTotal(total);
    }





}
