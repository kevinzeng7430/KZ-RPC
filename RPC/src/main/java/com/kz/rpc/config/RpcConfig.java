package com.kz.rpc.config;


import com.kz.rpc.fault.RetryStrategyKeys;
import com.kz.rpc.loadbalance.LoadBalanceKeys;
import com.kz.rpc.serializer.Serializer;
import com.kz.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC框架配置
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "kz-rpc";
    /**
     * 版本号
     */
    private String version = "1.0.0";
    /**
     * 服务端口
     */
    private Integer port = 8080;
    /**
     * 服务主机
     */
    private String serverHost = "localhost";
    /**
     * 模拟调用
     */
    private boolean mock = false;
    /**
     * 序列化方式
     */
    private String serializer = SerializerKeys.JDK;
    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
    /**
     * 负载均衡器
     */
    private String loadBalance = LoadBalanceKeys.ROUND_ROBIN;
    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;
}
