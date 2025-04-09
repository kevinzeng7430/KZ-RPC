package com.kz.rpc.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 */
public interface Serializer {
    /**
     * 序列化
     * @return 反序列化后的对象
     * @throws IOException IO异常
     */

    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
