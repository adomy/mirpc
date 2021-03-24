/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adomy.mirpc.core.remoting.invoker.MiRpcInvokerFactory;
import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;
import com.adomy.mirpc.core.serialize.MiRpcSerializer;

/**
 * RPC客户端
 *
 * @author adomyzhao
 * @version $Id: MiRpcClient.java, v 0.1 2021年03月23日 9:34 AM adomyzhao Exp $
 */
public abstract class MiRpcClient {

    /**
     * 日志
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(MiRpcClient.class);

    /**
     * 初始化处理
     * 
     * @param address
     * @param serializer
     * @param factory
     */
    public abstract void init(String address, final MiRpcSerializer serializer,
                              final MiRpcInvokerFactory factory) throws Exception;

    /**
     * 关闭处理
     */
    public abstract void close();

    /**
     * 判断客户端是否有效
     * 
     * @return
     */
    public abstract boolean isValid();

    /**
     * 发送处理
     * 
     * @param request
     * @throws Exception
     */
    public abstract void send(MiRpcRequest request) throws Exception;

}