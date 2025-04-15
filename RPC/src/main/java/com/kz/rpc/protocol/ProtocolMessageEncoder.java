package com.kz.rpc.protocol;

import com.kz.rpc.serializer.Serializer;
import com.kz.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageEncoder {
    /**
     * 编码
     *
     * @param protocolMessage 协议消息
     * @return byte[]
     */
    public static Buffer encode (ProtocolMessage<?> protocolMessage) throws IOException{
        if(protocolMessage == null || protocolMessage.getHeader() == null){
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 依次写入缓冲区
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getMessageType());
        buffer.appendByte(header.getStatusCode());
        buffer.appendLong(header.getRequestId());

        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum == null){
            throw new IOException("不支持的序列化器");
        }
        Serializer serializer = SerializerFactory.getSerializer(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
