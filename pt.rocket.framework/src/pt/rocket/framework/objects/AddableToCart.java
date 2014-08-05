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

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;

/**
 * Class that manages the full representation of an object that can be added to
 * Cart<br>
 * Used for Favourites and Last Viewed items
 * 
 * @author Andre Lopes
 * 
 */
public class AddableToCart {

	public final static int NO_SIMPLE_SELECTED = -1;

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
		price = CurrencyFormatter.formatCurrency(0.0);
		specialPrice = CurrencyFormatter.formatCurrency(0.0);
		maxSavingPercentage = 0.0;
		url = "";
		favoriteSelected = NO_SIMPLE_SELECTED;
		hasVariations = null;
		mSelectedSimpleValue = "...";
		selectedSimple = NO_SIMPLE_SELECTED;
	}

	public AddableToCart(CompleteProduct completeProduct) {
		sku = completeProduct.getSku();
		brand = completeProduct.getBrand();
		name = completeProduct.getName();
		price = completeProduct.getPrice();
		priceDouble = completeProduct.getPriceAsDouble();
		specialPriceDouble = completeProduct.getMaxPriceAsDouble();
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

		// Validate if has only one simple
		selectedSimple = (simples != null && simples.size() == 1) ? 0 : NO_SIMPLE_SELECTED;
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
}
