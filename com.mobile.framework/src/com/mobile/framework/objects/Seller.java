package com.mobile.framework.objects;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;

/**
 * Class that represents the Seller of a specific product
 * 
 * @author Paulo Carvalho
 * 
 */
public class Seller implements IJSONSerializable, Parcelable {
	
	public final static String TAG = LogTagHelper.create(Seller.class);

    private String name;
    
    private String url;
    
    private int minDeliveryTime;
    
    private int maxDeliveryTime;
    
    private int ratingCount;
    
    private int ratingValue;
    
    
    /**
     * Seller empty constructor.
     */
    public Seller() {
        name = "";
        url = "";
        minDeliveryTime = 1;
        maxDeliveryTime = 1;
        ratingCount = 0;
        ratingValue = 0;
    }
    
    /**
     * Seller Json constructor.
     */
    public Seller(JSONObject sellerObject) {
        name = "";
        url = "";
        minDeliveryTime = 1;
        maxDeliveryTime = 1;
        ratingCount = 0;
        ratingValue = 0;
        initialize(sellerObject);
    }
   
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMinDeliveryTime() {
        return minDeliveryTime;
    }

    public void setMinDeliveryTime(int minDeliveryTime) {
        this.minDeliveryTime = minDeliveryTime;
    }

    public int getMaxDeliveryTime() {
        return maxDeliveryTime;
    }

    public void setMaxDeliveryTime(int maxDeliveryTime) {
        this.maxDeliveryTime = maxDeliveryTime;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
            name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
            url = jsonObject.optString(RestConstants.JSON_URL_TAG);
            minDeliveryTime = jsonObject.optInt(RestConstants.JSON_SELLER_MIN_DELIVERY_TAG);
            maxDeliveryTime = jsonObject.optInt(RestConstants.JSON_SELLER_MAX_DELIVERY_TAG);
            
            JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.JSON_REVIEWS_TAG);
            
            if(reviewObject != null){
                ratingCount = reviewObject.optInt(RestConstants.JSON_TOTAL_TAG);
                ratingValue = reviewObject.optInt(RestConstants.JSON_RATINGS_AVERAGE_TAG);
            }

        return true;
    }
    
  
    @Override
    public JSONObject toJSON() {


        return null;
    }
    
    
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
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
	    dest.writeString(name);
	    dest.writeString(url);
	    dest.writeInt(minDeliveryTime);
	    dest.writeInt(maxDeliveryTime);
	    dest.writeInt(ratingCount);
	    dest.writeInt(ratingValue);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	protected Seller(Parcel in) {
	    name = in.readString();
	    url = in.readString();
	    minDeliveryTime = in.readInt();
	    maxDeliveryTime = in.readInt();
        ratingCount = in.readInt();
        ratingValue = in.readInt();
        
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<Seller> CREATOR = new Parcelable.Creator<Seller>() {
        public Seller createFromParcel(Parcel in) {
            return new Seller(in);
        }

        public Seller[] newArray(int size) {
            return new Seller[size];
        }
    };
    
    
}
