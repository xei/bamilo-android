package pt.rocket.framework.utils;

/**
 * Class used to save the regex can be used on the app 
 * @author sergiopereira
 *
 */
public class DarwinRegex {
	
	/**
	 * Remove the image tag, case-insensitive
	 */
	public static final String REGEX_HTML_IMG_TAG = "(?i)<img(.*?)\\>";
	
	/**
	 * Remove the tag of image resolution for a product
	 * http://static.theiconic.com.au/p/decjuba-5688-313901-1-catalog.jpg
	 */
	public static final String REGEX_IMAGE_RESOLUTION = "(\\-[a-zA-Z]*\\.[a-zA-Z]*)";
	
	/**
	 * FIXME - REGEX only for the resolution name 
	 * 
	 * // "(\\-[a-zA-Z]*\\.(?i)(jpg|png|gif|bmp|webp))"
	 * // "(.*?\\-)"
	 * // [^\\s]+(\\.(?i)(jpg|png|gif|bmp))$
	 * // ([a-zA-Z]*\\.[a-zA-Z]*)
	 * // (\\-[a-zA-Z]*\\.)
	 */
	public static final String REGEX_RESOLUTION_TAG = "(\\-)([a-zA-Z]*)(\\.)";
	

}
