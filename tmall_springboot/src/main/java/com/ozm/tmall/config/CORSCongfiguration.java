package com.ozm.tmall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//配置类，用于允许所有的请求都跨域。
//因为是二次请求，第一次是获取 html 页面，
//第二次通过 html 页面上的 js 代码异步获取数据，
//一旦部署到服务器就容易面临跨域请求问题，所以允许所有访问都跨域，
//就不会出现通过 ajax 获取数据获取不到的问题了。

/*跨域：浏览器从一个域名的网页去请求另一个域名的资源时，域名、端口、协议任一不同，都是跨域*/

/*拓展SpringMvc的功能属于WebMvcConfigurerAdapter类型的范畴
* 根据文档要求：1、标注Configuration注解
* 2、继承WebMvcConfigurerAdapter类型
* 3、不能标注@EnableWebMvc，最后重写相应方法
* */
@Configuration
public class CORSCongfiguration extends WebMvcConfigurerAdapter {
    /*添加允许跨域请求的功能重写的方法为addCorsMappings*/

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");

    }
}
