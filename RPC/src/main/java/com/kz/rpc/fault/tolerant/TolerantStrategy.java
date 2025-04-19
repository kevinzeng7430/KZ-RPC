package com.kz.rpc.fault.tolerant;

import com.kz.rpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    /**
     * 容错策略
     * @param
     * @return
     */
    RpcResponse tolerant(Map<String, Object> context, Exception e);
}
