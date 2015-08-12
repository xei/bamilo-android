package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines a simple (variation) of a give product.
 * @author GuilhermeSilva
 *
 */
public class NewProductBase implements Parcelable, IJSONSerializable {

    protected static final String TAG = NewProductBase.class.getSimpleName();

    protected String mSku;
    protected double mPrice;
    protected double mPriceConverted;
    protected double mSpecialPrice;
    protected double mSpecialPriceConverted;
    protected int mMaxSavingPercentage;


    /**
     * Empty constructor
     */
    public NewProductBase() {
        super();
    }

    /* (non-Javadoc)
         * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Mandatory
        mSku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
        mPrice = jsonObject.getDouble(RestConstants.JSON_PRICE_TAG);
        mPriceConverted = jsonObject.getDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
        // Optional
        mSpecialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
        mSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
        mMaxSavingPercentage = jsonObject.optInt(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
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
	 * ############ GETTERS ############
	 */

    public String getSku() {
        return mSku;
    }

    public double getPrice() {
        return mPrice;
    }

    public double getPriceConverted() {
        return mPriceConverted;
    }

    public double getSpecialPrice() {
        return mSpecialPrice;
    }

    public double getSpecialPriceConverted() {
        return mSpecialPriceConverted;
    }

    public int getMaxSavingPercentage() {
        return mMaxSavingPercentage;
    }

    public boolean hasDiscount() {
        return mSpecialPrice > 0 && mSpecialPrice != Double.NaN;
    }

    public double getPriceForTracking() {
        return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceConverted;
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

	private NewProductBase(Parcel in){
        // TODO
	}

    public static final Creator<NewProductBase> CREATOR = new Creator<NewProductBase>() {
        public NewProductBase createFromParcel(Parcel in) {
            return new NewProductBase(in);
        }

        public NewProductBase[] newArray(int size) {
            return new NewProductBase[size];
        }
    };
}
