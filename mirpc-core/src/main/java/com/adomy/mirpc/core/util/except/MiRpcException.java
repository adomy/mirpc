/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.adomy.mirpc.core.util.except;

/**
 * 演练平台异常类
 *
 * @author adomyzhao
 * @version $Id: MiRpcException.java, v 0.1 2019年10月15日 下午7:43 adomyzhao Exp $
 */
public class MiRpcException extends RuntimeException {

    private static final long serialVersionUID = 1267832138L;

    /**
     * 默认构造函数
     */
    public MiRpcException() {
        super();
    }

    /**
     * 构造函数1
     * 
     * @param msg
     */
    public MiRpcException(String msg) {
        super(msg);
    }

    /**
     * 构造函数2
     * 
     * @param cause
     */
    public MiRpcException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数3
     * 
     * @param msg
     * @param cause
     */
    public MiRpcException(String msg, Throwable cause) {
        super(msg, cause);
    }
}