/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.response;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 米RPC响应
 * 
 * @author adomyzhao
 * @version $Id: MiRpcResponse.java, v 0.1 2021年03月23日 5:07 PM adomyzhao Exp $
 */
public class MiRpcResponse implements Serializable {

    private static final long serialVersionUID = 1245422452L;

    /**
     * 请求ID
     */
    private String            requestId;

    /**
     * 是否成功
     */
    private boolean           success;

    /**
     * 错误信息
     */
    private String            errorMsg;

    /**
     * 结果数据
     */
    private Object            resultData;

    /**
     * Getter method for property requestId.
     *
     * @return property value of requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Setter method for property requestId.
     *
     * @param requestId value to be assigned to property requestId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Getter method for property success.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter method for property success.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter method for property errorMsg.
     *
     * @return property value of errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * Setter method for property errorMsg.
     *
     * @param errorMsg value to be assigned to property errorMsg
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * Getter method for property resultData.
     *
     * @return property value of resultData
     */
    public Object getResultData() {
        return resultData;
    }

    /**
     * Setter method for property resultData.
     *
     * @param resultData value to be assigned to property resultData
     */
    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

    /**
     * toString
     * 
     * @return
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", MiRpcResponse.class.getSimpleName() + "[", "]")
            .add("requestId='" + requestId + "'").add("errorMsg='" + errorMsg + "'")
            .add("resultData=" + resultData).toString();
    }
}