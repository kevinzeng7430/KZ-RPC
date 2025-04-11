package com.kz.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;
import com.kz.rpc.serializer.JdkSerializer;
import com.kz.rpc.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    // TODO 看看这个method是什么
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        try{
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            // TODO 注意这里请求地址硬编码了（需要使用注册中心和服务发现来解决）
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes).execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
