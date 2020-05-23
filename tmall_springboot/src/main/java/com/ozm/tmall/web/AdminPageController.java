package com.ozm.tmall.web;

//后台管理页面跳转专用控制器。
//因为是做前后端分离，所以数据是通过 RESTFUL接口来取的，而在业务上，
//除了 RESTFUL 服务要提供，还要提供页面跳转服务，
//所以所有的后台页面跳转都放在 AdminPageController
//这个控制器里。 而RSTFUL 专门放在 Category 对应的控制器 CategoryController.java 里面。

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
//后台管理系统跳转控制器
public class AdminPageController {
    //创建一个admin映射
    @GetMapping("/admin")
    public String admin(){
        //客户端跳转
        return "redirect:admin_category_list";
    }
    //访问地址 admin_category_list 就会访问 admin/listCategory.html 文件。
    @GetMapping("/admin_category_list")
    public String listCategory(){
        //服务端跳转
        return "admin/listCategory";
    }

    //跳转到编辑页面
    @GetMapping("/admin_category_edit")
    public String editCategory(){
        return "admin/editCategory";
    }
    //跳转到分类属性管理页面
    @GetMapping(value="/admin_property_list")
    public String listProperty(){
        return "admin/listProperty";

    }
    //跳转到属性编辑页面
    @GetMapping(value="/admin_property_edit")
    public String editProperty(){
        return "admin/editProperty";
    }

    //跳转到产品页面
    @GetMapping(value = "/admin_product_list")
    public String listProduct(){
        return "admin/listProduct";
    }

    //跳转到产品编辑页面
    @GetMapping(value = "/admin_product_edit")
    public String editProduct(){
        return "admin/editProduct";
    }

    //从产品编辑页面跳转到图片管理页面
    @GetMapping(value="/admin_productImage_list")
    public String listProductImage(){
        return "admin/listProductImage";
    }

    //从产品编辑页面跳转到属性管理页面
    @GetMapping(value="/admin_propertyValue_edit")
    public String editPropertyValue(){
        return "admin/editPropertyValue";

    }

    //映射到用户管理页面
    @GetMapping(value = "/admin_user_list")
    public String listUser(){
        return "admin/listUser";
    }

    //映射到订单管理页面
    @GetMapping(value = "/admin_order_list")
    public String listOrder(){
        return "admin/listOrder";
    }

}
