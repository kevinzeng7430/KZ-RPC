package com.kz.rpc.fault.tolerant;

import com.kz.rpc.model.RpcResponse;

import java.util.Map;

public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错", e);
    }
}
