package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author
 * @version 1.01
 * @date 2015/03/06
 */
public class BaseProduct implements Parcelable {
    protected String sku;
    protected String name;
    protected String brand;
    protected String price;
    protected String url;
    protected String specialPrice;
    protected double priceDouble;
    protected double specialPriceDouble;
    protected double specialPriceConverted;
    protected double priceConverted;
    protected boolean isFavourite;

    public BaseProduct(){
        sku = "";
        name= "";
        brand= "";
        price= "";
        url= "";
        specialPrice= "";
        priceDouble= 0d;
        specialPriceDouble= 0d;
        specialPriceConverted= 0d;
        priceConverted= 0d;
    }

    public BaseProduct(BaseProduct baseProduct){
        sku = baseProduct.getSku();
        name= baseProduct.getName();
        brand= baseProduct.getBrand();
        price= baseProduct.getPrice();
        url= baseProduct.getUrl();
        specialPrice= baseProduct.getSpecialPrice();
        priceDouble= baseProduct.getPriceDouble();
        specialPriceDouble= baseProduct.getSpecialPriceDouble();
        specialPriceConverted= baseProduct.getSpecialPriceConverted();
        priceConverted= baseProduct.getPriceConverted();
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
     * @return the price as a Double
     */
    public double getPriceDouble() {
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

    public double getSpecialPriceDouble() {
        return this.specialPriceDouble;
    }

    /**
     * @return the mSpecialPriceConverted
     */
    public double getSpecialPriceConverted() {
        return specialPriceConverted;
    }

    /**
     * @param mSpecialPriceConverted the mSpecialPriceConverted to set
     */
    public void setSpecialPriceConverted(double mSpecialPriceConverted) {
        this.specialPriceConverted = mSpecialPriceConverted;
    }

    /**
     * @return the mPriceConverted
     */
    public double getPriceConverted() {
        return priceConverted;
    }

    /**
     * @param mPriceConverted the mPriceConverted to set
     */
    public void setPriceConverted(double mPriceConverted) {
        this.priceConverted = mPriceConverted;
    }
    
    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    /*
	 * ############ PARCELABLE ############
	 */

    /**
     * Parcelable constructor
     * @param in The parcel object
     */
    protected BaseProduct(Parcel in) {
        sku = in.readString();
        name = in.readString();
        brand = in.readString();
        price = in.readString();
        url = in.readString();
        specialPrice = in.readString();
        priceDouble = in.readDouble();
        specialPriceDouble = in.readDouble();
        specialPriceConverted = in.readDouble();
        priceConverted = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sku);
        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(price);
        dest.writeString(url);
        dest.writeString(specialPrice);
        dest.writeDouble(priceDouble);
        dest.writeDouble(specialPriceDouble);
        dest.writeDouble(specialPriceConverted);
        dest.writeDouble(priceConverted);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BaseProduct> CREATOR = new Parcelable.Creator<BaseProduct>() {
        @Override
        public BaseProduct createFromParcel(Parcel in) {
            return new BaseProduct(in);
        }

        @Override
        public BaseProduct[] newArray(int size) {
            return new BaseProduct[size];
        }
    };
}
