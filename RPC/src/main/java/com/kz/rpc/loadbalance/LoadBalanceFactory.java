package com.kz.rpc.loadbalance;

import com.kz.rpc.spi.SpiLoader;

public class LoadBalanceFactory {
    static{
        SpiLoader.load(LoadBalance.class);
    }
    private static final LoadBalanceFactory DEFAULT_LOAD_BALANCE = new LoadBalanceFactory();
    public static LoadBalance getInstance(String key){
        return SpiLoader.getInstance(LoadBalance.class, key);
    }
}
