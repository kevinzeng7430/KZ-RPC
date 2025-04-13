package com.kz.rpc.registry;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kz.rpc.RpcApplication;
import com.kz.rpc.config.RegistryConfig;
import com.kz.rpc.config.RpcConfig;
import com.kz.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{
    private Client client;
    private KV kvClient;
    /**
     * etcd根路径
     */
    private static final String ETCD_ROOT_PATH = "/kz-rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建LeaseClient 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建租约30s
        long leaseId = leaseClient.grant(30).get().getID();

        //设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对和租约关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) throws Exception {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/" + rpcConfig.getServerHost() + ":" + rpcConfig.getPort();
        try{
            // 前缀查询
            GetOption getOption = GetOption.builder()
                    .isPrefix(true).build();
            List<KeyValue> keyValues =kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),getOption)
                    .get().getKvs();
            // 解析服务信息
            return keyValues.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());
        } catch ( Exception e) {
            // 处理异常
            e.printStackTrace();
            throw new RuntimeException("服务发现失败", e);
        }
    }


    @Override
    public void destroy() {
        System.out.println("销毁注册中心");
        // 释放资源
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }
}
