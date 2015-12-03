package com.mobile.newFramework.pojo;

/**
 * @author nunocastro
 */
public class RestConstants {

    // Common JSON constants
    public static final String JSON_DATA_TAG = "data";
    public static final String JSON_FORM_ENTITY_TAG = "form_entity";
    public static final String ID = "id";
    public final static String SUCCESS = "success";
    public final static String MESSAGE = "message";
    public final static String MESSAGES = "messages";
    public final static String JSON_MD5_TAG = "md5";
    public final static String JSON_IS_WISH_LIST_TAG = "is_wishlist";
    public final static String OTHER_ADDRESS_ID = "id_sales_order_address";
    public final static String QUERY = "q";


    // ConfigurationService Tags
    public static final String METADATA = "metadata";
    public static final String JSON_CALL_PHONE_TAG = "phone_number";

    // VersionInfo Tags
    public static final String JSON_VERSION_TAG = "version";
    public static final String JSON_CUSTOMER_ENTITY_TAG = "customer_entity";

    // CustomerAccountService Tags
    public static final String JSON_USER_AGENT_TAG = "user_agent";

    // Customer Tags
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String JSON_BIRTHDAY_TAG = "birthday";
    public static final String JSON_GENDER_TAG = "gender";

    // FormsService Tags
    public static final String JSON_NAME_TAG = "name";
    public static final String JSON_CODE_TAG = "code";
    public static final String JSON_NUMBER_TAG = "number";
    public static final String JSON_VAL_TAG = "val";
    public static final String JSON_HEX_VALUE_TAG = "hex_value";
    public static final String JSON_INTERVAL_TAG = "interval";

    // FormData Tags
    public static final String JSON_ACTION_TAG = "action";
    public static final String URL = "url";
    public static final String ZOOM = "zoom";

    // Form Tags
    public static final String JSON_FORM_TAG = "form";
    public static final String METHOD = "method";
    public static final String JSON_FIELDS_TAG = "fields";

    // FormField Tags
    public static final String TYPE = "type";
    public static final String KEY = "key";
    public static final String JSON_FIELD_NAME_TAG = JSON_NAME_TAG;
    public static final String LABEL = "label";
    public static final String JSON_DATA_SET_TAG = "dataset";
    public static final String VALUE = "value";
    public static final String JSON_DATA_SET_SOURCE_TAG = "dataset_source";
    public static final String JSON_SCENARIO_TAG = "scenario";
    public static final String JSON_TERMS_TAG = "terms";
    public static final String JSON_LINK_TEXT_TAG = "link_text";
    public static final String JSON_NEWSLETTER_CATEGORIES_SUBSCRIBED_TAG = "register[newsletter_categories_subscribed]";
    public static final String DELIVERY_TIME = "delivery_time";
    public static final String RELATED_DATA = "related_data";
    public static final String CHECKED = "checked";
    public static final String DISABLED = "disabled";
    public static final String JSON_FORMAT_TAG = "format";

    // FieldValidation Tags
    public static final String JSON_REQUIRED_TAG = "required";
    public static final String JSON_MIN_TAG = "min";
    public static final String JSON_MAX_TAG = "max";
    public static final String JSON_REGEX_TAG = "regex";
    public static final String JSON_PATTERN_TAG = "pattern";
    public static final String JSON_MATCH_TAG = "match";

    // NavigationList Tags
    public static final String JSON_IMAGE_TAG = "image";
    public static final String JSON_IMAGE_URL_TAG = "image_url";
    public static final String JSON_IMAGE_PORTRAIT_TAG = "image_portrait";
    public static final String JSON_IMAGE_LANDSCAPE_TAG = "image_landscape";

    // ProductService Tags
    public static final String JSON_SUGGESTIONS_TAG = "suggestions";

    public static final String JSON_OPTIONS_TAG = "options";
    public static final String REVIEW_RATING_FIELD = "ratings";

