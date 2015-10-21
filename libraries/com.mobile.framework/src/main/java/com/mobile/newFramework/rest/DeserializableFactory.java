package com.mobile.newFramework.rest;

import android.support.annotation.NonNull;

import com.mobile.newFramework.objects.IJSONSerializable;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.01
 * @date 2015/05/21
 */
public class DeserializableFactory {

    //private static final String TAG = DeserializableFactory.class.getSimpleName();

    public IJSONSerializable createObject(@NonNull String object) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        //Print.i(TAG, "CREATE OBJECT");
        Class<?> objectClass = Class.forName(object);
        Object concreteObject = objectClass.newInstance();
        return (concreteObject instanceof IJSONSerializable) ? (IJSONSerializable) concreteObject : null;
    }
}
