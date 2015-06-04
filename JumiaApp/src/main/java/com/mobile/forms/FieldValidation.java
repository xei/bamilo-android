///**
// * FieldValidation.java
// * form field validation class. Manages if the form field is required, the max and minimum number of characters and the regular expression.
// *
// * @author Guilherme Silva
// * @version 1.01
// * <p/>
// * 2012/06/18
// * <p/>
// * Copyright (c) Rocket Internet All Rights Reserved
// */
//package com.mobile.forms;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//import android.text.TextUtils;
//
//import com.mobile.framework.objects.IJSONSerializable;
//import com.mobile.framework.rest.RestConstants;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.mobile.framework.output.Log;
//
///**
// * Class that represents the form fiel validation parameters.
// * @author GuilhermeSilva
// *
// */
//public class FieldValidation implements IJSONSerializable, Parcelable {
//    private static final String TAG = FieldValidation.class.getName();
//
//    public static int MIN_CHARACTERS = 0;
//    public static int MAX_CHARACTERS = 40;
//    public static String DEFAULT_REGEX = "[0-9a-zA-Z-]*";
//
//    public boolean required;
//    public int min;
//    public int max;
//    public String regex;
//    public String message;
//
//    /**
//     * FormValidation empty constructor.
//     */
//    public FieldValidation() {
//        required = false;
//        min = MIN_CHARACTERS;
//        max = MAX_CHARACTERS;
//        regex = DEFAULT_REGEX;
//        message = "";
//    }
//
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//        // Validate json
//        if (jsonObject == null) {
//            return false;
//        }
//
//        //
//        required = jsonObject.optBoolean(RestConstants.JSON_REQUIRED_TAG, false);
//        Log.i(TAG, "code1message :  jsonObject : " + jsonObject.toString() + " required : " + required);
//        if (!required) {
//            JSONObject mJSONObject = null;
//
//            try {
//                mJSONObject = jsonObject.getJSONObject(RestConstants.JSON_REQUIRED_TAG);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (mJSONObject != null) {
//                required = mJSONObject.optBoolean(RestConstants.JSON_REQUIRED_VALUE_TAG, false);
//                Log.i(TAG, "code1message : " + required);
//                message = mJSONObject.optString(RestConstants.JSON_MESSAGE_IN_MESSAGES_TAG, "");
//                Log.i(TAG, "code1message : " + message);
//
//                // TODO : If contains message is required
//                if (message != null && !message.equals("")) required = true;
//            }
//
//        }
//
//        min = jsonObject.optInt(RestConstants.JSON_MIN_TAG, MIN_CHARACTERS);
//        max = jsonObject.optInt(RestConstants.JSON_MAX_TAG, MAX_CHARACTERS);
//        regex = jsonObject.optString(RestConstants.JSON_REGEX_TAG);
//
//        // CASE "match: {pattern: "/^[0-9]+$/u" }"
//        if (TextUtils.isEmpty(regex)) {
//            //this extra parsing option exists because
//            JSONObject matchObject = jsonObject.optJSONObject(RestConstants.JSON_MATCH_TAG);
//            if (null != matchObject) {
//                regex = matchObject.optString(RestConstants.JSON_PATTERN_TAG, DEFAULT_REGEX);
//                // TODO: Remove this hack after API fix
//                regex = regex.replace("$/u", "$/");
//            } else {
//                regex = DEFAULT_REGEX;
//            }
//            //Log.i(TAG, "RADIO RELATED: set regex" + regex + " " + jsonObject.toString());
//        }
//
//
//        if (regex.substring(0, 2).equals("a/")) {
//            regex = regex.substring(2);
//        }
//
//        if (regex.substring(0, 1).equals("/")) {
//            regex = regex.substring(1);
//        }
//
//        if (regex.charAt(regex.length() - 1) == '/') {
//            regex = regex.substring(0, regex.length() - 1);
//        }
//
//        return true;
//    }
//
//    /**
//     * @return if the field is required.
//     */
//    public boolean isRequired() {
//        return required;
//    }
//
//    /**
//     * @return the field error message.
//     */
//    public String getMessage() {
//        return message;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//    @Override
//    public JSONObject toJSON() {
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put(RestConstants.JSON_REQUIRED_TAG, required);
//            jsonObject.put(RestConstants.JSON_MIN_TAG, min);
//            jsonObject.put(RestConstants.JSON_MAX_TAG, max);
//            jsonObject.put(RestConstants.JSON_REGEX_TAG, regex);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }
//
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(MIN_CHARACTERS);
//        dest.writeInt(MAX_CHARACTERS);
//        dest.writeString(DEFAULT_REGEX);
//        dest.writeBooleanArray(new boolean[]{required});
//        dest.writeInt(min);
//        dest.writeInt(max);
//        dest.writeString(regex);
//        dest.writeString(message);
//    }
//
//    /**
//     * Parcel constructor
//     * @param in
//     */
//    private FieldValidation(Parcel in) {
//
//        MIN_CHARACTERS = in.readInt();
//        MAX_CHARACTERS = in.readInt();
//        DEFAULT_REGEX = in.readString();
//        in.readBooleanArray(new boolean[]{required});
//        min = in.readInt();
//        max = in.readInt();
//        regex = in.readString();
//        message = in.readString();
//    }
//
//    /**
//     * Create parcelable
//     */
//    public static final Parcelable.Creator<FieldValidation> CREATOR = new Parcelable.Creator<FieldValidation>() {
//        public FieldValidation createFromParcel(Parcel in) {
//            return new FieldValidation(in);
//        }
//
//        public FieldValidation[] newArray(int size) {
//            return new FieldValidation[size];
//        }
//    };
//}
