package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines a simple (variation) of a give product.
 * @author GuilhermeSilva
 *
 */
public class NewProductSimple implements IJSONSerializable, Parcelable {

    private static final String TAG = NewProductSimple.class.getSimpleName();

    protected String mSku;
    protected String mValue;
    protected double mPrice;
    protected double mSpecialPrice;
    protected double mPriceConverted;
    protected double mSpecialPriceConverted;
    protected int mStockQuantity;

    /**
     * Empty constructor
     */
    public NewProductSimple() {
        // ...
    }

    /* (non-Javadoc)
         * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            // Mandatory
            mSku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            mValue = jsonObject.getString(RestConstants.JSON_VARIATION_VALUE_TAG);
            mPrice = jsonObject.getDouble(RestConstants.JSON_PRICE_TAG);
            mStockQuantity = jsonObject.getInt(RestConstants.JSON_QUANTITY_TAG);
            // Optional
            mPriceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
            mSpecialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
            mSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
        } catch (JSONException e) {
            Print.w(TAG, "WARNING: JSE ON PARSE PRODUCT SIMPLE", e);
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
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
		// TODO
	}

	private NewProductSimple(Parcel in){
        // TODO
	}

    public static final Creator<NewProductSimple> CREATOR = new Creator<NewProductSimple>() {
        public NewProductSimple createFromParcel(Parcel in) {
            return new NewProductSimple(in);
        }

        public NewProductSimple[] newArray(int size) {
            return new NewProductSimple[size];
        }
    };
}
