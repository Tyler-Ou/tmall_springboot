package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.OrderItemDAO;
import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.pojo.OrderItem;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//添加Redis缓存，并由对应键来控制该类
@CacheConfig(cacheNames = "orderItems")
public class OrderItemService {

    //注入OrderItem数据
    //逻辑通过OrderItemsDAO取出数据再放在Order的OrderItem上
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi :orderItems) {
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();
            totalNumber+=oi.getNumber();
            productImageService.setFirstProdutImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
    }

    @Cacheable(key="'orderItems-oid-'+ #p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    @Cacheable(key="'orderItems-pid-'+ #p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }


    //根据订单的pid 获得产品的销量
    public int getSaleCount(Product product){
        List<OrderItem> list = orderItemDAO.findByProduct(product);
        int result = 0;
        //订单非空判断
        for(OrderItem orderItem:list){
            if (null!=orderItem){
                if(null!= orderItem.getOrder() && null!=orderItem.getOrder().getPayDate()){
                    result +=orderItem.getNumber(); //统计购买数量
                }
            }
        }

        return result;

    }

    //基于用户查询订单项中没有生成订单的订单项
    @Cacheable(key="'orderItems-uid-'+ #p0.id")
    public List<OrderItem> listByUser(User user){
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }

    @CacheEvict(allEntries = true)
    //更新数据
    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }
    @CacheEvict(allEntries = true)
    //添加数据
    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    @Cacheable(key = "'orderItems-one'+#p0")
    //获得数据
    public OrderItem get(int id){
       return orderItemDAO.findOne(id);
    }

    @CacheEvict(allEntries = true)
    //删除数据
    public void delete(int id){
        orderItemDAO.delete(id);
    }



}
