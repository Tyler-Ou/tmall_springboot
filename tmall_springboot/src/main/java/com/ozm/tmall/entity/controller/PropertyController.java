package com.ozm.tmall.entity.controller;

import com.ozm.tmall.entity.pojo.Property;
import com.ozm.tmall.entity.service.PropertyService;
import com.ozm.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    //映射属性分类，返回page对象用于分页
    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid,
     @RequestParam(value = "start",defaultValue = "0") int start,
     @RequestParam(value = "size",defaultValue = "5") int size)throws Exception{
        start = start<0?0:start;
        Page4Navigator<Property> page = propertyService.list(cid,start,size,5);
        return page;
    }

    @GetMapping("/properties/{id}")
    public Property get(@PathVariable("id") int id) throws Exception {
        Property bean=propertyService.get(id);
        return bean;
    }


    //当提交的数据是json或对象的形式时，使用RequestBody来接受
    @PostMapping("/properties")
    public Object add(@RequestBody Property bean) throws Exception {
        System.out.println("bean="+bean);
        propertyService.add(bean);
        return bean;
    }

    //删除数据
    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)throws Exception{
        propertyService.delete(id);
        return null;
    }

    //更新数据,前端中传入一个bean
    @PutMapping("/properties")
    public Object update(@RequestBody Property bean) throws Exception{
        propertyService.update(bean);
        return bean;
    }



}
