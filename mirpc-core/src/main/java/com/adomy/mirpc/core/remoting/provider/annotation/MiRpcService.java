/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者注解
 * 
 * @author adomyzhao
 * @version $Id: MiRpcService.java, v 0.1 2021年03月23日 8:15 PM adomyzhao Exp $
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MiRpcService {

    /**
     * 接口类型
     * 
     * @return
     */
    Class<?> interfaceType() default void.class;

    /**
     * 版本
     * 
     * @return 版本号
     */
    String version() default "DEFAULT";
}