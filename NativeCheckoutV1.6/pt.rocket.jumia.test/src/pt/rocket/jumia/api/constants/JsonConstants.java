package pt.rocket.jumia.api.constants;

public class JsonConstants {
	
	public static String API_URL = "https://mobile.jumia.com/mobapi";
	public static String API_MD5 = "/main/md5";
	
	public static final String JSON_SUCCESS_TAG = "success";
	public static final String JSON_ERROR_TAG = "error";
	public static final String JSON_DATA_TAG = "data";
	public static final String JSON_METADATA_TAG = "metadata";
	public static final String JSON_CATEGORY_URL_KEY_TAG = "url_key";
	public static final String JSON_BRAND_URL_KEY_TAG = "url";
	public static final String JSON_BRAND_BRAND_TAG = "brand";
	public static final String JSON_RESULTS_TAG = "results";
	public static final String JSON_PRICE_TAG = "price";
	public static final String JSON_SUGGESTION_TAG = "suggestions";
	public static final String JSON_PRODUCT_COUNT_TAG ="product_count";
	public static final String JSON_MESSAGES_TAG ="messages";
	
	public static final String JSON_SESSION_TAG ="session";
	public static final String JSON_USER_TAG ="user";
	public static final String JSON_USER_ID_TAG ="id_customer";
	public static final String JSON_ID_TAG ="id";
	public static final String JSON_EMAIL_TAG ="email";
	public static final String JSON_CARTITEM_TAG ="cartItems";
	public static final String JSON_CONFIGSKU_TAG ="configSku";
	
	
	public static final String JSON_LOGIN_EMAIL_TAG ="Alice_Module_Customer_Model_LoginForm[email]";
	public static final String JSON_LOGIN_PASSWORD_TAG ="Alice_Module_Customer_Model_LoginForm[password]";
	
	
	public static final String JSON_RATING_NAME_TAG ="RatingForm[name]";
	public static final String JSON_RATING_TITLE_TAG ="RatingForm[title]";
	public static final String JSON_RATING_SKU_TAG ="rating-catalog-sku";
	public static final String JSON_RATING_COMMENT_TAG ="RatingForm[comment]";
	public static final String JSON_RATING_QUALITY_TAG ="rating-option--quality";
	public static final String JSON_RATING_APPEARANCE_TAG ="rating-option--appearance";
	public static final String JSON_RATING_PRICE_TAG ="rating-option--price";
	
	
	public static final String JSON_REG_DAY_TAG ="Alice_Module_Customer_Model_RegistrationForm[day]";
	public static final String JSON_REG_MONTH_TAG ="Alice_Module_Customer_Model_RegistrationForm[month]";
	public static final String JSON_REG_YEAR_TAG ="Alice_Module_Customer_Model_RegistrationForm[year]";
	public static final String JSON_REG_GENDER_TAG ="Alice_Module_Customer_Model_RegistrationForm[gender]";
	public static final String JSON_REG_NEWSLETTER_TAG ="Alice_Module_Customer_Model_RegistrationForm[newsletter]";
	public static final String JSON_REG_EMAIL_TAG ="Alice_Module_Customer_Model_RegistrationForm[email]";
	public static final String JSON_REG_FIRSTNAME_TAG ="Alice_Module_Customer_Model_RegistrationForm[first_name]";
	public static final String JSON_REG_LASTNAME_TAG ="Alice_Module_Customer_Model_RegistrationForm[last_name]";
	public static final String JSON_REG_PASS_TAG ="Alice_Module_Customer_Model_RegistrationForm[password]";
	public static final String JSON_REG_PASS2_TAG ="Alice_Module_Customer_Model_RegistrationForm[password2]";
	
	
	public static final String JSON_EDIT_FIRSTNAME_TAG="Alice_Module_Customer_Model_EditForm[first_name]";
	public static final String JSON_EDIT_LASTNAME_TAG  ="Alice_Module_Customer_Model_EditForm[last_name]";
	public static final String JSON_EDIT_GENDER_TAG  ="Alice_Module_Customer_Model_EditForm[gender]";
	public static final String JSON_EDIT_YEAR_TAG  ="Alice_Module_Customer_Model_EditForm[year]";
	public static final String JSON_EDIT_MONTH_TAG  ="Alice_Module_Customer_Model_EditForm[month]";
	public static final String JSON_EDIT_DAY_TAG  ="Alice_Module_Customer_Model_EditForm[day]";
	
	
	public static final String JSON_FORGOTPASS_EMAIL_TAG  ="Alice_Module_Customer_Model_ForgotPasswordForm[email]";
	
	public static final String JSON_CONFIRMREG_EMAIL_TAG  ="Alice_Module_Customer_Model_ConfirmRegistrationForm[key]";
	
