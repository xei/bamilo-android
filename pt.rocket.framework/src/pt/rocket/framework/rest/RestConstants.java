/**
 * 
 */
package pt.rocket.framework.rest;

/**
 * @author nunocastro
 * 
 */
public class RestConstants {

	// Common JSON constants
	public static final String JSON_DATA_TAG = "data";
	public static final String JSON_ID_TAG = "id";
	public final static String JSON_SUCCESS_TAG = "success";
	public final static String JSON_MESSAGE_IN_MESSAGES_TAG = "message";

	// ConfigurationService Tags
	public static final String JSON_METADATA_TAG = "metadata";
	public static final String JSON_CALL_PHONE_TAG = "phone_number";

	// VersionInfo Tags
	public static final String JSON_VERSION_TAG = "version";

	// CustomerAccountService Tags
	public static final String JSON_USER_TAG = "user";

	// Customer Tags
	public static final String JSON_ID_CUSTOMER_TAG = "id_customer";
	public static final String JSON_FIRST_NAME_TAG = "first_name";
	public static final String JSON_LAST_NAME_TAG = "last_name";
	public static final String JSON_EMAIL_TAG = "email";
	public static final String JSON_BIRTHDAY_TAG = "birthday";
	public static final String JSON_GENDER_TAG = "gender";
	public static final String JSON_CREATED_AT_TAG = "created_at";
	public static final String JSON_PASSWORD_TAG = "password";

	// FormsService Tags
	public static final String JSON_NAME_TAG = "name";
	public static final String JSON_NUMBER_TAG = "number";
	public static final String JSON_VAL_TAG = "val";
	public static final String JSON_PRODUCTS_COUNT_TAG = "products_count";
	public static final String JSON_HEX_VALUE_TAG = "hex_value";
	public static final String JSON_INTERVAL_TAG = "interval";
	public static final String JSON_FILTERS_TAG = "filters";

	// FormData Tags
	public static final String JSON_ACTION_TAG = "action";
	public static final String JSON_URL_TAG = "url";

	// Form Tags
	public static final String JSON_FORM_TAG = "form";
	public static final String JSON_METHOD_TAG = "method";
	// public static final String JSON_ACTION_TAG = "action";
	public static final String JSON_SUBMIT_TAG = "submit";
	public static final String JSON_FIELDS_TAG = "fields";

	// FormField Tags
	public static final String JSON_TYPE_TAG = "type";
	public static final String JSON_KEY_TAG = "key";
	public static final String JSON_FIELD_NAME_TAG = JSON_NAME_TAG;
	public static final String JSON_LABEL_TAG = "label";
	public static final String JSON_VALIDATION_TAG = "rules";
	public static final String JSON_DATASET_TAG = "dataset";
	public static final String JSON_VALUE_TAG = "value";
	public static final String JSON_VALUES_TAG = "values";
	public static final String JSON_DATASET_SOURCE_TAG = "dataset_source";
	public static final String JSON_SCENARIO_TAG = "scenario";
	public static final String JSON_TERMS_TAG = "terms";
	public static final String JSON_LINK_TEXT_TAG = "link_text";

	// FieldValidation Tags
	public static final String JSON_REQUIRED_TAG = "required";
	public static final String JSON_REQUIREDVALUE_TAG = "requiredValue";
	public static final String JSON_MIN_TAG = "min";
	public static final String JSON_MAX_TAG = "max";
	public static final String JSON_REGEX_TAG = "regex";

	// NavigationList Tags
	public static final String JSON_ATTRIBUTES_TAG = "attributes";
	public static final String JSON_NAVLIST_NAME_TAG = JSON_NAME_TAG;
	public static final String JSON_NAVIGATION_URL_TAG = "navigation_url";
	public static final String JSON_IMAGE_TAG = "image";
	public static final String JSON_IMAGE_URL_TAG = "image_url";

	// ProductService Tags
	public static final String JSON_SUGGESTIONS_TAG = "suggestions";

