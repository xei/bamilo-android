package pt.rocket.framework.utils;

/**
 * Class that contains all static constants utilized in the framework
 * 
 * @author josedourado
 * 
 */
public class Constants {

	/**
	 * Bundle Info Constants - keys used in identifying the bundle parameters
	 * used to pass information around
	 */
	public static final String BUNDLE_URL_KEY = "url";
	public static final String BUNDLE_REQUEST_TYPE_KEY = "request_type";
	public static final String BUNDLE_OBJECT_TYPE_KEY = "object_type";
	public static final String BUNDLE_PRIORITY_KEY = "priority";
	public static final String BUNDLE_MD5_KEY = "md5";
	public static final String BUNDLE_TYPE_KEY = "type";
	public static final String BUNDLE_FORM_DATA_KEY = "form_data";
	public static final String BUNDLE_METADATA_KEY = "metadata";
	public static final String BUNDLE_ERROR_KEY = "error";
	public static final String BUNDLE_EVENT_TYPE_KEY = "eventtype";
	public static final String BUNDLE_RESPONSE_KEY = "response";
	public static final String BUNDLE_RESPONSE_ERROR_MESSAGE_KEY = "responseerrormessage";
	public static final String BUNDLE_ELAPSED_REQUEST_TIME = "elapsedtime";
	public static final String BUNDLE_JSON_VALIDATION_KEY = "jsonvalidation";
	public static final String BUNDLE_WRONG_PARAMETER_MESSAGE_KEY = "parametererrormessage";
	public static final String BUNDLE_ERROR_OCURRED_KEY = "errorocurred";
	/**
	 * Test bundle information - key used to pass the result of the assertion made for the generic rules
	 */
	public static String BUNDLE_GENERAL_ASSERTION_KEY = "general_assertion";

	/**
	 * MD5 TAG Constants - keys used to indicate the tag that has to be used
	 * when generating a md5 hash key
	 */

	public static String MD5_PRODUCTS_TAG = "products";

	/**
	 * Handler messages
	 */

	public static final int SUCCESS = 1;
	public static final int FAILURE = 0;
    
	
	/**
	 * Encryption variables
	 */
	
	public static String PIN="rocket123";
	
}
