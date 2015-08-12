package com.mobile.newFramework.objects.product;

import android.os.Parcel;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines a simple (variation) of a give product.
 * @author GuilhermeSilva
 *
 */
public class NewProductSimple extends NewProductBase {

    private String mVariation;
    private int mQuantity;
    private int mMinDeliveryTime;
    private int mMaxDeliveryTime;


    /**
     * Empty constructor.
     */
    public NewProductSimple() {
        super();
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        //NORMAL COMPLETE SIMPLE PRODUCT
        if(jsonObject.optJSONObject(RestConstants.JSON_META_TAG) != null){
            jsonObject = jsonObject.getJSONObject(RestConstants.JSON_META_TAG);
        }
        // Base
        super.initialize(jsonObject);

        // TODO
        mVariation = jsonObject.optString(RestConstants.JSON_VARIATION_TAG);
        mVariation = jsonObject.optString(RestConstants.JSON_SIZE_TAG);

        mQuantity = jsonObject.getInt(RestConstants.JSON_QUANTITY_TAG);
        mMinDeliveryTime = jsonObject.optInt(RestConstants.JSON_MIN_DELIVERY_TAG);
        mMaxDeliveryTime = jsonObject.optInt(RestConstants.JSON_MAX_DELIVERY_TAG);
        return true;
    }


    public String getVariationValue() {
        return mVariation;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public int getMinDeliveryTime() {
        return mMinDeliveryTime;
    }

    public int getMaxDeliveryTime() {
        return mMaxDeliveryTime;
    }

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
