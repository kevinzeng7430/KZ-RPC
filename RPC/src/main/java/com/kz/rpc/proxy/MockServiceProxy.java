package com.kz.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *  Mock 服务代理
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 根据方法的返回类型，生成特定的默认对象
        Class<?> returnType = method.getReturnType();
        log.info("MockServiceProxy invoke: {}", method.getName());

        return getDefaultObject(returnType);
    }

    /**
     * 根据返回类型，生成默认对象
     * @param returnType
     * @return
     */
    public Object getDefaultObject(Class<?> returnType) {
        // 2. 根据返回类型，生成默认对象
        if (returnType == String.class) {
            return "mock string";
        } else if (returnType == int.class || returnType == Integer.class) {
            return 0;
        } else if (returnType == long.class || returnType == Long.class) {
            return 0L;
        } else if (returnType == double.class || returnType == Double.class) {
            return 0.0;
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return false;
        } else {
            // 3. 如果是其他类型，返回 null
            return null;
        }
    }
}
