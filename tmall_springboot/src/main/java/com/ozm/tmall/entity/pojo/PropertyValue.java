package com.ozm.tmall.entity.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name="propertyvalue")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class PropertyValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    //属性值的pid对应产品
    @ManyToOne
    @JoinColumn(name="pid")

    private Product product;
    @ManyToOne

    //属性值的ptid对应属性
    @JoinColumn(name="ptid")
    private Property property;

    private String value;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "PropertyValue [id=" + id + ", product=" + product + ", property=" + property + ", value=" + value + "]";
    }


}
