package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.PropertyValueDAO;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.Property;
import com.ozm.tmall.entity.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="propertyValues")
public class PropertyValueService {

    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueDAO propertyValueDAO;

    //这个方法的作用是初始化PropertyValue。 为什么要初始化呢？
    // 因为对于PropertyValue的管理，没有增加，只有修改。
    // 所以需要通过初始化来进行自动地增加，以便于后面的修改。
    public void init(Product product){
        //通过分类获得分类属性
        List<Property> properties = propertyService.listByCategory(product.getCategory());
        //通过分类属性的id和产品id获得对应的属性值
        for(Property property:properties){
        //PropertyValue getByPropertyAndProduct(Property property, Product product);
            PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            if (null==propertyValue){
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }

        }
    }

    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    @Cacheable(key="'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property,product);
    }

    //数据更新
    @CacheEvict(allEntries=true)
    public void update(PropertyValue propertyValue){
         propertyValueDAO.save(propertyValue);
    }



}
