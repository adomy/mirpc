/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.request;

import java.io.Serializable;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * 米RPC请求
 * 
 * @author adomyzhao
 * @version $Id: MiRpcRequest.java, v 0.1 2021年03月23日 12:55 PM adomyzhao Exp $
 */
public class MiRpcRequest implements Serializable {

    private static final long serialVersionUID = 1245422452L;

    /**
     * 请求ID
     */
    private String            requestId;

    /**
     * 创建时间戳
     */
    private long              createTimeStamp;

    /**
     * 鉴权token
     */
    private String            accessToken;

    /**
     * 服务接口名
     */
    private String            interfaceName;

    /**
     * 服务版本号
     */
    private String            version;

    /**
     * 调用方法名
     */
    private String            methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[]        parameterTypes;

    /**
     * 参数实例列表
     */
    private Object[]          parameters;

    /**
     * RPC请求构造函数
     * 
     * @param requestId
     */
    public MiRpcRequest(String requestId) {
        this.requestId = requestId;
    }

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
     * Getter method for property createTimeStamp.
     *
     * @return property value of createTimeStamp
     */
    public Long getCreateTimeStamp() {
        return createTimeStamp;
    }

    /**
     * Setter method for property createTimeStamp.
     *
     * @param createTimeStamp value to be assigned to property createTimeStamp
     */
    public void setCreateTimeStamp(Long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    /**
     * Getter method for property accessToken.
     *
     * @return property value of accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Setter method for property accessToken.
     *
     * @param accessToken value to be assigned to property accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Getter method for property interfaceName.
     *
     * @return property value of interfaceName
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Setter method for property interfaceName.
     *
     * @param interfaceName value to be assigned to property interfaceName
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * Getter method for property version.
     *
     * @return property value of version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter method for property version.
     *
     * @param version value to be assigned to property version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Getter method for property methodName.
     *
     * @return property value of methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Setter method for property methodName.
     *
     * @param methodName value to be assigned to property methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Getter method for property parameterTypes.
     *
     * @return property value of parameterTypes
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Setter method for property parameterTypes.
     *
     * @param parameterTypes value to be assigned to property parameterTypes
     */
    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * Getter method for property parameters.
     *
     * @return property value of parameters
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Setter method for property parameters.
     *
     * @param parameters value to be assigned to property parameters
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    /**
     * toString
     * 
     * @return
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", MiRpcRequest.class.getSimpleName() + "[", "]")
            .add("requestId='" + requestId + "'").add("createTimeStamp=" + createTimeStamp)
            .add("accessToken='" + accessToken + "'").add("interfaceName='" + interfaceName + "'")
            .add("version='" + version + "'").add("methodName='" + methodName + "'")
            .add("parameterTypes=" + Arrays.toString(parameterTypes)).toString();
    }
}