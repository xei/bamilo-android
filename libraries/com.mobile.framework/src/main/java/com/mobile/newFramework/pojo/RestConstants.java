package com.mobile.newFramework.pojo;

/**
 * @author nunocastro
 */
public class RestConstants {

    // Common JSON constants
    public static final String JSON_DATA_TAG = "data";
    public static final String JSON_ID_TAG = "id";
    public final static String JSON_SUCCESS_TAG = "success";
    public final static String JSON_MESSAGE_IN_MESSAGES_TAG = "message";
    public final static String JSON_MESSAGES_TAG = "messages";
    public final static String JSON_SESSION_TAG = "session";
    public final static String JSON_MD5_TAG = "md5";
    public final static String JSON_IS_WISH_LIST_TAG = "is_wishlist";

    // ConfigurationService Tags
    public static final String JSON_METADATA_TAG = "metadata";
    public static final String JSON_CALL_PHONE_TAG = "phone_number";

    // VersionInfo Tags
    public static final String JSON_VERSION_TAG = "version";
    public static final String JSON_CUSTOMER_ENTITY_TAG = "customer_entity";

    // Customer Tags
    public static final String JSON_FIRST_NAME_TAG = "first_name";
    public static final String JSON_LAST_NAME_TAG = "last_name";
    public static final String JSON_EMAIL_TAG = "email";
    public static final String JSON_BIRTHDAY_TAG = "birthday";
    public static final String JSON_GENDER_TAG = "gender";
    public static final String JSON_CREATED_AT_TAG = "created_at";

    // FormsService Tags
    public static final String JSON_NAME_TAG = "name";
    public static final String JSON_CODE_TAG = "code";
    public static final String JSON_NUMBER_TAG = "number";
    public static final String JSON_VAL_TAG = "val";
    public static final String JSON_PRODUCTS_COUNT_TAG = "products_count";
    public static final String JSON_HEX_VALUE_TAG = "hex_value";
    public static final String JSON_INTERVAL_TAG = "interval";
    public static final String JSON_FILTERS_TAG = "filters";

    public static final String JSON_IS_SELECTED_TAG = "is_selected";
    public static final String JSON_IS_SECTION_BRAND_TAG = "is_section_brand";


    // FormData Tags
    public static final String JSON_ACTION_TAG = "action";
    public static final String JSON_URL_TAG = "url";

    // Form Tags
    public static final String JSON_FORM_TAG = "form";
    public static final String JSON_METHOD_TAG = "method";

    public static final String JSON_SUBMIT_TAG = "submit";
    public static final String JSON_FIELDS_TAG = "fields";

    // FormField Tags
    public static final String JSON_TYPE_TAG = "type";
    public static final String JSON_KEY_TAG = "key";
    public static final String JSON_FIELD_NAME_TAG = JSON_NAME_TAG;
    public static final String JSON_LABEL_TAG = "label";
    public static final String JSON_DATA_SET_TAG = "dataset";
    public static final String JSON_VALUE_TAG = "value";
    public static final String JSON_DATA_SET_SOURCE_TAG = "dataset_source";
    public static final String JSON_SCENARIO_TAG = "scenario";
    public static final String JSON_TERMS_TAG = "terms";
    public static final String JSON_LINK_TEXT_TAG = "link_text";
    public static final String JSON_NEWSLETTER_CATEGORIES_SUBSCRIBED_TAG = "newsletter_categories_subscribed";
    public static final String JSON_DELIVERY_TIME = "delivery_time";
    public static final String JSON_RELATED_FIELD_TAG = "related_field";
    public static final String JSON_CHECKED_TAG = "checked";

    // FieldValidation Tags
    public static final String JSON_REQUIRED_TAG = "required";
    public static final String JSON_REQUIRED_VALUE_TAG = "requiredValue";
    public static final String JSON_MIN_TAG = "min";
    public static final String JSON_MAX_TAG = "max";
    public static final String JSON_REGEX_TAG = "regex";
    public static final String JSON_PATTERN_TAG = "pattern";
    public static final String JSON_MATCH_TAG = "match";

