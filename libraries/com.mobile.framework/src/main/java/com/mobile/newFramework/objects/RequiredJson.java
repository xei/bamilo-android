package com.mobile.newFramework.objects;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Type of json structures.
 * @author ricardo
 */
public class RequiredJson {

    public static final int NONE = -1;
    public static final int COMPLETE_JSON = 1;
    public static final int OBJECT_DATA = 2;
    public static final int METADATA = 3;
    public static final int FORM_ENTITY = 4;
    public static final int CART_ENTITY = 5;
    public static final int CUSTOMER_ENTITY = 6;

    @IntDef({
            NONE,
            COMPLETE_JSON,
            OBJECT_DATA,
            METADATA,
            FORM_ENTITY,
            CART_ENTITY,
            CUSTOMER_ENTITY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface JsonStruct{}
}
