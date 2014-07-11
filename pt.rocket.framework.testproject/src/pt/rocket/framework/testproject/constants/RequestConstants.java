package pt.rocket.framework.testproject.constants;

import java.util.Random;

import android.util.Log;

public class RequestConstants {
	
	/**
	 * Customer
	 */
	public static final String CUSTOMER_EMAIL = "msilvaTester5@mailinator.com";
	public static final String CUSTOMER_EMAIL2 = "testcalabash@mailinator.com";
	static Random rand = new Random();

	static int n = rand.nextInt(100000) + 1;
	public static final String CUSTOMER_NEW_EMAIL = "msilvaTester"+n+"@mailinator.com";
	public static final String CUSTOMER_WRONG_EMAIL = "testestest@mailinator.com";
	public static final String CUSTOMER_PASSWORD = "123456";
	public static final String CUSTOMER_PASSWORD2 = "password1";
	public static final String CUSTOMER_PASSWORD_2 = "qwerty";
	public static final String CUSTOMER_DAY = "1";
	public static final String CUSTOMER_MONTH = "1";
	public static final String CUSTOMER_YEAR = "1995";
	public static final String CUSTOMER_GENDER = "male";
	public static final String CUSTOMER_FIRST_NAME = "tester";
	public static final String CUSTOMER_LAST_NAME = "tester";
	public static final String CUSTOMER_BIRTHDAY = "01-01-1995";
	public static final String CUSTOMER_NEWSLETTER = "5";
	
	/**
	 * Facebook Login
	 */
	public static final String FACEBOOK_CUSTOMER_EMAIL = "fbjumiatester@mailinator.com";
	public static final String FACEBOOK_CUSTOMER_GENDER = "female";
	public static final String FACEBOOK_CUSTOMER_FIRST_NAME = "fb_tester";
	public static final String FACEBOOK_CUSTOMER_LAST_NAME = "fb_tester";
	public static final String FACEBOOK_CUSTOMER_BIRTHDAY = "01-01-1995";
	/**
	 * Login
	 */
	public final static String KEY_LOGIN_EMAIL = "Alice_Module_Customer_Model_LoginForm[email]";
	public final static String KEY_LOGIN_PASSWORD = "Alice_Module_Customer_Model_LoginForm[password]";
	
	/**
	 * Forgot Password
	 */
	public final static String KEY_FORGOT_PASSWORD_EMAIL = "Alice_Module_Customer_Model_ForgotPasswordForm[email]";
	
	/**
	 * Facebook Login
	 */
	public final static String KEY_FACEBOOK_LOGIN_EMAIL = "email";
	public final static String KEY_FACEBOOK_LOGIN_FIRST_NAME = "first_name";
	public final static String KEY_FACEBOOK_LOGIN_LAST_NAME = "last_name";
	public final static String KEY_FACEBOOK_LOGIN_BIRTHDAY = "birthday";
	public final static String KEY_FACEBOOK_LOGIN_GENDER = "gender";
	
	/**
	 * Change Password
	 */
	public static final String KEY_CHANGE_PASSWORD_PASSWORD = "Alice_Module_Customer_Model_PasswordForm[password]";
	public static final String KEY_CHANGE_PASSWORD_PASSWORD2 = "Alice_Module_Customer_Model_PasswordForm[password2]";
	public static final String KEY_CHANGE_PASSWORD_EMAIL = "Alice_Module_Customer_Model_PasswordForm[email]";
	
	/**
	 * Register
	 */
	public static final String KEY_CREATE_CUSTOMER_DAY = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[day]";
	public static final String KEY_CREATE_CUSTOMER_MONTH = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[month]";
	public static final String KEY_CREATE_CUSTOMER_YEAR = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[year]";
	public static final String KEY_CREATE_CUSTOMER_GENDER = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[gender]";
	public static final String KEY_CREATE_CUSTOMER_EMAIL = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[email]";
	public static final String KEY_CREATE_CUSTOMER_FIRST_NAME = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[first_name]";
	public static final String KEY_CREATE_CUSTOMER_LAST_NAME = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[last_name]";
	public static final String KEY_CREATE_CUSTOMER_PASSWORD = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[password]";
	public static final String KEY_CREATE_CUSTOMER_PASSWORD2 = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[password2]";
	public static final String KEY_CREATE_CUSTOMER_BIRTHDAY = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[birthday]";
	public static final String KEY_CREATE_CUSTOMER_NEWSLETTER = "Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[newsletter_categories_subscribed]";
	
