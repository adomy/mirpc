/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.samples.simple.consumer;

import java.util.concurrent.TimeUnit;

import com.adomy.mirpc.core.remoting.invoker.MiRpcInvokerFactory;
import com.adomy.mirpc.core.remoting.invoker.enums.InvokeTypeEnum;
import com.adomy.mirpc.core.remoting.invoker.reference.MiRpcReferenceBean;
import com.adomy.mirpc.core.remoting.invoker.router.enums.LoadBalanceEnum;
import com.adomy.mirpc.core.remoting.network.impl.netty.client.NettyMiRpcClient;
import com.adomy.mirpc.core.serialize.impl.HessianMiRpcSerializer;
import com.adomy.mirpc.samples.simple.api.DemoDTO;
import com.adomy.mirpc.samples.simple.api.DemoService;

/**
 * @author adomyzhao
 * @version $Id: ConsumerApplication.java, v 0.1 2021年03月24日 5:27 PM adomyzhao Exp $
 */
public class ConsumerApplication {

    public static void main(String[] args) throws Exception {
        MiRpcReferenceBean referenceBean = new MiRpcReferenceBean();
        referenceBean.setClientClass(NettyMiRpcClient.class);
        referenceBean.setSerializerClass(HessianMiRpcSerializer.class);
        referenceBean.setInvokeType(InvokeTypeEnum.SYNC);
        referenceBean.setLoadBalance(LoadBalanceEnum.RANDOM);
        referenceBean.setInterfaceClass(DemoService.class);
        referenceBean.setServiceVersion("DEFAULT");
        referenceBean.setRemoteAddress("127.0.0.1:5200");
        referenceBean.setTimeout(3000);

        DemoService object = (DemoService) referenceBean.getObject();

        DemoDTO dto = object.test("hhhhhh");
        System.out.println(dto);

        TimeUnit.SECONDS.sleep(20);

        MiRpcInvokerFactory.getInstance().stop();
    }
}