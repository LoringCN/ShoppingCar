/**
 * Copyright (c) 2005-2014 leelong.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package cn.huace.common.utils;

import java.io.StringWriter;

/**
 * 关于异常的工具类.
 * 
 * @author 陆小凤
 */
public class Exceptions
{
    
    private Exceptions()
    {
        super();
    }
    
    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Exception e)
    {
        if (e instanceof RuntimeException)
        {
            return (RuntimeException)e;
        }
        else
        {
            return new RuntimeException(e);
        }
    }
    
    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Exception e)
    {
        StringWriter stringWriter = new StringWriter();
        return stringWriter.toString();
    }
    
    /**
     * 判断异常是否由某些底层的异常引起.
     */
    public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses)
    {
        Throwable cause = ex;
        while (cause != null)
        {
            for (Class<? extends Exception> causeClass : causeExceptionClasses)
            {
                if (causeClass.isInstance(cause))
                {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }
}
