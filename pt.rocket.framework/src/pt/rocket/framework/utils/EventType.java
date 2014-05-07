/**
 * EventType
 * Type of the available events in the EventManager. For each type there
 * is an associated class, example: Type:GET_CATEGORIES_EVENT Class:
 * GetCategoriesEvent
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.utils;

import pt.rocket.framework.rest.RestContract;

/**
 * 
 * @author guilherme Type of the available events in the EventManager. For each
 *         type there is an associated class, example: 
 *         Type:GET_CATEGORIES_EVENT        Class: GetCategoriesEvent
 * 		Each event specifies its action and the timeti should remain in the cache by the httpclient
 * 
 */
public enum EventType {
	INITIALIZE,
	
	INIT_SHOP,
	
	INIT_FORMS("http:/forms/index/", RestContract.MAX_CACHE_TIME),
		
	GET_CATEGORIES_EVENT("http:/catalog/categories/", RestContract.MAX_CACHE_TIME), 

	GET_PRODUCTS_EVENT("http:/search?setDevice=mobileApi", RestContract.DEFAULT_CACHE_TIME), 
	
	LOGIN_EVENT("https:/customer/login?setDevice=mobileApi", null),
	
	FACEBOOK_LOGIN_EVENT("https:/customer/facebooklogin?setDevice=mobileApi&facebook=true", null),
		
	GET_LOGIN_FORM_EVENT("login", RestContract.MAX_CACHE_TIME),
	
	GET_LOGIN_FORM_FALLBACK_EVENT("http:/forms/login/", RestContract.MAX_CACHE_TIME),

	LOGOUT_EVENT("https:/customer/logout/", null), 

	GET_WISHLIST_EVENT,

	ADD_ITEMS_TO_WISHLIST_EVENT, 

	REMOVE_ITEM_FROM_WISHLIST_EVENT, 

	GET_TEASERS_EVENT("http:/main/getteasers/", RestContract.MAX_CACHE_TIME),
	
	GET_PRODUCT_EVENT,
	
	GET_SEARCH_SUGGESTIONS_EVENT("http:/search/suggest/", RestContract.DEFAULT_CACHE_TIME),
	
	ADD_ITEM_TO_SHOPPING_CART_EVENT("https:/order/add?setDevice=mobileApi", null),
	
	REMOVE_ITEM_FROM_SHOPPING_CART_EVENT("https:/order/remove?setDevice=mobileApi", null),
	
	GET_SHOPPING_CART_ITEMS_EVENT("https:/order/cartdata?setDevice=mobileApi", RestContract.NO_CACHE),
	
	GET_REGISTRATION_FORM_EVENT("register", RestContract.MAX_CACHE_TIME),
	
	GET_REGISTRATION_FORM_FALLBACK_EVENT("http:/forms/register/", RestContract.MAX_CACHE_TIME),
	
	GET_REGISTRATION_EDIT_FORM_EVENT("edit", RestContract.MAX_CACHE_TIME),
	
    CHANGE_PASSWORD_EVENT("https:/customer/changepass?setDevice=mobileApi", null),
	
	GET_CHANGE_PASSWORD_FORM_EVENT("changepassword", RestContract.MAX_CACHE_TIME),
	
	GET_FORGET_PASSWORD_FORM_EVENT("forgotpassword", RestContract.MAX_CACHE_TIME),
	GET_FORGET_PASSWORD_FORM_FALLBACK_EVENT("http:/forms/forgotpassword/", RestContract.MAX_CACHE_TIME),

    FORGET_PASSWORD_EVENT("https:/customer/forgotpassword?setDevice=mobileApi", null),
	
    REGISTER_ACCOUNT_EVENT("https:/customer/create?setDevice=mobileApi", null), 
    
    EDIT_ACCOUNT_EVENT("https:/customer/edit/", null),
    
    GET_NAVIGATION_LIST_COMPONENTS_EVENT("http:/main/getstatic?key=mobile_navigation", RestContract.MAX_CACHE_TIME),
    
    GET_TERMS_EVENT("http:/main/getstatic?key=terms_mobile", RestContract.MAX_CACHE_TIME ),

    GET_RATING_OPTIONS_EVENT("http:/rating/options/", RestContract.MAX_CACHE_TIME),
    
    GET_PRODUCT_REVIEWS_EVENT,
    
    REVIEW_PRODUCT_EVENT("http:/rating/add/", null),
            
    CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT("https:/order/cartchange/", null),
    
    GET_FORMS_DATASET_LIST_EVENT,
    
    GET_API_INFO("http:/main/md5?setDevice=mobileApi", RestContract.MIN_CACHE_TIME),
        
	GET_CUSTOMER("https:/customer/getdetails?setDevice=mobileApi", RestContract.MIN_CACHE_TIME),
	
	STORE_LOGIN,
	
	GET_MIN_ORDER_AMOUNT("http:/main/getstatic?key=api_cartminorderamount", RestContract.MAX_CACHE_TIME),
	
	GET_RESOLUTIONS("http:/main/imageresolutions/", RestContract.MAX_CACHE_TIME),
	
	GET_CALL_TO_ORDER_PHONE("http:/main/getconfig/module/configurationml/key/phone_number/", RestContract.DEFAULT_CACHE_TIME),
	
