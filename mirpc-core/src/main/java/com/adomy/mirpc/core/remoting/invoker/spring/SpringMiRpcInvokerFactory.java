/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.invoker.spring;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

import com.adomy.mirpc.core.registry.MiRpcRegistry;
import com.adomy.mirpc.core.remoting.invoker.MiRpcInvokerFactory;
import com.adomy.mirpc.core.remoting.invoker.annotation.MiRpcReference;
import com.adomy.mirpc.core.remoting.invoker.reference.MiRpcReferenceBean;
import com.adomy.mirpc.core.util.lang.AssertUtil;
import com.adomy.mirpc.core.util.lang.ServiceUtil;
import com.adomy.mirpc.core.util.logger.LoggerUtil;

/**
 * @author adomyzhao
 * @version $Id: SpringMiRpcInvokerFactory.java, v 0.1 2021年03月24日 1:34 PM adomyzhao Exp $
 */
public class SpringMiRpcInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter
                                       implements InitializingBean, DisposableBean {

    private static final Logger            LOGGER = LoggerFactory
        .getLogger(SpringMiRpcInvokerFactory.class);

    private Class<? extends MiRpcRegistry> resgiterClass;

    private Map<String, String>            resgiterParam;

    private MiRpcInvokerFactory            invokerFactory;

    @Override
    public boolean postProcessAfterInstantiation(Object bean,
                                                 String beanName) throws BeansException {

        // collection
        final Set<String> serviceKeyList = new HashSet<>();

        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (!field.isAnnotationPresent(MiRpcReference.class)) {
                return;
            }

            Class<?> interfaceClass = field.getType();
            AssertUtil.assertTrue(interfaceClass.isInterface(),
                "reference(MiRpcReference) must be interface.");

            MiRpcReference rpcReference = field.getAnnotation(MiRpcReference.class);

            MiRpcReferenceBean referenceBean = new MiRpcReferenceBean();
            referenceBean.setClientClass(rpcReference.client());
            referenceBean.setSerializerClass(rpcReference.serializer());
            referenceBean.setInvokeType(rpcReference.invokeType());
            referenceBean.setLoadBalance(rpcReference.loadBalance());
            referenceBean.setInterfaceClass(interfaceClass);
            referenceBean.setServiceVersion(rpcReference.version());
            referenceBean.setTimeout(rpcReference.timeout());
            referenceBean.setRemoteAddress(rpcReference.address());
            referenceBean.setAccessToken(rpcReference.accessToken());

            referenceBean.setMiRpcInvokerFactory(invokerFactory);

            Object serviceProxy;
            try {
                serviceProxy = referenceBean.getObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            field.setAccessible(true);
            field.set(bean, serviceProxy);

            String serviceKey = ServiceUtil.generateServiceKey(interfaceClass.getName(),
                rpcReference.version());
            serviceKeyList.add(serviceKey);
        });

        if (invokerFactory.getRegisterInstance() != null) {
            try {
                invokerFactory.getRegisterInstance().discovery(serviceKeyList);
            } catch (Exception e) {
                LoggerUtil.error(LOGGER, e, "");
            }
        }

        return super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.invokerFactory = MiRpcInvokerFactory.getInstance();
        this.invokerFactory.setRegisterClass(resgiterClass);
        this.invokerFactory.setRegisterParam(resgiterParam);
        this.invokerFactory.start();
    }

    @Override
    public void destroy() throws Exception {
        this.invokerFactory.stop();
    }

    /**
     * Setter method for property resgiterClass.
     *
     * @param resgiterClass value to be assigned to property resgiterClass
     */
    public void setResgiterClass(Class<? extends MiRpcRegistry> resgiterClass) {
        this.resgiterClass = resgiterClass;
    }

    /**
     * Setter method for property resgiterParam.
     *
     * @param resgiterParam value to be assigned to property resgiterParam
     */
    public void setResgiterParam(Map<String, String> resgiterParam) {
        this.resgiterParam = resgiterParam;
    }

}