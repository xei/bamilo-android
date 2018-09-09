package com.bamilo.android.framework.service.utils;

/**
 * Class used to save the regex can be used on the app 
 * @author sergiopereira
 *
 */
public class DarwinRegex {
	
//	/**
//	 * Remove the image tag, case-insensitive
//	 */
//	public static final String REGEX_HTML_IMG_TAG = "(?i)<img(.*?)\\>";
	
	/**
	 * Remove the tag of image resolution for a product
	 * http://static.theiconic.com.au/p/decjuba-5688-313901-1-catalog.jpg
	 */
	public static final String REGEX_IMAGE_RESOLUTION = "(\\-[a-zA-Z]*\\.[a-zA-Z]*)";
	
	/**
	 * // "(\\-[a-zA-Z]*\\.(?i)(jpg|png|gif|bmp|webp))"
	 * // "(.*?\\-)"
	 * // [^\\s]+(\\.(?i)(jpg|png|gif|bmp))$
	 * // ([a-zA-Z]*\\.[a-zA-Z]*)
	 * // (\\-[a-zA-Z]*\\.)
	 */
	public static final String REGEX_RESOLUTION_TAG = "(\\-)([a-zA-Z]*)(\\.)";
	
	
//	/**
//	 * Regex used on deep link for catalog
//	 */
//	public static final String CATALOG_DEEP_LINK = "/c/";
	
	/**
	 * Regex used on deep link for cart
	 */
	//public static final String CART_DEEP_LINK = "/cart";
	public static final String SKU_DELIMITER = "_";
	
	//public static final String DL_DELIMITER = "/";
	
	/**
	 * Regex used to remove the unused chars from cart value
	 */
	public static final String CART_VALUE = "[, ]";


	/**
	 * Regex used to get the domain from host<br>
	 *     - www.jumia.com.ng -> .jumia.com.ng
	 */
	public static final String COOKIE_DOMAIN = "^.*?\\.";

}
