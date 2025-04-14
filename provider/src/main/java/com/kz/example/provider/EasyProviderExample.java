package com.kz.example.provider;


import com.kz.example.common.service.UserService;
import com.kz.rpc.RpcApplication;
import com.kz.rpc.config.RegistryConfig;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.model.ServiceMetaInfo;
import com.kz.rpc.registry.LocalRegistry;
import com.kz.rpc.registry.Registry;
import com.kz.rpc.registry.RegistryFactory;
import com.kz.rpc.server.HttpServer;
import com.kz.rpc.server.serverImpl.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        //RPC框架配置
        RpcApplication.init();
        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e){
            throw new RuntimeException("注册服务失败", e);
        }
        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
