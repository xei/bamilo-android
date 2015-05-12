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
package com.mobile.framework.utils;

import com.mobile.framework.rest.RestContract;

/**
 * @author guilherme Type of the available events in the EventManager. For each type there is an associated class, example: Type:GET_CATEGORIES_EVENT
 *         Class: GetCategoriesEvent Each event specifies its action and the timeti should remain in the cache by the httpclient
 */
public enum EventType {
    INITIALIZE,

    INIT_FORMS("http:/forms/index/", RestContract.MAX_CACHE_TIME),

    GET_CATEGORIES_EVENT("http:/catalog/categories/", RestContract.MAX_CACHE_TIME),

    GET_PRODUCTS_EVENT("http:/search/", RestContract.NO_CACHE),

    LOGIN_EVENT("https:/customer/login/", RestContract.NO_CACHE),

    FACEBOOK_LOGIN_EVENT("https:/customer/facebooklogin?facebook=true", RestContract.NO_CACHE),

    GET_LOGIN_FORM_EVENT("login", RestContract.MAX_CACHE_TIME),

    GET_LOGIN_FORM_FALLBACK_EVENT("http:/forms/login/", RestContract.MAX_CACHE_TIME),

    LOGOUT_EVENT("https:/customer/logout/", RestContract.NO_CACHE),

    GET_TEASERS_EVENT("http:/main/getteasers/", RestContract.MAX_CACHE_TIME),

    GET_HOME_EVENT("http:/main/home/", RestContract.MAX_CACHE_TIME),

    GET_PRODUCT_EVENT,

    GET_SEARCH_SUGGESTIONS_EVENT("http:/search/suggest/", RestContract.DEFAULT_CACHE_TIME),

    ADD_ITEM_TO_SHOPPING_CART_EVENT("https:/order/add/", RestContract.NO_CACHE),

    ADD_ITEMS_TO_SHOPPING_CART_EVENT("https:/order/addmultiple/", RestContract.NO_CACHE),

    REMOVE_ITEM_FROM_SHOPPING_CART_EVENT("https:/order/remove/", RestContract.NO_CACHE),

    GET_SHOPPING_CART_ITEMS_EVENT("https:/order/cartdata/", RestContract.NO_CACHE),

    GET_REGISTRATION_FORM_EVENT("register", RestContract.MAX_CACHE_TIME),

    GET_REGISTRATION_FORM_FALLBACK_EVENT("http:/forms/register/", RestContract.MAX_CACHE_TIME),

    CHANGE_PASSWORD_EVENT("https:/customer/changepass/", RestContract.NO_CACHE),

    //GET_CHANGE_PASSWORD_FORM_EVENT("changepassword", RestContract.MAX_CACHE_TIME), // TODO - implement

    GET_FORGET_PASSWORD_FORM_EVENT("forgotpassword", RestContract.MAX_CACHE_TIME),

    GET_FORGET_PASSWORD_FORM_FALLBACK_EVENT("http:/forms/forgotpassword/", RestContract.MAX_CACHE_TIME),

    FORGET_PASSWORD_EVENT("https:/customer/forgotpassword/", RestContract.NO_CACHE),

    REGISTER_ACCOUNT_EVENT("https:/customer/create/", RestContract.NO_CACHE),

    GET_NAVIGATION_LIST_COMPONENTS_EVENT("http:/main/getstatic?key=mobile_navigation", RestContract.MAX_CACHE_TIME),

    GET_TERMS_EVENT("http:/main/getstatic?key=terms_mobile", RestContract.MAX_CACHE_TIME),

    GET_FORM_REVIEW_EVENT("http:/forms/review/", RestContract.MAX_CACHE_TIME),

    GET_FORM_RATING_EVENT("http:/forms/rating/", RestContract.MAX_CACHE_TIME),

    GET_FORM_SELLER_REVIEW_EVENT("http:/forms/sellerreview/", RestContract.MAX_CACHE_TIME),

    GET_PRODUCT_REVIEWS_EVENT,

    REVIEW_RATING_PRODUCT_EVENT,

    CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT("https:/order/cartchange/", RestContract.NO_CACHE),

    GET_FORMS_DATASET_LIST_EVENT,

    GET_API_INFO("http:/main/md5/", RestContract.NO_CACHE),

    GET_CUSTOMER("https:/customer/getdetails/", RestContract.NO_CACHE),

    GET_RESOLUTIONS("http:/main/imageresolutions/", RestContract.MAX_CACHE_TIME),

    GET_PROMOTIONS("http:/main/getstatic?key=mobile_promotions", RestContract.NO_CACHE),

    TRACK_ORDER_EVENT("http:/order/trackingorder/", RestContract.NO_CACHE),

    /**
     * NATIVE CHECKOUT EVENTS
     */

    GET_SIGNUP_FORM_EVENT("registersignup", RestContract.MAX_CACHE_TIME),

    GET_SIGNUP_FORM_FALLBACK_EVENT("http:/forms/registersignup/", RestContract.MAX_CACHE_TIME),

    SET_SIGNUP_EVENT("https:/customer/create/", RestContract.NO_CACHE),

