package pt.rocket.framework.objects;

/**
 * Object that contains one of the last viewed products
 * @author Manuel Silva
 *
 */
public class LastViewed {
	protected final static String TAG = LastViewed.class.getSimpleName();

    private String product_sku;
    private String product_name;
    private String product_price;
    private String product_url;
    private String image_url;
	
	
	public LastViewed() {
	    
	}
	
	/**
	 * 
	 * @param sku
	 * @param name
	 * @param price
	 * @param url
	 * @param image_url
	 */
	public LastViewed(String sku, String name, String price, String url, String image_url) {
	    this.setProductSku(sku);
	    this.setProductName(name);
	    this.setProductPrice(price);
	    this.setProductUrl(url);
	    this.setImageUrl(image_url);
	}

    /**
     * @return the product_sku
     */
    public String getProductSku() {
        return product_sku;
    }

    /**
     * @param product_sku the product_sku to set
     */
    public void setProductSku(String product_sku) {
        this.product_sku = product_sku;
    }

    /**
     * @return the product_name
     */
    public String getProductName() {
        return product_name;
    }

    /**
     * @param product_name the product_name to set
     */
    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    /**
     * @return the product_price
     */
    public String getProductPrice() {
        return product_price;
    }

    /**
     * @param product_price the product_price to set
     */
    public void setProductPrice(String product_price) {
        this.product_price = product_price;
    }

    /**
     * @return the image_url
     */
    public String getImageUrl() {
        return image_url;
    }

    /**
     * @param image_url the image_url to set
     */
    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    /**
     * @return the product_url
     */
    public String getProductUrl() {
        return product_url;
    }

    /**
     * @param product_url the product_url to set
     */
    public void setProductUrl(String product_url) {
        this.product_url = product_url;
    }

}
