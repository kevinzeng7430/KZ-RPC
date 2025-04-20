package com.kz.rpcspringbootstarter.annotation;

import com.kz.rpc.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default  void.class;
    String ServiceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