    // CompleteProduct Tags
    public static final String SKU = "sku";
    public static final String JSON_ATTRIBUTE_SET_ID_TAG = "attribute_set_id";
    public static final String JSON_PRICE_TAG = "price";
    public static final String JSON_PRICE_CONVERTED_TAG = "price_converted";
    public static final String BRAND = "brand";
    public static final String JSON_BRAND_ID_TAG = "brand_id";
    public static final String JSON_CATEGORIES_TAG = "categories";
    public static final String JSON_SIMPLES_TAG = "simples";
    public static final String JSON_IMAGE_LIST_TAG = "image_list";
    public static final String JSON_DESCRIPTION_TAG = "description";
    public static final String JSON_SHORT_DESC_TAG = "short_description";
    public static final String JSON_MAX_SAVING_PERCENTAGE_TAG = "max_saving_percentage";
    public static final String JSON_SPECIAL_PRICE_TAG = "special_price";
    public static final String JSON_SPECIAL_PRICE_CONVERTED_TAG = "special_price_converted";
    public static final String JSON_IS_NEW_TAG = "is_new";
    public static final String JSON_SIZE_GUIDE_URL_TAG = "size_guide";
    public static final String JSON_RELATED_PRODUCTS = "related_products";
    public static final String JSON_RATINGS_TOTAL_TAG = "ratings_total";

    //NEW RATING V1.6
    public static final String JSON_RATINGS_SUMMARY_TAG = "rating_reviews_summary";
    public static final String JSON_RATINGS_AVERAGE_TAG = "average";
    public static final String JSON_REVIEWS_TOTAL_TAG = "reviews_total";
    public static final String JSON_PRODUCT_TAG = "product";
    public static final String JSON_RATING_TYPE_TAG = "by_type";
    public static final String JSON_REVIEWS_TAG = "reviews";
    public static final String JSON_TOTAL_TAG = "total";
    public static final String JSON_COMMENT_TAG = "comment";
    public static final String JSON_COMMENT_DATE_TAG = "date";
    public static final String JSON_RATING_STAR_SIZE_TAG = "stars_size";
    public static final String JSON_DATA_SET_FORM_RATING_TAG = "data_set";
    public static final String JSON_TITLE_FORM_RATING_TAG = "display_title";
    public static final String JSON_ID_FORM_RATING_TAG = "id_rating_type";
    public static final String RATING = "rating";
    public static final String REVIEW = "review";
    public static final String JSON_IS_ENABLE_TAG = "is_enable";
    public static final String JSON_REQUIRED_LOGIN_TAG = "required_login";
    public static final String JSON_BASED_ON_TAG = "based_on";
    public static final String JSON_TOTAL_STARS_TAG = "total_stars";

    public static final String JSON_VARIATIONS_TAG = "variations";
    public static final String JSON_HAS_BUNDLE_TAG = "bundle";
    public static final String JSON_SHARE_URL_TAG = "share_url";
    public static final String JSON_SKU_TAG ="sku";

    // Seller tags
    public static final String JSON_SELLER_TAG = "seller_entity";
    public static final String JSON_MIN_DELIVERY_TAG = "min_delivery_time";
    public static final String JSON_MAX_DELIVERY_TAG = "max_delivery_time";
    public static final String JSON_WARRANTY_TAG = "warranty";

    // Variation Tags
    public static final String LINK = "link";
    public static final String JSON_VARIATION_IMAGE_TAG = JSON_IMAGE_TAG;
    public static final String JSON_VARIATION_NAME_TAG = JSON_NAME_TAG;
    public static final String JSON_VARIATION_PRICE_TAG = JSON_PRICE_TAG;
    public static final String JSON_VARIATION_SPECIALPRICE_TAG = JSON_SPECIAL_PRICE_TAG;

    // ProductsPage Tags
    public static final String JSON_RESULTS_TAG = "results";
    public static final String JSON_PRODUCT_COUNT_TAG = "product_count";
    public static final String JSON_TOTAL_PRODUCTS_TAG = "total_products";

    // Product Tags
    public static final String JSON_IMAGES_TAG = "images";

    // Category Tags
    public static final String JSON_CATEGORY_ID_TAG = "id_catalog_category";
    public static final String JSON_CATEGORY_TYPE_TAG = "type";
    public static final String JSON_CATEGORY_LABEL_TAG = LABEL;
    public static final String JSON_URL_KEY_TAG = "url_key";
    public static final String JSON_CHILDREN_TAG = "children";
    public static final String JSON_CATEGORY_URL_TAG = URL;

