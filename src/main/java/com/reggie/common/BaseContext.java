package com.reggie.common;

/**
 * @ClassName BaseContext
 * @Date 2022/10/7 9:06
 * 基于ThreadLocal封装工具类BaseContext，用户保存和获取当前登录用户的id
 *
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getId() {
        return threadLocal.get();
    }



}
