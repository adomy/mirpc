/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.adomy.mirpc.core.remoting.invoker.MiRpcInvokerFactory;
import com.adomy.mirpc.core.remoting.invoker.reference.MiRpcReferenceBean;
import com.adomy.mirpc.core.remoting.network.MiRpcClient;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 米RPC客户端工厂
 * 
 * @author adomyzhao
 * @version $Id: NettyConnectClient.java, v 0.1 2021年03月23日 8:35 PM adomyzhao Exp $
 */
public class MiRpcClientFactory {

    /**
     * NioEventLoopGroup
     */
    private static volatile NioEventLoopGroup        nioEventLoopGroup    = null;

    /**
     * RPC客户端实例MAP
     *
     * KEY-String: 远程地址信息，例如 192.168.1.101:5200
     * VALUE-Client: 连接客户端实例信息
     */
    private static volatile Map<String, MiRpcClient> rpcClientInstanceMap = null;

    /**
     * RPC客户端锁MAP
     * 
     * KEY-String: 远程地址信息，例如 192.168.1.101:5200
     * VALUE-Object: 锁实例，用于加锁客户端处理过程
     */
    private static volatile Map<String, Object>      rpcClientLockMap     = null;

    /**
     * 从缓存获取或是生成RPC客户端
     * 
     * @param remoteAddress
     * @param clientClass 
     * @param referenceBean
     * @return
     */
    public static MiRpcClient getOrBuildRpcClient(MiRpcReferenceBean referenceBean,
                                                  Class<? extends MiRpcClient> clientClass,
                                                  String remoteAddress) throws Exception {
        MiRpcInvokerFactory invokerFactory = referenceBean.getMiRpcInvokerFactory();

        // 初始化处理
        initMapForBean(referenceBean);

        // 如果已经存在，则直接返回
        MiRpcClient rpcClient = rpcClientInstanceMap.get(remoteAddress);
        if (rpcClient != null && rpcClient.isValid()) {
            return rpcClient;
        }

        // 如果不存在，则需要新建client，同时放入map当中去
        Object rpcClientLock = rpcClientLockMap.computeIfAbsent(remoteAddress, (t) -> new Object());
        synchronized (rpcClientLock) {
            // 再取一次，可能其他的线程已经初始化了
            rpcClient = rpcClientInstanceMap.get(remoteAddress);
            if (rpcClient != null) {
                // 有效时，直接返回
                if (rpcClient.isValid()) {
                    return rpcClient;
                }
                // 无效时，关闭client，同时从map中移除
                else {
                    rpcClient.close();
                    rpcClientInstanceMap.remove(remoteAddress);
                }
            }

            MiRpcClient client = clientClass.newInstance();
            try {
                client.init(remoteAddress, referenceBean.getSerializerInstance(), invokerFactory);
                rpcClientInstanceMap.put(remoteAddress, client);
            } catch (Exception e) {
                client.close();
                throw e;
            }

            return client;
        }
    }

    /**
     * 为Bean初始化资源和钩子
     * 
     * @param referenceBean
     */
    private static void initMapForBean(MiRpcReferenceBean referenceBean) {
        MiRpcInvokerFactory invokerFactory = referenceBean.getMiRpcInvokerFactory();

        if (nioEventLoopGroup == null) {
            synchronized (MiRpcClientFactory.class) {
                if (nioEventLoopGroup == null) {
                    // 初始化
                    nioEventLoopGroup = new NioEventLoopGroup();

                    // 加钩子
                    invokerFactory.addStoppedHooker(() -> {
                        nioEventLoopGroup.shutdownGracefully();
                    });
                }
            }
        }

        if (rpcClientInstanceMap == null) {
            synchronized (MiRpcClientFactory.class) {
                if (rpcClientInstanceMap == null) {
                    // 初始化
                    rpcClientInstanceMap = new ConcurrentHashMap<>();

                    // 加钩子
                    invokerFactory.addStoppedHooker(() -> {
                        for (String addressKey : rpcClientInstanceMap.keySet()) {
                            MiRpcClient client = rpcClientInstanceMap.get(addressKey);
                            client.close();
                        }
                        rpcClientInstanceMap.clear();
                    });
                }
            }
        }

        if (rpcClientLockMap == null) {
            synchronized (MiRpcClientFactory.class) {
                if (rpcClientLockMap == null) {
                    rpcClientLockMap = new ConcurrentHashMap<>();
                }
            }
        }
    }

    /**
     * Getter method for property nioEventLoopGroup.
     *
     * @return property value of nioEventLoopGroup
     */
    public static NioEventLoopGroup getNioEventLoopGroup() {
        return nioEventLoopGroup;
    }
}