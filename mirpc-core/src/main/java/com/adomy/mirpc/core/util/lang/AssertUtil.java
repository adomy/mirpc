/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.util.lang;

import java.util.Collection;

import com.adomy.mirpc.core.util.except.MiRpcException;

/**
 * 断言工具类
 * 
 * @author adomyzhao
 * @version $Id: AssertUtil.java, v 0.1 2021年03月23日 9:07 AM adomyzhao Exp $
 */
public class AssertUtil {

    /**
     * 断言为真
     *
     * @param condition 条件
     * @param message   错误描述
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new MiRpcException(message);
        }
    }

    /**
     * 断言为真
     *
     * @param condition 条件
     */
    public static void assertTrue(boolean condition) {
        assertTrue(condition, StringUtil.EMPTY);
    }

    /**
     * 断言为假
     *
     * @param condition 条件
     * @param message   错误描述
     */
    public static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    /**
     * 断言为假
     *
     * @param condition 条件
     */
    public static void assertFalse(boolean condition) {
        assertTrue(!condition, StringUtil.EMPTY);
    }

    /**
     * 不为空
     *
     * @param object 判断对象
     */
    public static void notNull(Object object) {
        notNull(object, "目标对象不能为空");
    }

    /**
     * 不为空
     *
     * @param object  判断对象
     * @param message 错误描述
     */
    public static void notNull(Object object, String message) {
        assertTrue(object != null, message);
    }

    /**
     * 字符串不为空
     *
     * @param str 字符串
     */
    public static void notBlank(String str) {
        notBlank(str, "字符串不能为空");
    }

    /**
     * 字符串不为空
     *
     * @param str     字符串
     * @param message 错误消息
     */
    public static void notBlank(String str, String message) {
        assertTrue(StringUtil.isNotBlank(str), message);
    }

    /**
     * 集合不为空
     *
     * @param collection 集合
     */
    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, "集合不能为空");
    }

    /**
     * 集合不为空
     *
     * @param collection 集合
     * @param message    错误消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        assertTrue(collection != null && !collection.isEmpty(), message);
    }

}