package com.mobile.newFramework.pojo;

import com.mobile.newFramework.objects.IJSONSerializable;

/**
 * Created by rsoares on 5/21/15.
 */
public class Metadata<T> {
    private String md5;
    private T data;

    public void setData(T data){
        this.data = data;
    }

    public void setData(IJSONSerializable ijsonSerializable){
        data = (T)ijsonSerializable;
    }
}
