package com.ozm.tmall.config;

import com.ozm.tmall.interceptor.LoginInterceptor;

import com.ozm.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//注册拦截器
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    /*
    *           "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",
                "forebuyone",
                "forebuy",
                "foreaddCart",
                "forecart",
                "forechangeOrderItem",
                "foredeleteOrderItem",
                "forecreateOrder",
                "forepayed",
                "forebought",
                "foreconfirmPay",
                "foreorderConfirmed",
                "foredeleteOrder",
                "forereview",
                "foredoreview"
    * */

    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }
    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加登录拦截器需要登录才可以访问的页面
        registry.addInterceptor(getLoginIntercepter()).
                addPathPatterns("/buy","/alipay","/payed","/cart",
                        "/bought","/confirmPay","/orderConfirmed","/forebuyone",
                        "/forebuy","/foreaddCart","/forecart","/forechangeOrderItem",
                        "/foredeleteOrderItem","/forecreateOrder","/forepayed","/forebought",
                        "/foreconfirmPay","/foreorderConfirmed","/foredeleteOrder","/forereview","/foredoreview"
                        );


        //追加渲染拦截器
        registry.addInterceptor(getOtherIntercepter())
                .addPathPatterns("/**");

    }
}
