package com.kz.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.kz.rpc.RpcApplication;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.model.RpcResponse;
import com.kz.rpc.model.ServiceMetaInfo;
import com.kz.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws  ExecutionException, InterruptedException {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        // 创建TCP客户端
        NetClient client = vertx.createNetClient();

        CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();
        client.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            if(!result.succeeded()) {
                System.err.println("连接失败");
                return;
            }
            NetSocket socket = result.result();
            // 发送请求数据
            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
            ProtocolMessage.Header header = new ProtocolMessage.Header();
            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
            header.setMessageType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
            header.setRequestId(IdUtil.getSnowflakeNextId());
            protocolMessage.setHeader(header);
            protocolMessage.setBody(rpcRequest);
            try {
                Buffer encodeBuff = ProtocolMessageEncoder.encode(protocolMessage);
                socket.write(encodeBuff);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 接收响应数据
            TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                // 解码
                ProtocolMessage<RpcResponse> protocolMessageResponse;
                try {
                    protocolMessageResponse = (ProtocolMessage<RpcResponse>) ProtocolMessageDecode.decode(buffer);
                    responseCompletableFuture.complete(protocolMessageResponse.getBody());
                } catch (Exception e) {
                    throw new RuntimeException("协议消息解码错误");
                }
            });
            socket.handler(bufferHandlerWrapper);
        });
        RpcResponse rpcResponse = responseCompletableFuture.get();
        client.close();
        return rpcResponse;
    }
}


/**
 * public void start(){
 *         // 创建Vert.x实例
 *         Vertx vertx = Vertx.vertx();
 *         // 创建TCP客户端
 *         NetClient client = vertx.createNetClient();
 *
 *         // 连接到服务器
 *         client.connect(8888, "localhost", result -> {
 *             if (result.succeeded()) {
 *                 System.out.println("Connected to server");
 *                 NetSocket socket = result.result();
 *
 *                 // 发送请求数据
 *                 byte[] requestData = "Hello, server!".getBytes();
 *                 socket.write(Buffer.buffer(requestData));
 *
 *                 // 接收响应数据
 *                 socket.handler(buffer -> {
 *                     byte[] responseData = buffer.getBytes();
 *                     System.out.println("Received response: " + new String(responseData));
 *                 });
 *             } else {
 *                 System.err.println("Failed to connect to server: " + result.cause());
 *             }
 *         });
 *     }
 *     public static void main(String[] args) {
 *         VertxTcpClient client = new VertxTcpClient();
 *         client.start(); // 启动TCP客户端
 *     }
 */