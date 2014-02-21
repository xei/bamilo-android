package pt.rocket.framework.testproject.constants;

import java.util.Random;

import android.util.Log;

public class RequestConstants {
	
	/**
	 * Customer
	 */
	public static final String CUSTOMER_EMAIL = "msilvaTester5@mailinator.com";
	static Random rand = new Random();

	static int n = rand.nextInt(10000) + 1;
	public static final String CUSTOMER_NEW_EMAIL = "msilvaTester"+n+"@mailinator.com";
	public static final String CUSTOMER_WRONG_EMAIL = "testestest@mailinator.com";
	public static final String CUSTOMER_PASSWORD = "123456";
	public static final String CUSTOMER_PASSWORD_2 = "qwerty";
	public static final String CUSTOMER_DAY = "1";
	public static final String CUSTOMER_MONTH = "1";
	public static final String CUSTOMER_YEAR = "1995";
	public static final String CUSTOMER_GENDER = "male";
	public static final String CUSTOMER_FIRST_NAME = "tester";
	public static final String CUSTOMER_LAST_NAME = "tester";
	public static final String CUSTOMER_BIRTHDAY = "01-01-1995";
	
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
	public static final String KEY_CREATE_CUSTOMER_DAY = "Alice_Module_Customer_Model_RegistrationForm[day]";
	public static final String KEY_CREATE_CUSTOMER_MONTH = "Alice_Module_Customer_Model_RegistrationForm[month]";
	public static final String KEY_CREATE_CUSTOMER_YEAR = "Alice_Module_Customer_Model_RegistrationForm[year]";
	public static final String KEY_CREATE_CUSTOMER_GENDER = "Alice_Module_Customer_Model_RegistrationForm[gender]";
	public static final String KEY_CREATE_CUSTOMER_EMAIL = "Alice_Module_Customer_Model_RegistrationForm[email]";
	public static final String KEY_CREATE_CUSTOMER_FIRST_NAME = "Alice_Module_Customer_Model_RegistrationForm[first_name]";
	public static final String KEY_CREATE_CUSTOMER_LAST_NAME = "Alice_Module_Customer_Model_RegistrationForm[last_name]";
	public static final String KEY_CREATE_CUSTOMER_PASSWORD = "Alice_Module_Customer_Model_RegistrationForm[password]";
	public static final String KEY_CREATE_CUSTOMER_PASSWORD2 = "Alice_Module_Customer_Model_RegistrationForm[password2]";
	public static final String KEY_CREATE_CUSTOMER_BIRTHDAY = "Alice_Module_Customer_Model_RegistrationForm[birthday]";
	
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
	
	public static final String PRODUCT_SKU_KE = "CO668SH84GVNNAFAMZ";
	public static final String PRODUCT_SKU_SIMPLE_KE = "CO668SH84GVNNAFAMZ-23604";
	
	public static final String PRODUCT_SKU_MA = "SA024ELAB8P5NAFAMZ";
	public static final String PRODUCT_SKU_SIMPLE_MA = "SA024ELAB8P5NAFAMZ-94391";
	
	public static final String PRODUCT_SKU_NG = "JU979MEAB6Z0NGAMZ";
	public static final String PRODUCT_SKU_SIMPLE_NG = "JU979MEAB6Z0NGAMZ-82778";
	
	public static final String PRODUCT_SKU_EG = "SA024ELAD8J3EGAMZ";
	public static final String PRODUCT_SKU_SIMPLE_EG = "SA024ELAD8J3EGAMZ-237228";
	
	public static final String PRODUCT_SKU_UG = "SA948ELAAEAUNAFAMZ";
	public static final String PRODUCT_SKU_SIMPLE_UG = "SA948ELAAEAUNAFAMZ-35467";
	
	/**
	 * About you
	 */
	
	public static final String KEY_CUSTOMER_SIGNUP_EMAIL = "Alice_Module_Customer_Model_RegistrationForm[email]";
	public static final String KEY_CUSTOMER_SIGNUP_SCENARIO = "Alice_Module_Customer_Model_RegistrationForm[scenario]";
	
	static Random rand2 = new Random();
	static int n2 = rand2.nextInt(10000) + 1;
	public static final String CUSTOMER_SIGNUP_EMAIL = "tester"+n+"@mailinator.com";
	public static final String CUSTOMER_SIGNUP_SCENARIO = "guest";
}

