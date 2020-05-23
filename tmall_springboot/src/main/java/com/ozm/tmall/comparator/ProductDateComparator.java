package com.ozm.tmall.comparator;

import com.ozm.tmall.entity.pojo.Product;

import java.util.Comparator;

//新品选择器
//把创建日期晚的放前面
public class ProductDateComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        //由于创建时间为时间戳，所以需要compareTo来比较
        //p1比p2小时则返回一个小于0的值，反之p1比p2大，则返回一个大于0的值
        return  p1.getCreateDate().compareTo(p2.getCreateDate());
    }
}