    // ShoppingCart Tags
    public static final String CART = "cart";
    public static final String TOTAL_PRODUCTS = "total_products";
    public static final String PRODUCTS = "products";
    public static final String AMOUNT = "amount";
    public static final String CODE = "code";
    public static final String EXTRA_COSTS = "extra_costs";
    public static final String SUM_COSTS_VALUE = "sum_costs_value";
    public static final String PRICE_RULES = "price_rules";
    public static final String SUB_TOTAL = "sub_total";
    public static final String SUB_TOTAL_CONVERTED = "sub_total_converted";
    public static final String TOTAL = "total";
    public static final String TOTAL_CONVERTED = "total_converted";
    public static final String CART_ENTITY = "cart_entity";
    public static final String COUPON = "coupon";
    public static final String DELIVERY = "delivery";
    public static final String VAT = "vat";
    public static final String LABEL_CONFIGURATION = "label_configuration";
    // ShoppingCartItem Tags
    public static final String JSON_ITEM_IMAGE_TAG = JSON_IMAGE_TAG;
    public static final String JSON_PRODUCT_URL_TAG = "url";
    public static final String JSON_QUANTITY_TAG = "quantity";
    public static final String JSON_ITEM_NAME_TAG = JSON_NAME_TAG;
    public static final String JSON_ITEM_SPECIAL_PRICE_TAG = "special_price";
    public static final String JSON_ITEM_SPECIAL_PRICE_CONVERTED_TAG = "special_price_converted";
    public static final String JSON_MAX_QUANTITY = "max_quantity";
    public static final String JSON_VARIATION_TAG = "variation";
    public static final String JSON_SIMPLE_SKU_TAG = "simple_sku";

    public static final String VARIATION_VALUE = "variation_value";

    // Teaser Campaign Tags
    public static final String CAMPAIGN_ENTITY = "campaign_entity";
    public static final String CMS = "cms";
    public static final String JSON_MOBILE_BANNER_TAG = "mobile_banner";
    public static final String JSON_DESKTOP_BANNER_TAG = "desktop_banner";
    public static final String JSON_SAVE_PRICE_TAG = "save_price";
    public static final String JSON_STOCK_PERCENTAGE_TAG = "stock_percentage";
    public static final String JSON_HAS_UNIQUE_SIZE_TAG = "has_unique_size";
    public static final String JSON_SIZES_TAG = "sizes";
    public static final String JSON_SIZE_TAG = "size";
    public static final String JSON_REMAINING_TIME_TAG = "remaining_time";
    public static final String JSON_UNIX_TIME_TAG = "unix_time";

    // Address Tags
    public static final String CUSTOMER_ADDRESS_ID = "customer_address_id";
    public static final String PHONE = "phone";
    public static final String ADDITIONAL_PHONE = "additional_phone";
    public static final String ADDRESS_1 = "address1";
    public static final String ADDRESS_2 = "address2";
    public static final String CITY = "city";
    public static final String POSTCODE = "postcode";
    public static final String JSON_IS_DEFAULT_BILLING_TAG = "is_default_billing";
    public static final String JSON_IS_DEFAULT_SHIPPING_TAG = "is_default_shipping";

    // Errors Tags
    public static final String VALIDATE = "validate";
    public static final String ERROR = "error";

    // ProductRatingPage Tags
    public static final String JSON_STARS_TAG = "stars";
    public static final String JSON_COMMENTS_TAG = "comments";

    // ProductReviewComment Tags
    public static final String TITLE = "title";
    public static final String JSON_SUB_TITLE_TAG = "sub_title";

    // ProductSimple Tags
    public static final String JSON_META_TAG = "meta";

    // PurchaseItem Tags
    public static final String JSON_PURCHASE_NAME_TAG = "name";
    public static final String CATEGORY = "category";
    public static final String JSON_PAID_PRICE_CONVERTED_TAG = "paidprice_converted";

    // Section Tags
    public static final String JSON_SECTION_NAME_TAG = "section_name";
    public static final String JSON_SECTION_MD5_TAG = "section_md5";
    public static final String JSON_SECTION_URL_TAG = "url";

    // Version Tags
    public static final String JSON_MIN_VERSION_TAG = "min_version";
    public static final String JSON_CUR_VERSION_TAG = "cur_version";

    /**
     * Order History TAGS
     */
    public static final String ORDERS = "orders";
    public static final String JSON_ORDER_DATE_TAG = "date";
    public static final String JSON_ORDER_TOTAL_TAG = "total";
    public static final String JSON_ORDER_CONF_SKU_TAG = "config_sku";
    public static final String JSON_ORDER_UNIT_PRICE_TAG = "unit_price";
    public static final String JSON_ORDER_TOTAL_NUM_TAG = "total_orders";

