package com.kz.rpc.protocol;

import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;
import com.kz.rpc.serializer.Serializer;
import com.kz.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageDecode {
    /**
     * 解码
     *
     * @param buffer 字节数组
     * @return ProtocolMessage
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException{
        // 分别从指定位置读出Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        if(magic != ProtocolConstant.PROTOCOL_MAGIC){
            throw new IOException("协议魔数错误");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setMessageType(buffer.getByte(3));
        header.setStatusCode(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        // 解决沾包问题，只读指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + (int) header.getBodyLength());
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum == null){
            throw new IOException("不支持的序列化器");
        }
        Serializer serializer = SerializerFactory.getSerializer(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getMessageType());
        if(messageTypeEnum == null){
            throw new IOException("不支持的消息类型");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                return new ProtocolMessage<>(header, serializer.deserialize(bodyBytes, RpcRequest.class));
            case RESPONSE:
                return new ProtocolMessage<>(header, serializer.deserialize(bodyBytes, RpcResponse.class));
            case HEART_BEAT:
            case OTHER:
            default:
                throw new IOException("不支持的消息类型");
        }
    }
}
