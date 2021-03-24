/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.provider.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.adomy.mirpc.core.remoting.provider.MiRpcProviderFactory;
import com.adomy.mirpc.core.remoting.provider.annotation.MiRpcService;
import com.adomy.mirpc.core.util.lang.AssertUtil;
import com.adomy.mirpc.core.util.lang.StringUtil;

/**
 * 基于Spring的RPC服务提供者工厂
 * 
 * @author adomyzhao
 * @version $Id: SpringMiRpcProviderFactory.java, v 0.1 2021年03月23日 8:16 PM adomyzhao Exp $
 */
public class SpringMiRpcProviderFactory extends MiRpcProviderFactory implements InitializingBean,
                                        ApplicationContextAware, DisposableBean {

    /**
     * 应用上下文
     */
    private ApplicationContext applicationContext;

    /**
     * 初始化
     * 
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // 步骤一：解析和添加服务
        this.parseAndAddService();

        // 步骤二：启动处理
        this.start();
    }

    /**
     * 解析和添加服务
     * 
     * @throws Exception
     */
    private void parseAndAddService() throws Exception {
        Map<String, Object> beanMap = this.applicationContext
            .getBeansWithAnnotation(MiRpcService.class);

        for (Object serviceBean : beanMap.values()) {
            AssertUtil.assertTrue(serviceBean.getClass().getInterfaces().length > 0,
                "MiRpc service(MiRpcService) must inherit interface.");

            MiRpcService service = serviceBean.getClass().getAnnotation(MiRpcService.class);

            String iFace = service.interfaceType().getName();
            if (StringUtil.isBlank(iFace)) {
                iFace = serviceBean.getClass().getInterfaces()[0].getName();
            }
            String iVersion = service.version();

            this.addService(iFace, iVersion, serviceBean);
        }
    }

    /**
     * 销毁处理
     * 
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        this.stop();
    }

    /**
     * setApplicationContext
     * 
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}