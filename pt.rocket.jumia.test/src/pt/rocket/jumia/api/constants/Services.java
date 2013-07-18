package pt.rocket.jumia.api.constants;

public class Services {

	
	/**
	 * Services
	 */
  
	public final static String BRAND_URL = "/catalog/brands/";
  
	public final static String CATEGORY_URL = "/catalog/categories/";
	public final static String CATEGORY_FULL_URL = "/catalog/categories/?full=1";
  
	public final static String STATIC_BLOCK_URL = "/main/getstaticblocks";
	public final static String STATIC_NAV_URL = "/main/getstatic?key=mobile_navigation";
  	public final static String STATIC_CVV_URL = "/main/getstatic?key=checkout_mobile_cvvinfo";
 	public final static String STATIC_ABOUT_URL = "/main/getstatic?key=footer_aboutlist";
 	public final static String STATIC_ABOUTV2_URL = "/main/getstatic?key=footer_aboutlist_furniture_v2";
 	public final static String STATIC_TERMS_URL = "/main/getstatic?key=terms_mobile";
  
 	public final static String FORM_INDEX_URL = "/forms/index";
 	public final static String FORM_RATING_URL = "/forms/rating";
 	public final static String FORM_REGISTER_URL = "/forms/register";
 	public final static String FORM_LOGIN_URL = "/forms/login";
 	public final static String FORM_EDIT_URL = "/forms/edit";
 	public final static String FORM_FORGOTPASS_URL = "/forms/forgotpassword";
 	public final static String FORM_CONFIRMREG_URL = "/forms/confirmregister";
 	public final static String FORM_CHANGEPASS_URL = "/forms/changepassword";
 	public final static String FORM_ADDRESSCREATE_URL = "/forms/addresscreate";
 	public final static String FORM_ADDRESSEDIT_URL = "/forms/addressedit";
 	public final static String FORM_PAYMENTMETHODS_URL = "/forms/paymentmethods";
 	
 	public final static String SEARCH_SEARCH_URL = "/search/find/";
 	public final static String SEARCH_SUGESTION_URL = "/search/suggest/";
 	public final static String SEARCH_ALL_PRODUCTS_URL = "/search/find/all-products/";
 	public final static String SEARCH_SPECIAL_PRODUCTS_URL = "/search/find/special-price/";
 	public final static String SEARCH_COUNT_URL = "/count/";
	
 	public final static String SEARCH_CATEGORIES_URL ="/categories";
 	
 	public final static String SEARCH_BRAND_URL = "/brand";
 	
 	
 	public final static String FETCH_CATALOG_URL = "/main/fetchdata/?key=catalog";
 	public final static String FETCH_SEARCH_URL = "/main/fetchdata/?key=search";
 	public final static String FETCH_MAIN_URL = "/main/fetchdata/?key=main";
 	public final static String FETCH_CUSTOMER_URL = "/main/fetchdata/?key=customer";
 	public final static String FETCH_NEWSLETTER_URL = "/main/fetchdata/?key=newsletter";
 	public final static String FETCH_RATING_URL = "/main/fetchdata/?key=rating";
 	public final static String FETCH_ORDER_URL = "/main/fetchdata/?key=order";
 	
 	
 	public final static String NEWSLETTER_VALIDATE_URL = "/newsletter/validate/?email=";
 	public final static String NEWSLETTER_UNSIGN_URL = "/newsletter/unsign/?email=";
 	
 	public final static String USER_LOGOUT_URL = "/customer/logout";
 	public final static String USER_LOGIN_URL = "/customer/login";
	public final static String USER_REGISTER_URL = "/customer/create";
	public final static String USER_DETAILS_URL = "/customer/getdetails";
	public final static String USER_RATING_URL = "/customer/rating";
 	public final static String USER_EDIT_URL = "/customer/edit";
 	public final static String USER_CONFIRMREG_URL = "/customer/confirmregister";
	public final static String USER_FORGOTPASS_URL = "/customer/forgotpassword";
 	public final static String USER_CHANGEPASS_URL = "/customer/changepassword";
 	public final static String USER_ADDRESSCREATE_URL = "/customer/addresscreate";
 	public final static String USER_ADDRESSEDIT_URL = "/customer/addressedit";
 	public final static String USER_PAYMENT_URL = "/customer/paymentmethods";
 	
 	
 	public final static String ORDER_ADD_URL = "/order/add";
 	public final static String ORDER_REMOVE_URL = "/order/remove";
 	public final static String ORDER_INFO_URL = "/order/cartdata";
 	
 	public final static String RATING_URL = "/rating/add";
 	
 	public final static String SESSION_URL = "/main/session?id=";

 	public final static String GETTEASERS_URL = "/main/getteasers/"; 
}
