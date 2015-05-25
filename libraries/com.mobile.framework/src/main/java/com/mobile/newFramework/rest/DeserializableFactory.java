package com.mobile.newFramework.rest;


import com.mobile.newFramework.objects.AvailableCountries;
import com.mobile.newFramework.objects.CountryConfigs;
import com.mobile.newFramework.objects.IJSONSerializable;

/**
 * Created by rsoares on 5/21/15.
 */
public class DeserializableFactory {

    public IJSONSerializable createObject(String object){
        if(object.equals(CountryConfigs.class.getName())){
            return new CountryConfigs();
        } else if(object.equals(AvailableCountries.class.getName())){
            return new AvailableCountries();
        }

        return null;
    }
}
