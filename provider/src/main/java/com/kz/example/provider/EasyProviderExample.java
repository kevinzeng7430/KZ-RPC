package com.kz.example.provider;


import com.kz.example.common.service.UserService;
import com.kz.rpc.registry.LocalRegistry;
import com.kz.rpc.server.HttpServer;
import com.kz.rpc.server.serverImpl.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