	public static final String JSON_CHANGE_PASS_TAG  ="Alice_Module_Customer_Model_PasswordForm[password]";
	public static final String JSON_CHANGE_EMAIL_TAG  ="Alice_Module_Customer_Model_PasswordForm[email]";
	public static final String JSON_CHANGE_PASS2_TAG  ="Alice_Module_Customer_Model_PasswordForm[password2]";
	
	public static final String JSON_ADDRESS_FIRSTNAME_TAG  ="Alice_Module_Customer_Model_AddressForm[first_name]";
	public static final String JSON_ADDRESS_MIDDLENAME_TAG  ="Alice_Module_Customer_Model_AddressForm[middle_name]";
	public static final String JSON_ADDRESS_LASTNAME_TAG  ="Alice_Module_Customer_Model_AddressForm[last_name]";
	public static final String JSON_ADDRESS_1_TAG  ="Alice_Module_Customer_Model_AddressForm[address1]";
	public static final String JSON_ADDRESS_2_TAG  ="Alice_Module_Customer_Model_AddressForm[address2]";
	public static final String JSON_ADDRESS_COMPANY_TAG  ="Alice_Module_Customer_Model_AddressForm[company]";
	public static final String JSON_ADDRESS_CITY_TAG  ="Alice_Module_Customer_Model_AddressForm[city]";
	public static final String JSON_ADDRESS_POSTCODE_TAG  ="Alice_Module_Customer_Model_AddressForm[postcode]";
	public static final String JSON_ADDRESS_PHONE_TAG  ="Alice_Module_Customer_Model_AddressForm[phone]";
	public static final String JSON_ADDRESS_REGION_TAG  ="Alice_Module_Customer_Model_AddressForm[region]";
	public static final String JSON_ADDRESS_FK_REGION_TAG  ="Alice_Module_Customer_Model_AddressForm[fk_customer_address_region]";
	public static final String JSON_ADDRESS_FK_CITY_TAG  ="Alice_Module_Customer_Model_AddressForm[fk_customer_address_city]";
	public static final String JSON_ADDRESS_COUNTRY_TAG  ="Alice_Module_Customer_Model_AddressForm[country]";
	public static final String JSON_ADDRESS_CITY_CODE_TAG  ="Alice_Module_Customer_Model_AddressForm[city_code]";
	public static final String JSON_ADDRESS_ID_USER_ADDRESS_TAG  ="Alice_Module_Customer_Model_AddressForm[id_customer_address]";
	public static final String JSON_ADDRESS_DEFAULTSHIPPING_TAG  ="Alice_Module_Customer_Model_AddressForm[is_default_shipping]";
	public static final String JSON_ADDRESS_DEFAULTBILLING_TAG  ="Alice_Module_Customer_Model_AddressForm[is_default_billing]";
	
	//Alice_Module_Checkout_Model_TestPaymentForm
	public static final String JSON_PAYMENT_TEST_TAG  ="PaymentMethodForm[parameter][payment_result]";	
	//Alice_Module_Checkout_Model_RecurringForm
	public static final String JSON_PAYMENT_RECURRING_RESULT_TAG  ="PaymentMethodForm[parameter][payment_result]";
	public static final String JSON_PAYMENT_RECURRING_CARD_TAG  ="PaymentMethodForm[parameter][card_id]";
	//Alice_Module_Checkout_Model_CreditcardForm
	public static final String JSON_PAYMENT_CC_RESULT_TAG  ="PaymentMethodForm[parameter][payment_result]";
	public static final String JSON_PAYMENT_CC_ID_TAG  ="PaymentMethodForm[parameter][card_id]";
	public static final String JSON_PAYMENT_CC_TYPE_TAG  ="PaymentMethodForm[parameter][cc_type]";
	public static final String JSON_PAYMENT_CC_NUMBER_TAG  ="PaymentMethodForm[parameter][cc_number]";
	public static final String JSON_PAYMENT_CC_HOLDER_TAG  ="PaymentMethodForm[parameter][cc_holder]";
	public static final String JSON_PAYMENT_CC_SAVE_TAG  ="PaymentMethodForm[parameter][cc_save]";
															
	public static final String JSON_ORDER_SKU_TAG  ="sku";
	public static final String JSON_ORDER_QUANTITY_TAG  ="quantity";
	public static final String JSON_ORDER_SKU_PRODUCT_TAG  ="p";
	
	public static final String JSON_HOMEPAGE_TAG  ="homepage_layout";
	
	
	/**
	 * https set type
	 */
	
	public static final String JSON_DEVICE_TAG ="?setDevice=mobileApi";
	

}
