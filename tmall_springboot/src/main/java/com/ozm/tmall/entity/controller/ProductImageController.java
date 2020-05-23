package com.ozm.tmall.entity.controller;

import com.ozm.tmall.entity.dao.ProductImageDAO;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.ProductImage;
import com.ozm.tmall.entity.service.ProductImageService;
import com.ozm.tmall.entity.service.ProductService;
import com.ozm.tmall.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductImageController {

    //获取图片（包含single和detail）
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @GetMapping("/products/{pid}/productImages")
    //  var url =  "products/"+pid+"/"+this.uri+"?type=single";
    public List<ProductImage> list(@PathVariable("pid")int pid,
                                   @RequestParam("type")String type)throws Exception{
         Product product = productService.get(pid);
         //判断类型是single还是detail
        if (ProductImageService.type_single.equals(type)){ //single
            List<ProductImage> singles =  productImageService.listSingleProductImages(product);
            return singles;
        }else if(ProductImageService.type_detail.equals(type)){ //detail
            List<ProductImage> details = productImageService.listDetailProductImages(product);
            return details;
        }
            return new ArrayList<>();
    }

    //添加图片
    // productImages?type=single&pid=pid~~~
    //MultipartFile image为post提交的图片信息
    @PostMapping("/productImages")
    public Object add(@RequestParam("type")String type,
                      @RequestParam("pid")int pid, MultipartFile image,
                      HttpServletRequest request)throws Exception{
        //获取新增的ProductImages信息，并添加到数据库中
        ProductImage bean = new ProductImage();
        Product product = productService.get(pid);
        bean.setProduct(product);
        bean.setType(type);
        productImageService.add(bean);

        //将图片文件保存在本地
        String folder = "img/";
        if (ProductImageService.type_single.equals(bean.getType())){
                folder = folder + "productSingle";
        }else{
                folder = folder + "productDetail";
        }

        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 217, 190, f_middle);
        }

        return bean;

    }
    //删除图片
    @DeleteMapping("/productImages/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
        ProductImage bean = productImageService.get(id);
        productImageService.delete(id);

        String folder = "img/";
        if(ProductImageService.type_single.equals(bean.getType()))
            folder +="productSingle";
        else
            folder +="productDetail";

        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        file.delete();
        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.delete();
            f_middle.delete();
        }

        return null;
    }




}
