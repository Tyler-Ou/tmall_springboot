package com.ozm.tmall.entity.controller;

import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.service.CategoryService;
import com.ozm.tmall.util.ImageUtil;
import com.ozm.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

//JPA:M_Controller
//这个就是专门用来提供 RESTFUL 服务器控制器,用来操作Service
@RestController  //表示这是一个控制器，并且对每个方法的返回值都会直接转换为 json 数据格式。
public class CategoryController {

    @Autowired
    CategoryService categoryService;
//    @GetMapping("/categories")
//    public List<Category> list() throws Exception {
//        return categoryService.list();
//    }

    //分页处理 处理并返回超链数以及相关信息
    /*RequestParam处理的是请求参数，而PathVariable处理的是路径变量,*/
    @GetMapping("/categories")
    public Page4Navigator<Category> list(
            @RequestParam(value = "start",defaultValue = "0")int start,
            @RequestParam(value = "szie",defaultValue = "5")int size)throws Exception{
        start=start<0?0:start;
        //规定最大显示超链数5
        Page4Navigator<Category> page = categoryService.list(start,size,5);
        return  page;
    }

    //分类信息以及图片上传
    @PostMapping("/categories")
    //将formData数据选择性地自动装配到参数上
    public Object list(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        //从请求中读出post请求的formData数据，自动装配name属性到bean上后保存到数据库中
        categoryService.add(bean);
        //将分类图片不保存在本地数据库上，而是选择保存上项目的中
        //接受上传图片，并保存到 img/category目录下
        //文件名使用新增分类的id
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }
    //上传文件，这个有问题 如果上传png他只能保存到缓存当中
    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws IOException {
        File imageFolder= new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,bean.getId()+".jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    //删除信息  返回 null, 会被RESTController 转换为空字符串。
    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") int id,HttpServletRequest request){
        categoryService.delete(id);
        //从文件流中删除对应id路径下的文件
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,id+".jpg");
        file.delete();
        return null;
    }

    //获取单个数据
    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id) throws Exception{
        Category bean = categoryService.get(id);
        return bean;
    }
    //更新某个数据
    @PutMapping("/categories/{id}")
    //发送过来的id参数可自动封装到Category上
    //put的参数要使用request.getParameter获取
    public Category update(Category bean,MultipartFile image,HttpServletRequest httpServletRequest) throws IOException {
            String name = httpServletRequest.getParameter("name");
            bean.setName(name);
            categoryService.update(bean);
            //并将本地文件进行覆盖
            if(image!=null) {
                saveOrUpdateImageFile(bean, image, httpServletRequest);
            }
            return  bean;
    }



}
