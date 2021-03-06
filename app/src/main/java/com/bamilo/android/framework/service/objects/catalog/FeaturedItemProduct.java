package com.bamilo.android.framework.service.objects.catalog;


import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the server side product. Contains id, url, name, image
 * url and price.
 *
 * @author Andre Lopes
 *
 * // TODO Update this class to use the ProductBase class
 *
 */
public class FeaturedItemProduct extends FeaturedItem implements Parcelable {

    private String sku;
    private double price;
    private double mSpecialPrice;

    /**
     * simple FeaturedProduct constructor.
     */
    public FeaturedItemProduct() {
        super();
    }

    public double getSpecialPrice() {
        return mSpecialPrice;
    }

    public double getPrice() {
        return price;
    }

    public String getSku() {
        return sku;
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
            if (!super.initialize(jsonObject)) {
                return false;
            }
            sku = jsonObject.getString(RestConstants.SKU);
            price = jsonObject.getDouble(RestConstants.PRICE);
            mSpecialPrice = jsonObject.optDouble(RestConstants.SPECIAL_PRICE);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = super.toJSON();
        try {
            jsonObject.put(RestConstants.PRICE, price);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }


    /*
     * ########### Parcelable ###########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(price);
        dest.writeDouble(mSpecialPrice);
        dest.writeString(sku);
    }

    private FeaturedItemProduct(Parcel in) {
        super(in);
        price = in.readDouble();
        mSpecialPrice = in.readDouble();
        sku = in.readString();
    }

    public static final Creator<FeaturedItemProduct> CREATOR = new Creator<FeaturedItemProduct>() {
        public FeaturedItemProduct createFromParcel(Parcel in) {
            return new FeaturedItemProduct(in);
        }

        public FeaturedItemProduct[] newArray(int size) {
            return new FeaturedItemProduct[size];
        }
    };

}
