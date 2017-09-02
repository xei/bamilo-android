package com.mobile.service.objects.addresses;

import android.os.Parcel;

import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to represent a phone prefix
 * @author spereira
 */
public class PhonePrefix extends FormListItem {

    private boolean isDefault;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public PhonePrefix(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        isDefault = jsonObject.optBoolean(RestConstants.IS_DEFAULT);
    }

    public boolean isDefault() {
        return isDefault;
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
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (isDefault ? 1 : 0));
    }

    /**
     * Constructor with parcel
     */
    private PhonePrefix(Parcel in) {
        super(in);
        isDefault = in.readByte() == 1;
    }

    /**
     * The creator
     */
    public static final Creator<PhonePrefix> CREATOR = new Creator<PhonePrefix>() {
        public PhonePrefix createFromParcel(Parcel in) {
            return new PhonePrefix(in);
        }

        public PhonePrefix[] newArray(int size) {
            return new PhonePrefix[size];
        }
    };


}
