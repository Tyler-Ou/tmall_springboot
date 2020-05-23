package com.ozm.tmall.entity.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import javax.persistence.*;

//一个分类对应多个分类属性，外键cid指向Category的主键 即可以使用注解ManyToOne

@Entity
@Table(name = "property")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name="cid")//注释本表中指向另一个表的外键
    //在注入时对category的主键id赋值，即为对该property赋值
    private Category category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Property [id=" + id + ", name=" + name + ", category=" + category + "]";
    }





}
