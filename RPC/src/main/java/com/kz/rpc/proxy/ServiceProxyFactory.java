package com.kz.rpc.proxy;


import java.lang.reflect.Proxy;

public class ServiceProxyFactory {

    /**
     * 创建服务代理
     *
     * @param serviceClass 服务接口类
     * @param <T>          服务接口类型
     * @return 服务代理对象
     */
    public static<T> T getProxy(Class<T> serviceClass) {
        Object proxy = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
        return serviceClass.cast(proxy); // 显式类型转换
    }
}
