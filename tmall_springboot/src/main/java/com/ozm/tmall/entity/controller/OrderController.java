package com.ozm.tmall.entity.controller;

import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.service.OrderItemService;
import com.ozm.tmall.entity.service.OrderService;
import com.ozm.tmall.util.Page4Navigator;
import com.ozm.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @GetMapping("/orders")
    public Page4Navigator<Order> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        //请求订单数据
        Page4Navigator<Order> page =orderService.list(start, size, 5);
        //为Order对象注入OrderItem数据
        orderItemService.fill(page.getContent());
//      removeOrderFromOrderItem，它的作用是把订单里的订单项的订单属性设置为空。。。
//      比如有个 order, 拿到它的 orderItems， 然后再把这些orderItems的order属性，设置为空。
//      为什么要做这个事情呢？ 因为SpringMVC ( springboot 里内置的mvc框架是 这个东西)的 RESTFUL 注解，在把一个Order转换为json的同时，
//      会把其对应的 orderItems 转换为 json数组， 而 orderItem对象上有 order属性， 这个order 属性又会被转换为 json对象，然后这个order 下又有 orderItems 。。。
//      就这样就会产生无穷递归，系统就会报错了。
//      所以这里采用 removeOrderFromOrderItem 把 OrderItem的order设置为空就可以了。
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }


    //发货 订单的增加和删除功能交由前台完成，后台不提供
    // var url =  "deliveryOrder/"+order.id;
    @PutMapping("/deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable int oid) throws IOException {
        Order o = orderService.get(oid);
        //添加发货时间
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return Result.success();
    }

}
