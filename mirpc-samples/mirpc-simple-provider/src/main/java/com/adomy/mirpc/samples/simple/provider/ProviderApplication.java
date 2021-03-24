/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.samples.simple.provider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.adomy.mirpc.core.registry.impl.local.LocalMiRpcRegistry;
import com.adomy.mirpc.core.remoting.network.impl.netty.server.NettyMiRpcServer;
import com.adomy.mirpc.core.remoting.provider.MiRpcProviderFactory;
import com.adomy.mirpc.core.serialize.impl.HessianMiRpcSerializer;
import com.adomy.mirpc.samples.simple.api.DemoService;
import com.adomy.mirpc.samples.simple.provider.service.DemoServiceImpl;

/**
 * @author adomyzhao
 * @version $Id: ProviderApplication.java, v 0.1 2021年03月24日 5:28 PM adomyzhao Exp $
 */
public class ProviderApplication {

    public static void main(String[] args) throws Exception {

        MiRpcProviderFactory factory = new MiRpcProviderFactory();
        factory.setServerClass(NettyMiRpcServer.class);
        factory.setSerializerClass(HessianMiRpcSerializer.class);
        factory.setServerIp("127.0.0.1");
        factory.setServerPort(5200);
        factory.setServerCoreSize(20);
        factory.setServerMaxSize(50);
        factory.setRegistryAddress(null);
        factory.setRegistryClass(LocalMiRpcRegistry.class);
        factory.setRegistryParam(new HashMap<>());

        factory.addService(DemoService.class.getName(), "DEFAULT", new DemoServiceImpl());

        factory.start();

        TimeUnit.MINUTES.sleep(30);

        factory.stop();
    }
}