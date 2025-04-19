package com.kz.rpc.fault.tolerant;

import com.kz.rpc.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FailSafeTolerantStrategy implements TolerantStrategy{
    private static final Logger log = LoggerFactory.getLogger(FailSafeTolerantStrategy.class);

    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理", e);

        return new RpcResponse();
    }
}
