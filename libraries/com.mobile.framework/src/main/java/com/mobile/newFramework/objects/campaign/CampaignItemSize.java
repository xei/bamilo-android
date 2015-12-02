package com.mobile.newFramework.objects.campaign;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONObject;
/**
 * 
 * @author sergiopereira
 */
public class CampaignItemSize implements IJSONSerializable, Parcelable {
    
    public String size;
    public double savePrice;
    public double specialPrice;
    public double price;
    public String simpleSku;

    /**
     * 
     */
    public CampaignItemSize(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    public boolean hasDiscount() {
        return specialPrice > 0 && specialPrice != Double.NaN;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        size = jsonObject.optString(RestConstants.SIZE);
        savePrice = jsonObject.optDouble(RestConstants.SAVE_PRICE);
        specialPrice = jsonObject.optDouble(RestConstants.SPECIAL_PRICE);
        price = jsonObject.optDouble(RestConstants.PRICE);
        simpleSku = jsonObject.optString(RestConstants.SKU);
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(size);
        dest.writeDouble(savePrice);
        dest.writeDouble(specialPrice);
        dest.writeDouble(price);
        dest.writeString(simpleSku);
    }
    
    public CampaignItemSize(Parcel in) {
        size = in.readString();
        savePrice = in.readDouble();
        specialPrice = in.readDouble();
        price = in.readDouble();
        simpleSku = in.readString();
    }
    
    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<CampaignItemSize> CREATOR = new Parcelable.Creator<CampaignItemSize>() {
        public CampaignItemSize createFromParcel(Parcel in) {
            return new CampaignItemSize(in);
        }

        public CampaignItemSize[] newArray(int size) {
            return new CampaignItemSize[size];
        }
    };
    
}

