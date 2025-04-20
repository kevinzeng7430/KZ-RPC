package com.kz.rpcspringbootstarter.bootstrap;

import com.kz.rpc.RpcApplication;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.server.tcp.VertxTcpServer;
import com.kz.rpcspringbootstarter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(RpcInitBootstrap.class);
    /**
     *  Spring 初始化时执行，初始化RPC框架
     */

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取Enable 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needService");

        // RPC框架初始化
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 启动服务器
        if(needServer){
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getPort());
        }else{
           log.info("RPC框架初始化完成，当前为消费者模式，不启动服务端");
        }
    }
}
