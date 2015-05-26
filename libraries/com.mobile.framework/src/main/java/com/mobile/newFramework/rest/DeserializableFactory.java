package com.mobile.newFramework.rest;

import com.mobile.newFramework.objects.IJSONSerializable;

/**
 * Created by rsoares on 5/21/15.
 */
public class DeserializableFactory {

    public IJSONSerializable createObject(String object) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        Class<?> objectClass = Class.forName(object);
        Object concreteObject = objectClass.newInstance();
        return (concreteObject instanceof IJSONSerializable) ? (IJSONSerializable) concreteObject : null;

    }
}
