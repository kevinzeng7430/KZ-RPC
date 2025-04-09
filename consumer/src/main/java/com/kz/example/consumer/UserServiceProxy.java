package com.kz.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kz.example.common.model.User;
import com.kz.example.common.service.UserService;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;
import com.kz.rpc.serializer.JdkSerializer;
import com.kz.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 */

public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 发起请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .parameters(new Object[]{user})
                .build();
        try{
            byte[] requestData = serializer.serialize(request);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(requestData)
                    .execute()) {
                // 获取响应数据
                result = httpResponse.bodyBytes();
            }
            RpcResponse response = serializer.deserialize(result, RpcResponse.class);
            return (User) response.getData();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
