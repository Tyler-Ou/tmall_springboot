package com.ozm.tmall.web;

import com.ozm.tmall.comparator.*;
import com.ozm.tmall.entity.pojo.*;
import com.ozm.tmall.entity.service.*;
import com.ozm.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

//前端内容控制器
@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;


    //首页映射
    @GetMapping("/forehome")
    public Object home() throws Exception {
        List<Category> cs= categoryService.list();
        productService.fill(cs);   //1. 通过分类填充产品集合
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    //注册映射
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password  =user.getPassword();
        //字符转义，避免恶意注册 如写入一段Js片段
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        //校验用户是否存在
        boolean exist = userService.isExist(name);
        if (exist){
            String message ="用户名已经被使用,不能使用";
            //失败返回1
            return Result.fail(message);
        }else{
            //成功返回0
            //注册时候的时候，会通过随机方式创建盐
            //并且加密算法采用 "md5", 除此之外还会进行 2次加密。
            //这个盐，如果丢失了，就无法验证密码是否正确了，所以会数据库里保存起来。
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();//盐
            int times = 2; //加密次数
            String algorithmName = "md5";//加密方式
            String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();
            user.setSalt(salt);
            user.setPassword(encodedPassword);//设置密码为盐+两次MD5加密后的结果
            //将user更新到数据库上
            userService.add(user);
            return Result.success();
        }
    }

    //登录映射  登录的时候， 通过 Shiro的方式进行校验
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session){
        //校验用户
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);
//-----------------------新方法使用shiro的Subject对象进行校验，校验成功后创建Session------------------------
        //获取当前活动的对象Subject  Subject 在 Shiro 这个安全框架下， Subject 就是当前用户
        Subject subject = SecurityUtils.getSubject();
        //将账号名和密码送入shiro   UsernamePasswordToken 当中
        UsernamePasswordToken token = new UsernamePasswordToken(name,userParam.getPassword());
        try {
            subject.login(token); //在这传入token供Realm验证(实际上在这里已经调用了shiroConfiguration，其对应的Realm在监控当前login)
            User user = userService.getByName(name);
            //校验没错，没有触发异常，即可将用户保存在Session上
            session.setAttribute("user", user);
            return Result.success();
        }catch (AuthenticationException e) {
            String message ="账号密码错误";
            return Result.fail(message);
        }