    // NavigationList Tags
    public static final String JSON_ATTRIBUTES_TAG = "attributes";
    public static final String JSON_IMAGE_TAG = "image";
    public static final String JSON_IMAGE_URL_TAG = "image_url";
    public static final String JSON_IMAGE_PORTRAIT_TAG = "image_portrait";
    public static final String JSON_IMAGE_LANDSCAPE_TAG = "image_landscape";

    // ProductService Tags
    public static final String JSON_SUGGESTIONS_TAG = "suggestions";

    public static final String JSON_OPTIONS_TAG = "options";
    public static final String REVIEW_RATING_FIELD = "ratings";

    // CompleteProduct Tags
    public static final String JSON_SKU_TAG = "sku";
    public static final String JSON_PROD_NAME_TAG = JSON_NAME_TAG;
    public static final String JSON_MAX_PRICE_TAG = "max_price";
    public static final String JSON_PRICE_TAG = "price";
    public static final String JSON_PRICE_CONVERTED_TAG = "price_converted";
    public static final String JSON_BRAND_TAG = "brand";
    public static final String JSON_CATEGORIES_TAG = "categories";
    public static final String JSON_PROD_ATTRIBUTES_TAG = JSON_ATTRIBUTES_TAG;
    public static final String JSON_SIMPLES_TAG = "simples";
    public static final String JSON_IMAGE_LIST_TAG = "image_list";
    public static final String JSON_PROD_URL_TAG = JSON_URL_TAG;
    public static final String JSON_DESCRIPTION_TAG = "description";
    public static final String JSON_SHORT_DESC_TAG = "short_description";
    public static final String JSON_MAX_SAVING_PERCENTAGE_TAG = "max_saving_percentage";
    public static final String JSON_MAX_SPECIAL_PRICE_TAG = "max_special_price";
    public static final String JSON_SPECIAL_PRICE_TAG = "special_price";
    public static final String JSON_SPECIAL_PRICE_CONVERTED_TAG = "special_price_converted";
    public static final String JSON_IS_NEW_TAG = "is_new";
    public static final String JSON_PROD_UNIQUES_TAG = "uniques";
    public static final String JSON_IS_FAVOURITE_TAG = "is_favourite";
    public static final String JSON_SIZE_GUIDE_URL_TAG = "size_guide";
    public static final String JSON_RELATED_PRODUCTS = "related_products";

    public static final String JSON_RATINGS_TOTAL_TAG = "ratings_total";
    public static final String JSON_RATINGS_TOTAL_SUM_TAG = "sum";
    public static final String JSON_RATINGS_TOTAL_AVG_TAG = "avr";

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
    public static final String JSON_RATING_TAG = "rating";
    public static final String JSON_REVIEW_TAG = "review";
    public static final String JSON_IS_ENABLE_TAG = "is_enable";
    public static final String JSON_REQUIRED_LOGIN_TAG = "required_login";

    public static final String JSON_VARIATIONS_TAG = "variations";
    public static final String JSON_HAS_BUNDLE_TAG = "bundle";

    // Seller tags
    public static final String JSON_HAS_SELLER_TAG = "has_seller";
    public static final String JSON_SELLER_TAG = "seller";
    public static final String JSON_SELLER_MIN_DELIVERY_TAG = "min_delivery_time";
    public static final String JSON_SELLER_MAX_DELIVERY_TAG = "max_delivery_time";

    // Variation Tags
    public static final String JSON_LINK_TAG = "link";
    public static final String JSON_VARIATION_IMAGE_TAG = JSON_IMAGE_TAG;

    // ProductsPage Tags
    public static final String JSON_RESULTS_TAG = "results";
    public static final String JSON_PRODUCT_COUNT_TAG = "product_count";
    public static final String JSON_CATALOG_NAME_TAG = "category_name";
    public static final String JSON_CATALOG_IDS_TAG = "category_ids";

    // Product Tags
    public static final String JSON_IMAGES_TAG = "images";

    // Image Tags
    public static final String JSON_PATH_TAG = "path";
    public static final String JSON_FORMAT_TAG = "format";
    public static final String JSON_WIDTH_TAG = "width";
    public static final String JSON_HEIGHT_TAG = "height";

