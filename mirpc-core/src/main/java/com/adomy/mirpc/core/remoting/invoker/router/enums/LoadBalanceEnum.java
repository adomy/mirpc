/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.router.enums;

import com.adomy.mirpc.core.remoting.invoker.router.MiRpcLoadBalancer;
import com.adomy.mirpc.core.remoting.invoker.router.impl.RandomMiRpcLoadBalancer;

/**
 * 负载均衡枚举
 * 
 * @author adomyzhao
 * @version $Id: MiRpcLoadBalancerEnum.java, v 0.1 2021年03月23日 9:44 AM adomyzhao Exp $
 */
public enum LoadBalanceEnum {

                             /**
                              * 随机选取
                              */
                             RANDOM(new RandomMiRpcLoadBalancer()),

    ;

    /**
     * 处理器
     */
    private MiRpcLoadBalancer loadBalancer;

    /**
     * 构造函数
     *
     * @param loadBalancer
     */
    LoadBalanceEnum(MiRpcLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * Getter method for property loadBalancer.
     *
     * @return property value of loadBalancer
     */
    public MiRpcLoadBalancer getLoadBalancer() {
        return loadBalancer;
    }
}