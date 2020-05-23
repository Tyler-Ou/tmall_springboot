package com.ozm.tmall.es;


import com.ozm.tmall.entity.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//需要和 jpa 的dao ，放在不同的包下，因为 jpa 的dao 做了 链接 redis 的
// 如果放在同一个包下，会彼此影响，出现启动异常。

//ElasticsearchRepository解释：
//Springboot提供了对 ElasticSearch专门的jpa的，就叫叫做 ElasticsearchRepository。
//用来做ElasticSearch JPA操作的api 跟Redis一样，在主入口程序上标上注解
public interface ProductESDAO extends ElasticsearchRepository<Product,Integer> {
}