	public static final String JSON_OPTIONS_TAG = "options";
	public static final String JSON_PROD_VALUE_TAG = "value";
	public static final String JSON_ID_RATING_OPTION_TAG = "id_rating_option";
	public static final String JSON_CODE_TAG = "code";

	public static final String REVIEW_KEY_FIELD = "key";
	public static final String REVIEW_OPTION_FIELD = "rating-option--";
	public static final String REVIEW_PRODUCT_SKU_FIELD = "rating-catalog-sku";
	public static final String REVIEW_RATING_FIELD = "ratings";
	public static final String REVIEW_CUSTOMER_ID = "rating-customer";

	// CompleteProduct Tags
	public static final String JSON_SKU_TAG = "sku";
	public static final String JSON_PROD_NAME_TAG = JSON_NAME_TAG;
	public static final String JSON_ID_CATALOG_CONFIG_TAG = "id_catalog_config";
	public static final String JSON_ATTRIBUTE_SET_ID_TAG = "attribute_set_id";
	public static final String JSON_ACTIVATED_AT_TAG = "activated_at";
	public static final String JSON_MAX_PRICE_TAG = "max_price";
	public static final String JSON_PRICE_TAG = "price";
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
	public static final String JSON_IS_NEW_TAG = "is_new";
	public static final String JSON_PROD_UNIQUES_TAG = "uniques";

	public static final String JSON_RATINGS_TOTAL_TAG = "ratings_total";
	public static final String JSON_RATINGS_TOTAL_SUM_TAG = "sum";
	public static final String JSON_RATINGS_TOTAL_AVG_TAG = "avr";

	public static final String JSON_VARIATIONS_TAG = "variations";

	// Variation Tags
	public static final String JSON_LINK_TAG = "link";
	public static final String JSON_VARIATION_IMAGE_TAG = JSON_IMAGE_TAG;

	// ProductsPage Tags
	public static final String JSON_RESULTS_TAG = "results";
	public static final String JSON_PRODUCT_COUNT_TAG = "product_count";
	public static final String JSON_CATALOG_NAME_TAG = "category_name";
	// public static final String JSON_PROD_CATEGORIES_TAG =
	// JSON_CATEGORIES_TAG;

	// Product Tags
	// public static final String JSON_PROD_DATA_TAG = JSON_DATA_TAG;
	// public static final String JSON_ATTRIBUTES_TWO_TAG =
	// JSON_PROD_ATTRIBUTES_TAG;
	public static final String JSON_IMAGES_TAG = "images";

	// Image Tags
	public static final String JSON_PATH_TAG = "path";
	public static final String JSON_FORMAT_TAG = "format";
	public static final String JSON_WIDTH_TAG = "width";
	public static final String JSON_HEIGHT_TAG = "height";

	// Category Tags
	public static final String JSON_CATEGORY_ID_TAG = "id_catalog_category";
	public static final String JSON_CATEGORY_NAME_TAG = JSON_NAME_TAG;
	public static final String JSON_LEFT_TAG = "lft";
	public static final String JSON_RIGHT_TAG = "rgt";
	public static final String JSON_URL_KEY_TAG = "url_key";
	public static final String JSON_SEGMENTS_TAG = "segments";
	public static final String JSON_CHILDREN_TAG = "children";
	public static final String JSON_API_URL_TAG = "api_url";
	public static final String JSON_INFO_URL_TAG = "info_url";
	public static final String JSON_CATEGORY_URL_TAG = JSON_URL_TAG;

