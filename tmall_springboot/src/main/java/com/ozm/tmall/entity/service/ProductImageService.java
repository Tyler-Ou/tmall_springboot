package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.ProductDAO;
import com.ozm.tmall.entity.dao.ProductImageDAO;
import com.ozm.tmall.entity.pojo.OrderItem;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="productImages")
public class ProductImageService {
    //创建ProductImageService，提供CRD。
    //同时还提供了两个常量，分别表示单个图片和详情图片：
    public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired
    ProductImageDAO productImageDAO;

    //添加图片
    @CacheEvict(allEntries=true)
    public void add(ProductImage bean){
        productImageDAO.save(bean);
    }

    //查找单个照片列表
    @Cacheable(key="'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product){
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product,type_single);
    }

    //查找详细照片列表
    @Cacheable(key="'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product){
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product,type_detail);
    }

    @Cacheable(key="'productImages-one-'+ #p0")
    public ProductImage get(int id) {
        return productImageDAO.findOne(id);
    }

    @CacheEvict(allEntries=true)
    //删除图片
    public void delete(int id) {
        productImageDAO.delete(id);
    }



    //设置预览图
    public void setFirstProdutImage(Product product) {
        //取出ProductImage 列表对象 一个Product id对应多个ProductImages对象 ，在乎你传入什么Product
        List<ProductImage> list = listSingleProductImages(product);
        //将取出的ProductImage 对象逐一赋值到对应的Product上
        if (!list.isEmpty()){
            product.setFirstProductImage(list.get(0));
        }else{
            product.setFirstProductImage(new ProductImage());
        }
    }

    //为多个Product对象设置预览图
    // （Ps:listProduct页显示多个Product，所以是为多个Product对象在内部使用setter的方式对Pro~img对象赋值）
    public void setFirstProdutImages(List<Product> products) {
        for (Product product : products)
            setFirstProdutImage(product);
    }
    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProdutImage(orderItem.getProduct());
        }
    }





}
