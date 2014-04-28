/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class CampaignItem implements IJSONSerializable, Parcelable {
	
	private static final String TAG = CampaignItem.class.getSimpleName();

	private Double mSavePrice;
	
	private double mSpecialPrice;

	private double mMaxSpecialPrice;

	private double mPrice;

	private double mMaxPrice;

	private String mSku;

	private String mBrand;

	private String mName;

	private String mImage;

	private ArrayList<String> mImages;

	private int mStockPercentage;

	private String mMaxSavingPercentage;

	private boolean hasUniqueSize;

	private ArrayList<String> mSizes;

	/**
	 * Empty constructor
	 */
	public CampaignItem() { }
	
	/**
	 * Constructor
	 * @param jsonObject
	 */
	public CampaignItem(JSONObject jsonObject) {
		initialize(jsonObject);
	}
	
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		Log.d(TAG, "ON INITIALIZE");
		
		mSavePrice = jsonObject.optDouble("save_price");
		mSpecialPrice = jsonObject.optDouble("special_price");
		mMaxSpecialPrice = jsonObject.optDouble("max_special_price");
		mPrice = jsonObject.optDouble("price");
		mMaxPrice = jsonObject.optDouble("max_price");
		mSku = jsonObject.optString("sku");
		mBrand = jsonObject.optString("brand");
		mName = jsonObject.optString("name");
		mStockPercentage = jsonObject.optInt("stock_percentage");
		mMaxSavingPercentage = jsonObject.optString("max_saving_percentage");
		hasUniqueSize = jsonObject.optBoolean("has_unique_size");
		
		JSONArray imagesA = jsonObject.optJSONArray("images");
		if(imagesA != null && imagesA.length() > 0) {
			mImage = imagesA.optString(0);
			mImages = new ArrayList<String>();
			for (int i = 0; i < imagesA.length(); i++) {
				mImages.add(imagesA.optString(i));
			}
		}
 
		JSONArray sizesA = jsonObject.optJSONArray("sizes");
		if(sizesA != null && sizesA.length() > 0) {
			mSizes = new ArrayList<String>();
			for (int i = 0; i < sizesA.length(); i++) {
				mSizes.add(sizesA.optString(i));
			}
		}
		
		Log.d(TAG, "ON INITIALIZE: " + toString());
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mBrand + " " + mName + " " + mSku + " " + mImage ;
	}

	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}
	
	/*
     * ########### Getters ###########
     */	
    /**
	 * @return the mSavePrice
	 */
	public Double getSavePrice() {
		return mSavePrice;
	}

	/**
	 * @return the mSpecialPrice
	 */
	public double getSpecialPrice() {
		return mSpecialPrice;
	}

	/**
	 * @return the mMaxSpecialPrice
	 */
	public double getMaxSpecialPrice() {
		return mMaxSpecialPrice;
	}

	/**
	 * @return the mPrice
	 */
	public double getPrice() {
		return mPrice;
	}

	/**
	 * @return the mMaxPrice
	 */
	public double getMaxPrice() {
		return mMaxPrice;
	}

	/**
	 * @return the mSku
	 */
	public String getSku() {
		return mSku;
	}

	/**
	 * @return the mBrand
	 */
	public String getBrand() {
		return mBrand;
	}

	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the mImage
	 */
	public String getImage() {
		return mImage;
	}

	/**
	 * @return the mImages
	 */
	public ArrayList<String> getImages() {
		return mImages;
	}

	/**
	 * @return the mStockPercentage
	 */
	public int getStockPercentage() {
		return mStockPercentage;
	}

	/**
	 * @return the mMaxSavingPercentage
	 */
	public String getMaxSavingPercentage() {
		return mMaxSavingPercentage;
	}

	/**
	 * @return the hasUniqueSize
	 */
	public boolean hasUniqueSize() {
		return hasUniqueSize;
	}

	/**
	 * @return the mSizes
	 */
	public ArrayList<String> getSizes() {
		return mSizes;
	}
	
	/*
     * ########### Setters ###########
     */
	/**
	 * @param mSavePrice the mSavePrice to set
	 */
	public void setSavePrice(Double mSavePrice) {
		this.mSavePrice = mSavePrice;
	}

	/**
	 * @param mSpecialPrice the mSpecialPrice to set
	 */
	public void setSpecialPrice(double mSpecialPrice) {
		this.mSpecialPrice = mSpecialPrice;
	}

	/**
	 * @param mMaxSpecialPrice the mMaxSpecialPrice to set
	 */
	public void setMaxSpecialPrice(double mMaxSpecialPrice) {
		this.mMaxSpecialPrice = mMaxSpecialPrice;
	}

	/**
	 * @param mPrice the mPrice to set
	 */
	public void setPrice(double mPrice) {
		this.mPrice = mPrice;
	}

	/**
	 * @param mMaxPrice the mMaxPrice to set
	 */
	public void setMaxPrice(double mMaxPrice) {
		this.mMaxPrice = mMaxPrice;
	}

	/**
	 * @param mSku the mSku to set
	 */
	public void setSku(String mSku) {
		this.mSku = mSku;
	}

	/**
	 * @param mBrand the mBrand to set
	 */
	public void setBrand(String mBrand) {
		this.mBrand = mBrand;
	}

	/**
	 * @param mName the mName to set
	 */
	public void setName(String mName) {
		this.mName = mName;
	}

	/**
	 * @param mImage the mImage to set
	 */
	public void setImage(String mImage) {
		this.mImage = mImage;
	}

	/**
	 * @param mImages the mImages to set
	 */
	public void setImages(ArrayList<String> mImages) {
		this.mImages = mImages;
	}

	/**
	 * @param mStockPercentage the mStockPercentage to set
	 */
	public void setStockPercentage(int mStockPercentage) {
		this.mStockPercentage = mStockPercentage;
	}

	/**
	 * @param mMaxSavingPercentage the mMaxSavingPercentage to set
	 */
	public void setMaxSavingPercentage(String mMaxSavingPercentage) {
		this.mMaxSavingPercentage = mMaxSavingPercentage;
	}

	/**
	 * @param hasUniqueSize the hasUniqueSize to set
	 */
	public void setHasUniqueSize(boolean hasUniqueSize) {
		this.hasUniqueSize = hasUniqueSize;
	}

	/**
	 * @param mSizes the mSizes to set
	 */
	public void setSizes(ArrayList<String> mSizes) {
		this.mSizes = mSizes;
	}

	/**
     * ########### Parcelable ###########
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
		//dest.writeString(mSavePrice);
		//dest.writeString(url);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public CampaignItem(Parcel in) {
        //mSavePrice = in.readString();
        //url = in.readString();
	}
	
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<CampaignItem> CREATOR = new Parcelable.Creator<CampaignItem>() {
        public CampaignItem createFromParcel(Parcel in) {
            return new CampaignItem(in);
        }

        public CampaignItem[] newArray(int size) {
            return new CampaignItem[size];
        }
    };

}
