package com.bamilo.android.framework.service.objects.statics;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.catalog.ITargeting;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
 * @deprecated Please use TargetLink.
 */
@Deprecated
public class TargetHelper implements ITargeting, IJSONSerializable, Parcelable {

    public static final int DEEP_LINK_SIZE = 2;

    public static final int DEEP_LINK_TARGET_POSITION = 0;

    public static final int DEEP_LINK_URL_POSITION = 1;

    public static final String DEEP_LINK_DELIMITER = "::";

    private String label;

    private String targetType;

    private String value;

    public TargetHelper(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    public void splitTarget(@NonNull String target){
        // Split pdv::http or catalog::http or campaign::http
        String[] deepLink = TextUtils.split(target, DEEP_LINK_DELIMITER);
        if (deepLink.length == DEEP_LINK_SIZE) {
            this.targetType = deepLink[DEEP_LINK_TARGET_POSITION];
            this.value = deepLink[DEEP_LINK_URL_POSITION];
        }
    }

    @Nullable
    public String getTarget() {
        return targetType;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getTargetValue() {
        return value;
    }

    @Override
    public TargetType getTargetType() {
        return ITargeting.TargetType.byValue(targetType);
    }

    @Override
    public String getTargetTitle() {
        return label;
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        label = jsonObject.optString(RestConstants.LABEL);
        splitTarget(jsonObject.getString(RestConstants.TARGET));
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    protected TargetHelper(Parcel in) {
        label = in.readString();
        targetType = in.readString();
        value = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(targetType);
        dest.writeString(value);
    }

    @SuppressWarnings("unused")
    public static final Creator<TargetHelper> CREATOR = new Creator<TargetHelper>() {
        @Override
        public TargetHelper createFromParcel(Parcel in) {
            return new TargetHelper(in);
        }

        @Override
        public TargetHelper[] newArray(int size) {
            return new TargetHelper[size];
        }
    };
}
