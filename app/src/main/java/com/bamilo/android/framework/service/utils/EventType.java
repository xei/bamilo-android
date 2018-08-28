package com.bamilo.android.framework.service.utils;

import com.bamilo.android.framework.service.rest.configs.AigRestContract;

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
public enum EventType {

    INITIALIZE,

    GET_CATEGORIES_EVENT("https:/catalog/categories/", AigRestContract.MAX_CACHE_TIME),

    GET_SUBCATEGORIES_EVENT("https:/catalog/categoryByUrlKey/", AigRestContract.MAX_CACHE_TIME),

    GET_CATALOG_EVENT("https:/search/find/"),

    LOGIN_EVENT("https:/customer/login/"),

    AUTO_LOGIN_EVENT(LOGIN_EVENT.action),

    EMAIL_CHECK("https:/customer/emailcheck/"),

    GET_LOGIN_FORM_EVENT("https:/forms/login/", AigRestContract.MAX_CACHE_TIME),

    LOGOUT_EVENT("https:/customer/logout/"),

    GET_HOME_EVENT("https:/main/home/", AigRestContract.MAX_CACHE_TIME),

    GET_SEARCH_SUGGESTIONS_EVENT("https:/search/suggester/", AigRestContract.DEFAULT_CACHE_TIME),

    ADD_ITEM_TO_SHOPPING_CART_EVENT("https:/cart/addproduct/"),

    ADD_ITEMS_TO_SHOPPING_CART_EVENT("https:/cart/addmultiple/"),

    REMOVE_ITEM_FROM_SHOPPING_CART_EVENT("https:/cart/removeproduct/"),

    GET_SHOPPING_CART_ITEMS_EVENT("https:/cart/getdata/"),

    GET_REGISTRATION_FORM_EVENT("https:/forms/register/", AigRestContract.MAX_CACHE_TIME),

    GET_CHANGE_PASSWORD_FORM_EVENT("https:/forms/changepassword/", AigRestContract.MAX_CACHE_TIME),

    CHANGE_PASSWORD_EVENT,

    GET_FORGET_PASSWORD_FORM_EVENT("https:/forms/forgotpassword/", AigRestContract.MAX_CACHE_TIME),

    EDIT_USER_DATA_FORM_EVENT("https:/forms/edit/"),

    EDIT_USER_DATA_EVENT,

    FORGET_PASSWORD_EVENT,

    REGISTER_ACCOUNT_EVENT,

    GET_STATIC_PAGE("https:/main/getstatic/", AigRestContract.MAX_CACHE_TIME),

    GET_FORM_REVIEW_EVENT("https:/forms/review/", AigRestContract.MAX_CACHE_TIME),

    GET_FORM_RATING_EVENT("https:/forms/rating/", AigRestContract.MAX_CACHE_TIME),

    GET_FORM_SELLER_REVIEW_EVENT("https:/forms/sellerreview/", AigRestContract.MAX_CACHE_TIME),

    REVIEW_RATING_PRODUCT_EVENT,

    CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT("https:/cart/updateproduct/"),

    GET_FORMS_DATA_SET_LIST_EVENT,

    GET_API_INFO("https:/main/md5/"),

    GET_CUSTOMER("https:/customer/getdetails/"),

    GET_RESOLUTIONS("https:/main/imageresolutions/", AigRestContract.MAX_CACHE_TIME),

    TRACK_ORDER_EVENT("https:/customer/trackingorder/"),

    MOBILE_VERIFICATION_EVENT("https:/customer/verifymobile/"),

    /**
     * NATIVE CHECKOUT EVENTS
     */

    GET_SIGNUP_FORM_EVENT("https:/forms/registersignup/", AigRestContract.MAX_CACHE_TIME),

    GUEST_LOGIN_EVENT("https:/customer/createsignup/"),

    GET_CREATE_ADDRESS_FORM_EVENT("https:/forms/addresscreate/", AigRestContract.MAX_CACHE_TIME),

    GET_EDIT_ADDRESS_FORM_EVENT("https:/forms/addressedit/", AigRestContract.MAX_CACHE_TIME),

    GET_DELETE_ADDRESS_FORM_EVENT("https:/customer/addressremove/"),

    GET_CUSTOMER_ADDRESSES_EVENT("https:/customer/getaddresslist/"),

    SET_DEFAULT_ADDRESS("https:/customer/makedefaultaddress/"),

    SET_DEFAULT_SHIPPING_ADDRESS(SET_DEFAULT_ADDRESS.action),

