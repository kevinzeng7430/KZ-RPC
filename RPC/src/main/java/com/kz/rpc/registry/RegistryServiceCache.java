package com.kz.rpc.registry;

import com.kz.rpc.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> ServiceCache;

    /**
     * 写缓存
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.ServiceCache = newServiceCache;
    }
    /**
     * 读缓存
     */
    List<ServiceMetaInfo> readCache() {
        return this.ServiceCache;
    }
    /**
     * 清除缓存
     */
    void clearCache() {
        this.ServiceCache = null;
    }
}
