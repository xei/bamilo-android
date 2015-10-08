//package com.mobile.newFramework.forms;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.objects.IJSONSerializable;
//import com.mobile.newFramework.objects.RequiredJson;
//import com.mobile.newFramework.pojo.RestConstants;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Class used to represent the new generic item from data set form field
// */
//public class RelatedFieldOption implements IJSONSerializable, Parcelable {
//
//    public static final String TAG = RelatedFieldOption.class.getSimpleName();
//
//    private String mLabel;
//
//    private String mValue;
//
//    private FieldValidation mRules;
//
//    private boolean isDefault;
//
//    /**
//     * Constructor
//     */
//    public RelatedFieldOption() {
//        // ...
//    }
//
//    public String getLabel() {
//        return mLabel;
//    }
//
//    public String getValue() {
//        return mValue;
//    }
//
//    public FieldValidation getRules() {
//        return mRules;
//    }
//
//    public boolean isDefault() {
//        return isDefault;
//    }
//
//    @Override
//    public String toString() {
//        return mLabel;
//    }
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//        boolean result = false;
//        try {
//            // Get label
//            mLabel = jsonObject.getString(RestConstants.LABEL);
//            // Get value
//            mValue = jsonObject.getString(RestConstants.VALUE);
//            // Get default
//            isDefault = jsonObject.optBoolean(RestConstants.JSON_CHECKED_TAG);
//            // Get rules
//            JSONObject rules = jsonObject.optJSONObject(RestConstants.JSON_RULES_TAG);
//            if (rules != null) {
//                mRules = new FieldValidation();
//                mRules.initialize(rules);
//            }
//            //Log.i(TAG, "DATASET: " + mLabel + " " + mValue + " " + hasRules() + " " + isDefault);
//            result = true;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//    @Override
//    public JSONObject toJSON() {
//        return null;
//    }
//
//    @Override
//    public RequiredJson getRequiredJson() {
//        return null;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(mLabel);
//        dest.writeString(mValue);
//        dest.writeValue(mRules);
//        dest.writeByte((byte) (isDefault ? 0x01 : 0x00));
//    }
//
//    /**
//     * Parcel constructor
//     */
//    private RelatedFieldOption(Parcel in) {
//        mLabel = in.readString();
//        mValue = in.readString();
//        mRules = (FieldValidation) in.readValue(FieldValidation.class.getClassLoader());
//        isDefault = in.readByte() != 0x00;
//    }
//
//    /**
//     * Create parcelable
//     */
//    public static final Creator<RelatedFieldOption> CREATOR = new Creator<RelatedFieldOption>() {
//        public RelatedFieldOption createFromParcel(Parcel in) {
//            return new RelatedFieldOption(in);
//        }
//
//        public RelatedFieldOption[] newArray(int size) {
//            return new RelatedFieldOption[size];
//        }
//    };
//
//}
