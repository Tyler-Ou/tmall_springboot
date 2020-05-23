package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.CategoryDAO;
import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.pojo.Product;
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

import java.util.ArrayList;
import java.util.List;

//JPA:M_Service
@Service //表明该类是一个服务类
//指明当前类的缓存 归 Redis上 key = categories的管理
@CacheConfig(cacheNames="categories")
public class CategoryService {
    /* ------------------       后端          ------------------      */
    @Autowired
    CategoryDAO categoryDao;
    //sort对象来着domain层，通常就是用于放置这个系统中，与数据库中的表，一一对应起来的JavaBean的
    //首先创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询,返回list。

    @Cacheable(key="'categories-all'")
    public List<Category> list() throws Exception{
        //Sort(Sort.Direction direction, String... properties)
        Sort sort = new Sort(Sort.Direction.ASC,"id");
        return categoryDao.findAll(sort);
    }


    //分页处理 参数用于定制分页对象，第一个参数为开始页，第二个参数为数据最大长度，第三个参数要显示的超链数
    //写入redis缓存
    @Cacheable(key = "'categories-page-'+#p0+'-'+#p1")
    public Page4Navigator<Category> list(int start,int size,int navigatePages){
        //要创建JPA的Page对象，首先要通过new PageRequest去创建Pageable，并传入参数
        //PageRequest(int page, int size, Sort sort)
        Sort sort =new Sort(Sort.Direction.ASC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        //最后才通过Pageable对象创建分页对象
        Page<Category> pageFromJPA = categoryDao.findAll(pageable);
        //public Page4Navigator(Page<T> pageFromJPA,int navigatePages)
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //图片上传以及信息添加
    @CacheEvict(allEntries = true)
    //@CachePut(key="'category-one-'+ #p0")
    public void add(Category bean){
        categoryDao.save(bean);
    }

    //信息删除
    @CacheEvict(allEntries = true)
    //@CacheEvict(key="'category-one-'+ #p0")
    public void delete(int id){
        categoryDao.delete(id);
    }

    //获取单个数据
    //表明当前返回值存储到redis的对应key中。
    //(默认redis里没值，就会通过JPA去查询值下次再取时就回到redis上去对应的key-value）
    //参数说明：其中p0为第一个参数
    @Cacheable(key = "'category-one-'+#p0")
    //@CachePut(key="'category-one-'+ #p0")
    public Category get(int id){
      Category c = categoryDao.findOne(id);
      return c;
    }

    //更新某个数据，为了实时更新数据当操作为添加、删除、更新时，则移除当前的redis上的缓存，并
    // 从JPA中重新读取数据再写入到缓存中
    @CacheEvict(allEntries = true)
    public void update(Category bean){
        categoryDao.save(bean);
    }

    /* ------------------       后端          ------------------      */

//    @CacheEvict(allEntries=true)
//    其意义是删除 categories~keys 里的所有的keys.
//    可是呢，下面有各种有一行注释掉的注解。 比如增加，注释掉的注解是：
//    CachePut(key="'category-one-'+ #p0")
//    它的作用是以 category-one-id 的方式增加到 Redis中去。
//    这样做本身其实是没有问题的，而且在 get 的时候，还可以使用，但是最后还是放弃这种做法了，为什么呢？
//    因为，虽然这种方式可以在 redis 中增加一条数据，但是： 它并不能更新分页缓存 categories-page-0-5 里的数据，
//    这样就会出现数据不一致的问题了。 即。在redis 中，有这一条单独数据的缓存，但是在分页数据里，却没有这一条，这样就矛盾了。
//    为了解决这个矛盾，最精细的做法是，同时更新分页缓存里的数据。 因为 redis 不是结构化的数据，它是 “nosql", " +
//     "为了做到 同时更新缓存分页缓存里的数据，会超级的复杂，而且超级容易出错，其开发量也会非常大。
//    那么怎么办呢？ 最后，我们采纳了折中的办法，即，一旦增加了某个分类数据，那么就把缓存里所有分类相关的数据，都清除掉。
//    下一次再访问的时候，一看，缓存里没数据，那么就会从数据库中读出来，读出来之后，再放在缓存里。如此这般，牺牲了一点小小的性能，
//    数据的一致性就得到了保障了。改和删除，都是同一个道理。


















    /* ------------------       前端          ------------------      */

    //将List<Product>中的Product中的category置为Null，在前台中不通过分类对象获得商品信息也行
    public void removeCategoryFromProduct(List<Category> categories){
        for(Category category:categories){
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category){
        List<Product> products =  category.getProducts();
        if(products!=null){
            for(Product product:products){
                product.setCategory(null);
            }
        }

        //置productsByRow中的product中的category为空
        List<List<Product>> productsByRow = category.getProductsByRow();
        if(productsByRow !=null){
            for(List<Product> products1:productsByRow){
                for(Product product:products1){
                    product.setCategory(null);
                }
            }
        }

    }



    /* ------------------       前端          ------------------      */




}