    /**
     * Order Tracking TAGS
     */
    public static final String JSON_ORDER_NR_TAG = "orderNr";
    public static final String JSON_GRAND_TOTAL_TAG = "grandTotal";
    public static final String JSON_GRAND_TOTAL_CONVERTED_TAG = "grandTotal_converted";

    public static final String JSON_ORDER_NUMBER_TAG = "order_nr";
    public static final String CREATION_DATE = "creation_date";
    public static final String PAYMENT_METHOD = "payment_method";
    public static final String PAYMENT = "payment";
    public static final String PAYMENT_METHOD_FORM = "paymentMethodForm";
    public static final String JSON_ORDER_STATUS_TAG = "status";
    public static final String SHIPPING_METHOD = "shipping_method";
    public static final String JSON_ORDER_BIL_ADDRESS_TAG = "billing_address";
    public static final String JSON_ORDER_SHIP_ADDRESS_TAG = "shipping_address";
    public static final String JSON_ORDER_COUPON_CODE_TAG = "coupon_code";


    /**
     * Address
     */
    public static final String SHIPPING = "shipping";
    public static final String BILLING = "billing";
    public static final String JSON_OTHER_TAG = "other";
    public static final String API_CALL = "api_call";
    public static final String REGION = "region";
    public static final String MULTI_STEP_ENTITY = "multistep_entity";


    public static final String JSON_NEXT_STEP_TAG = "next_step";
    public static final String JSON_SHIPPING_METHOD_TAG = "shippingMethodForm";
    public static final String TEXT = "text";
    public static final String JSON_TOOLTIP_TEXT_TAG = "tooltip_text";
    public static final String JSON_CVC_TEXT_TAG = "cvc_text";

    /**
     * Pickup Stations
     */
    public static final String JSON_PICKUP_STATION_ID = "id_pickupstation";
    public static final String JSON_PICKUP_ID = "pickup_id";
    public static final String JSON_PICKUP_ADDRESS = "address";
    public static final String JSON_PICKUP_PLACE = "place";
    public static final String JSON_PICKUP_CITY = "city";
    public static final String JSON_PICKUP_OPENING_HOURS = "opening_hours";
    public static final String JSON_PICKUP_REGIONS = "regions";

    public static final String JSON_CUSTOMER_TAG = "customer";
    public static final String JSON_ADDRESS_LIST_TAG = "address_list";

    public static final String JSON_BRANDS_TAG = "brands";
    public static final String JSON_ERROR_MESSAGE_TAG = "error_message";
    public static final String JSON_NOTICE_MESSAGE_TAG = "notice_message";
    public static final String JSON_RULES_TAG = "rules";
    public static final String JSON_SHIPPING_FEE_TAG = "shipping_fee";

    /**
     * Featured Items
     */
    public static final String JSON_FEATURED_BOX_TAG = "featured_box";
    public static final String JSON_FEATURED_BRAND_BOX_TAG = "featured_brandbox";
    public static final String JSON_FEATURED_SEARCH_TIPS_TAG = "search_tips";

    /**
     * Countries Configs Keys
     */
    public static final String JSON_FLAG_TAG = "flag";
    public static final String JSON_COUNTRY_ISO = "country_iso";
    public static final String JSON_FORCE_HTTPS = "force_https";
    public static final String JSON_IS_LIVE = "is_live";

    public static final String JSON_COUNTRY_CURRENCY_ISO = "currency_iso";
    public static final String JSON_COUNTRY_CURRENCY_SYMBOL = "currency_symbol";
    public static final String JSON_COUNTRY_CURRENCY_POSITION = "currency_position";
    public static final String JSON_COUNTRY_NO_DECIMALS = "no_decimals";
    public static final String JSON_COUNTRY_THOUSANDS_SEP = "thousands_sep";
    public static final String JSON_COUNTRY_DECIMALS_SEP = "decimals_sep";
    public static final String JSON_COUNTRY_LANGUAGES = "languages";
    public static final String JSON_COUNTRY_LANG_DEFAULT = "default";
    public static final String JSON_COUNTRY_GA_ID = "ga_android_id";
    public static final String JSON_COUNTRY_CS_EMAIL = "cs_email";
    public static final String JSON_FACEBOOK_IS_AVAILABLE = "facebook_is_available";
    public static final String JSON_COUNTRY_GTM_ID = "gtm_android";
    public static final String JSON_COUNTRY_HAS_CART_POPUP = "has_cart_popup";

