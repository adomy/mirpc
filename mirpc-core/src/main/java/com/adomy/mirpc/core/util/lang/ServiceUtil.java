/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.util.lang;

/**
 * @author adomyzhao
 * @version $Id: ServiceUtil.java, v 0.1 2021年03月24日 12:59 PM adomyzhao Exp $
 */
public class ServiceUtil {

    /**
     * 生成服务唯一KEY
     *
     * @param interfaceName
     * @param serviceVersion
     * @return
     */
    public static String generateServiceKey(String interfaceName, String serviceVersion) {
        if (StringUtil.isBlank(serviceVersion)) {
            serviceVersion = "DEFAULT";
        }

        return interfaceName + "#" + serviceVersion;
    }
}