    // Category Tags
    public static final String JSON_CATEGORY_ID_TAG = "id_catalog_category";
    public static final String JSON_CATEGORY_NAME_TAG = JSON_NAME_TAG;
    public static final String JSON_URL_KEY_TAG = "url_key";
    public static final String JSON_CHILDREN_TAG = "children";
    public static final String JSON_API_URL_TAG = "api_url";
    public static final String JSON_CATEGORY_URL_TAG = JSON_URL_TAG;
    public static final String JSON_HAS_CHILDREN = "hasChildren";

    // ShoppingCart Tags
    public static final String JSON_CART_TAG = "cart";
    public static final String JSON_CART_VALUE_TAG = "cartValue";
    public static final String JSON_CART_VALUE_CONVERTED_TAG = "cartValue_converted";
    public static final String JSON_CART_COUNT_TAG = "cartCount";
    public static final String JSON_CART_ITEMS_TAG = "cartItems";
    public static final String JSON_CART_VAT_VALUE_TAG = "vat_value";
    public static final String JSON_CART_SHIPPING_VALUE_TAG = "shipping_value";
    public static final String JSON_CART_COUPON_VALUE_TAG = "couponMoneyValue";
    public static final String JSON_CART_COUPON_CODE_TAG = "couponCode";
    public static final String JSON_CART_CLEAN_VALUE_TAG = "cartCleanValue";
    public static final String JSON_CART_SUM_COSTS_TAG = "sum_costs";
    public static final String JSON_CART_EXTRA_COSTS_TAG = "extra_costs";
    public static final String JSON_CART_SUM_COSTS_VALUE_TAG = "sum_costs_value";
    public static final String JSON_CART_PRICE_RULES_TAG = "price_rules";
    public static final String JSON_CART_SUB_TOTAL = "sub_total";
    public static final String JSON_CART_SUB_TOTAL_CONVERTED = "sub_total_converted";
    public static final String JSON_CART_VAT_LABEL_ENABLE = "vat_label_enable";

    // ShoppingCartItem Tags
    public static final String JSON_ITEM_IMAGE_TAG = JSON_IMAGE_TAG;
    public static final String JSON_PRODUCT_URL_TAG = "url";
    public static final String JSON_CONFIG_SKU_TAG = "configSku";
    public static final String JSON_QUANTITY_TAG = "quantity";
    public static final String JSON_REAL_QUANTITY_TAG = "real_quantity";
    public static final String JSON_CONFIG_ID = "configId";
    public static final String JSON_ITEM_NAME_TAG = JSON_NAME_TAG;
    public static final String JSON_STOCK_TAG = "stock";
    public static final String JSON_ITEM_SPECIAL_PRICE_TAG = "specialPrice";
    public static final String JSON_ITEM_SPECIAL_PRICE_CONVERTED_TAG = "specialPrice_converted";
    public static final String JSON_ITEM_PRICE_TAG = "unit_price";
    public static final String JSON_ITEM_PRICE_CONVERTED_TAG = "unit_price_converted";
    public static final String JSON_TAX_AMOUNT_TAG = "tax_amount";
    public static final String JSON_MAX_QUANTITY = "max_quantity";
    public static final String JSON_VARIATION_TAG = "variation";

    // TeaserSpecification Tags
    public static final String JSON_TEASER_IMAGE_URL_TAG = JSON_IMAGE_URL_TAG;
    public static final String JSON_TEASER_IMAGES_TAG = "image_list";

    // Teaser Campaign Tags
    public static final String JSON_CAMPAIGN_TAG = "campaign";
    public static final String JSON_CAMPAIGN_NAME_TAG = "campaign_name";
    public static final String JSON_CAMPAIGN_URL_TAG = "campaign_url";
    public static final String JSON_CMS_TAG = "cms";
    public static final String JSON_MOBILE_BANNER_TAG = "mobile_banner";
    public static final String JSON_DESKTOP_BANNER_TAG = "desktop_banner";
    public static final String JSON_SAVE_PRICE_TAG = "save_price";
    public static final String JSON_STOCK_PERCENTAGE_TAG = "stock_percentage";
    public static final String JSON_HAS_UNIQUE_SIZE_TAG = "has_unique_size";
    public static final String JSON_SIZES_TAG = "sizes";
    public static final String JSON_SIZE_TAG = "size";
    public static final String JSON_REMAINING_TIME_TAG = "remaining_time";
    public static final String JSON_UNIX_TIME_TAG = "unix_time";


