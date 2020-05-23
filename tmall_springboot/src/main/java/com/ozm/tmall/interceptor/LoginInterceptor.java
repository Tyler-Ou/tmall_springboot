package com.ozm.tmall.interceptor;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* 登录检查
*
* */
public class LoginInterceptor implements HandlerInterceptor {
    //请求前拦截
    //a. 不需要登录也可以访问的
    //如：注册，登录，产品，首页，分类，查询等等
    //b. 需要登录才能够访问的
    //如：购买行为，加入购物车行为，查看购物车，查看我的订单等等
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("user");
        //这里判断由Shiro校验，登录时ubject.isAuthenticated()为true
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) { //判断由shiro校验的身份是否通过认证
            response.sendRedirect("login");
            return false;
        }
        return true;
//            if (user==null){
//                //未登录
//                response.sendRedirect("login");
//                return false;
//            }else{
//                //已登录,放行请求
//                return true;
//            }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