	// ShoppingCart Tags
	public static final String JSON_CART_TAG = "cart";
	public static final String JSON_CART_VALUE_TAG = "cartValue";
	public static final String JSON_CART_COUNT_TAG = "cartCount";
	public static final String JSON_CART_ITEMS_TAG = "cartItems";
	public static final String JSON_CART_VAT_VALUE_TAG = "vat_value";
	public static final String JSON_CART_SHIPPING_VALUE_TAG = "shipping_value";
	public static final String JSON_CART_COUPON_VALUE_TAG = "couponMoneyValue";
	public final static String JSON_MESSAGES_TAG = "messages";
	public static final String JSON_CART_CLEAN_VALUE_TAG = "cartCleanValue";
	public static final String JSON_CART_SUM_COSTS_TAG = "sum_costs";
	public static final String JSON_CART_EXTRA_COSTS_TAG = "extra_costs";
	public static final String JSON_CART_SUM_COSTS_VALUE_TAG = "sum_costs_value";
	public static final String JSON_CART_PRICE_RULES_TAG = "price_rules";
	// ShoppingCartItem Tags
	public static final String JSON_ITEM_IMAGE_TAG = JSON_IMAGE_TAG;
	public static final String JSON_PRODUCT_URL_TAG = "url";
	public static final String JSON_CONFIG_SKU_TAG = "configSku";
	public static final String JSON_QUANTITY_TAG = "quantity";
	public static final String JSON_CONFIG_ID = "configId";
	public static final String JSON_ITEM_NAME_TAG = JSON_NAME_TAG;
	public static final String JSON_STOCK_TAG = "stock";
	public static final String JSON_ITEM_SPECIAL_PRICE_TAG = "specialPrice";
	public static final String JSON_ITEM_PRICE_TAG = "unit_price";
	public static final String JSON_TAX_AMOUNT_TAG = "tax_amount";
	public static final String JSON_MAX_QUANTITY = "max_quantity";
	public static final String JSON_VARIATION = "variation";
	public static final String JSON_CART_RULE_DISPLAY_NAMES = "cart_rule_display_names";
	public static final String JSON_SALES_ORDER_ITEM = "salesOrderItem";
	public static final String JSON_CART_RULE_DISCOUNT = "cart_rule_discount";

	// MinOrderAmount Tags
	public static final String JSON_PAYMENT_SETTINGS_TAG = "payment_settings";
	public static final String JSON_CART_MIN_ORDER_TAG = "cart_min_order_amount";

	// HomePage Tags
	public static final String JSON_HOMEPAGE_ID_TAG = "homepage_id";
	public static final String JSON_HOMEPAGE_TITLE_TAG = "homepage_title";
	public static final String JSON_HOMEPAGE_DEFAULT_TAG = "homepage_default";
	public static final String JSON_HOMEPAGE_LAYOUT_TAG = "homepage_layout";

	// TeaserSpecification Tags
	public static final String JSON_TEASER_ATTRIBUTES_TAG = JSON_ATTRIBUTES_TAG;
	public static final String JSON_TEASER_DESCRIPTION_TAG = JSON_DESCRIPTION_TAG;
	public static final String JSON_TEASER_IMAGE_URL_TAG = JSON_IMAGE_URL_TAG;
	public static final String JSON_TEASER_IMAGES_TAG = "image_list";
	public static final String JSON_GROUP_TYPE_TAG = "group_type";
	public static final String JSON_GROUP_TITLE_TAG = "group_title";
	public static final String JSON_TARGET_TAG = "target_type";

	// ImageTeaserGroup Tags
	public static final String JSON_TEASER_URL_TAG = "product_url";
	// private static final String JSON_WIDTH_TAG = "width";
	// private static final String JSON_HEIGHT_TAG = "height";
	// private static final String JSON_FORMAT_TAG = "format";
	
	// Teaser Campaign Tags XXX
	public static final String JSON_CAMPAIGN_TAG = "campaign";
	public static final String JSON_CAMPAIGN_NAME_TAG = "campaign_name";
	public static final String JSON_CAMPAIGN_URL_TAG = "campaign_url";
	public static final String JSON_CMS_TAG = "cms";
	public static final String JSON_MOBILE_BANNER_TAG = "mobile_banner";
	public static final String JSON_START_TIME_TAG = "start_time";
	public static final String JSON_END_TIME_TAG = "end_time";
	public static final String JSON_SAVE_PRICE_TAG = "save_price";
	public static final String JSON_STOCK_PERCENTAGE_TAG = "stock_percentage";
	public static final String JSON_HAS_UNIQUE_SIZE_TAG = "has_unique_size";
	public static final String JSON_SIZES_TAG = "sizes";
	

