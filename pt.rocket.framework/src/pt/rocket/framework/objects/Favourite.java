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
 * Class that manages the full representation of a given favourite.
 * 
 * @author Andre Lopes
 * 
 */
public class Favourite {
	private String sku;
	private String brand;
	private String name;
	private String price;
	private String specialPrice;
	private Double maxSavingPercentage;
	private String url;
	private boolean isNew;
	private int selectedSimple;
	private boolean isComplete;

	private ArrayList<String> imageList;

	private ArrayList<ProductSimple> simples;

	private ArrayList<Variation> variations;

	private ArrayList<String> knownVariations;

	/**
	 * Complete favourite empty constructor.
	 */
	public Favourite() {
		imageList = new ArrayList<String>();
		simples = new ArrayList<ProductSimple>();
		variations = new ArrayList<Variation>();
		knownVariations = new ArrayList<String>();

		price = CurrencyFormatter.formatCurrency(0.0);
		specialPrice = CurrencyFormatter.formatCurrency(0.0);
		maxSavingPercentage = 0.0;

		url = "";
	}

	public Favourite(CompleteProduct completeProduct) {
		sku = completeProduct.getSku();
		brand = completeProduct.getBrand();
		name = completeProduct.getName();
		price = completeProduct.getPrice();
		specialPrice = completeProduct.getSpecialPrice();
		maxSavingPercentage = completeProduct.getMaxSavingPercentage();
		url = completeProduct.getUrl();
		isNew = Boolean.getBoolean(completeProduct.getAttributes().get(RestConstants.JSON_IS_NEW_TAG));
		selectedSimple = -1; // NO_SIMPLE_SELECTED
		isComplete = true;
		imageList = completeProduct.getImageList();
		simples = completeProduct.getSimples();
		variations = completeProduct.getVariations();
		knownVariations = completeProduct.getKnownVariations();
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
}
