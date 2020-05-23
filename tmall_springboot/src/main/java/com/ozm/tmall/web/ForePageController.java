package com.ozm.tmall.web;

import com.ozm.tmall.entity.pojo.Order;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpSession;

//前台路径控制器
@Controller
public class ForePageController {
    //首页
    @GetMapping(value="/")
    public String index(){
        return "redirect:home";
    }
    @GetMapping(value="/home")
    public String home(){
        return "fore/home";
    }

    //注册页
    @GetMapping(value="/register")
    public String register(){
        return "fore/register";
    }

    //注册成功页
    @GetMapping(value="/registerSuccess")
    public String registerSuccess(){
        return "fore/registerSuccess";
    }

    //登录页面
    @GetMapping(value="/login")
    public String login(){
        return "fore/login";
    }

    //登出页面 退出的时候，通过shiro的校验 subject.logout 退出。
    @GetMapping(value="/forelogout")
    public String logout(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated())
            subject.logout(); //默认清理Session
        //session.removeAttribute("user");
        return "redirect:home";
    }

    //商品页面
    @GetMapping(value="/product")
    public String product(){
        return "fore/product";
    }

    //分类页面
    @GetMapping(value="/category")
    public String category(){
        return "fore/category";
    }

    //搜索结果页面
    @GetMapping(value="/search")
    public String searchResult(){
        return "fore/search";
    }


    //立即购买页面
    @GetMapping(value="/buy")
    public String buy(){
        return "fore/buy";
    }

    //查看购物车页面
    @GetMapping(value="/cart")
    public String cart(){
        return "fore/cart";
    }

    //支付页面
    @GetMapping(value="/alipay")
    public String alipay(){
        return "fore/alipay";
    }
    //支付成功页面
    @GetMapping(value = "/payed")
    public String payed(){
        return "fore/payed";
    }

    //我的订单页，即已购买
    @GetMapping(value="/bought")
    public String bought(){
        return "fore/bought";
    }


    //确认收货页面
    @GetMapping(value="/confirmPay")
    public String confirmPay(){
        return "fore/confirmPay";
    }

    //确认收货页面中的确认支付页面
    @GetMapping(value="/orderConfirmed")
    public String orderConfirmed(){
        return "fore/orderConfirmed";
    }


    //评价页面
    @GetMapping(value="/review")
    public String review(){
        return "fore/review";
    }

}