    public static final String JSON_ITEM_TAG = "item";
    public static final String JSON_RELEVANCE_TAG = "relevance";


    /**
     * BUNDLES
     */
    public static final String JSON_BUNDLE_PRODUCTS = "bundle_products";
    public static final String JSON_BUNDLE_PRODUCT_LEADER_POS = "leader_simple_position";
    public static final String BUNDLE_ENTITY = "bundle_entity";

    /**
     * OFFERS
     */

    public static final String JSON_OFFERS_TAG = "offers";
    public static final String JSON_OFFERS_MIN_PRICE_TAG = "min_price";
    public static final String JSON_OFFERS_MIN_PRICE_CONVERTED_TAG = "min_price_converted";

    /**
     * VALIDATE
     */

    public static final String JSON_VALID_TAG = "valid";
    public static final String VARIATION_NAME = "variation_name";
    public static final String VARIATIONS_AVAILABLE_LIST = "variations_available_list";
    public static final String VARIATION_DEFAULT_POSITION = "variation_default_position";

    /**
     * PDV BUCKET INFO
     */

    public static final String JSON_SUMMARY_TAG = "summary";
    public static final String JSON_SPECIFICATIONS_TAG = "specifications";
    public static final String JSON_HEAD_LABEL_TAG = "head_label";
    public static final String JSON_BODY_TAG = "body";

    /**
     * BANNER
     */

    public static final String BANNER = "banner";
    public static final String PHONE_IMAGE = "phone_image";
    public static final String TABLET_IMAGE = "tablet_image";


    /**
     * SHOP IN SHOP
     */

    public static final String JSON_HTML_TAG = "html";

    /**
     * CATALOG
     */
    public static final String JSON_FILTERS_TAG = "filters";
    public static final String JSON_SEARCH_TERM_TAG = "search_term";
    public static final String JSON_MULTI = "multi";
    public static final String JSON_FILTER_SEPARATOR = "filter_separator";
    public static final String JSON_OPTION_TAG = "option";

    public static final String TOTAL_PAGES = "total_pages";
    public static final String CURRENT_PAGE = "current_page";
    public static final String PAGE = "page";
    public static final String PER_PAGE = "per_page";
    public static final String PAGINATION = "pagination";
    public static final String ADDRESSES = "addresses";
    public static final String VERTICAL = "vertical";
    public static final String FASHION = "fashion";
    public static final String WISH_LIST_PRODUCTS = "wishlist_products";

    public static final String IS_GLOBAL = "is_global";
    public static final String GLOBAL = "global";
    public static final String FULFILLMENT = "fulfillment";
    public static final String CMS_INFO = "cms_info";


    public static final String PRICE_RANGE = "price_range";
    public static final String EXIST = "exist";
    public static final String IS_DEFAULT = "is_default";
    public static final String POSITION = "position";

    public static final String TARGET = "target";

    public static final String MOBILE_ABOUT = "mobile_about_cms";
    public static final String ORDER_NUMBER = "order_number";
    public static final String ORDER_NR = "ordernr";
    public static final String UPDATE_AT = "updated_at";


    public static final String TRANSACTION_SHIPPING = "transaction_shipping";
    public static final String TRANSACTION_TAX = "transaction_tax";
    public static final String ORDER_GRAND_TOTAL = "order_grand_total";
    public static final String REASON = "reason";
    public static final String FIELD = "field";


    public static final String SLUG = "slug";

    /**
     * HOME PAGE
     */

    public static final String HAS_DATA = "has_data";
    public static final String SELLER = "seller";

    // Request parameters
    public static final String MAX_ITEMS = "maxitems";
    public static final String SORT = "sort";
    public static final String DIRECTION = "dir";
    public static final String HASH = "hash";

    public static final String RICH_RELEVANCE_ENABLED = "rich_relevance_enabled";
    public static final String REQ = "req";

    public static final String CLICK_REQUEST = "click_request";
    public static final String PARMS = "params";
    public static final String PARAM = "param";
}
