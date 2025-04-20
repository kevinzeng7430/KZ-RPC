package com.kz.rpcspringbootstarter.annotation;

import com.kz.rpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.kz.rpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.kz.rpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    boolean needService() default true;
}
