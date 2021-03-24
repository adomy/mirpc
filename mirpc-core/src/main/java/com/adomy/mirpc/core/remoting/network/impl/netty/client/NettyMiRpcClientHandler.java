/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.impl.netty.client;

import com.adomy.mirpc.core.remoting.invoker.MiRpcInvokerFactory;
import com.adomy.mirpc.core.remoting.network.heartbeat.HeartBeat;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author adomyzhao
 * @version $Id: NettyMiRpcClientHandler.java, v 0.1 2021年03月23日 8:30 PM adomyzhao Exp $
 */
public class NettyMiRpcClientHandler extends SimpleChannelInboundHandler<MiRpcResponse> {

    /**
     * MiRpcInvokerFactory
     */
    private MiRpcInvokerFactory miRpcInvokerFactory;

    /**
     * NettyMiRpcClient
     */
    private NettyMiRpcClient    nettyMiRpcClient;

    /**
     * 构造函数
     * 
     * @param factory
     * @param client
     */
    public NettyMiRpcClientHandler(MiRpcInvokerFactory factory, NettyMiRpcClient client) {
        this.miRpcInvokerFactory = factory;
        this.nettyMiRpcClient = client;
    }

    /**
     * 读取到返回结果后的处理逻辑
     * 
     * @param ctx
     * @param miRpcResponse
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                MiRpcResponse miRpcResponse) throws Exception {
        this.miRpcInvokerFactory.notifyResponseFuture(miRpcResponse.getRequestId(), miRpcResponse);
    }

    /**
     * 异常处理
     * 
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 用户事件触发
     * 
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            this.nettyMiRpcClient.send(HeartBeat.HEART_BEAT_PING);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}