	// TeaserBrandElement Tags
	public static final String JSON_BRAND_DESCRIPTION_TAG = JSON_DESCRIPTION_TAG;
	// private static final String JSON_ID_TAG = "id";
	// private static final String JSON_IMAGE_LIST_TAG = "image_list";
	// private static final String JSON_IMAGE_URL_TAG = "image_url";
	public static final String JSON_BRAND_URL_TAG = "brand_url";
	// private static final String JSON_DESCRIPTION_TAG = "description";
	public static final String JSON_TARGET_TYPE_TAG = "target_type";
	public static final String JSON_IMAGE_DEVICE_TYPE_TAG = "device_type";

	// Address Tags
	public static final String JSON_ADDRESS_ID_TAG = "id_customer_address";
	public static final String JSON_ADDRESS_ID_TAG_2 = "customer_address_id";
	// public static final String JSON_FIRST_NAME_TAG = "first_name";
	public static final String JSON_MIDDLE_NAME_TAG = "middle_name";
	// public static final String JSON_LAST_NAME_TAG = "last_name";
	public static final String JSON_PHONE_TAG = "phone";
	public static final String JSON_TABLET_TAG = "tablet";
	public static final String JSON_ADDRESS1_TAG = "address1";
	public static final String JSON_ADDRESS2_TAG = "address2";
	public static final String JSON_CITY_TAG = "city";
	public static final String JSON_REGION_TAG = "region";
	public static final String JSON_POSTCODE_TAG = "postcode";
	public static final String JSON_COMPANY_TAG = "company";
	public static final String JSON_IS_DEFAULT_BILLING_TAG = "is_default_billing";
	public static final String JSON_IS_DEFAULT_SHIPPING_TAG = "is_default_shipping";

	// Brand Tags
	public static final String JSON_INNER_OBJECT_TAG = "brand";
	// private static final String JSON_URL_TAG = "url";
	// private static final String JSON_NAME_TAG = "name";
	// private static final String JSON_IMAGE_TAG = "image";

	// Errors Tags
	public static final String JSON_VALIDATE_TAG = "validate";
	public static final String JSON_ERROR_TAG = "error";

	// ProductImage Tags
	public static final String JSON_ID_CATALOG_PRODUCT_IMAGE_TAG = "id_catalog_product_image";
	public static final String JSON_FK_CATALOG_CONFIG_TAG = "fk_catalog_config";
	public static final String JSON_FK_CATALOG_SIMPLE_TAG = "fk_catalog_simple";
	// public static final String JSON_IMAGE_TAG = "image";
	public static final String JSON_MAIN_TAG = "main";
	public static final String JSON_UPDATED_AT_TAG = "updated_at";
	public static final String JSON_UPDATED_AT_TS_TAG = "updated_at_ts";
	// public static final String JSON_SKU_TAG = "sku";
	public static final String JSON_FK_CATALOG_BRAND_TAG = "fk_catalog_brand";
	public static final String JSON_SKU_SIMPLE_TAG = "sku_simple";
	public static final String JSON_ID_CATALOG_BRAND_TAG = "id_catalog_brand";
	public static final String JSON_BRAND_NAME_TAG = "brand_name";
	public static final String JSON_BRAND_URL_KEY_TAG = "brand_url_key";
	// public static final String JSON_URL_TAG = "url";
	// public static final String JSON_PATH_TAG = "path";
	public static final String JSON_SPRITE_TAG = "sprite";

	// ProductRatingPage Tags
	public static final String JSON_AGGREGATEDATA_TAG = "aggregatedData";
	public static final String JSON_STARS_TAG = "stars";
	public static final String JSON_SIZE_STARS_FORE_TAG = "size-stars-fore";
	public static final String JSON_COMMENTS_COUNT_TAG = "commentsCount";
	public static final String JSON_COMMENTS_TAG = "comments";

