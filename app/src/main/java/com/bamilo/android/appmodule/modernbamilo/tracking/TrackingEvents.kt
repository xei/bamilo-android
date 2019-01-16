package com.bamilo.android.appmodule.modernbamilo.tracking

import android.content.Context
import android.content.Intent

/**
 * This interface contains all the events that we want to track by event tracker platforms.
 */
interface TrackingEvents {

    // The following function is not an event, but it must be implemented for each platform
    fun initialize(context: Context)

    // User Attribution
    fun install(intent: Intent)
    fun appOpen(appOpenMethod: AppOpenMethod)
    fun inviteFriends()

    // Account
    fun signUp(userId: String?, emailAddress: String?, phoneNumber: String?, signUpMethod: SignUpMethod)
    fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginMethod: LoginMethod)
    fun logout()
//    fun editProfile()
//    fun addAddress()
//    fun editAddress()
//    fun removeAddress()

    // Purchase
    fun addToCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int = 1)
    fun removeFromCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int = 1)
    fun beginCheckout(basketValue: Long, numberOfBasketItems: Int)
    fun purchase(value: Long, numberOfItems: Int, coupon: String, transactionId: String, paymentMethod: PaymentMethod, cityName: String)
    fun cancelOrder(transaction_id: String, value: Long, quantity: Int)
//    fun conductSurvey()

    // Navigation
    fun search(searchTerm: String)
    fun sortProductsList(sortingMethodKey: String)

    // Product
    fun viewProduct(id: String, sku: String, title: String, amount: Long, categoryId: String, categoryUrl: String, brandId: String?, brandTitle: String?)
    fun viewProductGallery(id: String)
    fun shareProduct(id: String)
    fun addToWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int = 1)
    fun removeFromWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int = 1)
    fun addProductReview(id: String, title: String, amount: Long, categoryId: String)
    fun notifyMe(id: String, title: String, categoryId: String)


    object ParamsKeys {
        const val APP_OPEN_METHOD = "app_open_method"
        const val USER_ID = "user_id"
        const val USER_EMAIL_ADDRESS = "email"
        const val USER_PHONE_NUMBER = "phone"
        const val SIGN_UP_METHOD = "sign_up_method"
        const val LOGIN_METHOD = "login_method"
        const val ITEM_ID = "item_id"
        const val ITEM_SKU = "item_sku"
        const val ITEM_NAME = "item_name"
        const val ITEM_CATEGORY = "item_category"
        const val ITEM_CATEGORY_URL = "item_category_url"
        const val ITEM_BRAND = "item_brand"
        const val ITEM_BRAND_NAME = "item_brand_name"
        const val PRICE = "price"
        const val QUANTITY = "quantity"
        const val BASKET_VALUE = "basket_value"
        const val NO_OF_ITEMS = "number_of_items"
        const val VALUE = "value"
        const val COUPON = "coupon"
        const val TRANSACTION_ID = "transaction_id"
        const val PAYMENT_METHOD = "payment_method"
        const val CITY_NAME = "city_name"
        const val SEARCH_TERM = "search_term"
        const val SORTING_METHOD_KEY = "sort_key"
    }

    // Param Enumerations:

    enum class AppOpenMethod(val value: String) : CharSequence by value {
        LAUNCHER("launcher"),
        DEEP_LINK("deep_link"),
        NOTIFICATION("notification");

        override fun toString() = value
    }

    enum class SignUpMethod(val value: String) : CharSequence by value {
        REGISTER_WITH_EMAIL("email"),
        REGISTER_WITH_PHONE("phone");

        override fun toString() = value
    }

    enum class LoginMethod(val value: String) : CharSequence by value {
        LOGIN_WITH_EMAIL("email"),
        LOGIN_WITH_PHONE("phone");

        override fun toString() = value
    }

    enum class AddToCartType(val value: String) : CharSequence by value {
        ADD_TO_CART_BTN("ADD_TO_CART_BTN"),
        BUY_NOW_BTN("BUY_NOW_BTN");

        override fun toString() = value
    }

    enum class PaymentMethod(val value: String) : CharSequence by value {
        MPG("mpg"),
        IPG("ipg"),
        COD("cod");

        override fun toString() = value
    }

}