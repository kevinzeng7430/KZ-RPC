package com.kz.rpc.proxy;


import com.kz.rpc.RpcApplication;

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
        if(RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        Object proxy = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
        return serviceClass.cast(proxy); // 显式类型转换
    }

    /**
     * 创建服务代理
     *
     * @param serviceClass 服务接口类
     * @param <T>          服务接口类型
     * @return 服务代理对象
     */
    public static<T> T getMockProxy(Class<T> serviceClass) {
        Object proxy = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
        return serviceClass.cast(proxy); // 显式类型转换
    }
}
/** * 这个动态代理的运行过程可以分为以下几个步骤：
 *

 * 1. **调用`ServiceProxyFactory.getProxy`方法**：
 *    - 传入`UserService.class`作为参数，表示需要为`UserService`接口创建代理对象。
 *
 * 2. **创建代理对象**：
 *    - 在`ServiceProxyFactory`中，使用`Proxy.newProxyInstance`方法创建代理对象。
 *    - 该方法需要三个参数：
 *      - **类加载器**：`serviceClass.getClassLoader()`，用于加载代理类。
 *      - **接口数组**：`new Class[]{serviceClass}`，指定代理对象需要实现的接口。
 *      - **调用处理器**：`new ServiceProxy()`，定义代理对象的方法调用逻辑。
 *
 * 3. **代理对象的行为**：
 *    - 当调用`userService.getUser(user)`时，代理对象会将方法调用转发给`ServiceProxy`的`invoke`方法。
 *    - 在`ServiceProxy`的`invoke`方法中，可以自定义逻辑，例如：
 *      - 远程调用服务。
 *      - 日志记录。
 *      - 参数校验等。
 *
 * 4. **返回结果**：
 *    - `ServiceProxy`的`invoke`方法处理完逻辑后，将结果返回给调用者。
 *
 * ### 运行时的关键点：
 * - **动态代理**：JDK动态代理通过`java.lang.reflect.Proxy`和`InvocationHandler`实现，代理对象在运行时动态生成。
 * - **类型转换**：`serviceClass.cast(proxy)`确保返回的代理对象是`UserService`类型。
 *
 * 动态代理的核心是将方法调用的控制权交给`InvocationHandler`，从而实现灵活的逻辑扩展。
 */