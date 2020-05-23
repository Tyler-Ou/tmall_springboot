package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.PropertyDAO;
import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.pojo.Property;
import com.ozm.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="properties")
public class PropertyService {
    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;

    @CacheEvict(allEntries=true)
    public void add(Property bean) {
        propertyDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id){
        propertyDAO.delete(id);
    }

    @Cacheable(key="'properties-one-'+ #p0")
    public Property get(int id){
        return  propertyDAO.findOne(id);
    }

    @CacheEvict(allEntries=true)
    public void update(Property bean){
        propertyDAO.save(bean);
    }

    //返回自定义分页对象用于在控制器上返回数据
    @Cacheable(key="'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid,int start,int size,int navigatePages){
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.ASC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        //该方法虽然没有实现体，但是按照JPA的规范，依然能找到对应的<Property>并且带分页的page对象
        Page<Property> pageFromJPA =propertyDAO.findByCategory(category,pageable);
        return new Page4Navigator<Property>(pageFromJPA,navigatePages);
    }

    //PropertyService, 增加通过分类获取所有属性集合的方法
    @Cacheable(key="'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category){
        return propertyDAO.findByCategory(category);
    }







}
