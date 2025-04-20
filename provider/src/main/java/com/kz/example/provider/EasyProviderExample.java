package com.kz.example.provider;


import com.kz.example.common.service.UserService;
import com.kz.rpc.RpcApplication;
import com.kz.rpc.bootstrap.ProviderBootstrap;
import com.kz.rpc.config.RegistryConfig;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.model.ServiceMetaInfo;
import com.kz.rpc.model.ServiceRegisterInfo;
import com.kz.rpc.registry.LocalRegistry;
import com.kz.rpc.registry.Registry;
import com.kz.rpc.registry.RegistryFactory;
import com.kz.rpc.server.HttpServer;
import com.kz.rpc.server.serverImpl.VertxHttpServer;
import com.kz.rpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;


public class EasyProviderExample {
    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);
        // 初始化服务
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
