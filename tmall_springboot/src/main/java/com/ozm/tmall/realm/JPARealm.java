package com.ozm.tmall.realm;

import com.ozm.tmall.entity.pojo.User;
import com.ozm.tmall.entity.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

//Ps:JPARealm 这个类，用户提供，但是不由用户自己调用，而是由 Shiro 去调用
//shiro的中介域Realm 在项目中用于验证账号登录和授权、账号密码加密
public class JPARealm extends AuthorizingRealm {
    //重写其抽象类的抽象方法
    @Autowired
    UserService userService;


    //授权权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //能进入到这里，表示账号已经通过验证了, 直接返回一个授权对象即可
        return new SimpleAuthorizationInfo();

    }

    //验证授权
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //通过shiro的配置从前端传入用户名获取对应用户，同时保存传入的密码
        //如果对应用户存在的情况下，即从数据库中取出对应的密码和盐
        //前端传入的密码配合盐进行两次md5加密
        //加密后和从数据库中获取的密码进行对比，如果相同，则返回一个授权对象
        UsernamePasswordToken t = (UsernamePasswordToken)token; //这里要配置好shiro才能获取到对应的token
        String foreUserName = t.getUsername(); //因为这个Realm是由shiro去调用操作的
        String forePassword = new String(t.getPassword());
        //通过用户名获取对应用户对象
        User user = userService.getByName(foreUserName);
        //获取数据库中的密码
        String passwordInDB = user.getPassword();
        //获取盐
        String salt = user.getSalt();
        //使用盐对前端的密码进行二次加密
        String passwordEncoded = new SimpleHash("md5",forePassword,salt,2).toString();
        //二次加密的值与密码进行对比
        if(null==user||!passwordEncoded.equals(passwordInDB)){
            throw new AuthenticationException();
        }
        //对比成功后返回授权对象，并将用户名和密码存入
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(foreUserName,forePassword,getName());
        return authenticationInfo;
    }
}
