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
package pt.rocket.pojo;

/**
 * 
 * @author guilherme Type of the available events in the EventManager. For each
 *         type there is an associated class, example: 
 *         Type:GET_CATEGORIES_EVENT        Class: GetCategoriesEvent
 * 
 * 
 */
public enum EventType {

	INITIALIZE,
	
	INIT_SHOP,
	//FIXME 
//	INIT_FORMS("http:/forms/index/"),
	INIT_FORMS("http:/forms/"),	
	
	GET_CATEGORIES_EVENT("http:/catalog/categories/"), 

	GET_PRODUCTS_EVENT("http:/search/"), 
	//FIXME Change security
//	LOGIN_EVENT("https:/customer/login/"),
	LOGIN_EVENT("http:/customer/login/"),
		
	GET_LOGIN_FORM_EVENT("login"),

	//FIXME Change security
//	LOGOUT_EVENT("https:/customer/logout/"), 
	LOGOUT_EVENT("http:/customer/logout/"), 
	
	GET_WISHLIST_EVENT,

	ADD_ITEMS_TO_WISHLIST_EVENT, 

	REMOVE_ITEM_FROM_WISHLIST_EVENT, 

	GET_TEASERS_EVENT("http:/main/getteasers/"),
	
	GET_PRODUCT_EVENT,
	
	GET_SEARCH_SUGGESTIONS_EVENT("http:/search/suggest/"),
	//FIXME Change security
//	ADD_ITEM_TO_SHOPPING_CART_EVENT("https:/order/add/"),
	ADD_ITEM_TO_SHOPPING_CART_EVENT("http:/order/add/"),
	//FIXME Change security
//	REMOVE_ITEM_FROM_SHOPPING_CART_EVENT("https:/order/remove/"),
	REMOVE_ITEM_FROM_SHOPPING_CART_EVENT("http:/order/remove/"),
	//FIXME Change security
//	GET_SHOPPING_CART_ITEMS_EVENT("https:/order/cartdata/"),
	GET_SHOPPING_CART_ITEMS_EVENT("http:/order/cartdata/"),
	
	GET_REGISTRATION_FORM_EVENT("register"),
	
	GET_REGISTRATION_EDIT_FORM_EVENT("edit"),
	//FIXME Change security
//   CHANGE_PASSWORD_EVENT("https:/customer/changepass/"),
     CHANGE_PASSWORD_EVENT("http:/customer/changepass/"),
	
	GET_CHANGE_PASSWORD_FORM_EVENT("changepassword"),
	
	GET_FORGET_PASSWORD_FORM_EVENT("forgotpassword"),
	//FIXME Change security
//   FORGET_PASSWORD_EVENT("https:/customer/forgotpassword/"),
     FORGET_PASSWORD_EVENT("http:/customer/forgotpassword/"),
    //FIXME Change security
//    REGISTER_ACCOUNT_EVENT("https:/customer/create/"), 
    REGISTER_ACCOUNT_EVENT("http:/customer/create/"), 
 	//FIXME Change security
//     EDIT_ACCOUNT_EVENT("https:/customer/edit/"),
   EDIT_ACCOUNT_EVENT("http:/customer/edit/"),
    
    GET_NAVIGATION_LIST_COMPONENTS_EVENT("http:/main/getstatic?key=mobile_navigation"),
    
    GET_TERMS_EVENT("http:/main/getstatic?key=terms_mobile"),

    GET_PRIVACY_EVENT("http:/main/getstatic?key=privacy_mobile"),

    GET_RATING_OPTIONS_EVENT("http:/rating/options/"),
    
    GET_PRODUCT_REVIEWS_EVENT,
    
    REVIEW_PRODUCT_EVENT("http:/rating/add/"),
    //FIXME Change security        
//    CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT("https:/order/cartchange/"),
    CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT("http:/order/cartchange/"),
    
    GET_FORMS_DATASET_LIST_EVENT,
    
    GET_API_INFO("http:/main/md5/"),
    //FIXME Change security    
//	GET_CUSTOMER("https:/customer/getdetails/"),
	GET_CUSTOMER("http:/customer/getdetails/"),
	
	STORE_LOGIN,
	
	GET_MIN_ORDER_AMOUNT("http:/main/getstatic?key=api_cartminorderamount");
	
//	GET_SESSION_STATE("http:/main/session/");
    
    public final String action;

	EventType(String action) {
		this.action = action;
	}
	
	/**
	 * 
	 */
	private EventType() {
		this(null);
	}
}
