# tmall_springboot
基于前后端分离+SpringBoot+BootStrap、Vue.js、JQuery+JPA+Redis的天猫整站

## 一、项目介绍和演示
SPRINGBOOT天猫整站，基于 **前后端分离思想**， 由于该商城高并发的特点，后端框架便使用了方便维护的 **SpringM
VC**、**SpringBoot**框架，而前端框架则选择了主流的**BootStrap**、**Vue.js**，**JQuery**三大前端框架，页面使用**Thymeleaf3**渲染。为了方便**校验登录**和**访问控制**、**授权**在安全框架方面使⽤了**Shiro** ，因为该项目的数据使用**JPA**  进行**DAO**操作, 所以在搜索引擎方面使⽤和SpringBoot兼容性高的**ElasticSearch**搜索引擎。**ElasticSearch**在SpringBoot上有专门JPA 接口 **ElasticsearchRepository** 进行DAO操作。最后因为电商网站的数据在处理过程中，通常在高并发的场景下涉及到了大量的重复读写，这样一来会消耗了很多性能和读取时间，为了可以满足高并发场景下实时的读取需求，所以缓存⽅⾯⽤了Redis。


## 二、项目结构
### 1. 项目名称
**项目名称：** tmall_springboot
### 2. java源代码包结构
├─tmall                 
│  ├─comparator         比较器类 用于搜索结果筛选     
│  ├─config             配置器类 用于配置 跨域、 redis、Shiro、页面拦截

│  ├─entity             实体类	
│  │ ├─controller      控制器
│  │ ├─dao              持久层进行DAO操作
│  │ ├─pojo 	       实体
│  │ ├─service	       服务

│  ├─es                 Elasticsearch持久层API配置类
│  ├─exception         全局异常处理器类
│  ├─interceptor       拦截器类 
│  ├─realm              shiro-Realm类
│  ├─test               测试类			
│  ├─util               工具类   

│  ├─web                页面类	
│  │ ├─AdminPageController      后台页面控制器
│  │ ├─ForePageController       前端页面映射控制器
│  │ ├─ForeRestController       前端页面内容请求控制器

Application          程序主入口            

### 3. 资源包结构

├─resource        资源目录    
│  ├─public       公共资源
│  │ ├─css        公共CSS
│  │ ├─img      	页面展示用图片		
│  │ ├─js         公共JS	
│  ├─templates         thymeleaf3对应的模板目录 
│  │ ├─admin           后台Html展示页
│  │ ├─fore            前台Html展示页			
│  │ ├─include         组件页
│  │ │ ├─admin         后台组件页
│  │ │ ├─fore          前台组件页
.properties      全局配置文件    

## 三、功能场景
典型场景
1. 购物车
立即购买 加入购物车 查看购物车页面 购物车页面操作

2. 订单状态流转
生成订单 确认支付 后台发货 确认收货 评价

3. CRUD 
后台各种功能

4. 分页
后台各种功能

5. 一类产品多属性配置
属性管理

6. 一款产品多图片维护
产品图片管理

7. 产品展示
前台首页 前台产品页

8. 搜索查询-基于elastic search
搜索

9. 登录、注册 - 基于 shiro
注册 登录 退出

10. 登录验证 - 基于 shiro
登录状态拦截器

11. 事务管理
ForeRESTController.对createOrder进行事务管理
等等 。。。

12. 缓存处理
全站数据通过 redis 进行了缓存

## 四、技术总结
● 后端：Spring SpringMVC、SpringBoot   

● 前端：BootStrap、Vue.js、JQuery、Thymeleaf3

● 安全：Shiroㅤㅤ ● 搜索引擎：elastic search    

● 缓存：Redisㅤㅤ● 数据：JPA

● 仓库管理：Gitㅤ ● 集成环境：idea

● 服务器容器：docker-mysql

● elastic search、Redis文件及其对应的可视化工具均在 在tool夹文件内


## 五、项目地址
[https://github.com/Tyler-Ou/tmall_springboot](https://github.com/Tyler-Ou/tmall_springboot)
