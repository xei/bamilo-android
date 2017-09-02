package com.mobile.service.forms;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * FieldValidation.java
 * form field validation class. Manages if the form field is required, the max and minimum number of characters and the regular expression.
 *
 * @author Guilherme Silva
 * @version 1.01
 * <p/>
 * 2012/06/18
 * <p/>
 * Copyright (c) Rocket Internet All Rights Reserved
 */
public class FieldValidation implements IJSONSerializable, Parcelable {

    public static final String TAG = FieldValidation.class.getSimpleName();

    public static int MIN_CHARACTERS = 0;
    public static int MAX_CHARACTERS = 40;
    public static String DEFAULT_REGEX = "[0-9a-zA-Z-]*";

    public boolean isRequired;
    public int min;
    public int max;
    public String regex;
    public String message;
    private String regexErrorMessage;

    /**
     * FormValidation empty constructor.
     */
    public FieldValidation() {
        isRequired = false;
        min = MIN_CHARACTERS;
        max = MAX_CHARACTERS;
        regex = DEFAULT_REGEX;
        message = "";
        regexErrorMessage = "";
    }


    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(@NonNull JSONObject jsonObject) {
        // Get required
        JSONObject required = jsonObject.optJSONObject(RestConstants.REQUIRED);
        // Get message
        if (required != null) {
            isRequired = true;
            message = required.optString(RestConstants.MESSAGE);
        }
        // Get range
        min = jsonObject.optInt(RestConstants.MIN, MIN_CHARACTERS);
        max = jsonObject.optInt(RestConstants.MAX, MAX_CHARACTERS);
        // Get regex
        regex = jsonObject.optString(RestConstants.REGEX, DEFAULT_REGEX);
        // Get match
        JSONObject matchObject = jsonObject.optJSONObject(RestConstants.MATCH);
        if (matchObject != null) {
            regex = matchObject.optString(RestConstants.PATTERN, DEFAULT_REGEX);
            regexErrorMessage = matchObject.optString(RestConstants.MESSAGE);
        }
        return true;
    }

    /**
     * @return if the field is required.
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * @return the field error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the Regex error message.
     */
    public String getRegexErrorMessage() {
        return regexErrorMessage;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.REQUIRED, isRequired);
            jsonObject.put(RestConstants.MIN, min);
            jsonObject.put(RestConstants.MAX, max);
            jsonObject.put(RestConstants.REGEX, regex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MIN_CHARACTERS);
        dest.writeInt(MAX_CHARACTERS);
        dest.writeString(DEFAULT_REGEX);
        dest.writeBooleanArray(new boolean[]{isRequired});
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeString(regex);
        dest.writeString(message);
        dest.writeString(regexErrorMessage);
    }

    /**
     * Parcel constructor
     */
    private FieldValidation(Parcel in) {
        MIN_CHARACTERS = in.readInt();
        MAX_CHARACTERS = in.readInt();
        DEFAULT_REGEX = in.readString();
        in.readBooleanArray(new boolean[]{isRequired});
        min = in.readInt();
        max = in.readInt();
        regex = in.readString();
        message = in.readString();
        regexErrorMessage = in.readString();
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<FieldValidation> CREATOR = new Parcelable.Creator<FieldValidation>() {
        public FieldValidation createFromParcel(Parcel in) {
            return new FieldValidation(in);
        }

        public FieldValidation[] newArray(int size) {
            return new FieldValidation[size];
        }
    };
}
