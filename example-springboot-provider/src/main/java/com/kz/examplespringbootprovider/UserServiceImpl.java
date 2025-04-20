package com.kz.examplespringbootprovider;

import com.kz.example.common.model.User;
import com.kz.example.common.service.UserService;
import com.kz.rpcspringbootstarter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名" + user.getName());
        return user;
    }
}
