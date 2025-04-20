package com.kz.examplespringbootconsumer;

import com.kz.example.common.model.User;
import com.kz.example.common.service.UserService;
import com.kz.rpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("kevin z");
        User result = userService.getUser(user);
        System.out.println("返回结果" + result.getName());
    }
}
