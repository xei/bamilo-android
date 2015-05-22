package com.mobile.newFramework.rest;


import com.mobile.framework.objects.CountryConfigs;
import com.mobile.framework.objects.IJSONSerializable;

/**
 * Created by rsoares on 5/21/15.
 */
public class DeserializableFactory {

    public IJSONSerializable createObject(String object){
        if(object.equals(CountryConfigs.class.getName())){
            return new CountryConfigs();
        }
        return null;
    }
}