	GET_PROMOTIONS("http:/main/getstatic?key=mobile_promotions", null),
	
	TRACK_ORDER_EVENT("http:/order/trackingorder/?setDevice=mobileApi", null),
	
	/**
	 * NATIVE CHECKOUT EVENTS
	 */
	
	GET_SIGNUP_FORM_EVENT("registersignup", RestContract.MAX_CACHE_TIME),
	
	GET_SIGNUP_FORM_FALLBACK_EVENT("http:/forms/registersignup/", RestContract.MAX_CACHE_TIME),
	
	SET_SIGNUP_EVENT("https:/customer/create/", RestContract.NO_CACHE),
	
	GET_POLL_FORM_EVENT("poll", RestContract.MAX_CACHE_TIME),
	
	GET_POLL_FORM_FALLBACK_EVENT("http:/forms/poll/", RestContract.MAX_CACHE_TIME),
	
	SET_POLL_ANSWER_EVENT("https:/multistep/checkoutpoll/", RestContract.NO_CACHE),
	
	GET_CREATE_ADDRESS_FORM_EVENT("addresscreate", RestContract.MAX_CACHE_TIME),
	
	GET_CREATE_ADDRESS_FORM_FALLBACK_EVENT("http:/forms/addresscreate/", RestContract.MAX_CACHE_TIME),
	
	GET_EDIT_ADDRESS_FORM_EVENT("addressedit", RestContract.MAX_CACHE_TIME),
	
	GET_EDIT_ADDRESS_FORM_FALLBACK_EVENT("http:/forms/addressedit/", RestContract.MAX_CACHE_TIME),
	
	GET_CUSTOMER_ADDRESSES_EVENT("https:/customer/address/list/", RestContract.MIN_CACHE_TIME),
	
	GET_BILLING_FORM_EVENT("https:/multistep/billing/", RestContract.NO_CACHE),
	
	GET_DEFAULT_BILLING_ADDRESS_EVENT("https:/customer/billingaddress/", RestContract.NO_CACHE),
	
	GET_DEFAULT_SHIPPING_ADDRESS_EVENT("https:/customer/shippingaddress/", RestContract.NO_CACHE),
	
	CREATE_ADDRESS_EVENT("https:/customer/address/create/", RestContract.NO_CACHE),
	
	EDIT_ADDRESS_EVENT("https:/customer/address/save/", RestContract.NO_CACHE),
	
	// DELETE_ADDRESS_EVENT("https:/customer/address/delete/", RestContract.NO_CACHE),
	
	SET_BILLING_ADDRESS_EVENT("https:/multistep/billing/", RestContract.NO_CACHE),
	
	SET_SHIPPING_ADDRESS_EVENT("https:/multistep/shipping/", RestContract.NO_CACHE),
	
	GET_REGIONS_EVENT("https:/customer/address/regions/", RestContract.MAX_CACHE_TIME),
	
	GET_CITIES_EVENT("https:/customer/address/cities/", RestContract.MAX_CACHE_TIME),
	
	GET_SHIPPING_METHODS_EVENT("https:/multistep/shippingmethod/", RestContract.DEFAULT_CACHE_TIME),
	
	SET_SHIPPING_METHOD_EVENT("https:/multistep/shippingmethod/", RestContract.DEFAULT_CACHE_TIME),
	
	GET_PAYMENT_METHODS_EVENT("https:/multistep/paymentmethod/", RestContract.DEFAULT_CACHE_TIME),
	
	SET_PAYMENT_METHOD_EVENT("https:/multistep/paymentmethod/", RestContract.DEFAULT_CACHE_TIME),
	
	GET_MY_ORDER_EVENT("http:/order/status/", RestContract.NO_CACHE), // TODO : Update the url
	
	CHECKOUT_FINISH_EVENT("https:/multistep/finish/", RestContract.NO_CACHE),
	
	NATIVE_CHECKOUT_AVAILABLE("http:/main/getconfig/module/configuration/key/native_checkout_mobile_api/", RestContract.NO_CACHE),
	
	ADD_VOUCHER("http:/order/addvoucher/", RestContract.NO_CACHE), 
	
	REMOVE_VOUCHER("http:/order/removevoucher/", RestContract.NO_CACHE),
	
	SEARCH_PRODUCT("http:/product.html", RestContract.NO_CACHE),
	
	GET_CAMPAIGN_EVENT("http:/campaign/get/", RestContract.MAX_CACHE_TIME),
	
	GET_NEWSLETTERS_FORM_EVENT("managenewsletters", RestContract.MAX_CACHE_TIME),
	
	GET_NEWSLETTERS_FORM_FALLBACK_EVENT("http:/forms/managenewsletters/", RestContract.MAX_CACHE_TIME),
	
	SUBSCRIBE_NEWSLETTERS_EVENT("https:/customer/managenewsletters/", RestContract.NO_CACHE),
	
	;
    
    public final String action;
    public final Integer cacheTime;

	EventType(String action, Integer cacheTime) {
		this.action = action;
		this.cacheTime = cacheTime;
	}
	
	/**
	 * 
	 */
	private EventType() {
		this(null, 0);
	}

}
