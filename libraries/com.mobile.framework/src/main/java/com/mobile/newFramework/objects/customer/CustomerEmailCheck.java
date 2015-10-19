package com.mobile.newFramework.objects.customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class CustomerEmailCheck implements IJSONSerializable, Parcelable {

    boolean mExist;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public CustomerEmailCheck() {
        // ...
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mExist = jsonObject.optBoolean(RestConstants.EXIST);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public boolean exist() {
        return mExist;
    }

    /*
     * ############ PARCELABLE ############
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mExist ? 1 : 0));
    }

    protected CustomerEmailCheck(Parcel in) {
        mExist = in.readByte() == 1;
    }

    public static final Creator<CustomerEmailCheck> CREATOR = new Creator<CustomerEmailCheck>() {
        @Override
        public CustomerEmailCheck createFromParcel(Parcel in) {
            return new CustomerEmailCheck(in);
        }

        @Override
        public CustomerEmailCheck[] newArray(int size) {
            return new CustomerEmailCheck[size];
        }
    };

}
