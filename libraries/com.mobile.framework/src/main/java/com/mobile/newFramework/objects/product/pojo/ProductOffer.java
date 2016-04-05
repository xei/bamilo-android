package com.mobile.newFramework.objects.product.pojo;


import android.os.Parcel;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.Seller;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the offer
 * @author Paulo Carvalho
 *
 * // TODO USE THE PRODUCT BASE
 *
 */
public class ProductOffer extends ProductMultiple implements IJSONSerializable{

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
            JSONObject sellerObject = jsonObject.getJSONObject(RestConstants.SELLER_ENTITY);
            if (sellerObject != null) {
                seller = new Seller(sellerObject);
            }
            JSONObject productObject = jsonObject.getJSONObject(RestConstants.PRODUCT);
            if (productObject != null) {
                super.initialize(productObject);
                minDeliveryTime = productObject.optInt(RestConstants.MIN_DELIVERY_TIME);
                maxDeliveryTime = productObject.optInt(RestConstants.MAX_DELIVERY_TIME);
            }
        } catch (JSONException e) {
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
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public double getFinalPrice() {
        return mSpecialPrice == 0.0 ? mPrice : mSpecialPrice;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.maxDeliveryTime);
        dest.writeInt(this.minDeliveryTime);
        dest.writeParcelable(this.seller, 0);
    }

    protected ProductOffer(Parcel in) {
        super(in);
        this.maxDeliveryTime = in.readInt();
        this.minDeliveryTime = in.readInt();
        this.seller = in.readParcelable(Seller.class.getClassLoader());
    }

    public static final Creator<ProductOffer> CREATOR = new Creator<ProductOffer>() {
        public ProductOffer createFromParcel(Parcel source) {
            return new ProductOffer(source);
        }

        public ProductOffer[] newArray(int size) {
            return new ProductOffer[size];
        }
    };
}
