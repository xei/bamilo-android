package com.bamilo.android.framework.service.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;

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

    @Nullable
    public IJSONSerializable createObject(@NonNull String object) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        //Print.i(TAG, "CREATE OBJECT");
        Class<?> objectClass = Class.forName(object);
        Object concreteObject = objectClass.newInstance();
        return (concreteObject instanceof IJSONSerializable) ? (IJSONSerializable) concreteObject : null;
    }
}
