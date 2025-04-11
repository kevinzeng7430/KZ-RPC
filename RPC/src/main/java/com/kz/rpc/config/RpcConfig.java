package com.kz.rpc.config;


import lombok.Data;

/**
 * RPC框架配置
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "kz-rpc";
    /**
     * 版本号
     */
    private String version = "1.0.0";
    /**
     * 服务端口
     */
    private Integer port = 8080;
    /**
     * 服务主机
     */
    private String serverHost = "localhost";

}
