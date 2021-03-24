/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.adomy.mirpc.core.remoting.invoker.enums.InvokeTypeEnum;
import com.adomy.mirpc.core.remoting.invoker.router.enums.LoadBalanceEnum;
import com.adomy.mirpc.core.remoting.network.MiRpcClient;
import com.adomy.mirpc.core.remoting.network.impl.netty.client.NettyMiRpcClient;
import com.adomy.mirpc.core.serialize.MiRpcSerializer;
import com.adomy.mirpc.core.serialize.impl.HessianMiRpcSerializer;

/**
 * MiRpcReference
 * 
 * @author adomyzhao
 * @version $Id: MiRpcReference.java, v 0.1 2021年03月23日 9:50 AM adomyzhao Exp $
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MiRpcReference {

    /**
     * 客户端
     * @return
     */
    Class<? extends MiRpcClient> client() default NettyMiRpcClient.class;

    /**
     * 序列化
     * @return
     */
    Class<? extends MiRpcSerializer> serializer() default HessianMiRpcSerializer.class;

    /**
     * 调用方式
     * 
     * @return
     */
    InvokeTypeEnum invokeType() default InvokeTypeEnum.SYNC;

    /**
     * 负载均衡
     * 
     * @return
     */
    LoadBalanceEnum loadBalance() default LoadBalanceEnum.RANDOM;

    /**
     * 服务版本号
     * 
     * @return
     */
    String version() default "";

    /**
     * 服务调用超时
     * 
     * @return
     */
    long timeout() default 3000;

    /**
     * 目标地址
     * 
     * @return
     */
    String address() default "";

    /**
     * 鉴权token
     * 
     * @return
     */
    String accessToken() default "";

}