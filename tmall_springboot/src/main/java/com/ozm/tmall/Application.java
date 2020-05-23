package com.ozm.tmall;

import com.ozm.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//启动类 代替自动生成的 TmallSpringbootApplication.java
//启动缓存用于redis服务器
@EnableCaching
//启动es 为es和jpa分别指定不同的包名，否则会出错
@EnableElasticsearchRepositories(basePackages = "com.ozm.tmall.es")
//ex要JPA操作的对象
@EnableJpaRepositories(basePackages = {"com.ozm.tmall.entity.dao", "com.ozm.tmall.entity.pojo"})
@SpringBootApplication
public class Application {
    //检查Redis是否启动，即检查6379 redis服务器的端口是否启动
    //如果未启动，那么就会退出 springboot。
    //静态代码块 随着类的加载而执行，而且只执行一次
    static {
        PortUtil.checkPort(6379,"Redis 服务端",true);
        PortUtil.checkPort(9300,"ElasticSearch 服务端",true);
        PortUtil.checkPort(5601,"Kibana 工具", true);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
