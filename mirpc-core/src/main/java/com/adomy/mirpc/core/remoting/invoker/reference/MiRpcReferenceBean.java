/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.reference;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.adomy.mirpc.core.registry.MiRpcRegistry;
import com.adomy.mirpc.core.remoting.invoker.MiRpcInvokerFactory;
import com.adomy.mirpc.core.remoting.invoker.enums.InvokeTypeEnum;
import com.adomy.mirpc.core.remoting.invoker.future.MiRpcResponseFuture;
import com.adomy.mirpc.core.remoting.invoker.generic.MiRpcGenericService;
import com.adomy.mirpc.core.remoting.invoker.router.MiRpcLoadBalancer;
import com.adomy.mirpc.core.remoting.invoker.router.enums.LoadBalanceEnum;
import com.adomy.mirpc.core.remoting.network.MiRpcClient;
import com.adomy.mirpc.core.remoting.network.factory.MiRpcClientFactory;
import com.adomy.mirpc.core.remoting.network.impl.netty.client.NettyMiRpcClient;
import com.adomy.mirpc.core.remoting.network.request.MiRpcRequest;
import com.adomy.mirpc.core.remoting.network.response.MiRpcResponse;
import com.adomy.mirpc.core.serialize.MiRpcSerializer;
import com.adomy.mirpc.core.serialize.impl.HessianMiRpcSerializer;
import com.adomy.mirpc.core.util.except.MiRpcException;
import com.adomy.mirpc.core.util.lang.AssertUtil;
import com.adomy.mirpc.core.util.lang.ServiceUtil;
import com.adomy.mirpc.core.util.lang.StringUtil;

/**
 * @author adomyzhao
 * @version $Id: MiRpcReferenceBean.java, v 0.1 2021年03月23日 9:42 AM adomyzhao Exp $
 */
public class MiRpcReferenceBean {

    private static final Logger              LOGGER          = LoggerFactory
        .getLogger(MiRpcReferenceBean.class);

    /**
     * 客户端类
     */
    private Class<? extends MiRpcClient>     clientClass     = NettyMiRpcClient.class;

    /**
     * 序列化器类
     */
    private Class<? extends MiRpcSerializer> serializerClass = HessianMiRpcSerializer.class;

    /**
     * RPC Invoker 工厂
     */
    private MiRpcInvokerFactory              miRpcInvokerFactory;

    /**
     * 调用类型
     */
    private InvokeTypeEnum                   invokeType      = InvokeTypeEnum.SYNC;

    /**
     * 负载均衡模式
     */
    private LoadBalanceEnum                  loadBalance     = LoadBalanceEnum.RANDOM;

    /**
     * 目标接口类
     */
    private Class<?>                         interfaceClass;

    /**
     * 服务版本号
     */
    private String                           serviceVersion;

    /**
     * 执行超时毫秒数
     */
    private long                             timeout         = 3000;

    /**
     * 远端地址
     */
    private String                           remoteAddress;

    /**
     * 鉴权token
     */
    private String                           accessToken;

    /**
     * RPC Client实例
     */
    private MiRpcClient                      clientInstance;

    /**
     * RPC 序列化实例
     */
    private MiRpcSerializer                  serializerInstance;

    /**
     * 初始化处理
     */
    public void init() throws Exception {
        Assert.notNull(this.clientClass, "");
        Assert.notNull(this.serializerClass, "");
        Assert.notNull(this.invokeType, "");
        Assert.notNull(this.loadBalance, "");
        Assert.notNull(this.interfaceClass, "");

        this.miRpcInvokerFactory = this.miRpcInvokerFactory == null
            ? MiRpcInvokerFactory.getInstance()
            : this.miRpcInvokerFactory;

        this.serializerInstance = this.serializerClass.newInstance();
    }

