package com.kz.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kz.rpc.RpcApplication;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.constant.RpcConstant;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;
import com.kz.rpc.model.ServiceMetaInfo;
import com.kz.rpc.registry.Registry;
import com.kz.rpc.registry.RegistryFactory;
import com.kz.rpc.serializer.JdkSerializer;
import com.kz.rpc.serializer.Serializer;
import com.kz.rpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ServiceProxy implements InvocationHandler {
    @Override
    // TODO 看看这个method是什么
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 指定序列化器
        //Serializer serializer = new JdkSerializer();
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        try{
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 从注册中心获取服务提供者地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry  registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList =registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("没有找到服务提供者");
            }
            ServiceMetaInfo selectServiceMetaInfo = serviceMetaInfoList.get(0);

            // 注意这里请求地址硬编码了（需要使用注册中心和服务发现来解决）
//            HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
//                    .body(bodyBytes).execute()
            try (HttpResponse httpResponse = HttpRequest.post(selectServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()) {
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
