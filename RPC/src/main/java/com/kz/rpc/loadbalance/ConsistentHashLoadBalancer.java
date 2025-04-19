package com.kz.rpc.loadbalance;

import com.kz.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalance{
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();
    private final int VIRTUAL_NODE_NUM = 10;
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        // 构建虚拟环节点
        for(ServiceMetaInfo serviceMetaInfo :serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }
        // 获取请求参数的hash值
        int hash = getHash(requestParams);
        // ceilingEntry 方法是 Java 中 TreeMap 类的一部分，用于检索键值对（Entry），其键值是 大于或等于 给定键的最小键。如果没有这样的键，则返回 null
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry == null){
            // 如果没有找到，则返回第一个节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }
    private int getHash(Object key) {
        return key.hashCode();
    }
}
