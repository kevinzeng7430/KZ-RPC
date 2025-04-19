package com.kz.rpc.fault.tolerant;

import com.kz.rpc.model.RpcResponse;

import java.util.Map;

public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        return null;
    }
}