	// ProductReviewComment Tags
	public static final String JSON_TITLE_TAG = "title";
	public static final String JSON_DETAILS_TAG = "detail";
	public static final String JSON_NICKNAME_TAG = "nickname";
	public static final String JSON_DATE_TAG = "created_at";
	// private static final String JSON_OPTIONS_TAG = "options";

	// private static final String JSON_SIZE_STARS_FORE_TAG = "size-stars-fore";
	public static final String JSON_TYPE_TITLE_TAG = "type_title";

	// ProductSimple Tags
	public static final String JSON_META_TAG = "meta";

	// ProductSimpleUnique Tags
	public static final String JSON_VARIATION_TAG = "variation";

	// PurchaseItem Tags
	// public static final String JSON_SKU_TAG = "sku";
	public static final String JSON_PURCHASE_NAME_TAG = "name";
	public static final String JSON_CATEGORY_TAG = "category";
	public static final String JSON_PAIDPRICE_TAG = "paidprice";
	// public static final String JSON_QUANTITY_TAG = "quantity";

	// Section Tags
	public static final String JSON_SECTION_NAME_TAG = "section_name";
	public static final String JSON_SECTION_MD5_TAG = "section_md5";
	public static final String JSON_SECTION_URL_TAG = "url";
	// Version Tags
	public static final String JSON_MIN_VERSION_TAG = "min_version";
	public static final String JSON_CUR_VERSION_TAG = "cur_version";

	/**
	 * Promotion TAGS
	 */
	public static final String JSON_COUPON_CODE_TAG = "coupon_code";
	public static final String JSON_END_DATE_TAG = "end_date";
	public static final String JSON_TERMS_CONDITIONS_TAG = "terms_conditions";

	/**
	 * Order Tracking TAGS
	 */
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
	public static final String JSON_ORDER_SHIP_AMOUNT_TAG = "shipping_amount";
	public static final String JSON_ORDER_EXTRA_PAYMENTS_TAG = "extra_payment_cost";
	public static final String JSON_ORDER_BNP_DISCOUNT_TAG = "bnp_discount_amount";
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
	public static final String JSON_FASTLINE_TAG = "fastlane";
	public static final String JSON_CUSTOMER_ID_TAG = "fk_customer";
	public static final String JSON_COUNTRY_ID_TAG = "fk_country";
	public static final String JSON_REGION_ID_TAG = "fk_customer_address_region";
	public static final String JSON_CITY_ID_TAG = "fk_customer_address_city";
	public static final String JSON_HIDDEN_TAG = "hidden";
	public static final String JSON_CREATED_BY_TAG = "created_by";
	public static final String JSON_UPDATED_BY_TAG = "updated_by";
	public static final String JSON_CUSTOMER_FIRST_NAME_TAG = "customer_first_name";
	public static final String JSON_CUSTOMER_LAST_NAME_TAG = "customer_last_name";
	public static final String JSON_CUSTOMER_ADDRESS_COLLECTION_TAG = "address_collection";
	public static final String JSON_REGION_NAME_TAG = "customer_address_region";
	public static final String JSON_API_CALL_TAG = "api_call";

	/**
	 * Native Checkout
	 */
	public static final String JSON_NATIVE_CHECKOUT_TAG = "native_checkout";
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
	public static final String JSON_PICKUP_ID_PICKUPSTATION_REGION = "id_pickupstation_region";
	public static final String JSON_PICKUP_PAYMENT_METHOD = "payment_method";
	public static final String JSON_PICKUP_REGIONS = "regions";

	public static final String JSON_BILLING_FORM_TAG = "billingForm";
	public static final String JSON_CUSTOMER_TAG = "customer";
	public static final String JSON_ADDRESS_LIST_TAG = "address_list";
	
	

	

}
