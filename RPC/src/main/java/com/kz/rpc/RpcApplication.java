package com.kz.rpc;

import com.kz.rpc.config.RegistryConfig;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.constant.RpcConstant;
import com.kz.rpc.registry.Registry;
import com.kz.rpc.registry.RegistryFactory;
import com.kz.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架启动类
 * 相当于holder，存放项目全局用到的变量。双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {


    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，传入自定义配置
     */
    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("RPC框架初始化成功，当前配置为：{}", rpcConfig);
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("注册中心初始化成功，当前配置为：{}", registryConfig);
    }
    /**
     * 初始化
     */
    public static void init(){
       RpcConfig newRpcConfig;
       try {
           newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);

       }catch (Exception e){
           log.error("RPC框架初始化失败，使用默认配置", e);
           newRpcConfig = new RpcConfig();
       }
       init(newRpcConfig);
    }
    /**
     * 获取配置
     * @return
     */
    public static RpcConfig getRpcConfig() {
        // 双检锁单例模式
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
    /**
     * 框架初始化
     * @param  newRegistry
     * @return 配置项值
     */
}
