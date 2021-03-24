/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.impl.netty.client;

import java.util.concurrent.TimeUnit;

import com.adomy.mirpc.core.remoting.invoker.MiRpcInvokerFactory;
import com.adomy.mirpc.core.remoting.network.MiRpcClient;
import com.adomy.mirpc.core.remoting.network.factory.MiRpcClientFactory;
import com.adomy.mirpc.core.remoting.network.heartbeat.HeartBeat;
import com.adomy.mirpc.core.remoting.network.impl.netty.codec.NettyDecoder;
import com.adomy.mirpc.core.remoting.network.impl.netty.codec.NettyEncoder;
import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;
import com.adomy.mirpc.core.serialize.MiRpcSerializer;
import com.adomy.mirpc.core.util.lang.NetworkUtil;
import com.adomy.mirpc.core.util.logger.LoggerUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Netty模式的RPC客户端
 * 
 * @author adomyzhao
 * @version $Id: NettyMiRpcClient.java, v 0.1 2021年03月23日 8:27 PM adomyzhao Exp $
 */
public class NettyMiRpcClient extends MiRpcClient {

    /**
     * Channel
     */
    private Channel channel;

    /**
     * 初始化处理
     * 
     * @param address
     * @param serializer
     * @param factory
     */
    @Override
    public void init(String address, MiRpcSerializer serializer,
                     MiRpcInvokerFactory factory) throws Exception {
        // address
        Object[] array = NetworkUtil.parseIpPort(address);
        String host = (String) array[0];
        int port = (int) array[1];

        // init
        final NettyMiRpcClient client = this;
        NioEventLoopGroup eventLoopGroup = MiRpcClientFactory.getNioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new IdleStateHandler(0, 0, HeartBeat.HEART_BEAT_INTERVAL,
                            TimeUnit.SECONDS))
                        .addLast(new NettyEncoder(MiRpcRequest.class, serializer))
                        .addLast(new NettyDecoder(MiRpcResponse.class, serializer))
                        .addLast(new NettyMiRpcClientHandler(factory, client));
                }
            })
            // option设置
            .option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

        this.channel = bootstrap.connect(host, port).sync().channel();

        // valid
        if (!isValid()) {
            this.channel.close();
            return;
        }

        LoggerUtil.debug(LOGGER,
            ">>>>>>>>>>> xxl-rpc netty client proxy, connect to server success at host:{}, port:{}",
            host, port);
    }

    /**
     * 关闭处理
     */
    @Override
    public void close() {
        if (isValid()) {
            this.channel.close();
        }
    }

    /**
     * 判断客户端是否有效
     * 
     * @return
     */
    @Override
    public boolean isValid() {
        return this.channel != null && this.channel.isActive();
    }

    /**
     * 消息发送处理
     * 
     * @param request
     * @throws Exception
     */
    @Override
    public void send(MiRpcRequest request) throws Exception {
        System.out.println("send request, request=" + request);
        this.channel.writeAndFlush(request).sync();
    }
}