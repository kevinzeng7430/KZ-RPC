package com.kz.rpc.server.serverImpl;

import com.kz.rpc.server.HttpServer;
import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        //创建Vertx实例
        Vertx vertx = Vertx.vertx();

        //创建Http服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        //监听端口并处理请求
       httpServer.requestHandler(new HttpServerHandler());

        //启动Http服务器并监听窗口
        httpServer.listen(port, result->{
            if(result.succeeded()){
                System.out.println("VertxHttpServer started on port " + port);
            } else {
                System.out.println("Failed to start VertxHttpServer: " + result.cause());
            }
        });
    }
}