    // TeaserBrandElement Tags
    public static final String JSON_TARGET_TYPE_TAG = "target_type";
    public static final String JSON_IMAGE_DEVICE_TYPE_TAG = "device_type";

    // Address Tags
    public static final String JSON_ADDRESS_ID_TAG = "id_customer_address";
    public static final String JSON_ADDRESS_ID_TAG_2 = "customer_address_id";
    public static final String JSON_PHONE_TAG = "phone";
    public static final String JSON_ADDITIONAL_PHONE_TAG = "additional_phone";
    public static final String JSON_TABLET_TAG = "tablet";
    public static final String JSON_ADDRESS1_TAG = "address1";
    public static final String JSON_ADDRESS2_TAG = "address2";
    public static final String JSON_CITY_TAG = "city";
    public static final String JSON_POSTCODE_TAG = "postcode";
    public static final String JSON_IS_DEFAULT_BILLING_TAG = "is_default_billing";
    public static final String JSON_IS_DEFAULT_SHIPPING_TAG = "is_default_shipping";

    // Errors Tags
    public static final String JSON_VALIDATE_TAG = "validate";
    public static final String JSON_ERROR_TAG = "error";

    // ProductImage Tags
    public static final String JSON_UPDATED_AT_TAG = "updated_at";

    // ProductRatingPage Tags
    public static final String JSON_STARS_TAG = "stars";
    public static final String JSON_COMMENTS_TAG = "comments";

    // ProductReviewComment Tags
    public static final String JSON_TITLE_TAG = "title";
    public static final String JSON_SUB_TITLE_TAG = "sub_title";

    // ProductSimple Tags
    public static final String JSON_META_TAG = "meta";

    // PurchaseItem Tags
    public static final String JSON_PURCHASE_NAME_TAG = "name";
    public static final String JSON_CATEGORY_TAG = "category";
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
    public static final String JSON_ORDERS_TAG = "orders";
    public static final String JSON_ORDER_DATE_TAG = "date";
    public static final String JSON_ORDER_TOTAL_TAG = "total";
    public static final String JSON_ORDER_CONF_SKU_TAG = "config_sku";
    public static final String JSON_ORDER_UNIT_PRICE_TAG = "unit_price";
    public static final String JSON_ORDER_PAYMENT_TAG = "payment";
    public static final String JSON_ORDER_TOTAL_NUM_TAG = "total_orders";
    public static final String JSON_ORDER_PAGINATION_TAG = "pagination";
    public static final String JSON_ORDER_CURRENT_PAGE_TAG = "current_page";
    public static final String JSON_ORDER_TOTAL_PAGES_TAG = "total_pages";
    /**
     * Order Tracking TAGS
     */
    public static final String JSON_ORDER_NR_TAG = "orderNr";
    public static final String JSON_GRAND_TOTAL_TAG = "grandTotal";
    public static final String JSON_GRAND_TOTAL_CONVERTED_TAG = "grandTotal_converted";

    public static final String JSON_ORDER_TAG = "order";
    public static final String JSON_ORDER_ID_TAG = "order_id";
    public static final String JSON_ORDER_NUMBER_TAG = "order_nr";
    public static final String JSON_ORDER_CREATION_DATE_TAG = "creation_date";
    public static final String JSON_ORDER_ITEM_COLLECTION_TAG = "item_collection";
    public static final String JSON_ORDER_LAST_UPDATE_TAG = "last_order_update";
    public static final String JSON_ORDER_PAYMENT_METHOD_TAG = "payment_method";
    public static final String JSON_ORDER_STATUS_TAG = "status";
    public static final String JSON_ORDER_ITEM_STATUS_TAG = "item_status";
    public static final String JSON_ORDER_ITEM_STATUS_UPDATE_TAG = "last_status_change";
    public static final String JSON_ORDER_GRAND_TOTAL_TAG = "grand_total";
    public static final String JSON_ORDER_GRAND_TOTAL_CONVERTED_TAG = "grand_total_converted";
    public static final String JSON_ORDER_SHIP_AMOUNT_TAG = "shipping_amount";
    public static final String JSON_ORDER_EXTRA_PAYMENTS_TAG = "extra_payment_cost";
    public static final String JSON_ORDER_INSTALLMENT_FEES_TAG = "installment_fees";
    public static final String JSON_ORDER_USER_DEVICE_TAG = "customer_device";
    public static final String JSON_ORDER_SHIP_MET_TAG = "shipping_method";
    public static final String JSON_ORDER_PAYMENT_PROVIDER_TAG = "provider";
    public static final String JSON_ORDER_BIL_ADDRESS_TAG = "billing_address";
    public static final String JSON_ORDER_SHIP_ADDRESS_TAG = "shipping_address";
    public static final String JSON_ORDER_COUPON_DISCOUNT_TAG = "discount";
    public static final String JSON_ORDER_COUPON_CODE_TAG = "coupon_code";

