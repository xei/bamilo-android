package com.mobile.newFramework.pojo;

import java.util.List;

/**
 * Created by spereira on 5/19/15.
 */
public class BaseResponse<T>{
    public String success;
    public List<String> messages;
    public Metadata<T> metadata;

    public BaseResponse(){
        metadata = new Metadata<T>();
    }

}


