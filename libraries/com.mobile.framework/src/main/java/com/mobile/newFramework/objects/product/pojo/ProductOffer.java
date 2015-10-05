package com.mobile.newFramework.objects.product.pojo;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.Seller;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the offer
 *
 * @author Paulo Carvalho
 *
 * // TODO USE THE PRODUCT BASE
 *
 */
public class ProductOffer extends ProductMultiple implements IJSONSerializable, Parcelable{

    protected static final String TAG = ProductOffer.class.getSimpleName();

    private int maxDeliveryTime;
    private int minDeliveryTime;
    private Seller seller;

    /**
     * Complete product empty constructor.
     */
    @SuppressWarnings("unused")
    public ProductOffer() {
        maxDeliveryTime = 0;
        minDeliveryTime = 0;
        seller = new Seller();
    }

    /**
     * Complete product empty constructor.
     */
    public ProductOffer(JSONObject offer) {
        maxDeliveryTime = 0;
        minDeliveryTime = 0;
        seller = new Seller();
        initialize(offer);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {

            JSONObject sellerObject = jsonObject.getJSONObject(RestConstants.JSON_SELLER_TAG);

            if(sellerObject != null)
                seller = new Seller(sellerObject);

            JSONObject productObject = jsonObject.getJSONObject(RestConstants.JSON_PRODUCT_TAG);

            if(productObject != null){

                initializeProductBase(productObject);

                initializeProductMultiple(productObject);

                minDeliveryTime = productObject.optInt(RestConstants.JSON_MIN_DELIVERY_TAG);
                maxDeliveryTime = productObject.optInt(RestConstants.JSON_MAX_DELIVERY_TAG);

            }

        } catch (JSONException e) {

//            Log.e(TAG, "Error initializing the complete product", e);
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
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    public double getFinalPrice() {
        return mSpecialPrice == 0.0 ? mPrice : mSpecialPrice;
    }

    public String getFinalPriceString() {
        return mSpecialPrice == 0.0 ? mPrice+"" : mSpecialPrice+"";
    }

    public int getMaxDeliveryTime() {
        return maxDeliveryTime;
    }

    public int getMinDeliveryTime() {
        return minDeliveryTime;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /*
     * ############ PARCELABLE ############
     */


    protected ProductOffer(Parcel in) {
        maxDeliveryTime = in.readInt();
        minDeliveryTime = in.readInt();
        seller = (Seller) in.readValue(Seller.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxDeliveryTime);
        dest.writeInt(minDeliveryTime);
        dest.writeValue(seller);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductOffer> CREATOR = new Parcelable.Creator<ProductOffer>() {
        @Override
        public ProductOffer createFromParcel(Parcel in) {
            return new ProductOffer(in);
        }

        @Override
        public ProductOffer[] newArray(int size) {
            return new ProductOffer[size];
        }
    };


}
