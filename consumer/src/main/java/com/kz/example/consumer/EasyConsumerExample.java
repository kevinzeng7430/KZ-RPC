package com.kz.example.consumer;

import com.kz.example.common.model.User;
import com.kz.example.common.service.UserService;
import com.kz.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        //  todo 消费服务
        // 1. 获取服务
        //UserService userService = new UserServiceProxy(); // 通过注册中心获取服务
        // 创建代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("kz");
        // 2. 调用代理对象的方法调用服务
        User newUser = userService.getUser(user);
        if(newUser != null) {
            System.out.println("Consumer: " + newUser.getName());
        } else {
            System.out.println("Consumer: null");
        }
        // 3. 处理结果
    }
}
