package com.mobile.newFramework.utils;

import com.mobile.newFramework.rest.configs.AigRestContract;

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

    INIT_FORMS("http:/forms/index/", AigRestContract.MAX_CACHE_TIME),

    GET_CATEGORIES_EVENT("http:/catalog/categories/", AigRestContract.MAX_CACHE_TIME),

    GET_CATALOG_EVENT("http:/search/find/", AigRestContract.NO_CACHE),

    LOGIN_EVENT("https:/customer/login/", AigRestContract.NO_CACHE),

    EMAIL_CHECK("http:/customer/emailcheck/", AigRestContract.NO_CACHE),

    FACEBOOK_LOGIN_EVENT("https:/customer/facebooklogin/", AigRestContract.NO_CACHE),

    GET_LOGIN_FORM_EVENT("http:/forms/login/", AigRestContract.MAX_CACHE_TIME),

    LOGOUT_EVENT("https:/customer/logout/", AigRestContract.NO_CACHE),

    GET_HOME_EVENT("http:/main/home/", AigRestContract.MAX_CACHE_TIME),

    GET_SEARCH_SUGGESTIONS_EVENT("http:/search/suggest/", AigRestContract.DEFAULT_CACHE_TIME),

    ADD_ITEM_TO_SHOPPING_CART_EVENT("https:/cart/addproduct/", AigRestContract.NO_CACHE),

    ADD_ITEMS_TO_SHOPPING_CART_EVENT("https:/cart/addmultiple/", AigRestContract.NO_CACHE),

    REMOVE_ITEM_FROM_SHOPPING_CART_EVENT("https:/cart/removeproduct/", AigRestContract.NO_CACHE),

    GET_SHOPPING_CART_ITEMS_EVENT("https:/cart/getdata/", AigRestContract.NO_CACHE),

    GET_REGISTRATION_FORM_EVENT("http:/forms/register/", AigRestContract.MAX_CACHE_TIME),

    GET_CHANGE_PASSWORD_FORM_EVENT("http:/forms/changepassword/", AigRestContract.MAX_CACHE_TIME),

    CHANGE_PASSWORD_EVENT("https:/customer/changepassword/", AigRestContract.NO_CACHE),

    GET_FORGET_PASSWORD_FORM_EVENT("http:/forms/forgotpassword/", AigRestContract.MAX_CACHE_TIME),

    EDIT_USER_DATA_FORM_EVENT("http:/forms/edit/", AigRestContract.NO_CACHE),

    EDIT_USER_DATA_EVENT("http:/customer/edit/", AigRestContract.NO_CACHE),

    FORGET_PASSWORD_EVENT("https:/customer/forgotpassword/", AigRestContract.NO_CACHE),

    REGISTER_ACCOUNT_EVENT("https:/customer/create/", AigRestContract.NO_CACHE),

    GET_STATIC_PAGE("http:/main/getstatic/", AigRestContract.MAX_CACHE_TIME),

    GET_FORM_REVIEW_EVENT("http:/forms/review/", AigRestContract.MAX_CACHE_TIME),

    GET_FORM_RATING_EVENT("http:/forms/rating/", AigRestContract.MAX_CACHE_TIME),

    GET_FORM_SELLER_REVIEW_EVENT("http:/forms/sellerreview/", AigRestContract.MAX_CACHE_TIME),

    REVIEW_RATING_PRODUCT_EVENT,

    CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT("https:/cart/updateproduct/", AigRestContract.NO_CACHE),

    GET_FORMS_DATA_SET_LIST_EVENT,

    GET_API_INFO("http:/main/md5/", AigRestContract.NO_CACHE),

    GET_CUSTOMER("https:/customer/getdetails/", AigRestContract.NO_CACHE),

    GET_RESOLUTIONS("http:/main/imageresolutions/", AigRestContract.MAX_CACHE_TIME),

    TRACK_ORDER_EVENT("http:/customer/trackingorder/", AigRestContract.NO_CACHE),

    /**
     * NATIVE CHECKOUT EVENTS
     */

    GET_SIGNUP_FORM_EVENT("http:/forms/registersignup/", AigRestContract.MAX_CACHE_TIME),

    GUEST_LOGIN_EVENT("https:/customer/createsignup/", AigRestContract.NO_CACHE),

    GET_CREATE_ADDRESS_FORM_EVENT("http:/forms/addresscreate/", AigRestContract.MAX_CACHE_TIME),

    GET_EDIT_ADDRESS_FORM_EVENT("http:/forms/addressedit/", AigRestContract.MAX_CACHE_TIME),

    GET_CUSTOMER_ADDRESSES_EVENT("https:/customer/getaddresslist/", AigRestContract.NO_CACHE),

    SET_DEFAULT_ADDRESS("https:/customer/makedefaultaddress/", AigRestContract.NO_CACHE),

    SET_DEFAULT_SHIPPING_ADDRESS(SET_DEFAULT_ADDRESS.action, AigRestContract.NO_CACHE),

    SET_DEFAULT_BILLING_ADDRESS(SET_DEFAULT_ADDRESS.action, AigRestContract.NO_CACHE),

    CREATE_ADDRESS_EVENT("https:/customer/addresscreate/", AigRestContract.NO_CACHE),

    CREATE_ADDRESS_SIGNUP_EVENT("https:/customer/address/create/", AigRestContract.NO_CACHE),

    EDIT_ADDRESS_EVENT("https:/customer/addressedit/", AigRestContract.NO_CACHE),

    GET_BILLING_FORM_EVENT("https:/forms/multistepaddresses/", AigRestContract.NO_CACHE),

    SET_BILLING_ADDRESS_EVENT("https:/forms/multistepaddresses/", AigRestContract.NO_CACHE),

    GET_REGIONS_EVENT,

    GET_CITIES_EVENT,

    GET_POSTAL_CODE_EVENT,

    GET_PHONE_PREFIXES,

    GET_SHIPPING_METHODS_EVENT("https:/multistep/shippingmethod/", AigRestContract.DEFAULT_CACHE_TIME),

    SET_SHIPPING_METHOD_EVENT("https:/multistep/shippingmethod/", AigRestContract.DEFAULT_CACHE_TIME),

    GET_PAYMENT_METHODS_EVENT("https:/multistep/paymentmethod/", AigRestContract.DEFAULT_CACHE_TIME),

    SET_PAYMENT_METHOD_EVENT("https:/multistep/paymentmethod/", AigRestContract.DEFAULT_CACHE_TIME),

    GET_MY_ORDERS_LIST_EVENT("http:/customer/orderlist/", AigRestContract.NO_CACHE),

    CHECKOUT_FINISH_EVENT("https:/multistep/finish/", AigRestContract.NO_CACHE),

    NATIVE_CHECKOUT_AVAILABLE("http:/main/getconfig/module/configuration/key/native_checkout_mobile_api/", AigRestContract.NO_CACHE),

    ADD_VOUCHER("http:/cart/addvoucher/", AigRestContract.NO_CACHE),

    REMOVE_VOUCHER("http:/cart/removevoucher/", AigRestContract.NO_CACHE),

    GET_CAMPAIGN_EVENT("http:/campaign/get/", AigRestContract.NO_CACHE),

    GET_NEWSLETTERS_FORM_EVENT("http:/forms/managenewsletters/", AigRestContract.NO_CACHE),

    SUBSCRIBE_NEWSLETTERS_EVENT("https:/customer/managenewsletters/", AigRestContract.NO_CACHE),

    GET_GLOBAL_CONFIGURATIONS,

    GET_COUNTRY_CONFIGURATIONS("http:/main/getconfigurations/", AigRestContract.NO_CACHE),

    GET_FAVOURITE_LIST,

    GET_RECENTLY_VIEWED_LIST,

    GET_PRODUCT_BUNDLE("http:/catalog/bundle/", AigRestContract.NO_CACHE),

    ADD_PRODUCT_BUNDLE("http:/cart/addbundle/", AigRestContract.NO_CACHE),

    GET_SELLER_REVIEWS,

    VALIDATE_PRODUCTS("http:/catalog/validate/", AigRestContract.NO_CACHE),

    GET_SHOP_EVENT("http:/main/getstatic/key/", AigRestContract.MAX_CACHE_TIME),

    GET_PRODUCT_DETAIL("http:/catalog/detail/", AigRestContract.NO_CACHE),

    GET_PRODUCT_REVIEWS(GET_PRODUCT_DETAIL.action, AigRestContract.MAX_CACHE_TIME),

    GET_PRODUCT_OFFERS(GET_PRODUCT_DETAIL.action, AigRestContract.NO_CACHE),

    ADD_PRODUCT_TO_WISH_LIST("http:/wishlist/addproduct/", AigRestContract.NO_CACHE),

    REMOVE_PRODUCT_FROM_WISH_LIST("http:/wishlist/removeproduct/", AigRestContract.NO_CACHE),

    GET_WISH_LIST("http:/wishlist/getproducts/", AigRestContract.NO_CACHE),

    GET_FAQ_TERMS("http:/main/getfaqandterms/", AigRestContract.MAX_CACHE_TIME),

    SET_NEWSLETTER_SIGUNP("http:/main/newsletter/signup/", AigRestContract.MAX_CACHE_TIME),

    GET_RICH_RELEVANCE_EVENT("http:/richrelevance/request/", AigRestContract.NO_CACHE);


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

}
