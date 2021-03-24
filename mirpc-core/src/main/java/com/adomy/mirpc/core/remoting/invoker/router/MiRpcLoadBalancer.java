/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.router;

import java.util.Set;

/**
 * RPC路由负载均衡器
 * 
 * @author adomyzhao
 * @version $Id: MiRpcLoadBalancer.java, v 0.1 2021年03月23日 9:44 AM adomyzhao Exp $
 */
public interface MiRpcLoadBalancer {

    /**
     * 路由处理
     * 
     * @param serviceKey
     * @param serviceAddressSet
     * @return
     */
    String route(String serviceKey, Set<String> serviceAddressSet);
}