    SET_DEFAULT_BILLING_ADDRESS(SET_DEFAULT_ADDRESS.action),

    CREATE_ADDRESS_EVENT,

    CREATE_ADDRESS_SIGNUP_EVENT("https:/customer/address/create/"),

    EDIT_ADDRESS_EVENT,

    GET_REGIONS_EVENT,

    GET_CITIES_EVENT,

    GET_POSTAL_CODE_EVENT,

    GET_PHONE_PREFIXES,

    GET_MY_ORDERS_LIST_EVENT("https:/customer/orderlist/"),

    ADD_VOUCHER("https:/cart/addvoucher/"),

    REMOVE_VOUCHER("https:/cart/removevoucher/"),

    GET_CAMPAIGN_EVENT("https:/campaign/get/"),

    GET_NEWSLETTER_PREFERENCES_FORM_EVENT("https:/forms/managenewsletterpreferences/"),

    GET_NEWSLETTER_UN_SUBSCRIBE_FORM(),

    SUBSCRIBE_NEWSLETTERS_EVENT,

    GET_GLOBAL_CONFIGURATIONS,

    GET_COUNTRY_CONFIGURATIONS("https:/main/getconfigurations/"),

    GET_FAVOURITE_LIST,

    GET_RECENTLY_VIEWED_LIST,

    GET_PRODUCT_BUNDLE("https:/catalog/bundle/"),

    ADD_PRODUCT_BUNDLE("https:/cart/addbundle/"),

    GET_SELLER_REVIEWS,

    VALIDATE_PRODUCTS("https:/catalog/validate/"),

    GET_SHOP_EVENT("https:/main/getstatic/key/", AigRestContract.MAX_CACHE_TIME),

    GET_PRODUCT_DETAIL("https:/catalog/detail/"),

    GET_DELIVERY_TIME("https:/catalog/deliverytime/"),

    GET_PRODUCT_REVIEWS(GET_PRODUCT_DETAIL.action, AigRestContract.MAX_CACHE_TIME),

    GET_PRODUCT_OFFERS(GET_PRODUCT_DETAIL.action),

    ADD_PRODUCT_TO_WISH_LIST("https:/wishlist/addproduct/"),

    REMOVE_PRODUCT_FROM_WISH_LIST("https:/wishlist/removeproduct/"),

    GET_WISH_LIST("https:/wishlist/getproducts/"),

    GET_FAQ_TERMS("https:/main/getfaqandterms/", AigRestContract.MAX_CACHE_TIME),

    GET_RICH_RELEVANCE_EVENT("https:/richrelevance/request/"),

    // ############# MULTI STEP CHECKOUT #############

    SET_MULTI_STEP_ADDRESSES("https:/multistep/addresses/"),

    GET_MULTI_STEP_ADDRESSES("https:/multistep/getstepaddresses/"),

    GET_MULTI_STEP_SHIPPING("https:/multistep/getstepshipping/"),

    SET_MULTI_STEP_SHIPPING,

    GET_MULTI_STEP_PAYMENT("https:/multistep/getsteppayment/"),

    SET_MULTI_STEP_PAYMENT,

    GET_MULTI_STEP_FINISH("https:/multistep/getstepfinish/"),

    SET_MULTI_STEP_FINISH("https:/multistep/finish/"),

    // ############# CART #############

    CLEAR_SHOPPING_CART_EVENT("https:/cart/clear/"),

    // ############# ORDER RETURN #############

    GET_RETURN_REASON_FORM("https:/forms/returndetail/", AigRestContract.MAX_CACHE_TIME),

    GET_RETURN_REASONS,

    SUBMIT_FORM,

    GET_EXTERNAL_LINKS("https:/main/getexternallinks/"),

    RETURN_METHODS_FORM_EVENT("https:/forms/returnmethods/"),

    RETURN_REFUND_FORM_EVENT("https:/forms/refundmethod/"),

    RETURN_FINISH_EVENT("https:/return/finishreturn/"),

    GET_EVENT_EVENT("https:/customer/getaddressbyid/");


    public final String action;
    public final Integer cacheTime;

    /**
     * Constructor with action and cache time
     * @param action The action
     * @param cacheTime The cache time {@link AigRestContract}
     */
    EventType(String action, Integer cacheTime) {
        this.action = action;
        this.cacheTime = cacheTime;
    }

    /**
     * Empty constructor
     */
    EventType() {
        this(null, AigRestContract.NO_CACHE);
    }

    /**
     * Endpoint constructor with no cache
     */
    EventType(String s) {
        this(s, AigRestContract.NO_CACHE);
    }

}
