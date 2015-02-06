/**
 * 
 */
package com.mobile.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mobile.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Class used to represent an item associated a campaign
 * 
 * @author sergiopereira
 */
public class CampaignItem implements IJSONSerializable, Parcelable {

	private static final String TAG = CampaignItem.class.getSimpleName();

	private double mSavePrice;

	private double mSpecialPrice;

	private double mPrice;

	private String mSku;

	private String mBrand;

	private String mName;

	private String mImage;

	private ArrayList<String> mImages;

	private int mStockPercentage;

	private double mMaxSavingPercentage;

	private boolean hasUniqueSize;

	private int mRemainingTime;

	private ArrayList<CampaignItemSize> mSizes;

	private CampaignItemSize mSelectedSize;

	private int mSelectedSizePosition;

	private double mPriceConverted;

	private double mSpecialPriceConverted;

	/**
	 * Empty constructor
	 */
	public CampaignItem() {
	}

	/**
	 * Constructor
	 * 
	 * @param jsonObject
	 */
	public CampaignItem(JSONObject jsonObject) {
		initialize(jsonObject);
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
		Log.d(TAG, "ON INITIALIZE");

		mSavePrice = jsonObject.optDouble(RestConstants.JSON_SAVE_PRICE_TAG, 0d);
		mSpecialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG, 0d);
		mSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG, 0d);
		mPrice = jsonObject.optDouble(RestConstants.JSON_PRICE_TAG, 0d);
		mPriceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG, 0d);
		mSku = jsonObject.optString(RestConstants.JSON_SKU_TAG);
		mBrand = jsonObject.optString(RestConstants.JSON_BRAND_TAG);
		mName = jsonObject.optString(RestConstants.JSON_NAME_TAG);
		mStockPercentage = jsonObject.optInt(RestConstants.JSON_STOCK_PERCENTAGE_TAG, 0);
		mMaxSavingPercentage = jsonObject.optDouble(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG, 0d);
		hasUniqueSize = jsonObject.optBoolean(RestConstants.JSON_HAS_UNIQUE_SIZE_TAG);
		mRemainingTime = jsonObject.optInt(RestConstants.JSON_REMAINING_TIME_TAG, -1);

		// Save images
		JSONArray imagesA = jsonObject.optJSONArray(RestConstants.JSON_IMAGES_TAG);
		if (imagesA != null && imagesA.length() > 0) {
			mImage = imagesA.optString(0);
			mImages = new ArrayList<String>();
			for (int i = 0; i < imagesA.length(); i++) {
				mImages.add(imagesA.optString(i));
			}
		}

		// Save sizes
		JSONArray sizesA = jsonObject.optJSONArray(RestConstants.JSON_SIZES_TAG);
		if (sizesA != null && sizesA.length() > 0) {
			mSizes = new ArrayList<CampaignItemSize>();
			for (int i = 0; i < sizesA.length(); i++) {
				JSONObject sizeO = sizesA.optJSONObject(i);
				if (sizeO != null)
					mSizes.add(new CampaignItemSize(sizeO));
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mBrand + " " + mName + " " + mSku + " " + mImage;
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
	 * @return the mPrice
	 */
	public double getPrice() {
		return mPrice;
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
	public double getMaxSavingPercentage() {
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

	/***
	 * @return the mRemainingTime
	 */
	public int getRemainingTime() {
		return mRemainingTime;
	}

	/**
	 * @return the mPriceConverted
	 */
	public double getPriceConverted() {
		return mPriceConverted;
	}

	/**
	 * @return the mSpecialPriceConverted
	 */
	public double getSpecialPriceConverted() {
		return mSpecialPriceConverted;
	}

	/**
	 * Return the paid price for tracking.
	 * 
	 * @return double
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		Log.i(TAG, "ORIGIN PRICE VALUES: " + mPrice + " " + mSpecialPrice);
		Log.i(TAG, "PRICE VALUE FOR TRACKING: " + mPriceConverted + " " + mSpecialPriceConverted);
		return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceConverted;
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
		return (hasSizes() && mSelectedSizePosition >= 0 && mSelectedSizePosition < mSizes.size()) ? true : false;
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
	 * @param mSavePrice
	 *            the mSavePrice to set
	 */
	public void setSavePrice(Double mSavePrice) {
		this.mSavePrice = mSavePrice;
	}

	/**
	 * @param mSpecialPrice
	 *            the mSpecialPrice to set
	 */
	public void setSpecialPrice(double mSpecialPrice) {
		this.mSpecialPrice = mSpecialPrice;
	}

	/**
	 * @param mPrice
	 *            the mPrice to set
	 */
	public void setPrice(double mPrice) {
		this.mPrice = mPrice;
	}

	/**
	 * @param mSku
	 *            the mSku to set
	 */
	public void setSku(String mSku) {
		this.mSku = mSku;
	}

	/**
	 * @param mBrand
	 *            the mBrand to set
	 */
	public void setBrand(String mBrand) {
		this.mBrand = mBrand;
	}

	/**
	 * @param mName
	 *            the mName to set
	 */
	public void setName(String mName) {
		this.mName = mName;
	}

	/**
	 * @param mImage
	 *            the mImage to set
	 */
	public void setImage(String mImage) {
		this.mImage = mImage;
	}

	/**
	 * @param mImages
	 *            the mImages to set
	 */
	public void setImages(ArrayList<String> mImages) {
		this.mImages = mImages;
	}

	/**
	 * @param mStockPercentage
	 *            the mStockPercentage to set
	 */
	public void setStockPercentage(int mStockPercentage) {
		this.mStockPercentage = mStockPercentage;
	}

	/**
	 * @param mMaxSavingPercentage
	 *            the mMaxSavingPercentage to set
	 */
	public void setMaxSavingPercentage(double mMaxSavingPercentage) {
		this.mMaxSavingPercentage = mMaxSavingPercentage;
	}

	/**
	 * @param hasUniqueSize
	 *            the hasUniqueSize to set
	 */
	public void setHasUniqueSize(boolean hasUniqueSize) {
		this.hasUniqueSize = hasUniqueSize;
	}

	/**
	 * @param mSizes
	 *            the mSizes to set
	 */
	public void setSizes(ArrayList<CampaignItemSize> mSizes) {
		this.mSizes = mSizes;
	}

	/**
	 * @param mSelectedSize
	 *            the mSelectedSize to set
	 */
	public void setSelectedSize(CampaignItemSize mSelectedSize) {
		this.mSelectedSize = mSelectedSize;
	}

	/**
	 * @param mSelectedSizePosition
	 *            the mSelectedSizePosition to set
	 */
	public void setSelectedSizePosition(int mSelectedSizePosition) {
		this.mSelectedSizePosition = mSelectedSizePosition;
	}

	/**
	 * @param mRemainingTime
	 *            the mRemainingTime to set
	 */
	public void setRemainingTime(int mRemainingTime) {
		this.mRemainingTime = mRemainingTime;
	}

	/**
	 * @param priceConverted
	 *            the mPriceConverted to set
	 */
	public void setPriceConverted(double priceConverted) {
		this.mPriceConverted = priceConverted;
	}

	/**
	 * @param specialPriceConverted
	 *            the mSpecialPriceConverted to set
	 */
	public void setSpecialPriceConverted(double specialPriceConverted) {
		this.mSpecialPriceConverted = specialPriceConverted;
	}

	/**
	 * ########### Parcelable ###########
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(mSavePrice);
		dest.writeDouble(mSpecialPrice);
		dest.writeDouble(mPrice);
		dest.writeString(mSku);
		dest.writeString(mBrand);
		dest.writeString(mName);
		dest.writeString(mImage);
		dest.writeList(mImages);
		dest.writeInt(mStockPercentage);
		dest.writeDouble(mMaxSavingPercentage);
		dest.writeBooleanArray(new boolean[] { hasUniqueSize });
		dest.writeList(mSizes);
		dest.writeParcelable(mSelectedSize, 0);
		dest.writeInt(mSelectedSizePosition);
		dest.writeInt(mRemainingTime);
		dest.writeDouble(mPriceConverted);
		dest.writeDouble(mSpecialPriceConverted);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	public CampaignItem(Parcel in) {
		mSavePrice = in.readDouble();
		mSpecialPrice = in.readDouble();
		mPrice = in.readDouble();
		mSku = in.readString();
		mBrand = in.readString();
		mName = in.readString();
		mImage = in.readString();
		mImages = new ArrayList<String>();
		in.readList(mImages, String.class.getClassLoader());
		mStockPercentage = in.readInt();
		mMaxSavingPercentage = in.readDouble();
		boolean[] bolArray = new boolean[1];
		in.readBooleanArray(bolArray);
		hasUniqueSize = bolArray[0];
		mSizes = new ArrayList<CampaignItemSize>();
		in.readList(mSizes, CampaignItemSize.class.getClassLoader());
		mSelectedSize = in.readParcelable(CampaignItemSize.class.getClassLoader());
		mSelectedSizePosition = in.readInt();
		mRemainingTime = in.readInt();
		mPriceConverted = in.readDouble();
		mSpecialPriceConverted = in.readDouble();
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