    /**
     * Address
     */
    public static final String JSON_SHIPPING_TAG = "shipping";
    public static final String JSON_BILLING_TAG = "billing";
    public static final String JSON_OTHER_TAG = "other";
    public static final String JSON_CUSTOMER_ID_TAG = "fk_customer";
    public static final String JSON_COUNTRY_ID_TAG = "fk_country";
    public static final String JSON_REGION_ID_TAG = "fk_customer_address_region";
    public static final String JSON_CITY_ID_TAG = "fk_customer_address_city";
    public static final String JSON_HIDDEN_TAG = "hidden";
    public static final String JSON_CREATED_BY_TAG = "created_by";
    public static final String JSON_UPDATED_BY_TAG = "updated_by";
    public static final String JSON_REGION_NAME_TAG = "customer_address_region";
    public static final String JSON_API_CALL_TAG = "api_call";
    public static final String JSON_ID_ADDRESS_REGION_TAG = "id_customer_address_region";


    /**
     * Native Checkout
     */
    public static final String JSON_NATIVE_CHECKOUT_TAG = "native_checkout";

    public static final String JSON_MULTI_STEP_ENTITY_TAG = "multistep_entity";


    public static final String JSON_NEXT_STEP_TAG = "next_step";
    public static final String JSON_NATIVE_CHECKOUT_AVAILABLE = "native_checkout_mobile_api";
    public static final String JSON_SHIPPING_METHOD_TAG = "shippingMethodForm";
    public static final String JSON_TEXT_TAG = "text";
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
    public static final String JSON_PICKUP_PAYMENT_METHOD = "payment_method";
    public static final String JSON_PICKUP_REGIONS = "regions";

    public static final String JSON_BILLING_FORM_TAG = "billingForm";
    public static final String JSON_CUSTOMER_TAG = "customer";
    public static final String JSON_ADDRESS_LIST_TAG = "address_list";

    public static final String JSON_PRODUCTS_TAG = "products";
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

    public static final String JSON_ITEM_TAG = "item";
    public static final String JSON_RELEVANCE_TAG = "relevance";


    /**
     * BUNDLES
     */

    public static final String JSON_BUNDLE_ID = "bundle_id";
    public static final String JSON_BUNDLE_NAME = "bundle_name";
    public static final String JSON_BUNDLE_PRICE = "bundle_price";
    public static final String JSON_BUNDLE_PRICE_CONVERTED = "bundle_price_converted";
    public static final String JSON_BUNDLE_LEADER_POS = "bundle_leader_config_position";
    public static final String JSON_BUNDLE_PRODUCTS = "bundle_products";

    public static final String JSON_MAX_PRICE_CONVERTED_TAG = "max_price_converted";
    public static final String JSON_BUNDLE_PRODUCT_LEADER_POS = "leader_simple_position";
    public static final String JSON_MAX_SPECIAL_PRICE_CONVERTED_TAG = "max_special_price_converted";

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
    public static final String JSON_VARIATION_NAME_TAG = "variation_name";
    public static final String JSON_VARIATION_VALUE_TAG = "variation_value";

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

    public static final String JSON_BANNER_TAG = "banner";
    public static final String JSON_BANNER_PHONE_IMG_TAG = "phone_image";
    public static final String JSON_BANNER_TABLET_IMG_TAG = "tablet_image";


    /**
     * SHOP IN SHOP
     */

    public static final String JSON_HTML_TAG = "html";

    /**
     * CATALOG
     */
    public static final String JSON_SEARCH_TERM_TAG = "searchterm";
}
