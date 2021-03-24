/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.generic;

/**
 * 泛化服务
 *
 * @author adomyzhao
 * @version $Id: MiRpcGenericService.java, v 0.1 2021年03月24日 12:54 PM adomyzhao Exp $
 */
public interface MiRpcGenericService {

    /**
     * generic invoke
     *
     * @param interfaceName  iface name
     * @param version        iface version
     * @param method         method name
     * @param parameterTypes parameter types, limit base type like "int、java.lang.Integer、java.util.List、java.util.Map ..."
     * @param parameters     parameters
     * @return
     */
    Object invoke(String interfaceName, String version, String method, Class[] parameterTypes,
                  Object[] parameters);
}