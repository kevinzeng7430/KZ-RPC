package com.kz.rpc.registry;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 */
public class LocalRegistry{
    /**
     * 注册信息本地存储
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();
    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param serviceClass 服务类
     */
    public static void register(String serviceName, Class<?> serviceClass) {
        map.put(serviceName, serviceClass);
    }

    /**
     * 获取服务
     * @param serviceName 服务名称
     * @return 服务类
     */
    public static Class<?> getService(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName 服务名称
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }


}
