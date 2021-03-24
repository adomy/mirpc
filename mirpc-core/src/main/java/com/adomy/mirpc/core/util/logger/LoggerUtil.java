/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.adomy.mirpc.core.util.logger;

import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * 日志打印工具，注意日志的级别选择<br>
 *
 * @author tangzw
 * @version $Id: LoggerUtil.java, v 0.1 2019年09月24日 下午9:29 tangzw Exp $
 */
public final class LoggerUtil {

    /**
     * 输出debug日志
     *
     * @param logger    logger
     * @param msgPattern  格式化信息,错误日志上下文信息描述，尽量带上业务特征
     * @param args   格式化参数
     */
    public static void debug(final Logger logger, final String msgPattern, final Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(msgPattern, args));
        }
    }

    /**
     * 生成<font color="blue">通知</font>级别日志<br>
     *
     * @param logger
     * @param msg
     */
    public static void info(Logger logger, String msg) {
        if (logger.isInfoEnabled()) {
            logger.info(format(msg));
        }
    }

    /**
     * 生成<font color="blue">通知</font>级别日志<br> 通过消息模版占位符{num},如:{0}，可处理任意多个输入参数
     * <p>
     * example: infoWithPattern("key1={},key2={}", value1,value2);
     *
     * @param logger
     * @param msgPattern
     * @param args
     */
    public static void info(Logger logger, String msgPattern, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(format(msgPattern, args));
        }
    }

    /**
     * 生成<font color="brown">警告</font>级别日志<br>
     *
     * @param logger
     * @param msg
     */
    public static void warn(Logger logger, String msg) {
        logger.warn(format(msg));
    }

    /**
     * 生成<font color="brown">警告</font>级别日志<br> 通过消息模版占位符{},可处理任意多个输入参数
     *
     * @param logger
     * @param msgPattern
     * @param obj
     */
    public static void warn(Logger logger, String msgPattern, Object... obj) {
        logger.warn(format(msgPattern, obj));
    }

    /**
     * 生成<font color="brown">警告</font>级别日志<br> 通过消息模版占位符{},可处理任意多个输入参数，同时输出异常栈信息
     *
     * @param logger
     * @param ex
     * @param msgPattern
     * @param obj
     */
    public static void warn(Logger logger, Throwable ex, String msgPattern, Object... obj) {
        logger.warn(format(msgPattern, obj), ex);
    }

    /**
     * 生成<font color="brown">error</font>级别日志<br> 通过消息模版占位符{},可处理任意多个输入参数
     *
     * @param logger
     * @param msgPattern
     * @param args
     */
    public static void error(Logger logger, String msgPattern, Object... args) {
        logger.error(format(msgPattern, args));
    }

    /**
     * 生成<font color="brown">error</font>级别日志<br>, 通过消息模版占位符{},可处理任意多个输入参数，同时输出异常栈信息
     * @param logger
     * @param ex
     * @param msgPattern
     * @param args
     */
    public static void error(Logger logger, Throwable ex, String msgPattern, Object... args) {
        logger.error(format(msgPattern, args), ex);
    }

    /**
     * 根据日志格式pattern，格式化日志，默认携带线程信息
     *
     * @param pattern
     * @param obj
     * @return
     */
    public static String format(String pattern, Object... obj) {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(pattern, obj);
        return "[" + Thread.currentThread().getId() + "]" + formattingTuple.getMessage();
    }
}