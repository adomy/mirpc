/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.callback;

/**
 * 米RPC调用回调接口
 * 
 * @author adomyzhao
 * @version $Id: MiRpcInvokeCallback.java, v 0.1 2021年03月24日 2:36 PM adomyzhao Exp $
 */
public interface MiRpcInvokeCallback<T> {

    /**
     * 成功时处理
     * 
     * @param result
     */
    void onSuccess(T result);

    /**
     * 失败时处理
     * 
     * @param exception
     */
    void onFailure(Throwable exception);
}