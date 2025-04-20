package com.kz.rpc.bootstrap;

import com.kz.rpc.RpcApplication;
import com.kz.rpc.config.RegistryConfig;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.model.ServiceMetaInfo;
import com.kz.rpc.model.ServiceRegisterInfo;
import com.kz.rpc.registry.LocalRegistry;
import com.kz.rpc.registry.Registry;
import com.kz.rpc.registry.RegistryFactory;
import com.kz.rpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {
    public  static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            // 注册服务
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo   serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e){
                throw new RuntimeException("注册服务失败", e);
            }
            // 启动web服务
            VertxTcpServer tcpServer = new VertxTcpServer();
            //HttpServer httpServer = new VertxHttpServer();
            tcpServer.doStart(rpcConfig.getPort());
        }
    }
}
