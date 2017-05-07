package com.mobile.newFramework.objects.addresses;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent an generic list item, label -> value
 */
public class FormListItem implements IJSONSerializable, Parcelable {

    private int mValue;
    protected String mLabel;

    public FormListItem(int mValue, String mLabel) {
        this.mValue = mValue;
        this.mLabel = mLabel;
    }

    /**
     * Empty constructor
     *
     * @throws JSONException
     */
    public FormListItem(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    /**
     * ########### GETTERS ###########
     */

    /**
     * @return the id
     */
    public int getValue() {
        return mValue;
    }

    /**
     * @return the id
     */
    public String getValueAsString() {
        return String.valueOf(mValue);
    }

    /**
     * @return the name
     */
    public String getLabel() {
        return mLabel;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mLabel;
    }

    /**
     * ############### IJSON ###############
     */

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mValue = jsonObject.getInt(RestConstants.VALUE);
        mLabel = jsonObject.getString(RestConstants.LABEL);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.COMPLETE_JSON;
    }

    /**
     * ############### Parcelable ###############
     */

	/*
     * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#describeContents()
	 */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mValue);
        dest.writeString(mLabel);
    }

    /**
     * Constructor with parcel
     */
    protected FormListItem(Parcel in) {
        mValue = in.readInt();
        mLabel = in.readString();
    }

    /**
     * The creator
     */
    public static final Creator<FormListItem> CREATOR = new Creator<FormListItem>() {
        public FormListItem createFromParcel(Parcel in) {
            return new FormListItem(in);
        }

        public FormListItem[] newArray(int size) {
            return new FormListItem[size];
        }
    };

}
