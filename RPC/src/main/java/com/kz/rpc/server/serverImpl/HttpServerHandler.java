package com.kz.rpc.server.serverImpl;

import com.kz.rpc.RpcApplication;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;
import com.kz.rpc.registry.LocalRegistry;
import com.kz.rpc.serializer.JdkSerializer;
import com.kz.rpc.serializer.Serializer;
import com.kz.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ServiceLoader;


public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        // 指定序列化器
        //final Serializer serializer = new JdkSerializer();
//        ServiceLoader<Serializer> serviceLoader = ServiceLoader.load(Serializer.class);
//        for (Serializer s : serviceLoader) {
//            if (s instanceof JdkSerializer) {
//                serializer = s;
//                break;
//            }
//        }
        Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
        // 记录日志
        System.out.println("receive request" + httpServerRequest.method() + " " + httpServerRequest.uri());

        // 处理请求
        httpServerRequest.bodyHandler(body ->{
           byte[] data = body.getBytes();
           RpcRequest rpcRequest = null;
           try {
                // 反序列化请求
                rpcRequest = serializer.deserialize(data, RpcRequest.class);
              } catch (Exception e) {
                e.printStackTrace();
           }
           // 构造响应结果
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest == null){
                rpcResponse.setMessage("请求参数错误");
                doResponse(httpServerRequest, rpcResponse, serializer);
                return;
            }

            try{
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.getService(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), rpcRequest.getParameters());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("success");
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                throw new RuntimeException(e);

            }

            //响应
            doResponse(httpServerRequest, rpcResponse, serializer);
        });
    }

    void doResponse(HttpServerRequest request, RpcResponse response, Serializer serializer){
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            byte[] data = serializer.serialize(response);
            httpServerResponse.end(Buffer.buffer(data));
        } catch (Exception e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
