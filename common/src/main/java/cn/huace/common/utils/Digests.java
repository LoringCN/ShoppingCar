/**
 * Copyright (c) 2005-2014 leelong.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package cn.huace.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * 支持SHA-1/MD5消息摘要的工具类.
 * 
 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
 * 
 * @author 陆小凤
 */
public class Digests
{
    
    private static final String SHA1 = "SHA-1";
    
    private static final String MD5 = "MD5";
    
    private static SecureRandom random = new SecureRandom();

    private static final String CHARSET = "UTF-8";
    /**
     * 对输入字符串进行sha1散列.
     */
    public static byte[] sha1(byte[] input)
    {
        return digest(input, SHA1, null, 1);
    }
    
    public static byte[] sha1(byte[] input, byte[] salt)
    {
        return digest(input, SHA1, salt, 1);
    }
    
    public static byte[] sha1(byte[] input, byte[] salt, int iterations)
    {
        return digest(input, SHA1, salt, iterations);
    }
    
    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            
            if (salt != null)
            {
                digest.update(salt);
            }
            
            byte[] result = digest.digest(input);
            
            for (int i = 1; i < iterations; i++)
            {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        }
        catch (GeneralSecurityException e)
        {
            throw Exceptions.unchecked(e);
        }
    }
    
    /**
     * 生成随机的Byte[]作为salt.
     * 
     * @param numBytes byte数组的大小
     */
    public static byte[] generateSalt(int numBytes)
    {
        Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);
        
        byte[] bytes = new byte[numBytes];
        random.nextBytes(bytes);
        return bytes;
    }
    
    /**
     * 对文件进行md5散列.
     */
    public static byte[] md5(InputStream input)
        throws IOException
    {
        return digest(input, MD5);
    }
    
    /**
     * 对文件进行sha1散列.
     */
    public static byte[] sha1(InputStream input)
        throws IOException
    {
        return digest(input, SHA1);
    }
    
    private static byte[] digest(InputStream input, String algorithm)
        throws IOException
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int bufferLength = 8 * 1024;
            byte[] buffer = new byte[bufferLength];
            int read = input.read(buffer, 0, bufferLength);
            
            while (read > -1)
            {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, bufferLength);
            }
            
            return messageDigest.digest();
        }
        catch (GeneralSecurityException e)
        {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * md5加密，返回加密后的字符串
     * @param str 待加密字符串
     * @param charset 字符集，为null时，默认使用utf-8字符集
     * @return 加密后字符串
     * @throws UnsupportedEncodingException
     */
    public static String md5(String str,String charset) throws UnsupportedEncodingException {
        if(StringUtils.isEmpty(str)){
            return null;
        }
        byte[] tmpBytes = StringUtils.isEmpty(charset)?str.getBytes():str.getBytes(CHARSET);
        byte[] bytes = digest(tmpBytes,MD5,null,1);
        return byteToHexString(bytes);
    }
    private static String byteToHexString(byte[] bytes){
        int j;
        StringBuilder sb = new StringBuilder();
        for (int i =0;i<bytes.length;i++){
            j = bytes[i];
            if(j < 0){
                j += 256;
            }
            if(j >= 0 && j < 16){
                sb.append("0");
            }
            sb.append(Integer.toHexString(j));
        }
        return sb.toString();
    }
}
