/**
 * CompleteProduct.java
 * Complete PRoduct class. Represents the complete product used in the products detials activity.
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */

package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.apache.commons.collections4.CollectionUtils;

import de.akquinet.android.androlog.Log;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that manages the full representation of an object that can be added to
 * Cart<br>
 * Used for Favourites and Last Viewed items
 * 
 * @author Andre Lopes
 * 
 */
public class AddableToCart implements Parcelable {

	public final static int NO_SIMPLE_SELECTED = -1;

	private static final String TAG = AddableToCart.class.getSimpleName();

	protected String sku;
	protected String brand;
	protected String name;
	protected String price;
	protected String specialPrice;
	protected Double maxSavingPercentage;
	protected String url;
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
	private Double priceDouble;
	private Double specialPriceDouble;
	private double mSpecialPriceConverted;
	private double mPriceConverted;
	private ArrayList<String> mCategories;
	private Double mRatingsAverage;

	/**
	 * Complete favourite empty constructor.
	 */
	public AddableToCart() {
		imageList = new ArrayList<String>();
		simples = new ArrayList<ProductSimple>();
		variations = new ArrayList<Variation>();
		knownVariations = new ArrayList<String>();
		priceDouble = 0.0;
		specialPriceDouble = 0.0;
		price = CurrencyFormatter.formatCurrency("0");
		specialPrice = CurrencyFormatter.formatCurrency("0");
		maxSavingPercentage = 0.0;
		url = "";
		favoriteSelected = NO_SIMPLE_SELECTED;
		hasVariations = null;
		mSelectedSimpleValue = "...";
		selectedSimple = NO_SIMPLE_SELECTED;
		mSpecialPriceConverted = 0d;
		mPriceConverted = 0d;
		mCategories = new ArrayList<String>();
		mRatingsAverage = 0.0;
	}

	public AddableToCart(CompleteProduct completeProduct) {
		sku = completeProduct.getSku();
		brand = completeProduct.getBrand();
		name = completeProduct.getName();
		price = completeProduct.getPrice();
		priceDouble = completeProduct.getPriceAsDouble();
		specialPriceDouble = completeProduct.getSpecialPriceAsDouble();
		specialPrice = completeProduct.getSpecialPrice();
		maxSavingPercentage = completeProduct.getMaxSavingPercentage();
		url = completeProduct.getUrl();
		isNew = Boolean.getBoolean(completeProduct.getAttributes().get(RestConstants.JSON_IS_NEW_TAG));
		selectedSimple = NO_SIMPLE_SELECTED;
		isComplete = true;
		imageList = completeProduct.getImageList();
		simples = completeProduct.getSimples();
		variations = completeProduct.getVariations();
		knownVariations = completeProduct.getKnownVariations();
		favoriteSelected = NO_SIMPLE_SELECTED;
		hasVariations = null;
		mSelectedSimpleValue = "...";
		// Converted values
		mPriceConverted = completeProduct.getPriceConverted();
		mSpecialPriceConverted = completeProduct.getSpecialPriceConverted();
		// Validate if has only one simple
		selectedSimple = (simples != null && simples.size() == 1) ? 0 : NO_SIMPLE_SELECTED;
		if(CollectionUtils.isNotEmpty(completeProduct.getCategories())) mCategories = completeProduct.getCategories(); 
		mRatingsAverage = completeProduct.getRatingsAverage();
	}

	/**
	 * @return the sku
	 */
	public String getSku() {
		return sku;
	}

	/**
	 * @param sku
	 *            the sku to set
	 */
	public void setSku(String sku) {
		this.sku = sku;
	}

	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand
	 *            the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the price as a Double
	 */
	public Double getPriceAsDouble() {
		return priceDouble;
	}

	/**
	 * 
	 * @param priceDouble
	 */
	public void setPriceAsDouble(double priceDouble) {
		this.priceDouble = priceDouble;
	}

	public void setSpecialPriceDouble(Double priceDouble) {
		this.specialPriceDouble = priceDouble;
	}

	public Double getSpecialPriceDouble() {
		return this.specialPriceDouble;
	}

	/**
	 * @return the specialPrice
	 */
	public String getSpecialPrice() {
		return specialPrice;
	}

	/**
	 * @param specialPrice
	 *            the specialPrice to set
	 */
	public void setSpecialPrice(String specialPrice) {
		this.specialPrice = specialPrice;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
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
		return (simples != null && simples.size() > 1) ? true : false;
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
	 * @return the mSpecialPriceConverted
	 */
	public double getSpecialPriceConverted() {
		return mSpecialPriceConverted;
	}

	/**
	 * @param mSpecialPriceConverted the mSpecialPriceConverted to set
	 */
	public void setSpecialPriceConverted(double mSpecialPriceConverted) {
		this.mSpecialPriceConverted = mSpecialPriceConverted;
	}

	/**
	 * @return the mPriceConverted
	 */
	public double getPriceConverted() {
		return mPriceConverted;
	}

	/**
	 * @param mPriceConverted the mPriceConverted to set
	 */
	public void setPriceConverted(double mPriceConverted) {
		this.mPriceConverted = mPriceConverted;
	}

	/**
	 * Return the paid price for tracking.
	 * 
	 * @return double
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		Log.i(TAG, "ORIGIN PRICE VALUES: " + priceDouble + " " + specialPriceDouble);
		Log.i(TAG, "PRICE VALUE FOR TRACKING: " + mPriceConverted + " " + mSpecialPriceConverted);
		return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceConverted;
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
        return mSpecialPriceConverted > 0 ? true : false;
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
		dest.writeString(sku);
		dest.writeString(brand);
		dest.writeString(name);
		dest.writeString(price);
		dest.writeString(specialPrice);
		dest.writeDouble(maxSavingPercentage);
		dest.writeString(url);
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
		dest.writeDouble(priceDouble);
		dest.writeDouble(specialPriceDouble);
		dest.writeDouble(mPriceConverted);
		dest.writeDouble(mSpecialPriceConverted);
		dest.writeList(mCategories);
		dest.writeDouble(mRatingsAverage);
	}

	private AddableToCart(Parcel in) {
		sku = in.readString();
		brand = in.readString();
		name = in.readString();
		price = in.readString();
		specialPrice = in.readString();
		maxSavingPercentage = in.readDouble();
		url = in.readString();
		isNew = in.readByte() == 1;
		selectedSimple = in.readInt();
		isComplete = in.readByte() == 1;
		imageList = new ArrayList<String>();
		in.readList(imageList, String.class.getClassLoader());
		simples = new ArrayList<ProductSimple>();
		in.readList(simples, ProductSimple.class.getClassLoader());
		variations = new ArrayList<Variation>();
		in.readList(variations, Variation.class.getClassLoader());
		knownVariations = new ArrayList<String>();
		in.readList(knownVariations, String.class.getClassLoader());
		favoriteSelected = in.readInt();
		hasVariations = in.readByte() == 1;
		mSelectedSimpleValue = in.readString();
		mChooseVariationWarning = in.readByte() == 1;
		mStockVariationWarning = in.readByte() == 1;
		priceDouble = in.readDouble();
		specialPriceDouble = in.readDouble();
		mPriceConverted = in.readDouble();
		mSpecialPriceConverted = in.readDouble();
        mCategories = new ArrayList<String>();
        in.readList(mCategories, String.class.getClassLoader());
        mRatingsAverage = in.readDouble();
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