	/**
	 * Add To Cart
	 */
	public static final String KEY_ADD_CART_SKU = "p";
	public static final String KEY_ADD_CART_SKU_SIMPLE = "sku";
	public static final String KEY_ADD_CART_QUANTITY = "quantity";
	
	
	/**
	 * Product
	 */
	public static final String PRODUCT_SKU_CI = "RO828BOAB8MOAFRAMZ";
	public static final String PRODUCT_SKU_SIMPLE_CI = "RO828BOAB8MOAFRAMZ-99274";
	
	public static final String PRODUCT_SKU_KE = "SA949ELAA79UNAFAMZ";
	public static final String PRODUCT_SKU_SIMPLE_KE = "SA949ELAA79UNAFAMZ-13791";
	
	public static final String PRODUCT_SKU_MA = "SA024ELADVY4MAAMZ";
	public static final String PRODUCT_SKU_SIMPLE_MA = "SA024ELADVY4MAAMZ-267947";
	
	public static final String PRODUCT_SKU_NG = "SA948ELAC2WFNGAMZ";
	public static final String PRODUCT_SKU_SIMPLE_NG = "SA948ELAC2WFNGAMZ-181086";
	
	public static final String PRODUCT_SKU_EG = "AC452FAADFISEGAMZ";
	public static final String PRODUCT_SKU_SIMPLE_EG = "AC452FAADFISEGAMZ-254608";
	
	public static final String PRODUCT_SKU_UG = "SA948ELAA9T5NAFAMZ";
	public static final String PRODUCT_SKU_SIMPLE_UG = "SA948ELAA9T5NAFAMZ-21695";
	
	/**
	 * About you
	 */
	
	public static final String KEY_CUSTOMER_SIGNUP_EMAIL = "Alice_Module_Mobapi_Form_Ext1m1_Customer_RegisterSignupForm[email]";
	public static final String KEY_CUSTOMER_SIGNUP_SCENARIO = "Alice_Module_Mobapi_Form_Ext1m1_Customer_RegisterSignupForm[scenario]";
	
	static Random rand2 = new Random();
	static int n2 = rand2.nextInt(10000) + 1;
	public static final String CUSTOMER_SIGNUP_EMAIL = "tester"+n+"123123123@mailinator.com";
	public static final String CUSTOMER_SIGNUP_SCENARIO = "guest";
	
	/**
	 * Poll
	 */
	public static final String KEY_POLL_ANSWER= "Alice_Module_Checkout_Model_PollingForm[pollQuestion]";
	public static final String POLL_ANSWER = "Facebook";
	
	/**
	 * Create Address
	 */
	public static final String KEY_CREATE_ADDRESS_FIRST_NAME= "Alice_Module_Customer_Model_AddressForm[first_name]";
	public static final String CREATE_ADDRESS_FIRST_NAME = "msilva";

	public static final String KEY_CREATE_ADDRESS_LAST_NAME= "Alice_Module_Customer_Model_AddressForm[last_name]";
	public static final String CREATE_ADDRESS_LAST_NAME = "last name";

	public static final String KEY_CREATE_ADDRESS_ADDRESS1= "Alice_Module_Customer_Model_AddressForm[address1]";
	public static final String CREATE_ADDRESS_ADDRESS1 = "address 1";
	
	public static final String KEY_CREATE_ADDRESS_ADDRESS2= "Alice_Module_Customer_Model_AddressForm[address2]";
	public static final String CREATE_ADDRESS_ADDRESS2 = "address 1";
	
	public static final String KEY_CREATE_ADDRESS_CITY= "Alice_Module_Customer_Model_AddressForm[city]";
	public static final String CREATE_ADDRESS_CITY = "Porto";
	
	public static final String KEY_CREATE_ADDRESS_PHONE= "Alice_Module_Customer_Model_AddressForm[phone]";
	public static final String CREATE_ADDRESS_PHONE = "123456789";
	
	public static final String KEY_CREATE_ADDRESS_FK_REGION= "Alice_Module_Customer_Model_AddressForm[fk_customer_address_region]";
	public static final String CREATE_ADDRESS_FK_REGION = "233";
	public static final String CREATE_ADDRESS_FK_REGION_IC = "319";
	
