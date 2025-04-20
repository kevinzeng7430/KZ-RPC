package com.kz.example.consumer;

import com.kz.example.common.model.User;
import com.kz.example.common.service.UserService;
import com.kz.rpc.bootstrap.ConsumerBootstrap;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.proxy.ServiceProxyFactory;
import com.kz.rpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);
        ConsumerBootstrap.init();
        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("kevin");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("Consumer: " + newUser.getName());
        } else {
            System.out.println("Consumer: null");
        }
        long number = userService.getNumber();
        System.out.println("Consumer: " + number);
    }
}

