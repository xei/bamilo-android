package com.mobile.newFramework.objects.statics;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.catalog.ITargeting;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

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
 */
public class TargetHelper implements ITargeting, IJSONSerializable, Parcelable {

    public static final int DEEP_LINK_SIZE = 2;

    public static final int DEEP_LINK_TARGET_POSITION = 0;

    public static final int DEEP_LINK_URL_POSITION = 1;

    public static final String DEEP_LINK_DELIMITER = "::";

    public static final String TARGET_TYPE_PDV = "pdv";

    public static final String TARGET_TYPE_CATALOG = TargetType.CATALOG.getValue();

    public static final String TARGET_TYPE_CAMPAIGN = TargetType.CAMPAIGN.getValue();

    private String label;

    private String targetType;

    private String value;

    public TargetHelper(String label, String targetType, String value) {
        this.label = label;
        this.targetType = targetType;
        this.value = value;
    }

    public TargetHelper(@NonNull String target){
        splitTarget(target);
    }

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
    public RequiredJson getRequiredJson() {
        return null;
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
    public static final Parcelable.Creator<TargetHelper> CREATOR = new Parcelable.Creator<TargetHelper>() {
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