	public static final String KEY_CREATE_ADDRESS_FK_CITY= "Alice_Module_Customer_Model_AddressForm[fk_customer_address_city]";
	public static final String CREATE_ADDRESS_FK_CITY = "373";
	public static final String CREATE_ADDRESS_FK_CITY_IC = "33264";
	
	public static final String KEY_CREATE_ADDRESS_COUNTRY= "Alice_Module_Customer_Model_AddressForm[country]";
	public static final String CREATE_ADDRESS_COUNTRY = "Germany";
	
	/**
	 * Set Shipping Method
	 */
	
	public static final String KEY_SET_SHIPPING_METHOD = "shippingMethodForm[shipping_method]";
	public static final String KEY_SET_SHIPPING_STATION = "shippingMethodForm[pickup_station]";
	public static final String KEY_SET_SHIPPING_REGION = "shippingMethodForm[pickup_station_customer_address_region]";
	
	public static final String SET_SHIPPING_METHOD = "UniversalShippingMatrix";
	
	public static final String SET_SHIPPING_METHOD2 = "FlatRate";
	public static final String SET_SHIPPING_STATION = "15";
	public static final String SET_SHIPPING_REGION = "105";
	
	/**
	 * Set Payment Method
	 */
	
	public static final String KEY_SET_PAYMENT_METHOD = "paymentMethodForm[payment_method]";
	public static final String SET_PAYMENT_METHOD_EG = "5";
	public static final String SET_PAYMENT_METHOD_IC = "1";
	public static final String SET_PAYMENT_METHOD_MA = "2";
	public static final String SET_PAYMENT_METHOD_NG = "1";
	public static final String SET_PAYMENT_METHOD_KE = "3";
	public static final String SET_PAYMENT_METHOD_UG = "7";
	
	
	/**
	 * Set Billing Method
	 */
	
	public static final String KEY_BILLING_METHOD_BILLING_ADDRESS_ID = "billingForm[billingAddressId]";
	//public static final String BILLING_METHOD_BILLING_ADDRESS_ID = "116953";
	
	public static final String KEY_BILLING_METHOD_DIFFERENT = "billingForm[shippingAddressDifferent]";
	public static final String BILLING_METHOD_DIFFERENT = "1";
	
	public static final String KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID = "billingForm[shippingAddressId]";
	
	public static final String BILLING_METHOD_SHIPPING_ADDRESS_ID_EG = "116953";
	public static final String BILLING_METHOD_SHIPPING_ADDRESS_ID_IC = "11977";
	public static final String BILLING_METHOD_SHIPPING_ADDRESS_ID_MA = "58028";
	public static final String BILLING_METHOD_SHIPPING_ADDRESS_ID_NG = "311198";
	public static final String BILLING_METHOD_SHIPPING_ADDRESS_ID_KE = "15253";
	public static final String BILLING_METHOD_SHIPPING_ADDRESS_ID_UG = "17404";
	
	public static final String SHIPPING_METHOD_EG = "UniversalShippingMatrix";
	public static final String SHIPPING_METHOD_IC = "UniversalShippingMatrix";
	public static final String SHIPPING_METHOD_MA = "FlatRate";
	public static final String SHIPPING_METHOD_NG = "UniversalShippingMatrix";
	public static final String SHIPPING_METHOD_KE = "UniversalShippingMatrix";
	public static final String SHIPPING_METHOD_UG = "UniversalShippingMatrix";
	
	/**
	 * Newsletter
	 */
	
	public static final String SIGNUP_NEWSLETTER_CATEGORIE = "Alice_Module_Mobapi_Form_Ext1m3_Customer_NewsletterHomepageForm[newsletter_categories_subscribed]";
	public static final String SIGNUP_NEWSLETTER_CATEGORIE_VALUE = "6";
	public static final String SIGNUP_NEWSLETTER_CATEGORIE_VALUE_UG = "7";
	public static final String SIGNUP_NEWSLETTER_CATEGORIE_VALUE_EG = "5";
	public static final String SIGNUP_NEWSLETTER_EMAIL = "Alice_Module_Mobapi_Form_Ext1m3_Customer_NewsletterHomepageForm[email]";
}
