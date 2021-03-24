/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.provider;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adomy.mirpc.core.registry.MiRpcRegistry;
import com.adomy.mirpc.core.registry.impl.local.LocalMiRpcRegistry;
import com.adomy.mirpc.core.remoting.network.MiRpcServer;
import com.adomy.mirpc.core.remoting.network.impl.netty.server.NettyMiRpcServer;
import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;
import com.adomy.mirpc.core.serialize.MiRpcSerializer;
import com.adomy.mirpc.core.serialize.impl.HessianMiRpcSerializer;
import com.adomy.mirpc.core.util.lang.AssertUtil;
import com.adomy.mirpc.core.util.lang.NetworkUtil;
import com.adomy.mirpc.core.util.lang.ServiceUtil;
import com.adomy.mirpc.core.util.lang.StringUtil;
import com.adomy.mirpc.core.util.lang.ThrowUtil;
import com.adomy.mirpc.core.util.logger.LoggerUtil;

/**
 * RPC服务提供者工厂
 * 
 * @author adomyzhao
 * @version $Id: MiRpcProviderFactory.java, v 0.1 2021年03月22日 8:40 PM adomyzhao Exp $
 */
public class MiRpcProviderFactory {

    private static final Logger              LOGGER           = LoggerFactory
        .getLogger(MiRpcProviderFactory.class);

    /**
     * 服务器线程池core大小
     */
    private int                              serverCoreSize;

    /**
     * 服务器线程池max大小
     */
    private int                              serverMaxSize;

    /**
     * 服务的IP地址
     */
    private String                           serverIp;

    /**
     * 服务的端口地址
     */
    private int                              serverPort;

    /**
     * 鉴权使用的accessToken
     */
    private String                           accessToken;

    /**
     * 服务注册地址
     * 
     * default use registryAddress to registry, otherwise use ip:port if registryAddress is null
     */
    private String                           registryAddress;

    /**
     * 服务注册参数
     */
    private Map<String, String>              registryParam;

    /**
     * 服务器类
     */
    private Class<? extends MiRpcServer>     serverClass      = NettyMiRpcServer.class;

    /**
     * 序列化类
     */
    private Class<? extends MiRpcSerializer> serializerClass  = HessianMiRpcSerializer.class;

    /**
     * 服务注册类名
     */
    private Class<? extends MiRpcRegistry>   registryClass    = LocalMiRpcRegistry.class;

    /**
     * 服务器实例
     */
    private MiRpcServer                      serverInstance;

    /**
     * RPC序列化实例
     */
    private MiRpcSerializer                  serializerInstance;

    /**
     * RPC注册器实例
     */
    private MiRpcRegistry                    registryInstance;

    /**
     * 服务Bean缓存
     */
    private Map<String, Object>              serviceBeanCache = new ConcurrentHashMap<>();

    /**
     * 启动处理
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        // 第一步：CLASS校验
        AssertUtil.notNull(serializerClass, "must specify rpc serializer class name.");
        this.serializerInstance = this.serializerClass.newInstance();

        AssertUtil.notNull(registryClass, "must specify rpc registry class name.");
        this.registryInstance = this.registryClass.newInstance();

        AssertUtil.notNull(serverClass, "must specify rpc server class name.");
        this.serverInstance = this.serverClass.newInstance();

        // 第二步：属性预处理
        this.serverCoreSize = this.serverCoreSize <= 0 ? 50 : this.serverCoreSize;
        this.serverMaxSize = this.serverMaxSize <= 0 ? 200 : this.serverMaxSize;
        this.serverIp = this.serverIp == null ? NetworkUtil.getIp() : this.serverIp;
        this.serverPort = this.serverPort <= 0 ? 5200 : this.serverPort;
        this.registryAddress = StringUtil.isBlank(this.registryAddress)
            ? NetworkUtil.getIpPort(this.serverIp, this.serverPort)
            : this.registryAddress;

        // 第三步：网络校验
        AssertUtil.assertFalse(NetworkUtil.isPortUsed(this.serverPort),
            "MiRpc provider port [" + this.serverPort + "] is already used.");

        // 第四步：设置钩子函数
        // 4.1 启动钩子
        this.serverInstance.setStartedHooker(() -> {
            if (this.registryInstance == null) {
                return;
            }

            // 启动注册器
            this.registryInstance.start(this.registryParam);

            // 服务注册
            this.registryInstance.register(this.serviceBeanCache.keySet(), this.registryAddress);
        });

        // 4.2 停止钩子
        this.serverInstance.setStoppedHooker(() -> {
            if (this.registryInstance == null) {
                return;
            }

            // 服务注销
            this.registryInstance.unRegister(this.serviceBeanCache.keySet(), this.registryAddress);

            // 服务注销
            this.registryInstance.stop();
        });

        // 第五步：启动处理
        this.serverInstance.start(this);
    }

    /**
     * 停止处理
     * 
     * @throws Exception
     */
    public void stop() throws Exception {
        this.serverInstance.stop();
    }

    /**
     * 添加服务
     * 
     * @throws Exception
     */
    public void addService(String interfaceName, String serviceVersion,
                           Object proxy) throws Exception {
        String serviceKey = ServiceUtil.generateServiceKey(interfaceName, serviceVersion);
        this.serviceBeanCache.put(serviceKey, proxy);

        LoggerUtil.info(LOGGER,
            "MiRpc, provider factory add service success. serviceKey = {}, serviceBean = {}",
            serviceKey, proxy.getClass());
    }

