package com.kz.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
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
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry{
    private Client client;
    private KV kvClient;
    /**
     * etcd根路径
     */
    private static final String ETCD_ROOT_PATH = "/kz-rpc/";
    /**
     * 本机注册的节点key集合，用于维护续期
     *
     */
    private final Set<String> localRegistryNodeKeySet = new HashSet<>();

    /**
     * 注册缓存中心
     * @param
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();


    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
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

        //添加节点信息到本地缓存
        localRegistryNodeKeySet.add(registerKey);

    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        // 删除本地缓存
        localRegistryNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) throws Exception {
        //优先从缓存中心获取服务
        List<ServiceMetaInfo> serviceMetaInfos = registryServiceCache.readCache();
        if(serviceMetaInfos != null){
            log.info("服务缓存命中，服务信息为：{}", serviceMetaInfos);
            return serviceMetaInfos;
        }

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
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        watch(key);
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
        System.out.println("当前节点下线");
        // 释放资源
        for(String key : localRegistryNodeKeySet){
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8));
            }catch (Exception e){
                throw new RuntimeException(key + "注销失败", e);
            }
        }
        if(kvClient != null){
            kvClient.close();
        }
        if(client != null){
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("0/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 续期
                for (String key : localRegistryNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                        if(CollUtil.isEmpty(keyValues)){
                            continue;
                        }
                        // 节点未过期重新注册
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey
     */
    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        // 之前未被监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }

}