//-----------------------旧方法使用通过用户名和密码获取到User对象,.获取成功后创建Session------------------------
//        User user =userService.get(name,userParam.getPassword());
//        if(null==user){
//            String message ="账号密码错误";
//            return Result.fail(message);
//        }
//        else{
//            session.setAttribute("user", user);
//            return Result.success();
//        }

    }

    //产品页映射
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid){
//        1. 获取参数pid
//        2. 根据pid获取Product 对象product
//        3. 根据对象product，获取这个产品对应的单个图片集合
//        4. 根据对象product，获取这个产品对应的详情图片集合
//        5. 获取产品的所有属性值
//        6. 获取产品对应的所有的评价
//        7. 设置产品的销量和评价数量
//        8. 把上述取值放在 map 中
//        9. 通过 Result 把这个 map 返回到浏览器去
        //从数据库中获取值后，赋值给product对象用于返回
        Product product = productService.get(pid);
        List<ProductImage> images_single = productImageService.listSingleProductImages(product);
        List<ProductImage> images_detail = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(images_single);
        product.setProductDetailImages(images_detail);
        List<PropertyValue> propertyValues = propertyValueService.list(product);//所有属性值
        List<Review> reviews = reviewService.list(product);//对应商品所有评价
        productService.setSaleAndReviewNumber(product); //销量和评价数量
        productImageService.setFirstProdutImage(product);//设置预览图
        //将数据打包封装返回前端
        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", propertyValues);//里面包含property对象
        map.put("reviews", reviews);
        return Result.success(map);
    }


    //检查登录映射（模态登录） 使用shiro检查登录
    @GetMapping("/forecheckLogin")
    public Object checkLogin( HttpSession session) {
        //使用Shiro的Subject对象
        Subject subject = SecurityUtils.getSubject(); //当前活动的对象（即当前用于校验的user）
        if (subject.isAuthenticated()){//通过验证
            return Result.success();
        }else{
            return Result.fail("未登录");
        }
//        User user =(User)  session.getAttribute("user");
//        if(null!=user)
//            return Result.success();
//        return Result.fail("未登录");

    }


    //分类页面映射,通过分类找到对应商品
    @GetMapping("/forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort) {
        Category category = categoryService.get(cid);
        //通过分类找到对应商品
        //新增一个通过分类查询所有产品的方法
        //List<Product> findByCategoryOrderById(Category category);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        //将category中的list<Product>中的Product中的category置空，避免重复Json化
        categoryService.removeCategoryFromProduct(category);
        //获取Product列表 List<Product>  通过getProducts获取列表
        //sort为空时即不排序
        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        return category;
    }



    //搜索结果
    @PostMapping("/foresearch")
    public Object search(@RequestParam("keyword") String keyword){
        //从产品列表中取出对应keyword的产品
        if(null==keyword)
            keyword = "";
        //控制搜索结果长度，使用分页Pageable对象和模糊关键字查找对应的产品List
        List<Product> ps= productService.search(keyword,0,20);
        //返回预览图和销量、评价数量用于前端搜索结果页面显示
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    //立即购买映射 接收Pid和num ,从Session中取User对象
    @GetMapping("/forebuyone")
    public Object buyone(@RequestParam("pid") int pid, @RequestParam("num") int num, HttpSession session){
        //新增订单项OrderItem， 新增订单项要考虑两个情况
        //1、如果订单项存在某个商品的OrderItem还没有生成订单，
        // 并且存在于购物车中，就需要在对应的OrderItem基础上调整数据
        //2、如果不存在某个产品对应的OrderItem，那么就新增一个订单项OrderItem
        return buyoneAndAddCart(pid,num,session);
    }

    //加入购物车隐射,其逻辑和立即购物时一样的，
    //都是从数据库中校验某个用户加入购物车或者立即的产品有没有订单项，如果没有则添加
    //如果有订单项则获取该对象然后修改里面的数量等等
    //其核心都是为了生成订单项，用于往后生成订单的逻辑作铺垫
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }


    //返回订单项id,用于跳转到对应的订单项页中，利用对应的订单项id生成购买的订单
    private int buyoneAndAddCart(int pid, int num, HttpSession session) {

        Product p = productService.get(pid);
        User user = (User)session.getAttribute("user");
        boolean found = false; //默认找不到
        int oiid = 0;
        //第一种情况
        //基于用户对象user，查询没有生成订单的订单项集合
        //找到对应的订单项然后进行操作
        List<OrderItem> orderItemList = orderItemService.listByUser(user);
        for(OrderItem orderItem:orderItemList){
            //如果在对应用户对应商品中找到相同的订单项，则对该订单项进行操作
            if (orderItem.getProduct().getId()==p.getId()){
                orderItem.setNumber(orderItem.getNumber()+num);
                //将对应的orderItem对象更新到数据库上
                orderItemService.update(orderItem);
                found = true;
                oiid = orderItem.getId();
                break;
            }
        }

        //第二种情况 对应用户的购物车内没有找到对应产品的订单项，那么就需要生成一个订单项
        if (!found){
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setProduct(p);
            orderItem.setNumber(num);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }

        return oiid;

    }

    //利用Oiid获得对应订单项，在订单页中读出对应数据
    @GetMapping("/forebuy")
    public Object buy(String[] oiid,HttpSession session){
        //这里要用字符串数组试图获取多个oiid，而不是int类型仅仅获取一个oiid?
        // 因为根据购物流程环节与表关系，结算页面还需要显示在购物车中选中的多条OrderItem数据，
        // 所以为了兼容从购物车页面跳转过来的需求，要用字符串数组获取多个oiid
        List<OrderItem> orderItems = new ArrayList<>(); //当商品在购物车被多选立即购物时，就需要接收多个OrderItem
        float total = 0;//用于计算总价
        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total += oi.getProduct().getPromotePrice()*oi.getNumber();
            //将当前的OrderItem添加到List<OrderItem>中
            orderItems.add(oi);
        }
        //为每个OrderItem对应的每个产品设置预览图，按顺序，这个设置是单纯的setter
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        //返回的OrderItems中的product上就会有上一部设置的预览图
        session.setAttribute("ois", orderItems);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);


    }


    //从购物车中获取订单项信息
    @GetMapping("/forecart")
    public Object cart(HttpSession session){
        //从数据库中读出对应用户的订单项
        User user = (User)session.getAttribute("user");
        // List<OrderItem> findByUserAndOrderIsNull(User user);
        List<OrderItem> orderItemList = orderItemService.listByUser(user);
        //为每个订单项中的商品设置预览图
        productImageService.setFirstProdutImagesOnOrderItems(orderItemList);
        return orderItemList;
    }


    //从购物车中删除对应的订单项
    @GetMapping("/foredeleteOrderItem")
    public Object deleteOrderItem(@RequestParam("oiid") int oiid,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user) //非登录状态
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }

    //从购物车中更新订单数量 var url = "forechangeOrderItem?pid="+pid+"&num="+num;
    @GetMapping("/forechangeOrderItem")
    public Object changeOrderItem(@RequestParam("pid")int pid,@RequestParam("num")int num,HttpSession session){
        //通过pid和user从List<orderItem>中读出对应的orderItem
        Product product = productService.get(pid);
        User user  = (User)session.getAttribute("user");
        //检查登录状态
        if(null==user)
            return Result.fail("未登录");
        List<OrderItem> orderItemList = orderItemService.listByUser(user);
        for(OrderItem orderItem:orderItemList){
            if(orderItem.getProduct().getId()==product.getId()){
                orderItem.setNumber(num);
                //将数量更新在数据上
                orderItemService.update(orderItem);
                break;
            }
        }
        return Result.success();
    }

    //创建订单
    @PostMapping("/forecreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
//        1. 从session中获取user对象
//        2. 根据当前时间加上一个4位随机数生成订单号
//        3. 根据上述参数，创建订单对象
//        4. 把订单状态设置为等待支付
//        5. 从session中获取订单项集合 ( 在结算功能的ForeRESTController.buy() ，订单项集合被放到了session中 )
//        7. 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
//        8. 统计本次订单的总金额
//        9. 返回总金额 和oid方便跳转到付款页面
        User user = (User)session.getAttribute("user");
        if (null==user){
            return Result.fail("未登录");
        }

        String orderCode =  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user); //设置user对象，用于设置uid
        order.setStatus(OrderService.waitPay);
        //将每个orderItem的order设置为当前order
        List<OrderItem> orderItemList = (List<OrderItem>)session.getAttribute("ois");
        float total =orderService.add(order,orderItemList);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);

    }

    //支付成功页面
    @GetMapping("/forepayed")
    public Object payed(@RequestParam("oid")int oid){
        //更新支付时间到订单上和更改状态带待发货
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    //获取已购买的订单列表
    @GetMapping("/forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        //设置rder底下的OrderItem的order为Null，避免重复json化。进入死循环
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    //确认收货的前提是需要在后台将订单状态设置为已发货才行
    //我的订单确认收货页 <a v-if="o.status=='waitConfirm'" :href="'confirmPay?oid='+o.id">
    @GetMapping(value = "/foreconfirmPay")
    public Object confirmPay(int oid){
        Order order = orderService.get(oid);
        //为order填充orderItem数据
        orderItemService.fill(order);
        //移除当前order重复的List<OrderItem>下的orderItem的order
        orderService.cacl(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }


    //我的订单确认收货页的确认支付页
    // var url =  foreorderConfirmed"+"?oid="+oid;
    @GetMapping(value = "/foreorderConfirmed")
    public Object orderConfirmed(@RequestParam("oid") int oid) {
        //获取订单，并且将订单状态设置为待评价，并且设置支付时间,最后更新到数据库上
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return order;
    }

    //删除订单 ，当然这个删除并不是真的删除而是将状态设置为delete
    @PutMapping(value = "/foredeleteOrder")
    public Object deleteOrder(@RequestParam("oid") int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    //请求评价（请求订单中订单项的评价）
    @GetMapping(value = "/forereview")
    public Object review(@RequestParam("oid") int oid){
        //获得订单项
//        1 获取参数oid
//        2 根据oid获取订单对象o
//        3 为订单对象填充订单项
//        4 获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片了。（这里没有对订单里的每种产品都评价，因为复杂度就比较高了，初学者学起来太吃力，有可能就放弃学习了，所以考虑到学习的平滑性，就仅仅提供对第一个产品的评价）
//        5 获取这个产品的评价集合
//        6 为产品设置评价数量和销量
//        7 把产品，订单和评价集合放在map上
//        8 通过 Result 返回这个map
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        Product product = order.getOrderItems().get(0).getProduct();
        //通过产品获得评价列表
        List<Review> reviews = reviewService.list(product);
        //为当前的产品对象设置销量和评价数量的，用于前端显示
        productService.setSaleAndReviewNumber(product);
        Map<String,Object> map = new HashMap<>();
        map.put("p", product);
        map.put("o", order);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    //提交评价  var url =  "foredoreview?oid="+vue.o.id+"&pid="+vue.p.id+"&content="+vue.content;
    @PostMapping(value = "/foredoreview")
    public Object doreview(@RequestParam("oid")int oid,
                           @RequestParam("pid")int pid,
                           @RequestParam("content")String content,
                           HttpSession session
                           ){
        //评价完成后获得订单对象，设置一下订单状态
        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        //1、将状态更新到数据库中
        orderService.update(order);
        //2、过滤内容
        content = HtmlUtils.htmlEscape(content);
        //3、  将uid和pid、content写入review表中
        //基本添加对象到数据库中意义都是new，然后设置值再更新到数据库上
        Review review = new Review();
        Product product = productService.get(pid);
        User user = (User)session.getAttribute("user");
        review.setProduct(product);
        review.setContent(content);
        review.setCreateDate(new Date());
        review.setUser(user);
        //将封装好的review更新到数据库上
        reviewService.add(review);
        return Result.success();

    }




}
