package com.ozm.tmall.entity.pojo;
//因为是做前后端分离，而前后端数据交互用的是 json 格式。
//那么 Category 对象就会被转换为 json 数据。
//而本项目使用 jpa 来做实体类的持久化，jpa 默认会使用 hibernate,
//在 jpa 工作过程中，就会创造代理类来继承 Category ，
//并添加 handler 和 hibernateLazyInitializer 这两个无须 json 化的属性，
//所以这里需要用 JsonIgnoreProperties 把这两个属性忽略掉。

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

/*操作：根据category创建JPA:O */
@Entity
@Table(name = "category")
//忽略无须json化的属性
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Category {

/*                         后端                                        */
    @Id //表明该字段为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //表明该字段为自增字段
    @Column(name = "id" ) //列名为id
    Integer id;

    @Column(name = "name")
    String name;

    //创建字段的setter和getter方法

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /* ------------------       后端          ------------------      */

    /* ------------------       前端          ------------------      */

    //ps:由于Product对象里面也有Category对象,Category对象下也有Product，为了避免springMVC重复json化的过程
    //只需要把products下的Product对象中的category对象中的Product置为null即可，同理productsByRow
    //一个分类下有多个产品
    //根据业务需求，选择性地将重复迭代的对象置为null
    //category-list<Product>-product-category=null
    //order-list<Orderitem>-Orderitem-(order=null)
    @Transient
    List<Product> products;
    //一个分类又对应多个 List<Product>
    //用于首页竖状导航的分类名称右边显示推荐产品列表
    @Transient
    List<List<Product>> productsByRow;


    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }
    public void setProductsByRow(List<List<Product>> productsByRow) {
        this.productsByRow = productsByRow;
    }








    /* ------------------       前端          ------------------      */

}
