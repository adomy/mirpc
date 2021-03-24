/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.serialize;

/**
 * 序列化器
 * 
 * @author adomyzhao
 * @version $Id: Serializer.java, v 0.1 2021年03月23日 9:03 AM adomyzhao Exp $
 */
public interface MiRpcSerializer {

    /**
     * 序列化
     * 
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * 
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Object deserialize(byte[] bytes, Class<T> clazz);

}