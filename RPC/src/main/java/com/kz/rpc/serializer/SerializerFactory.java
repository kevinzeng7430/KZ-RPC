package com.kz.rpc.serializer;

import com.kz.rpc.spi.SpiLoader;

public class SerializerFactory {
//    private static final Map<String, Serializer> SERIALIZER_MAP = Map.of(
//            SerializerKeys.JDK, new JdkSerializer(),
//            SerializerKeys.KRYO, new KryoSerializer(),
//            SerializerKeys.HESSIAN, new HessianSerializer(),
//            SerializerKeys.JSON, new JsonSerializer()
//    );

    static {
        SpiLoader.load(Serializer.class);
    }
    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();
    /**
     * 获取序列化器
     * @param serializerKey 序列化器key
     * @return 序列化器
     */
    public static Serializer getSerializer(String serializerKey) {
        return SpiLoader.getInstance(Serializer.class, serializerKey);
        /**
         * getOrDefault() 是 Java Map 接口的一个方法，它有两个参数：
         * 要查找的 key（这里是 serializerKey）
         * 默认值（这里是 DEFAULT_SERIALIZER）
         */
    }

}
