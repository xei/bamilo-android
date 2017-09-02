package com.mobile.service.pojo;

import com.mobile.service.objects.IJSONSerializable;

/**
 * Class used to save the data, that can be Void.
 * @author rsoares
 */
public class Metadata<T> {
    private String md5;
    private T data;

    public void setData(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public void setData(IJSONSerializable ijsonSerializable) {
        data = (T) ijsonSerializable;
    }

    public T getData() {
        return this.data;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
