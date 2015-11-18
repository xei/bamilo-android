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
    //TODO remove this hammer
    public static final int ARRAY_DATA_FIRST = 4;

    @IntDef({
            NONE,
            COMPLETE_JSON,
            OBJECT_DATA,
            METADATA,
            ARRAY_DATA_FIRST
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface JsonStruct{}
}
