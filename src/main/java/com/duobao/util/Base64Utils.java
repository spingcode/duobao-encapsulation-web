package com.duobao.util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by Administrator on 2017/11/22 0022.
 */
public class Base64Utils {
    public static String encodeBase64(byte[] bytes){
        Object retObj= null;
        try {
            byte[] input = bytes;
            Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
            Method mainMethod= clazz.getMethod("encode", byte[].class);
            mainMethod.setAccessible(true);
            retObj = mainMethod.invoke(null, new Object[]{input});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return (String)retObj;
    }
    /***
     * decode by Base64
     */
    public static String decodeBase64(String input) {
        Object retObj= null;
        try {
            Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
            Method mainMethod= clazz.getMethod("decode", String.class);
            mainMethod.setAccessible(true);
            retObj = mainMethod.invoke(null, input);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new String((byte[])retObj);
    }
    
    /**
     * 将 BASE64 编码的字符串 s 进行解码
     *
     * @return String
     * @author lifq
     * @date 2015-3-4 上午09:24:26
     */
    public static String decode(String s) {
    	return new String(Base64.decodeBase64(s.getBytes()));  
    }
}