package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PropertyDAO extends JpaRepository<Property,Integer> {
    //根据分类进行查询,返回的是带Property对象的Page对象
    //1. 这是jpa 的规范，对于条件查询可以按照这种方式来开发
    // 还可以直接在方法的参数上加入分页或排序的参数，比如：
    //Page findByName(String name, Pageable pageable);
    Page<Property> findByCategory(Category category, Pageable pageable);

    //增加通过分类获取所有属性集合的方法
    List<Property> findByCategory(Category category);





}
