package com.mobile.framework.utils;

/**
 * Class that contains all static constants utilized in the framework
 *
 * @author josedourado
 */
public class Constants {

    /**
     * Bundle Info Constants - keys used in identifying the bundle parameters used to pass information around
     */
    public static final String BUNDLE_URL_KEY = "url";
    public static final String BUNDLE_DATA_KEY = "data";
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
    public static final String BUNDLE_EVENT_TASK = "eventtask";
    public static final String BUNDLE_ELAPSED_REQUEST_TIME = "elapsedtime";
    public static final String BUNDLE_JSON_VALIDATION_KEY = "jsonvalidation";
    public static final String BUNDLE_WRONG_PARAMETER_MESSAGE_KEY = "parametererrormessage";
    public static final String BUNDLE_ERROR_OCURRED_KEY = "errorocurred";
    public static final String BUNDLE_NEXT_STEP_KEY = "next_step";

    public static final String BUNDLE_METADATA_REQUIRED_KEY = "no_metada";
    public static final String BUNDLE_GENERAL_RULES_FALSE_KEY = "general_rules_false";
    public static final String BUNDLE_GENERAL_RULES_GET_COUNTRIES_KEY = "general_rules_get_countries_key";

    public static final String BUNDLE_ORDER_SUMMARY_KEY = "order_summary";
    public static final String BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY = "responsesuccessmessage";
    /**
     * Test bundle information - key used to pass the result of the assertion made for the generic rules
     */
    public static String BUNDLE_GENERAL_ASSERTION_KEY = "general_assertion";

    /**
     * MD5 TAG Constants - keys used to indicate the tag that has to be used when generating a md5 hash key
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

    public static String PIN = "rocket123";

    /**
     * ######## Device Info ########
     */
    public static final String INFO_PRE_INSTALL = "preInstalment";
    public static final String INFO_BRAND = "brand";
    public static final String INFO_SIM_OPERATOR = "operator";
    public static final String INFO_BUNDLE_VERSION = "bundleVersion";

}
