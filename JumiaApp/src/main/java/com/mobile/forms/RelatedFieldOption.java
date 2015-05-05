package com.mobile.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.rest.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 *
 */
public class RelatedFieldOption implements IJSONSerializable, Parcelable {

    public static final String TAG = RelatedFieldOption.class.getSimpleName();

    private String mLabel;

    private String mValue;

    private FieldValidation mRules;

    public RelatedFieldOption() {
        // ...
    }

    public String getLabel() {
        return mLabel;
    }

    public String getValue() {
        return mValue;
    }

    public FieldValidation getRules() {
        return mRules;
    }

    public boolean hasRules() {
        return mRules != null;
    }

    @Override
    public String toString() {
        return mLabel;
    }

    /* (non-Javadoc)
         * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        boolean result = false;
        try {
            // Get label
            mLabel = jsonObject.getString(RestConstants.JSON_LABEL_TAG);
            // Get value
            mValue = jsonObject.getString(RestConstants.JSON_VALUE_TAG);
            // Get rules
            JSONObject rules = jsonObject.optJSONObject(RestConstants.JSON_RULES_TAG);
            if (rules != null) {
                mRules = new FieldValidation();
                mRules.initialize(rules);
            }
            Log.i(TAG, "DATASET: " + mLabel + " " + mValue + " " + hasRules());
            result = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLabel);
        dest.writeString(mValue);
        dest.writeValue(mRules);
    }

    /**
     * Parcel constructor
     * @param in
     */
    private RelatedFieldOption(Parcel in) {
        mLabel = in.readString();
        mValue = in.readString();
        mRules = (FieldValidation) in.readValue(FieldValidation.class.getClassLoader());
    }

    /**
     * Create parcelable
     */
    public static final Creator<RelatedFieldOption> CREATOR = new Creator<RelatedFieldOption>() {
        public RelatedFieldOption createFromParcel(Parcel in) {
            return new RelatedFieldOption(in);
        }

        public RelatedFieldOption[] newArray(int size) {
            return new RelatedFieldOption[size];
        }
    };
    
}
