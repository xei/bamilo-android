package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import java.util.ArrayList;

/**
 * Class that manages the full representation of an object that can be added to
 * Cart<br>
 * Used for Favourites and Last Viewed items
 * 
 * @author Andre Lopes
 * 
 */
public class AddableToCart extends BaseProduct {

	public final static int NO_SIMPLE_SELECTED = -1;

	private static final String TAG = AddableToCart.class.getSimpleName();

	protected Double maxSavingPercentage;
	protected boolean isNew;
	protected int selectedSimple;
	protected boolean isComplete;
	protected ArrayList<String> imageList;
	protected ArrayList<ProductSimple> simples;
	protected ArrayList<Variation> variations;
	protected ArrayList<String> knownVariations;
	protected int favoriteSelected;
	public Boolean hasVariations;
	protected String mSelectedSimpleValue;
	protected Boolean mChooseVariationWarning = false;
	protected boolean mStockVariationWarning = false;
	protected ArrayList<String> mCategories;
	protected Double mRatingsAverage;
	protected String mSizeGuideUrl;

	/**
	 * Complete favourite empty constructor.
	 */
	public AddableToCart() {
		imageList = new ArrayList<>();
		simples = new ArrayList<>();
		variations = new ArrayList<>();
		knownVariations = new ArrayList<>();
		priceDouble = 0.0;
		specialPriceDouble = 0.0;
		price = "0";
		specialPrice = "0";
		maxSavingPercentage = 0.0;
		url = "";
		favoriteSelected = NO_SIMPLE_SELECTED;
		hasVariations = null;
		mSelectedSimpleValue = "...";
		selectedSimple = NO_SIMPLE_SELECTED;
		specialPriceConverted = 0d;
		priceConverted = 0d;
		mCategories = new ArrayList<>();
		mRatingsAverage = 0.0;
		mSizeGuideUrl = "";
	}

	public AddableToCart(CompleteProduct completeProduct) {
        super(completeProduct);
		maxSavingPercentage = completeProduct.getMaxSavingPercentage();
        isNew = Boolean.getBoolean(completeProduct.getAttributes().get(RestConstants.JSON_IS_NEW_TAG));
		isComplete = true;
		imageList = completeProduct.getImageList();
		simples = completeProduct.getSimples();
		variations = completeProduct.getVariations();
		knownVariations = completeProduct.getKnownVariations();
		favoriteSelected = NO_SIMPLE_SELECTED;
		hasVariations = null;
		mSelectedSimpleValue = "...";
		// Validate if has only one simple
		selectedSimple = (simples != null && simples.size() == 1) ? 0 : NO_SIMPLE_SELECTED;
		if(CollectionUtils.isNotEmpty(completeProduct.getCategories())) mCategories = completeProduct.getCategories(); 
		mRatingsAverage = completeProduct.getRatingsAverage();
		mSizeGuideUrl = completeProduct.getSizeGuideUrl();
	}

	/**
	 * @return the maxSavingPercentage
	 */
	public Double getMaxSavingPercentage() {
		return maxSavingPercentage;
	}

	/**
	 * @param maxSavingPercentage
	 *            the maxSavingPercentage to set
	 */
	public void setMaxSavingPercentage(Double maxSavingPercentage) {
		this.maxSavingPercentage = maxSavingPercentage;
	}

	/**
	 * @return the isNew
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * @param isNew
	 *            the isNew to set
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * @return the selectedSimple
	 */
	public int getSelectedSimple() {
		if(selectedSimple == NO_SIMPLE_SELECTED){
			return selectedSimple = (simples != null && simples.size() == 1) ? 0 : NO_SIMPLE_SELECTED;
		}
		return selectedSimple;
	}

	/**
	 * @param selectedSimple
	 *            the selectedSimple to set
	 */
	public void setSelectedSimple(int selectedSimple) {
		this.selectedSimple = selectedSimple;
	}

	/**
	 * @return the isComplete
	 */
	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * @param isComplete
	 *            the isComplete to set
	 */
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	/**
	 * @return the imageList
	 */
	public ArrayList<String> getImageList() {
		return imageList;
	}

	/**
	 * @param imageList
	 *            the imageList to set
	 */
	public void setImageList(ArrayList<String> imageList) {
		this.imageList = imageList;
	}

	/**
	 * @return the simples
	 */
	public ArrayList<ProductSimple> getSimples() {
		return simples;
	}

	/**
	 * @param simples
	 *            the simples to set
	 */
	public void setSimples(ArrayList<ProductSimple> simples) {
		this.simples = simples;
	}

	/**
	 * @return the variations
	 */
	public ArrayList<Variation> getVariations() {
		return variations;
	}

	/**
	 * @param variations
	 *            the variations to set
	 */
	public void setVariations(ArrayList<Variation> variations) {
		this.variations = variations;
	}

	/**
	 * @return the knownVariations
	 */
	public ArrayList<String> getKnownVariations() {
		return knownVariations;
	}

