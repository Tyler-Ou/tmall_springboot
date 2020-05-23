package com.ozm.tmall.entity.controller;

import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.service.ProductImageService;
import com.ozm.tmall.entity.service.ProductService;
import com.ozm.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    //读出Product数据
    @GetMapping("/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid,
    @RequestParam(value = "start", defaultValue = "0") int start,
    @RequestParam(value = "size", defaultValue = "5")int size) throws Exception{
        start=start<0?0:start;
        Page4Navigator<Product> page  = productService.list(cid,start,size,5);
        //对ProductImages进行内部赋值
        productImageService.setFirstProdutImages(page.getContent());
        return page;
    }

    //添加Product数据
    //添加数据 接收从前台传过来的json数据
    @PostMapping("/products")
    public Product add(@RequestBody Product bean)throws Exception{
        bean.setCreateDate(new Date());
        productService.add(bean);
        return bean;
    }

    //删除数据
    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id")int id, HttpServletRequest request)
            throws Exception{
            productService.delete(id);
            return null;
    }

    //更新数据
    @PutMapping("/products")
    public Object update(@RequestBody Product bean) throws Exception {
        productService.update(bean);
        return bean;
    }

    //获取单个数据
    @GetMapping("/products/{id}")
    public Product get(@PathVariable("id") int id) throws Exception {
        Product bean=productService.get(id);
        return bean;
    }



}
