/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.common;

/**
 * 通用回调处理器
 * 
 * @author adomyzhao
 * @version $Id: CommonCallback.java, v 0.1 2021年03月22日 9:03 PM adomyzhao Exp $
 */
public interface HandleHooker {

    /**
     * 执行处理
     */
    void run() throws Exception;
}