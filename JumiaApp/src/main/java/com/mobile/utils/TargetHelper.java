package com.mobile.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobile.newFramework.utils.TextUtils;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/10/14
 *
 */
public class TargetHelper {

    public static final int DEEP_LINK_SIZE = 2;

    public static final int DEEP_LINK_TARGET_POSITION = 0;

    public static final int DEEP_LINK_URL_POSITION = 1;

    public static final String DEEP_LINK_DELIMITER = "::";

    public static final String TARGET_TYPE_PDV = "pdv";

    public static final String TARGET_TYPE_CATALOG = "catalog";

    public static final String TARGET_TYPE_CAMPAIGN = "campaign";
    
    private String target;

    private String value;

    public TargetHelper(String target, String value) {
        this.target = target;
        this.value = value;
    }

    public TargetHelper(@NonNull String target){
        splitTarget(target);
    }

    public void splitTarget(@NonNull String target){
        // Split pdv::http or catalog::http or campaign::http
        String[] deepLink = TextUtils.split(target, DEEP_LINK_DELIMITER);
        if (deepLink.length == DEEP_LINK_SIZE) {
            this.target = deepLink[DEEP_LINK_TARGET_POSITION];
            this.value = deepLink[DEEP_LINK_URL_POSITION];
        }
    }

    @Nullable
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
