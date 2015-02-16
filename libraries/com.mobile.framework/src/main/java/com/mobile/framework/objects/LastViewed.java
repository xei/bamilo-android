package com.mobile.framework.objects;

/**
 * Object that contains one of the last viewed products
 * @author Manuel Silva
 *
 */
public class LastViewed {
	protected final static String TAG = LastViewed.class.getSimpleName();

    private String mSku;
	private String mBrand;
    private String mName;
    private String mPrice;
    private String mUrl;
    private String mImage;
	
	
    /**
     * Empty constructor
     */
	public LastViewed() { }
	
	/**
	 * 
	 * @param sku
	 * @param brand
	 * @param name
	 * @param price
	 * @param url
	 * @param image_url
	 */
	public LastViewed(String sku, String brand, String name, String price, String url, String image_url) {
	    this.setProductSku(sku);
	    this.setProductBrand(brand);
	    this.setProductName(name);
	    this.setProductPrice(price);
	    this.setProductUrl(url);
	    this.setImageUrl(image_url);
	}

	/**
     * @return the product_sku
     */
    public String getProductSku() {
        return mSku;
    }

    /**
     * @param product_sku the product_sku to set
     */
    public void setProductSku(String product_sku) {
        this.mSku = product_sku;
    }

    /**
     * @return the product_name
     */
    public String getProductName() {
        return mName;
    }

    /**
     * @param product_name the product_name to set
     */
    public void setProductName(String product_name) {
        this.mName = product_name;
    }

    /**
     * @return the product_price
     */
    public String getProductPrice() {
        return mPrice;
    }

    /**
     * @param product_price the product_price to set
     */
    public void setProductPrice(String product_price) {
        this.mPrice = product_price;
    }

    /**
     * @return the image_url
     */
    public String getImageUrl() {
        return mImage;
    }

    /**
     * @param image_url the image_url to set
     */
    public void setImageUrl(String image_url) {
        this.mImage = image_url;
    }

    /**
     * @return the product_url
     */
    public String getProductUrl() {
        return mUrl;
    }

    /**
     * @param product_url the product_url to set
     */
    public void setProductUrl(String product_url) {
        this.mUrl = product_url;
    }
    
    /**
	 * @return the mBrand
	 */
	public String getProductBrand() {
		return mBrand;
	}

	/**
	 * Set the brand
	 * @param brand
	 */
	public void setProductBrand(String brand) {
		this.mBrand = brand;
	}

}
