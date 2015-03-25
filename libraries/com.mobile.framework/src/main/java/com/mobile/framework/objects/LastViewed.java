package com.mobile.framework.objects;

/**
 * Object that contains one of the last viewed products
 * @author Manuel Silva
 *
 */
public class LastViewed extends BaseProduct{
	protected final static String TAG = LastViewed.class.getSimpleName();

    private String imageUrl;

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
	    this.setSku(sku);
	    this.setBrand(brand);
	    this.setName(name);
	    this.setPrice(price);
	    this.setUrl(url);
	    this.setImageUrl(image_url);
	}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image) {
        this.imageUrl = image;
    }
}