    /**
     * 调用服务
     * 
     * @param request
     * @return
     */
    public MiRpcResponse invokeService(MiRpcRequest request) {
        MiRpcResponse response = new MiRpcResponse();
        response.setSuccess(true);
        response.setRequestId(request.getRequestId());

        // 1. 生成服务查找KEY
        String serviceKey = ServiceUtil.generateServiceKey(request.getInterfaceName(),
            request.getVersion());
        Object serviceBean = serviceBeanCache.get(serviceKey);

        // 2. 服务代理不存在，直接返回失败
        if (serviceBean == null) {
            response.setSuccess(false);
            response.setErrorMsg("The serviceKey [" + serviceKey + "] not found.");
            return response;
        }

        // 3. 服务延迟过高，直接丢弃并返回失败
        if (System.currentTimeMillis() - request.getCreateTimeStamp() > 3 * 60 * 1000) {
            response.setSuccess(false);
            response.setErrorMsg(
                "The timestamp difference between admin and executor exceeds the limit.");
            return response;
        }

        // 4. 服务鉴权不匹配
        if (this.accessToken != null
            && !StringUtil.equals(request.getAccessToken(), this.accessToken)) {
            response.setSuccess(false);
            response.setErrorMsg("The access token [" + request.getAccessToken() + "] is wrong.");
            return response;
        }

        // 5. 执行invoke处理
        try {
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Class<?> beanClass = serviceBean.getClass();
            Method method = beanClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);

            // 反射执行方法
            Object result = method.invoke(serviceBean, parameters);

            response.setResultData(result);
        } catch (Exception e) {
            LoggerUtil.error(LOGGER, e, "MiRpc provider invokeService error.");
            response.setSuccess(false);
            response.setErrorMsg(ThrowUtil.getStackTraceAsString(e));
        }

        return response;
    }

    /**
     * Getter method for property serviceBeanCache.
     *
     * @return property value of serviceBeanCache
     */
    public Map<String, Object> getServiceBeanCache() {
        return serviceBeanCache;
    }

    /**
     * Getter method for property serverCoreSize.
     *
     * @return property value of serverCoreSize
     */
    public int getServerCoreSize() {
        return serverCoreSize;
    }

    /**
     * Getter method for property serverMaxSize.
     *
     * @return property value of serverMaxSize
     */
    public int getServerMaxSize() {
        return serverMaxSize;
    }

    /**
     * Getter method for property serverClass.
     *
     * @return property value of serverClass
     */
    public Class<? extends MiRpcServer> getServerClass() {
        return serverClass;
    }

    /**
     * Getter method for property serverInstance.
     *
     * @return property value of serverInstance
     */
    public MiRpcServer getServerInstance() {
        return serverInstance;
    }

    /**
     * Getter method for property serializerInstance.
     *
     * @return property value of serializerInstance
     */
    public MiRpcSerializer getSerializerInstance() {
        return serializerInstance;
    }

    /**
     * Getter method for property registryInstance.
     *
     * @return property value of registryInstance
     */
    public MiRpcRegistry getRegistryInstance() {
        return registryInstance;
    }

    /**
     * Getter method for property serverIp.
     *
     * @return property value of serverIp
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * Getter method for property serverPort.
     *
     * @return property value of serverPort
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Setter method for property serverClass.
     *
     * @param serverClass value to be assigned to property serverClass
     */
    public void setServerClass(Class<? extends MiRpcServer> serverClass) {
        this.serverClass = serverClass;
    }

    /**
     * Setter method for property serializerClass.
     *
     * @param serializerClass value to be assigned to property serializerClass
     */
    public void setSerializerClass(Class<? extends MiRpcSerializer> serializerClass) {
        this.serializerClass = serializerClass;
    }

    /**
     * Setter method for property serverIp.
     *
     * @param serverIp value to be assigned to property serverIp
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * Setter method for property serverPort.
     *
     * @param serverPort value to be assigned to property serverPort
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Setter method for property serverCoreSize.
     *
     * @param serverCoreSize value to be assigned to property serverCoreSize
     */
    public void setServerCoreSize(int serverCoreSize) {
        this.serverCoreSize = serverCoreSize;
    }

    /**
     * Setter method for property serverMaxSize.
     *
     * @param serverMaxSize value to be assigned to property serverMaxSize
     */
    public void setServerMaxSize(int serverMaxSize) {
        this.serverMaxSize = serverMaxSize;
    }

    /**
     * Setter method for property registryAddress.
     *
     * @param registryAddress value to be assigned to property registryAddress
     */
    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
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
     * Setter method for property registryClass.
     *
     * @param registryClass value to be assigned to property registryClass
     */
    public void setRegistryClass(Class<? extends MiRpcRegistry> registryClass) {
        this.registryClass = registryClass;
    }

    /**
     * Setter method for property registryParam.
     *
     * @param registryParam value to be assigned to property registryParam
     */
    public void setRegistryParam(Map<String, String> registryParam) {
        this.registryParam = registryParam;
    }

}
