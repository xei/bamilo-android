/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Class used to represent an item associated a campaign
 * @author sergiopereira
 */
public class CampaignItem implements IJSONSerializable, Parcelable {
	
	private static final String TAG = CampaignItem.class.getSimpleName();

	private double mSavePrice;
	
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
	
	private ArrayList<CampaignItemSize> mSizes;
	
	private CampaignItemSize mSelectedSize;
	
	private int mSelectedSizePosition;

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
		
		mSavePrice = jsonObject.optDouble(RestConstants.JSON_SAVE_PRICE_TAG);
		mSpecialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
		mMaxSpecialPrice = jsonObject.optDouble(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG);
		mPrice = jsonObject.optDouble(RestConstants.JSON_PRICE_TAG);
		mMaxPrice = jsonObject.optDouble(RestConstants.JSON_MAX_PRICE_TAG);
		mSku = jsonObject.optString(RestConstants.JSON_SKU_TAG);
		mBrand = jsonObject.optString(RestConstants.JSON_BRAND_TAG);
		mName = jsonObject.optString(RestConstants.JSON_NAME_TAG);
		mStockPercentage = jsonObject.optInt(RestConstants.JSON_STOCK_PERCENTAGE_TAG);
		mMaxSavingPercentage = jsonObject.optString(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
		hasUniqueSize = jsonObject.optBoolean(RestConstants.JSON_HAS_UNIQUE_SIZE_TAG);
		
		// Save images
		JSONArray imagesA = jsonObject.optJSONArray(RestConstants.JSON_IMAGES_TAG);
		if(imagesA != null && imagesA.length() > 0) {
			mImage = imagesA.optString(0);
			mImages = new ArrayList<String>();
			for (int i = 0; i < imagesA.length(); i++) {
				mImages.add(imagesA.optString(i));
			}
		}
 
		// Save sizes
		JSONArray sizesA = jsonObject.optJSONArray(RestConstants.JSON_SIZES_TAG);
		if(sizesA != null && sizesA.length() > 0) {
			mSizes = new ArrayList<CampaignItemSize>();
			for (int i = 0; i < sizesA.length(); i++) {
				JSONObject sizeO = sizesA.optJSONObject(i);
				if(sizeO != null)
					mSizes.add(new CampaignItemSize(sizeO));
			}
		}

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
	 * @return the mSizes
	 */
	public ArrayList<CampaignItemSize> getSizes() {
		return mSizes;
	}
	
	/**
	 * @return the mSelectedSize
	 */
	public CampaignItemSize getSelectedSize() {
		return mSelectedSize;
	}

	/**
	 * @return the mSelectedSizePosition
	 */
	public int getSelectedSizePosition() {
		return mSelectedSizePosition;
	}

	/*
     * ########### Validators ###########
     */
	/**
	 * @return the hasUniqueSize
	 */
	public boolean hasUniqueSize() {
		return hasUniqueSize;
	}
	
	/**
	 * @return the hasSizes except itself
	 */
	public boolean hasSizes() {
		return (mSizes != null && mSizes.size() > 0) ? true : false;
	}
	
	/**
	 * @return the hasSizes
	 */
	public boolean hasSelectedSize() {
		return (hasSizes() && mSelectedSizePosition >= 0  && mSelectedSizePosition < mSizes.size()) ? true : false;
	}
	
	/**
	 * @return the hasSizes
	 */
	public boolean hasStock() {
		return (mStockPercentage > 0) ? true : false;
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
	public void setSizes(ArrayList<CampaignItemSize> mSizes) {
		this.mSizes = mSizes;
	}
	
	/**
	 * @param mSelectedSize the mSelectedSize to set
	 */
	public void setSelectedSize(CampaignItemSize mSelectedSize) {
		this.mSelectedSize = mSelectedSize;
	}

	/**
	 * @param mSelectedSizePosition the mSelectedSizePosition to set
	 */
	public void setSelectedSizePosition(int mSelectedSizePosition) {
		this.mSelectedSizePosition = mSelectedSizePosition;
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
		dest.writeDouble(mSavePrice);
		dest.writeDouble(mSpecialPrice);
		dest.writeDouble(mMaxSpecialPrice);
		dest.writeDouble(mPrice);
		dest.writeDouble(mMaxPrice);
		dest.writeString(mSku);
		dest.writeString(mBrand);
		dest.writeString(mName);
		dest.writeString(mImage);
		dest.writeList(mImages);
		dest.writeInt(mStockPercentage);
		dest.writeString(mMaxSavingPercentage);
		dest.writeBooleanArray(new boolean[] {hasUniqueSize});
		dest.writeList(mSizes);
		dest.writeParcelable(mSelectedSize, 0);
		dest.writeInt(mSelectedSizePosition);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public CampaignItem(Parcel in) {
		mSavePrice = in.readDouble();
		mSpecialPrice = in.readDouble();
		mMaxSpecialPrice = in.readDouble();
		mPrice = in.readDouble();
		mMaxPrice = in.readDouble();
		mSku = in.readString();
		mBrand = in.readString();
		mName = in.readString();
		mImage = in.readString();
		in.readList(mImages, String.class.getClassLoader());
		mStockPercentage = in.readInt();
		mMaxSavingPercentage = in.readString();
		in.readBooleanArray(new boolean[] {hasUniqueSize});
		in.readList(mSizes, CampaignItemSize.class.getClassLoader());
		mSelectedSize = in.readParcelable(CampaignItemSize.class.getClassLoader());
		mSelectedSizePosition = in.readInt();
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

    /* ################## CAMPAIGN ITEM SIZE ################## */
    
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
		 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
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
		 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
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
		public final Parcelable.Creator<CampaignItemSize> CREATOR = new Parcelable.Creator<CampaignItemSize>() {
	        public CampaignItemSize createFromParcel(Parcel in) {
	            return new CampaignItemSize(in);
	        }

	        public CampaignItemSize[] newArray(int size) {
	            return new CampaignItemSize[size];
	        }
	    };
    	
    }
    
}
