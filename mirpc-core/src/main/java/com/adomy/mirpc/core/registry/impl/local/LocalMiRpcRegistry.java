/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.registry.impl.local;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.adomy.mirpc.core.registry.MiRpcRegistry;
import com.adomy.mirpc.core.util.lang.StringUtil;

/**
 * 本机注册器
 * 
 * @author adomyzhao
 * @version $Id: LocalMiRpcRegistry.java, v 0.1 2021年03月23日 6:04 PM adomyzhao Exp $
 */
public class LocalMiRpcRegistry implements MiRpcRegistry {

    /**
     * 服务注册数据
     */
    private Map<String, Set<String>> serviceRegisterData;

    /**
     * 启动注册器
     * 
     * @param param 注册参数MAP
     */
    @Override
    public void start(Map<String, String> param) {
        serviceRegisterData = new ConcurrentHashMap<>();
    }

    /**
     * 停止注册器
     */
    @Override
    public void stop() {
        serviceRegisterData.clear();
    }

    /**
     * 服务注册
     * 
     * @param serviceKeys
     * @param serviceAddress
     * @return
     */
    @Override
    public boolean register(Set<String> serviceKeys, String serviceAddress) {
        if (serviceKeys == null || StringUtil.isBlank(serviceAddress)) {
            return false;
        }

        for (String serviceKey : serviceKeys) {
            Set<String> serviceAddressSet = serviceRegisterData.computeIfAbsent(serviceKey,
                s -> new TreeSet<>());
            serviceAddressSet.add(serviceAddress);
        }
        return true;
    }

    /**
     * 服务注销
     * 
     * @param serviceKeys
     * @param serviceAddress
     * @return
     */
    @Override
    public boolean unRegister(Set<String> serviceKeys, String serviceAddress) {
        if (serviceKeys == null || StringUtil.isBlank(serviceAddress)) {
            return false;
        }

        for (String serviceKey : serviceKeys) {
            Set<String> serviceAddressSet = serviceRegisterData.get(serviceKey);
            Optional.ofNullable(serviceAddressSet).ifPresent(t -> t.remove(serviceAddress));
        }
        return true;
    }

    /**
     * 服务查找
     * 
     * @param serviceKeys
     * @return
     */
    @Override
    public Map<String, Set<String>> discovery(Set<String> serviceKeys) {
        if (serviceKeys == null) {
            return null;
        }

        Map<String, Set<String>> tempServiceAddressMap = new HashMap<>();
        for (String serviceKey : serviceKeys) {
            Set<String> addressSet = discovery(serviceKey);
            Optional.ofNullable(addressSet)
                .ifPresent(t -> tempServiceAddressMap.put(serviceKey, addressSet));
        }
        return tempServiceAddressMap;
    }

    /**
     * 服务查找
     * 
     * @param serviceKey
     * @return
     */
    @Override
    public Set<String> discovery(String serviceKey) {
        return serviceRegisterData.get(serviceKey);
    }
}