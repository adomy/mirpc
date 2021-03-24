/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.impl.netty.server;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.adomy.mirpc.core.remoting.network.MiRpcServer;
import com.adomy.mirpc.core.remoting.network.heartbeat.HeartBeat;
import com.adomy.mirpc.core.remoting.network.impl.netty.codec.NettyDecoder;
import com.adomy.mirpc.core.remoting.network.impl.netty.codec.NettyEncoder;
import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;
import com.adomy.mirpc.core.remoting.provider.MiRpcProviderFactory;
import com.adomy.mirpc.core.util.except.MiRpcException;
import com.adomy.mirpc.core.util.logger.LoggerUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * RPC服务器的NETTY实现类
 * 
 * @author adomyzhao
 * @version $Id: MiRpcNettyServer.java, v 0.1 2021年03月23日 10:03 AM adomyzhao Exp $
 */
public class NettyMiRpcServer extends MiRpcServer {

    /**
     * 服务器线程
     */
    private Thread serverThread;

    /**
     * 启动处理
     * 
     * @param factory
     * @throws Exception
     */
    @Override
    public void start(MiRpcProviderFactory factory) throws Exception {
        serverThread = new Thread(() -> startInner(factory));
        serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * 启动处理的内部操作
     * 
     * @param factory
     */
    private void startInner(MiRpcProviderFactory factory) {

        // 第一步：构造服务处理的线程池
        String serverType = factory.getServerClass().getSimpleName();
        int coreSize = factory.getServerCoreSize();
        int maxSize = factory.getServerMaxSize();
        ThreadPoolExecutor serviceHandleThreadPool = new ThreadPoolExecutor(coreSize, maxSize, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(5000),
            r -> new Thread(r,
                "MiRpcServer: " + serverType + "-serviceHandleThreadPool-" + r.hashCode()),
            (r, executor) -> {
                throw new MiRpcException(
                    "MiRpcServer: " + serverType + " ServiceHandleThreadPool is EXHAUSTED!");
            });

        // 第二步：启动NettyServer
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            // 连接空闲监控处理
                            .addLast(new IdleStateHandler(0, 0, HeartBeat.HEART_BEAT_INTERVAL * 3,
                                TimeUnit.SECONDS))
                            // 解码器，负责解码客户端的请求
                            .addLast(new NettyDecoder(MiRpcRequest.class,
                                factory.getSerializerInstance()))
                            // 编码器，负责编码服务器的响应
                            .addLast(new NettyEncoder(MiRpcResponse.class,
                                factory.getSerializerInstance()))
                            // 核心处理器，负责调用代理服务，返回结果
                            .addLast(new NettyMiRpcServerHandler(factory, serviceHandleThreadPool));
                    }
                })
                // socket配置
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 端口绑定并监听处理，future为对应处理结果保存单元
            ChannelFuture future = bootstrap.bind(factory.getServerPort()).sync();

            System.out.println(factory.getServerPort());

            // 处理完成，回调执行钩子函数
            onStarted();

            // 结果处理
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                LoggerUtil.info(LOGGER, "MiRpc remoting server stop.");
            } else {
                LoggerUtil.info(LOGGER, "MiRpc remoting server error.");
            }
        } finally {
            // stop
            try {
                serviceHandleThreadPool.shutdown(); // shutdownNow
            } catch (Exception e) {
                LoggerUtil.error(LOGGER, e, e.getMessage());
            }
            try {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            } catch (Exception e) {
                LoggerUtil.error(LOGGER, e, e.getMessage());
            }
        }
    }

    /**
     * 停止处理
     * 
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {

        // 中断线程
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }

        // 停止后的回调钩子
        onStopped();

        LoggerUtil.info(LOGGER, "MiRpc remoting server destroy success.");
    }
}