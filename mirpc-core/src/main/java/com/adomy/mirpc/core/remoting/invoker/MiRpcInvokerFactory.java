/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adomy.mirpc.core.common.HandleHooker;
import com.adomy.mirpc.core.registry.MiRpcRegistry;
import com.adomy.mirpc.core.registry.impl.local.LocalMiRpcRegistry;
import com.adomy.mirpc.core.remoting.invoker.callback.MiRpcInvokeCallback;
import com.adomy.mirpc.core.remoting.invoker.future.MiRpcResponseFuture;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;
import com.adomy.mirpc.core.util.except.MiRpcException;
import com.adomy.mirpc.core.util.logger.LoggerUtil;

/**
 * 米RPC调用工厂
 *
 * @author adomyzhao
 * @version $Id: MiRpcInvokerFactory.java, v 0.1 2021年03月22日 8:40 PM adomyzhao Exp $
 */
public class MiRpcInvokerFactory {

    private static final Logger                 LOGGER                     = LoggerFactory
        .getLogger(MiRpcInvokerFactory.class);

    /**
     * instance
     */
    private static volatile MiRpcInvokerFactory instance                   = new MiRpcInvokerFactory(
        LocalMiRpcRegistry.class, null);

    /**
     * 服务注册类
     */
    private Class<? extends MiRpcRegistry>      registerClass;

    /**
     * 服务注册参数信息
     */
    private Map<String, String>                 registerParam;

    /**
     * 服务注册器实例
     */
    private MiRpcRegistry                       registerInstance;

    /**
     * 停止处理的回调列表
     */
    private List<HandleHooker>                  stoppedHookers             = new ArrayList<>();

    /**
     * future response cache
     */
    private Map<String, MiRpcResponseFuture>    responseFutureCache        = new ConcurrentHashMap<>();

    /**
     * 响应回调处理的线程池
     */
    private ThreadPoolExecutor                  responseCallbackThreadPool = new ThreadPoolExecutor(
        20, 100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000),
        runnable -> new Thread(runnable, "MiRpc-responseCallbackThreadPool-" + runnable.hashCode()),
        (runnable, executor) -> {
                                                                                   throw new MiRpcException(
                                                                                       "MiRpc Invoke Callback Thread Pool is EXHAUSTED!");
                                                                               });

    /**
     * 构造函数
     * 
     * @param registerClass
     * @param registerParam
     */
    public MiRpcInvokerFactory(Class<? extends MiRpcRegistry> registerClass,
                               Map<String, String> registerParam) {
        this.registerClass = registerClass;
        this.registerParam = registerParam;
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static MiRpcInvokerFactory getInstance() {
        return instance;
    }

    /**
     * 启动处理
     */
    public void start() throws IllegalAccessException, InstantiationException {
        if (registerClass == null) {
            return;
        }

        registerInstance = this.registerClass.newInstance();
        registerInstance.start(this.registerParam);
    }

    /**
     * 停止处理
     */
    public void stop() {
        if (registerInstance != null) {
            registerInstance.stop();
        }

        if (!stoppedHookers.isEmpty()) {
            for (HandleHooker hooker : stoppedHookers) {
                try {
                    hooker.run();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    /**
     * 添加停止处理的hooker
     *
     * @param hooker
     */
    public void addStoppedHooker(HandleHooker hooker) {
        stoppedHookers.add(hooker);
    }

    /**
     * 将响应结果放入Future中
     *
     * @param requestId
     * @param response
     */
    public void notifyResponseFuture(String requestId, MiRpcResponse response) {
        MiRpcResponseFuture responseFuture = this.responseFutureCache.get(requestId);
        if (responseFuture == null) {
            return;
        }

        // 回调模式
        if (responseFuture.getCallback() != null) {
            try {
                this.responseCallbackThreadPool.execute(() -> {
                    MiRpcInvokeCallback callback = responseFuture.getCallback();

                    if (response.isSuccess()) {
                        callback.onSuccess(response.getResultData());
                    } else {
                        callback.onFailure(new MiRpcException(response.getErrorMsg()));
                    }
                });
            } catch (Exception e) {
                LoggerUtil.error(LOGGER, e, "回调处理异常");
            }
        }
        // 同步模式
        else {
            responseFuture.put(response);
        }

        // 移除本future
        this.delResponseFuture(requestId);
    }

    /**
     * 将future放入缓存中
     *
     * @param future
     */
    public void addResponseFuture(String requestId, MiRpcResponseFuture future) {
        this.responseFutureCache.put(requestId, future);
    }

    /**
     * 从缓存中移除future
     *
     * @param requestId
     */
    public void delResponseFuture(String requestId) {
        this.responseFutureCache.remove(requestId);
    }

    /**
     * Setter method for property registerClass.
     *
     * @param registerClass value to be assigned to property registerClass
     */
    public void setRegisterClass(Class<? extends MiRpcRegistry> registerClass) {
        this.registerClass = registerClass;
    }

    /**
     * Setter method for property registerParam.
     *
     * @param registerParam value to be assigned to property registerParam
     */
    public void setRegisterParam(Map<String, String> registerParam) {
        this.registerParam = registerParam;
    }

    /**
     * Getter method for property registerInstance.
     *
     * @return property value of registerInstance
     */
    public MiRpcRegistry getRegisterInstance() {
        return registerInstance;
    }
}