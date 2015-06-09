package com.mobile.newFramework.utils;


@Deprecated
public class LogTagHelper {

    public static String create(Class<? extends Object> clazz) {
        return clazz.getSimpleName();
    }
}
