/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.util.lang;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常处理工具
 * 
 * @author adomyzhao
 * @version $Id: ThrowUtil.java, v 0.1 2021年03月23日 5:21 PM adomyzhao Exp $
 */
public class ThrowUtil {

    /**
     * Returns a string containing the result of {@link Throwable#toString() toString()}, followed by
     * the full, recursive stack trace of {@code throwable}. Note that you probably should not be
     * parsing the resulting string; if you need programmatic access to the stack frames, you can call
     * {@link Throwable#getStackTrace()}.
     */
    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}