/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.adomy.mirpc.core.remoting.invoker.callback.MiRpcInvokeCallback;
import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;
import com.adomy.mirpc.core.util.except.MiRpcException;

/**
 * @author adomyzhao
 * @version $Id: MiRpcFutureResponse.java, v 0.1 2021年03月24日 1:32 PM adomyzhao Exp $
 */
public class MiRpcResponseFuture {

    /**
     * 请求
     */
    private MiRpcRequest        request;

    /**
     * 响应
     */
    private MiRpcResponse       response;

    /**
     * 锁
     */
    private CountDownLatch      countDownLatch;

    /**
     * 回调处理
     */
    private MiRpcInvokeCallback callback;

    /**
     * 构造函数
     *
     * @param request
     */
    public MiRpcResponseFuture(MiRpcRequest request) {
        this.request = request;
        this.countDownLatch = new CountDownLatch(1);
    }

    /**
     * 构造函数
     * 
     * @param request
     * @param callback
     */
    public MiRpcResponseFuture(MiRpcRequest request, MiRpcInvokeCallback callback) {
        this.request = request;
        this.callback = callback;
        this.countDownLatch = new CountDownLatch(1);
    }

    /**
     * 是否已完成
     * 
     * @return
     */
    public boolean isDone() {
        return this.countDownLatch.getCount() <= 0;
    }

    /**
     * 获取请求ID
     * 
     * @return
     */
    public String getRequestId() {
        return this.request.getRequestId();
    }

    /**
     * Getter method for property callback.
     *
     * @return property value of callback
     */
    public MiRpcInvokeCallback getCallback() {
        return callback;
    }

    /**
     * 放入响应结果
     * 
     * @param response
     */
    public void put(MiRpcResponse response) {
        this.response = response;
        this.countDownLatch.countDown();
    }

    /**
     * 阻塞获取
     * 
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public MiRpcResponse get() throws InterruptedException, ExecutionException {
        try {
            return get(-1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new MiRpcException(e);
        }
    }

    /**
     * 超时获取
     * 
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public MiRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException,
                                                          ExecutionException, TimeoutException {
        System.out.println(System.currentTimeMillis());
        if (timeout < 0) {
            countDownLatch.await();
        } else {
            countDownLatch.await(timeout, unit);
        }
        System.out.println(System.currentTimeMillis());
        return response;
    }
}