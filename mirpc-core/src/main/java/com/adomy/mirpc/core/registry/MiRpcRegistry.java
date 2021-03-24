/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.registry;

import java.util.Map;
import java.util.Set;

/**
 * 米RPC注册器
 *
 * @author adomyzhao
 * @version $Id: MiRpcRegister.java, v 0.1 2021年03月22日 8:45 PM adomyzhao Exp $
 */
public interface MiRpcRegistry {

    /**
     * 服务注册器启动
     *
     * @param param 注册参数MAP
     */
    void start(Map<String, String> param);

    /**
     * 服务注册器停止
     */
    void stop();

    /**
     * 服务注册
     * 
     * @param serviceKeys
     * @param serviceAddress
     * @return
     */
    boolean register(Set<String> serviceKeys, String serviceAddress);

    /**
     * 服务注销
     * 
     * @param serviceKeys
     * @param serviceAddress
     * @return
     */
    boolean unRegister(Set<String> serviceKeys, String serviceAddress);

    /**
     * 获取对应服务的调用地址集合
     * 
     * @param serviceKeys
     * @return
     */
    Map<String, Set<String>> discovery(Set<String> serviceKeys);

    /**
     * 获取对应服务的调用地址集合
     * 
     * @param serviceKey
     * @return
     */
    Set<String> discovery(String serviceKey);
}