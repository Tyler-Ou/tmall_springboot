package com.ozm.tmall.comparator;

import com.ozm.tmall.entity.pojo.Product;

import java.util.Comparator;

//综合比较器
//把 销量x评价 高的放前面
public class ProductAllComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        //如果p2比p1大则更值得推荐，反之
        return p2.getReviewCount()*p2.getSaleCount()-p1.getReviewCount()*p1.getSaleCount();

    }
}
