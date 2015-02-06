package com.mobile.framework.objects;

import org.json.JSONObject;

import com.mobile.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
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

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        size = jsonObject.optString(RestConstants.JSON_SIZE_TAG);
        savePrice = jsonObject.optDouble(RestConstants.JSON_SAVE_PRICE_TAG);
        specialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
        price = jsonObject.optDouble(RestConstants.JSON_PRICE_TAG);
        simpleSku = jsonObject.optString(RestConstants.JSON_SKU_TAG);
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

