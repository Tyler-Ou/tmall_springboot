package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageDAO extends JpaRepository<ProductImage,Integer> {
    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
}
