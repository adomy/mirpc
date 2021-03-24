/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.impl.netty.server;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adomy.mirpc.core.remoting.network.heartbeat.HeartBeat;
import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;
import com.adomy.mirpc.core.remoting.provider.MiRpcProviderFactory;
import com.adomy.mirpc.core.util.lang.StringUtil;
import com.adomy.mirpc.core.util.lang.ThrowUtil;
import com.adomy.mirpc.core.util.logger.LoggerUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Netty服务器模式下的核心处理逻辑
 * 
 * @author adomyzhao
 * @version $Id: NettyMiRpcServerHandler.java, v 0.1 2021年03月23日 12:55 PM adomyzhao Exp $
 */
public class NettyMiRpcServerHandler extends SimpleChannelInboundHandler<MiRpcRequest> {

    private static final Logger  LOGGER = LoggerFactory.getLogger(NettyMiRpcServerHandler.class);

    /**
     * RPC提供者工厂
     */
    private MiRpcProviderFactory miRpcProviderFactory;

    /**
     * 服务处理线程池s
     */
    private ThreadPoolExecutor   serviceHandleThreadPool;

    /**
     * 构造函数
     * 
     * @param miRpcProviderFactory
     * @param threadPoolExecutor
     */
    public NettyMiRpcServerHandler(MiRpcProviderFactory miRpcProviderFactory,
                                   ThreadPoolExecutor threadPoolExecutor) {
        this.miRpcProviderFactory = miRpcProviderFactory;
        this.serviceHandleThreadPool = threadPoolExecutor;
    }

    /**
     * 读请求
     * 
     * @param ctx
     * @param miRpcRequest
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                MiRpcRequest miRpcRequest) throws Exception {
        // 心跳请求处理
        if (StringUtil.equals(miRpcRequest.getRequestId(), HeartBeat.HEART_BEAT_ID)) {
            LoggerUtil.info(LOGGER, "MiRpc provider netty server read beat-ping.");

            return;
        }

        try {
            // 提交线程池执行处理
            serviceHandleThreadPool.execute(() -> {
                // 调用工厂执行代理服务
                MiRpcResponse response = miRpcProviderFactory.invokeService(miRpcRequest);

                try {
                    // 结果写入连接Channel中
                    ctx.writeAndFlush(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        // 异常case处理
        catch (Exception e) {
            MiRpcResponse response = new MiRpcResponse();
            response.setRequestId(miRpcRequest.getRequestId());
            response.setErrorMsg(ThrowUtil.getStackTraceAsString(e));

            ctx.writeAndFlush(response);
        }
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
        LoggerUtil.error(LOGGER, cause, "MiRpc provider netty server caught exception");
        ctx.close();
    }

    /**
     * 用户事件触发处理
     * 
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close(); // beat 3N, close if idle
            LoggerUtil.debug(LOGGER, "MiRpc provider netty server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}