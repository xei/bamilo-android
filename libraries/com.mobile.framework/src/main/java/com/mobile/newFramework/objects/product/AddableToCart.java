package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class that manages the full representation of an object that can be added to
 * Cart<br>
 * Used for Favourites and Last Viewed items
 * 
 * @author Andre Lopes
 * 
 */
public class AddableToCart extends NewProductPartial {

	public final static int NO_SIMPLE_SELECTED = -1;

	protected static final String TAG = AddableToCart.class.getSimpleName();

	protected int selectedSimple;
	protected boolean isComplete;
	protected ArrayList<String> imageList;
	protected ArrayList<NewProductSimple> simples;
	protected ArrayList<Variation> variations;
	protected ArrayList<String> knownVariations;
	public Boolean hasVariations;
	protected String mSelectedSimpleValue;
	protected Boolean mChooseVariationWarning = false;
	protected boolean mStockVariationWarning = false;
	protected String mCategories;
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
		hasVariations = null;
		mSelectedSimpleValue = "...";
		selectedSimple = NO_SIMPLE_SELECTED;
		mRatingsAverage = 0.0;
		mSizeGuideUrl = "";
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
	 * @return the simples
	 */
	public ArrayList<NewProductSimple> getSimples() {
		return simples;
	}

	/**
	 * @param simples
	 *            the simples to set
	 */
	public void setSimples(ArrayList<NewProductSimple> simples) {
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
     * @return the categories
     */
    public String getCategories() {
        return mCategories;
    }

	public String[] getCategoriesList(){
		return mCategories.split(",");
	}

    /**
     * @param categories
     *            the categories to set
     */
    public void setCategories(String categories) {
        this.mCategories = categories;
    }
    
    /**
     * @return the ratings average
     */
    public Double getRatingsAverage() {
        return mRatingsAverage;
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
		dest.writeInt(selectedSimple);
		dest.writeByte((byte) (isComplete ? 1 : 0));
		dest.writeList(imageList);
		dest.writeList(simples);
		dest.writeList(variations);
		dest.writeList(knownVariations);
		dest.writeByte((byte) (hasVariations ? 1 : 0));
		dest.writeString(mSelectedSimpleValue);
		dest.writeByte((byte) (mChooseVariationWarning ? 1 : 0));
		dest.writeByte((byte) (mStockVariationWarning ? 1 : 0));
		dest.writeString(mCategories);
		dest.writeDouble(mRatingsAverage);
		dest.writeString(mSizeGuideUrl);
	}

	private AddableToCart(Parcel in) {
        super(in);
		selectedSimple = in.readInt();
		isComplete = in.readByte() == 1;
		imageList = new ArrayList<>();
		in.readList(imageList, String.class.getClassLoader());
		simples = new ArrayList<>();
		in.readList(simples, NewProductSimple.class.getClassLoader());
		variations = new ArrayList<>();
		in.readList(variations, Variation.class.getClassLoader());
		knownVariations = new ArrayList<>();
		in.readList(knownVariations, String.class.getClassLoader());
		hasVariations = in.readByte() == 1;
		mSelectedSimpleValue = in.readString();
		mChooseVariationWarning = in.readByte() == 1;
		mStockVariationWarning = in.readByte() == 1;
		mCategories = in.readString();
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