    /**
     * 获取代理类
     * 
     * @return
     * @throws Exception
     */
    public Object getObject() throws Exception {
        init();

        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[] { this.interfaceClass }, (proxy, method, args) -> {
                String interfaceName = method.getDeclaringClass().getName();
                String serviceVersion = this.serviceVersion;
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] parameters = args;

                // 泛化调用处理逻辑
                if (StringUtil.equals(MiRpcGenericService.class.getName(), interfaceName)
                    && StringUtil.equals("invoke", methodName)) {
                    interfaceName = (String) args[0];
                    serviceVersion = (String) args[1];
                    methodName = (String) args[2];
                    parameterTypes = (Class[]) args[3];
                    parameters = (Object[]) args[4];
                }

                String finalAddress = remoteAddress;
                if (StringUtil.isNotBlank(finalAddress)
                    && miRpcInvokerFactory.getRegisterInstance() != null) {
                    String serviceKey = ServiceUtil.generateServiceKey(interfaceName,
                        serviceVersion);
                    MiRpcRegistry rpcRegistry = miRpcInvokerFactory.getRegisterInstance();
                    Set<String> addressSet = rpcRegistry.discovery(serviceKey);

                    MiRpcLoadBalancer loadBalancer = this.loadBalance.getLoadBalancer();
                    finalAddress = loadBalancer.route(serviceKey, addressSet);
                }

                AssertUtil.notBlank(finalAddress, "");

                // 拼接Request
                MiRpcRequest request = new MiRpcRequest(UUID.randomUUID().toString());
                request.setCreateTimeStamp(System.currentTimeMillis());
                request.setInterfaceName(interfaceName);
                request.setVersion(serviceVersion);
                request.setMethodName(methodName);
                request.setParameterTypes(parameterTypes);
                request.setParameters(parameters);

                this.clientInstance = MiRpcClientFactory.getOrBuildRpcClient(this,
                    NettyMiRpcClient.class, finalAddress);

                this.clientInstance.send(request);

                String requestId = request.getRequestId();
                MiRpcResponseFuture future = new MiRpcResponseFuture(request);
                this.miRpcInvokerFactory.addResponseFuture(requestId, future);

                try {
                    MiRpcResponse response = future.get(timeout, TimeUnit.MILLISECONDS);
                    if (response.isSuccess()) {
                        return response.getResultData();
                    } else {
                        throw new MiRpcException(response.getErrorMsg());
                    }
                } catch (Exception e) {
                    throw (e instanceof MiRpcException) ? e : new MiRpcException(e);
                } finally {
                    this.miRpcInvokerFactory.delResponseFuture(requestId);
                }
            });
    }

    /**
     * Setter method for property clientClass.
     *
     * @param clientClass value to be assigned to property clientClass
     */
    public void setClientClass(Class<? extends MiRpcClient> clientClass) {
        this.clientClass = clientClass;
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
     * Setter method for property miRpcInvokerFactory.
     *
     * @param miRpcInvokerFactory value to be assigned to property miRpcInvokerFactory
     */
    public void setMiRpcInvokerFactory(MiRpcInvokerFactory miRpcInvokerFactory) {
        this.miRpcInvokerFactory = miRpcInvokerFactory;
    }

    /**
     * Setter method for property invokeType.
     *
     * @param invokeType value to be assigned to property invokeType
     */
    public void setInvokeType(InvokeTypeEnum invokeType) {
        this.invokeType = invokeType;
    }

    /**
     * Setter method for property loadBalance.
     *
     * @param loadBalance value to be assigned to property loadBalance
     */
    public void setLoadBalance(LoadBalanceEnum loadBalance) {
        this.loadBalance = loadBalance;
    }

    /**
     * Setter method for property interfaceClass.
     *
     * @param interfaceClass value to be assigned to property interfaceClass
     */
    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    /**
     * Setter method for property serviceVersion.
     *
     * @param serviceVersion value to be assigned to property serviceVersion
     */
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    /**
     * Setter method for property timeout.
     *
     * @param timeout value to be assigned to property timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Setter method for property remoteAddress.
     *
     * @param remoteAddress value to be assigned to property remoteAddress
     */
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
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
     * Getter method for property clientClass.
     *
     * @return property value of clientClass
     */
    public Class<? extends MiRpcClient> getClientClass() {
        return clientClass;
    }

    /**
     * Getter method for property serializerClass.
     *
     * @return property value of serializerClass
     */
    public Class<? extends MiRpcSerializer> getSerializerClass() {
        return serializerClass;
    }

    /**
     * Getter method for property invokeType.
     *
     * @return property value of invokeType
     */
    public InvokeTypeEnum getInvokeType() {
        return invokeType;
    }

    /**
     * Getter method for property loadBalance.
     *
     * @return property value of loadBalance
     */
    public LoadBalanceEnum getLoadBalance() {
        return loadBalance;
    }

    /**
     * Getter method for property interfaceClass.
     *
     * @return property value of interfaceClass
     */
    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    /**
     * Getter method for property serviceVersion.
     *
     * @return property value of serviceVersion
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * Getter method for property timeout.
     *
     * @return property value of timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Getter method for property remoteAddress.
     *
     * @return property value of remoteAddress
     */
    public String getRemoteAddress() {
        return remoteAddress;
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
     * Getter method for property miRpcInvokerFactory.
     *
     * @return property value of miRpcInvokerFactory
     */
    public MiRpcInvokerFactory getMiRpcInvokerFactory() {
        return miRpcInvokerFactory;
    }

    /**
     * Getter method for property clientInstance.
     *
     * @return property value of clientInstance
     */
    public MiRpcClient getClientInstance() {
        return clientInstance;
    }

    /**
     * Getter method for property serializerInstance.
     *
     * @return property value of serializerInstance
     */
    public MiRpcSerializer getSerializerInstance() {
        return serializerInstance;
    }
}