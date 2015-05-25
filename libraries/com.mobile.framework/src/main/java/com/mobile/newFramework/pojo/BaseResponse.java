package com.mobile.newFramework.pojo;

import com.mobile.newFramework.rest.JumiaError;

import java.util.List;
import java.util.Map;

/**
 * Created by spereira on 5/19/15.
 */
public class BaseResponse<T>{
    public boolean success;
    public Map<String,List<String>> messages;
    //TODO remove message variable as soon as possible
    public String message;
    public Metadata<T> metadata;
    public Map<String, String> sessions;
    public JumiaError error;

    public BaseResponse(){
        metadata = new Metadata<T>();
    }

}


