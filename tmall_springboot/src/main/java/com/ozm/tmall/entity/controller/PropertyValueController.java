package com.ozm.tmall.entity.controller;


import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.PropertyValue;
import com.ozm.tmall.entity.service.ProductService;
import com.ozm.tmall.entity.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {

    @Autowired
    ProductService productService;
    @Autowired
    PropertyValueService propertyValueService;


    //读出属性列表和对应分类属性下对应产品的属性值
    //逻辑上先用分类获得属性，然后再用分类属性的Id和产品id获得对应的属性值
    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid")int pid) throws Exception{
        Product product = productService.get(pid);
        //初始化数据
        propertyValueService.init(product);
        //获得数据列表
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }

    //更新数据
    @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue bean) throws Exception {
        propertyValueService.update(bean);
        return bean;
    }



}
