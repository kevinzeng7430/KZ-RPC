package com.kz.rpc.registry;

import com.kz.rpc.spi.SpiLoader;

/**
 * 使用工厂模式，支持根据key获取注册中心对象实例
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认的注册中心
     */
    private static final String DEFAULT_REGISTRY = "etcd";

    /**
     * 获取实例
     */
    public static  Registry getInstance(String registry) {
        if (registry == null || registry.isEmpty()) {
            registry = DEFAULT_REGISTRY;
        }
        return SpiLoader.getInstance(Registry.class, registry);
    }
}