    GET_CREATE_ADDRESS_FORM_EVENT("addresscreate", RestContract.MAX_CACHE_TIME),

    GET_CREATE_ADDRESS_FORM_FALLBACK_EVENT("http:/forms/addresscreate/", RestContract.MAX_CACHE_TIME),

    GET_EDIT_ADDRESS_FORM_EVENT("addressedit", RestContract.MAX_CACHE_TIME),

    GET_EDIT_ADDRESS_FORM_FALLBACK_EVENT("http:/forms/addressedit/", RestContract.MAX_CACHE_TIME),

    GET_CUSTOMER_ADDRESSES_EVENT("https:/customer/address/list/", RestContract.NO_CACHE),

    SET_DEFAULT_SHIPPING_ADDRESS("https:/customer/address/makedefaultshipping/", RestContract.NO_CACHE),

    SET_DEFAULT_BILLING_ADDRESS("https:/customer/address/makedefaultbilling/", RestContract.NO_CACHE),

    GET_BILLING_FORM_EVENT("https:/multistep/billing/", RestContract.NO_CACHE),

    CREATE_ADDRESS_EVENT("https:/customer/address/create/", RestContract.NO_CACHE),

    CREATE_ADDRESS_SIGNUP_EVENT("https:/customer/address/create/?showGender=true", RestContract.NO_CACHE),

    EDIT_ADDRESS_EVENT("https:/customer/address/save/", RestContract.NO_CACHE),

    SET_BILLING_ADDRESS_EVENT("https:/multistep/billing/", RestContract.NO_CACHE),

    SET_SHIPPING_ADDRESS_EVENT("https:/multistep/shipping/", RestContract.NO_CACHE),

    GET_REGIONS_EVENT("https:/customer/address/regions/", RestContract.NO_CACHE),

    GET_CITIES_EVENT("https:/customer/address/cities/", RestContract.NO_CACHE),

    GET_SHIPPING_METHODS_EVENT("https:/multistep/shippingmethod/", RestContract.DEFAULT_CACHE_TIME),

    SET_SHIPPING_METHOD_EVENT("https:/multistep/shippingmethod/", RestContract.DEFAULT_CACHE_TIME),

    GET_PAYMENT_METHODS_EVENT("https:/multistep/paymentmethod/", RestContract.DEFAULT_CACHE_TIME),

    SET_PAYMENT_METHOD_EVENT("https:/multistep/paymentmethod/", RestContract.DEFAULT_CACHE_TIME),

    GET_MY_ORDERS_LIST_EVENT("http:/order/list/", RestContract.NO_CACHE),

    CHECKOUT_FINISH_EVENT("https:/multistep/finish/", RestContract.NO_CACHE),

    NATIVE_CHECKOUT_AVAILABLE("http:/main/getconfig/module/configuration/key/native_checkout_mobile_api/", RestContract.NO_CACHE),

    ADD_VOUCHER("http:/order/addvoucher/", RestContract.NO_CACHE),

    REMOVE_VOUCHER("http:/order/removevoucher/", RestContract.NO_CACHE),

    SEARCH_PRODUCT("http:/catalog/detail", RestContract.NO_CACHE),

    GET_CAMPAIGN_EVENT("http:/campaign/get/", RestContract.NO_CACHE),

    GET_NEWSLETTERS_FORM_EVENT("managenewsletters", RestContract.NO_CACHE),

    GET_NEWSLETTERS_FORM_FALLBACK_EVENT("http:/forms/managenewsletters/", RestContract.NO_CACHE),

    SUBSCRIBE_NEWSLETTERS_EVENT("https:/customer/managenewsletters/", RestContract.NO_CACHE),

    GET_GLOBAL_CONFIGURATIONS,

    GET_COUNTRY_CONFIGURATIONS("http:/main/getconfigurations/", RestContract.NO_CACHE),

    HOME_NEWSLETTERS_SIGNUP_FORM_EVENT,

    HOME_NEWSLETTERS_SIGNUP_FORM_FALLBACK_EVENT("https:/newsletter/signup/", RestContract.MAX_CACHE_TIME),

    GET_FAVOURITE_LIST,

    GET_RECENLTLY_VIEWED_LIST,

    GET_PRODUCT_BUNDLE("http:/catalog/bundle/sku/", RestContract.NO_CACHE),

    ADD_PRODUCT_BUNDLE("http:/order/addbundle/", RestContract.NO_CACHE),

    GET_PRODUCT_OFFERS,

    GET_SELLER_REVIEWS,

    VALIDATE_PRODUCTS("http:/catalog/validate/", RestContract.NO_CACHE),

    GET_SHOP_EVENT("http:/main/getstatic/", RestContract.MAX_CACHE_TIME);

    public final String action;
    public final Integer cacheTime;

    /**
     * Constructor with action and cache time
     * @param action The action
     * @param cacheTime The cache time {@link RestContract}
     */
    EventType(String action, Integer cacheTime) {
        this.action = action;
        this.cacheTime = cacheTime;
    }

    /**
     * Empty constructor
     */
    EventType() {
        this(null, RestContract.NO_CACHE);
    }

}