/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adomy.mirpc.core.common.HandleHooker;
import com.adomy.mirpc.core.remoting.provider.MiRpcProviderFactory;
import com.adomy.mirpc.core.util.logger.LoggerUtil;

/**
 * RPC服务器抽象类
 * 
 * @author adomyzhao
 * @version $Id: MiRpcServer.java, v 0.1 2021年03月23日 9:34 AM adomyzhao Exp $
 */
public abstract class MiRpcServer {

    /**
     * 日志实例
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(MiRpcServer.class);

    /**
     * 启动后执行的钩子
     */
    private HandleHooker          startedHooker;

    /**
     * 停止后执行的钩子
     */
    private HandleHooker          stoppedHooker;

    /**
     * 抽象方法 - 启动处理
     * 
     * @param factory
     * @throws Exception
     */
    public abstract void start(final MiRpcProviderFactory factory) throws Exception;

    /**
     * 抽象方法 - 停止处理
     * 
     * @throws Exception
     */
    public abstract void stop() throws Exception;

    /**
     * 启动后执行
     */
    public void onStarted() {
        if (startedHooker == null) {
            return;
        }

        try {
            startedHooker.run();
        } catch (Exception e) {
            LoggerUtil.error(LOGGER, e, "MiRpcServer onStarted handle error.");
        }
    }

    /**
     * 停止后执行
     */
    public void onStopped() {
        if (stoppedHooker == null) {
            return;
        }

        try {
            stoppedHooker.run();
        } catch (Exception e) {
            LoggerUtil.error(LOGGER, e, "MiRpcServer onStopped handle error.");
        }
    }

    /**
     * Setter method for property startedHooker.
     *
     * @param startedHooker value to be assigned to property startedHooker
     */
    public void setStartedHooker(HandleHooker startedHooker) {
        this.startedHooker = startedHooker;
    }

    /**
     * Setter method for property stoppedHooker.
     *
     * @param stoppedHooker value to be assigned to property stoppedHooker
     */
    public void setStoppedHooker(HandleHooker stoppedHooker) {
        this.stoppedHooker = stoppedHooker;
    }
}