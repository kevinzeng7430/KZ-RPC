package com.kz.rpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;

import java.io.IOException;

public class JsonSerializer implements Serializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        // 使用Hutool的ObjectMapper进行序列化
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        // 使用Hutool的ObjectMapper进行反序列化
        T obj = OBJECT_MAPPER.readValue(data, clazz);
        if(obj instanceof RpcRequest){
            return handleRpcRequest((RpcRequest) obj, clazz);
        }
        if(obj instanceof RpcResponse){
            return handleRpcResponse((RpcResponse) obj, clazz);
        }
        return obj;
    }
    /**
     * 处理RpcResponse
     * @param rpcResponse
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T handleRpcResponse(RpcResponse rpcResponse, Class<T> clazz) {
        try {
            // 重新序列化返回值，确保类型正确
            if (rpcResponse.getData() != null) {
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
                rpcResponse.setData(OBJECT_MAPPER.readValue(bytes, rpcResponse.getDataType()));
            }
            //类型转换
            return clazz.cast(rpcResponse);
        } catch (IOException e) {
            throw new RuntimeException("处理 RpcResponse 失败", e);
        }
    }
    /**
     * 处理RpcRequest
     * @param rpcRequest
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T handleRpcRequest(RpcRequest rpcRequest, Class<T> clazz) {
        try {
            // 重新序列化参数，确保类型正确
            Class<?>[] parameterType = rpcRequest.getParameterTypes();
            Object[] parameters = rpcRequest.getParameters();
            if (parameterType != null) {
                for (int i = 0; i < parameterType.length; i++) {
                    Class<?> type = parameterType[i];
                    if(!clazz.isAssignableFrom(parameters[i].getClass())){
                        // 如果参数类型不匹配，重新处理
                        //writeValueAsBytes() - 将对象序列化为字节数组：readValue() - 将字节数组反序列化为指定类型的对象：
                        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(parameters[i]);
                        parameters[i] = OBJECT_MAPPER.readValue(bytes, type);
                    }
                }
            }
            //类型转换
            return clazz.cast(rpcRequest);
        } catch (IOException e) {
            throw new RuntimeException("处理 RpcRequest 失败", e);
        }
    }
    /**
     * 对 RpcRequest 和 RpcResponse 分别处理是因为在使用 Jackson 进行反序列化时可能会遇到泛型擦除的问题。具体原因如下：
     * RpcRequest 需要特殊处理因为：
     * 包含方法参数类型信息 (Class<?>[] parameterTypes)
     * 反序列化时需要正确恢复参数类型，避免类型丢失
     * 确保方法调用时参数类型匹配正确
     * RpcResponse 需要特殊处理因为：
     * 包含返回值 (Object data)
     * 返回值可能是任意类型
     * 需要根据原始请求的返回类型进行正确的类型转换
     */

}
