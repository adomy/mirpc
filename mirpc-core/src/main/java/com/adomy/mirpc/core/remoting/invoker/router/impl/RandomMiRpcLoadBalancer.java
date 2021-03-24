/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.router.impl;

import java.util.Random;
import java.util.Set;

import com.adomy.mirpc.core.remoting.invoker.router.MiRpcLoadBalancer;

/**
 * LoadBalancer
 * 
 * @author adomyzhao
 * @version $Id: RandomMiRpcLoadBalancer.java, v 0.1 2021年03月24日 1:03 PM adomyzhao Exp $
 */
public class RandomMiRpcLoadBalancer implements MiRpcLoadBalancer {

    /**
     * 路由处理
     * 
     * @param serviceKey
     * @param serviceAddressSet
     * @return
     */
    @Override
    public String route(String serviceKey, Set<String> serviceAddressSet) {
        int setSize = serviceAddressSet.size();

        int randIndex = new Random().nextInt(setSize);
        return serviceAddressSet.toArray(new String[setSize])[randIndex];
    }
}