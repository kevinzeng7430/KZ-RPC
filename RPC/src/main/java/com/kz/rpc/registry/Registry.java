package com.kz.rpc.registry;

import com.kz.rpc.config.RegistryConfig;
import com.kz.rpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    /**
     * 初始化
     */
    void init(RegistryConfig registryConfig);
    /**
     * 注册服务
     *
     * @param serviceMetaInfo 服务元信息
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 取消注册服务
     *
     * @param serviceMetaInfo 服务元信息
     */
    void unregister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey) throws Exception;

    /**
     * 获取键值
     */
    /**
     * 销毁服务
     */
    void destroy();
}
