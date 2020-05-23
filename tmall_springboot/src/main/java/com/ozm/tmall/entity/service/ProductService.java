package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.ProductDAO;
import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.ProductImage;
import com.ozm.tmall.es.ProductESDAO;
import com.ozm.tmall.util.Page4Navigator;
import com.ozm.tmall.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductService {
    /* ------------------       后端          ------------------      */
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    //添加es的支持
    @Autowired
    ProductESDAO productESDAO;


    //加载数据
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA =productDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //添加数据
    public void add(Product bean){
        productDAO.save(bean);
        //添加es支持，即除了dao的支持外还需要将数据通过ElasticsearchRepository同步到es上
        productESDAO.save(bean);
    }

    //删除数据 按id来删除数据
    public void delete(int id){
        productDAO.delete(id);
        productESDAO.delete(id);
    }


    //更新数据 传入整个bean
    public void update(Product product){
        productDAO.save(product);
        productESDAO.save(product);
    }

    //获取单个数据
    public Product get(int id) {
        return productDAO.findOne(id);
    }

    /* ------------------       后端          ------------------      */

    /* ------------------       前端          ------------------      */
    //1. 为分类填充产品集合
    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }
    public void fill(Category category) {
        //pringboot 的缓存机制是通过切面编程 aop来实现的。
        //从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
        //所以要通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存。
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }
    //为多个分类填充推荐产品集合，即把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {//将List<Product> 进行分拆装入List<List<Product>>
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }


    //实现为产品设置销量和评价数量的方法
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }


    //取出搜索结果
    public List<Product> search(String keyword, int start, int size) {
        /* ------------------       es搜索         ------------------      */
        //初始化搜索结果到es上
        initDatabase2ES(start,size);
        //使用FunctionScoreQueryBuilder优化es搜索结果
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                .scoreMode("sum")
                .setMinScore(10);
        Sort sort  = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start, size,sort);
        //传入es搜索
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();

        /* ------------------       es搜索         ------------------      */


        /* ------------------       普通模糊搜索         ------------------      */
        //Sort sort = new Sort(Sort.Direction.DESC, "id");
        //Pageable pageable = new PageRequest(start, size, sort);
        //List<Product> products =productDAO.findByNameLike("%"+keyword+"%",pageable);
        //return products;
        /* ------------------       普通模糊搜索          ------------------      */
    }

    /* ------------------       前端          ------------------      */



    /* ------------------       es          ------------------      */
    //初始化数据到es上
    private void initDatabase2ES(int start, int size) {
       //搜索前试看es中是否存在相应的数据，如果不存在则通过dao从数据库中获取数据然后初始化到es上
        Pageable pageable = new PageRequest(start,size);
        Page<Product> productES = productESDAO.findAll(pageable);
        if (productES.getContent().isEmpty()){
            //数据不存在时，通过dao的方式获取数据并存在es上
            List<Product> products = productDAO.findAll();
            for (Product product:products){
                productESDAO.save(product);
            }
        }

    }

    /* ------------------       es          ------------------      */


}






