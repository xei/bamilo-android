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

	LOGOUT_EVENT("https:/customer/logout/", null), 

	GET_WISHLIST_EVENT,

	ADD_ITEMS_TO_WISHLIST_EVENT, 

	REMOVE_ITEM_FROM_WISHLIST_EVENT, 

	GET_TEASERS_EVENT("http:/main/getteasers/", RestContract.MAX_CACHE_TIME),
	
	GET_PRODUCT_EVENT,
	
	GET_SEARCH_SUGGESTIONS_EVENT("http:/search/suggest/", RestContract.DEFAULT_CACHE_TIME),
	
	ADD_ITEM_TO_SHOPPING_CART_EVENT("https:/order/add?setDevice=mobileApi", null),
	
	REMOVE_ITEM_FROM_SHOPPING_CART_EVENT("https:/order/remove?setDevice=mobileApi", null),
	
	GET_SHOPPING_CART_ITEMS_EVENT("https:/order/cartdata?setDevice=mobileApi", RestContract.MIN_CACHE_TIME),
	
	GET_REGISTRATION_FORM_EVENT("register", RestContract.MAX_CACHE_TIME),
	
	GET_REGISTRATION_EDIT_FORM_EVENT("edit", RestContract.MAX_CACHE_TIME),
	
    CHANGE_PASSWORD_EVENT("https:/customer/changepass?setDevice=mobileApi", null),
	
	GET_CHANGE_PASSWORD_FORM_EVENT("changepassword", RestContract.MAX_CACHE_TIME),
	
	GET_FORGET_PASSWORD_FORM_EVENT("forgotpassword", RestContract.MAX_CACHE_TIME),

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
	
	TRACK_ORDER_EVENT("http:/order/trackingorder/?setDevice=mobileApi", null)
	
//	GET_SESSION_STATE("http:/main/session/")
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
