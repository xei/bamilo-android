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

    public T getData(){
        return this.data;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
