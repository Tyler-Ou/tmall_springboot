package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDAO extends JpaRepository<Review,Integer> {
    //两个查询方法，一个返回某产品对应的评价集合，一个返回某产品对应的评价数量
    List<Review> findByProductOrderByIdDesc(Product product);
    int countByProduct(Product product);
}
