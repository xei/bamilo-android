/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperNativeCheckoutAvailability implements IJSONSerializable, Parcelable {


    private boolean isAvailable;

    public SuperNativeCheckoutAvailability() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {

        isAvailable = false;
        try {
            if(jsonObject.has(RestConstants.JSON_NATIVE_CHECKOUT_AVAILABLE) && jsonObject.getString(RestConstants.JSON_NATIVE_CHECKOUT_AVAILABLE).equalsIgnoreCase("1")){
                isAvailable = true;
            }

            if(isAvailable){
//                Log.i(TAG, "native checkout is available!");
            } else {
//                Log.i(TAG, "native checkout is not available!"+jsonObject.toString());
            }
        } catch (JSONException e) {
//            Log.d(TAG, "PARSE JSON", e);
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAvailable ? 1 : 0));

    }

    private SuperNativeCheckoutAvailability(Parcel in) {
        isAvailable = in.readByte() == 1;

    }

    public static final Creator<SuperNativeCheckoutAvailability> CREATOR = new Creator<SuperNativeCheckoutAvailability>() {
        public SuperNativeCheckoutAvailability createFromParcel(Parcel in) {
            return new SuperNativeCheckoutAvailability(in);
        }

        public SuperNativeCheckoutAvailability[] newArray(int size) {
            return new SuperNativeCheckoutAvailability[size];
        }
    };


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
