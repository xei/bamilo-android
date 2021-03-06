package com.bamilo.android.framework.service.objects.addresses;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to save the customer addresses
 *
 * @author sergiopereira
 */
public class AddressesEntity extends Addresses implements IJSONSerializable, Parcelable {

    public static final String TAG = AddressesEntity.class.getSimpleName();

    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    public AddressesEntity() {
        super();
    }


    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject json) throws JSONException {
        JSONObject jsonObject = json.getJSONObject(RestConstants.CUSTOMER_ENTITY).getJSONObject(RestConstants.ADDRESS_LIST);
        return super.initialize(jsonObject);
    }

    /**
     * #################### PARCEL ####################
     */

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    /**
     * Parcel constructor
     */
    private AddressesEntity(Parcel in) {
        super(in);
    }

    /**
     * Create parcelable
     */
    public static final Creator<AddressesEntity> CREATOR = new Creator<AddressesEntity>() {
        public AddressesEntity createFromParcel(Parcel in) {
            return new AddressesEntity(in);
        }

        public AddressesEntity[] newArray(int size) {
            return new AddressesEntity[size];
        }
    };

}
