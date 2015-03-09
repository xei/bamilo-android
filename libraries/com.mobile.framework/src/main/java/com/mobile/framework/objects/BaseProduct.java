package com.mobile.framework.objects;

/**
 * Created by rsoares on 3/6/15.
 */
public class BaseProduct {
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


}
