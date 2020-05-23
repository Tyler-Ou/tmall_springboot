package com.ozm.tmall.entity.dao;


import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.Property;
import com.ozm.tmall.entity.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {


    List<PropertyValue> findByProductOrderByIdDesc(Product product);
    PropertyValue getByPropertyAndProduct(Property property, Product product);

}
