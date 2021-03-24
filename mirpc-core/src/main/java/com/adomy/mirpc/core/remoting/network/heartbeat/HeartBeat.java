/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.heartbeat;

import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;

/**
 * 心跳请求
 * 
 * @author adomyzhao
 * @version $Id: HeartBeat.java, v 0.1 2021年03月23日 4:44 PM adomyzhao Exp $
 */
public class HeartBeat {

    /**
     * 心跳间隔
     */
    public static final int    HEART_BEAT_INTERVAL = 30;

    /**
     * 心跳请求ID
     */
    public static final String HEART_BEAT_ID       = "BEAT_PING_PONG";

    /**
     * 心跳请求
     */
    public static MiRpcRequest HEART_BEAT_PING     = new MiRpcRequest(HEART_BEAT_ID);
}