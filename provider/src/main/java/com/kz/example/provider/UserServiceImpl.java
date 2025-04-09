package com.kz.example.provider;

import com.kz.example.common.model.User;
import com.kz.example.common.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("Provider: " + user.getName());
        return user;
    }
}