	/**
	 * @param knownVariations
	 *            the knownVariations to set
	 */
	public void setKnownVariations(ArrayList<String> knownVariations) {
		this.knownVariations = knownVariations;
	}

	/**
	 * @return the favoriteSelected
	 */
	public int getFavoriteSelected() {
		return favoriteSelected;
	}

	/**
	 * @param favoriteSelected
	 *            the favoriteSelected to set
	 */
	public void setFavoriteSelected(int favoriteSelected) {
		this.favoriteSelected = favoriteSelected;
	}

	public boolean hasSimples() {
		return simples != null && simples.size() > 1;
	}

	public void setSelectedSimpleValue(String value) {
		mSelectedSimpleValue = value;
	}

	public String getSelectedSimpleValue() {
		return mSelectedSimpleValue;
	}

	public void setChooseVariationWarning(boolean bool) {
		mChooseVariationWarning = bool;
	}

	public boolean showChooseVariationWarning() {
		return mChooseVariationWarning;
	}

	public void setVariationStockWarning(boolean bool) {
		mStockVariationWarning = bool;
	}

	public boolean showStockVariationWarning() {
		return mStockVariationWarning;
	}

	/**
	 * Return the paid price for tracking.
	 * 
	 * @return double
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		Print.i(TAG, "ORIGIN PRICE VALUES: " + priceDouble + " " + specialPriceDouble);
		Print.i(TAG, "PRICE VALUE FOR TRACKING: " + priceConverted + " " + specialPriceConverted);
		return specialPriceConverted > 0 ? specialPriceConverted : priceConverted;
	}
	
	   /**
     * @return the categories
     */
    public ArrayList<String> getCategories() {
        return mCategories;
    }
    
    /**
     * @param categories
     *            the categories to set
     */
    public void setCategories(ArrayList<String> categories) {
        this.mCategories = categories;
    }
    
    /**
     * @return the ratings average
     */
    public Double getRatingsAverage() {
        return mRatingsAverage;
    }
    
    /**
     * Validate special price.
     * @return true or false
     * @author sergiopereira
     */
    public boolean hasDiscount() {
        return specialPriceConverted > 0;
    }
    
    /**
     * Set size guide URL
     * @author sergiopereira
     */
    public void setSizeGuideUrl(String url) {
        mSizeGuideUrl = url;
    }
    
    /**
     * Get size guide URL
     * @return String
     * @author sergiopereira
     */
    public String getSizeGuideUrl() {
        return mSizeGuideUrl;
    }
    
    /**
     * Validate size guide url
     * @return true or false
     * @author sergiopereira
     */
    public boolean hasSizeGuide() {
        return TextUtils.isEmpty(mSizeGuideUrl);
    }

	/*
	 * ############ PARCELABLE ############
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
        super.writeToParcel(dest, flags);
		dest.writeDouble(maxSavingPercentage);
		dest.writeByte((byte) (isNew ? 1 : 0));
		dest.writeInt(selectedSimple);
		dest.writeByte((byte) (isComplete ? 1 : 0));
		dest.writeList(imageList);
		dest.writeList(simples);
		dest.writeList(variations);
		dest.writeList(knownVariations);
		dest.writeInt(favoriteSelected);
		dest.writeByte((byte) (hasVariations ? 1 : 0));
		dest.writeString(mSelectedSimpleValue);
		dest.writeByte((byte) (mChooseVariationWarning ? 1 : 0));
		dest.writeByte((byte) (mStockVariationWarning ? 1 : 0));
		dest.writeList(mCategories);
		dest.writeDouble(mRatingsAverage);
		dest.writeString(mSizeGuideUrl);
	}

	private AddableToCart(Parcel in) {
        super(in);
		maxSavingPercentage = in.readDouble();
		isNew = in.readByte() == 1;
		selectedSimple = in.readInt();
		isComplete = in.readByte() == 1;
		imageList = new ArrayList<>();
		in.readList(imageList, String.class.getClassLoader());
		simples = new ArrayList<>();
		in.readList(simples, ProductSimple.class.getClassLoader());
		variations = new ArrayList<>();
		in.readList(variations, Variation.class.getClassLoader());
		knownVariations = new ArrayList<>();
		in.readList(knownVariations, String.class.getClassLoader());
		favoriteSelected = in.readInt();
		hasVariations = in.readByte() == 1;
		mSelectedSimpleValue = in.readString();
		mChooseVariationWarning = in.readByte() == 1;
		mStockVariationWarning = in.readByte() == 1;
        mCategories = new ArrayList<>();
        in.readList(mCategories, String.class.getClassLoader());
        mRatingsAverage = in.readDouble();
        mSizeGuideUrl = in.readString();
	}

	public static final Parcelable.Creator<AddableToCart> CREATOR = new Parcelable.Creator<AddableToCart>() {
		public AddableToCart createFromParcel(Parcel in) {
			return new AddableToCart(in);
		}

		public AddableToCart[] newArray(int size) {
			return new AddableToCart[size];
		}
	};

}