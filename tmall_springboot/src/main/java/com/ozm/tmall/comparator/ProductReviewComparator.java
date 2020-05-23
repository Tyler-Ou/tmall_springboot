package com.ozm.tmall.comparator;

import com.ozm.tmall.entity.pojo.Product;

import java.util.Comparator;

//人气比较器
//把评价数量多的放前面
public class ProductReviewComparator implements Comparator<Product> {

    //并不是所有方法都是Comparator接口的而是object类的，一般方法内都会有，所有不用全部实现
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount() - p1.getReviewCount();
    }
}
