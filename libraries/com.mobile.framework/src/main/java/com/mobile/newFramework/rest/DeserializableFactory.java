package com.mobile.newFramework.rest;

import com.mobile.newFramework.objects.IJSONSerializable;

/**
 * Created by rsoares on 5/21/15.
 */
public class DeserializableFactory {

    //private static final String TAG = DeserializableFactory.class.getSimpleName();

    public IJSONSerializable createObject(String object) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        //Print.i(TAG, "CREATE OBJECT");
        Class<?> objectClass = Class.forName(object);
        Object concreteObject = objectClass.newInstance();
        return (concreteObject instanceof IJSONSerializable) ? (IJSONSerializable) concreteObject : null;
    }
}
