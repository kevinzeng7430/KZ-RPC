package com.kz.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kz.rpc.RpcApplication;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.constant.RpcConstant;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;
import com.kz.rpc.model.ServiceMetaInfo;
import com.kz.rpc.protocol.*;
import com.kz.rpc.registry.Registry;
import com.kz.rpc.registry.RegistryFactory;
import com.kz.rpc.serializer.JdkSerializer;
import com.kz.rpc.serializer.Serializer;
import com.kz.rpc.serializer.SerializerFactory;
import com.kz.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy implements InvocationHandler {
    /**
     * 服务代理（JDK动态代理）
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        try {

            // 从注册中心获取服务提供者地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("没有找到服务提供者");
            }
            ServiceMetaInfo selectServiceMetaInfo = serviceMetaInfoList.get(0);
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectServiceMetaInfo);
            return rpcResponse.getData();
//            // 发送请求
//            Vertx vertx = Vertx.vertx();
//            NetClient netClient = vertx.createNetClient();
//            CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();
//            netClient.connect(selectServiceMetaInfo.getServicePort(), selectServiceMetaInfo.getServiceHost(), result -> {
//                if (result.succeeded()) {
//                    System.out.println("连接成功");
//                    io.vertx.core.net.NetSocket netSocket = result.result();
//                    // 发送数据 构造消息
//                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
//                    ProtocolMessage.Header header = new ProtocolMessage.Header();
//                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
//                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
//                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
//                    header.setMessageType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
//                    header.setRequestId(IdUtil.getSnowflakeNextId());
//                    protocolMessage.setHeader(header);
//                    protocolMessage.setBody(rpcRequest);
//
//                    // 发送数据
//                    try {
//                        Buffer encodeBuff = ProtocolMessageEncoder.encode(protocolMessage);
//                        netSocket.write(encodeBuff);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    // 处理响应
//                    netSocket.handler(buffer -> {
//                        // 接收响应，解码
//                        ProtocolMessage<RpcResponse> protocolMessageResponse;
//                        try {
//                            protocolMessageResponse = (ProtocolMessage<RpcResponse>) ProtocolMessageDecode.decode(buffer);
//                            responseCompletableFuture.complete(protocolMessageResponse.getBody());
//                        } catch (IOException e) {
//                            throw new RuntimeException("协议消息解码错误");
//                        }
//                    });
//                }else {
//                    System.out.println("连接失败");
//                }
//            });
//            RpcResponse rpcResponse = responseCompletableFuture.get();
//            netClient.close();
//            return rpcResponse.getData();
        }catch (IOException e) {
            throw new RuntimeException("协议消息编码错误");
        }
    }
}


/**
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
*/