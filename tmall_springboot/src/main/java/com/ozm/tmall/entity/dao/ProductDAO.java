package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product,Integer> {
    //同样也也是根据Jpa的规范获取Product对象
    Page<Product>  findByCategory(Category category, Pageable pageable);
    //新增一个通过分类查询所有产品的方法
    List<Product> findByCategoryOrderById(Category category);
    //使用关键字搜索返回一个带分页的产品列表，用于搜索结果页
    List<Product> findByNameLike(String keyword, Pageable pageable);


}
