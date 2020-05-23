package com.ozm.tmall.entity.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.elasticsearch.annotations.Document;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="product")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
//告诉es如何匹配此类
@Document(indexName = "tmall_springboot",type = "product")
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @ManyToOne
    @JoinColumn(name="cid")
    private Category category;

    //如果既没有指明 关联到哪个Column,又没有明确要用@Transient忽略，那么就会自动关联到表对应的同名字段
    private String name; //产品名称
    private String subTitle; //小标题
    private float originalPrice;//原始价格
    private float promotePrice;//优惠价格
    private int stock;//库存
    private Date createDate;//创建日期

    //产品图片对象，因为可能一个产品包含多个图片对象，所以这里接收的是序列化的图片对象
    //该字段非数据库所拥有
    @Transient
    private ProductImage firstProductImage;


    //前端页面显示属性
    @Transient
    private List<ProductImage> productSingleImages; //单个产品图片集合
    @Transient
    private List<ProductImage> productDetailImages; //详情产品图片集合
    @Transient
    private int reviewCount; //销量
    @Transient
    private int saleCount; //累计评价


    public void setProductSingleImages(List<ProductImage> productSingleImages) {
        this.productSingleImages = productSingleImages;
    }

    public void setProductDetailImages(List<ProductImage> productDetailImages) {
        this.productDetailImages = productDetailImages;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }


    public List<ProductImage> getProductSingleImages() {
        return productSingleImages;
    }

    public List<ProductImage> getProductDetailImages() {
        return productDetailImages;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getSaleCount() {
        return saleCount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public float getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(float promotePrice) {
        this.promotePrice = promotePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ProductImage getFirstProductImage() {
        return firstProductImage;
    }

    public void setFirstProductImage(ProductImage firstProductImage) {
        this.firstProductImage = firstProductImage;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", category=" + category + ", name=" + name + ", subTitle=" + subTitle
                + ", originalPrice=" + originalPrice + ", promotePrice=" + promotePrice + ", stock=" + stock
                + ", createDate=" + createDate + ", firstProductImage=" + firstProductImage + ", reviewCount="
                + reviewCount + ", saleCount=" + saleCount + "]";
    }


}
