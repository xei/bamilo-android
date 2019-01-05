package com.bamilo.android.appmodule.modernbamilo.tracking

import android.content.Context

/**
 * This interface contains all the events that we want to track by event tracker platforms.
 */
interface TrackingEvents {

    // The following function is not an event, but it must be implemented for each platform
    fun initialize(context: Context)

    // Account
    fun register(userId: String?, emailAddress: String?, phoneNumber: String?, registrationType: RegistrationType, succeed: Boolean)
    fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginType: LoginType, succeed: Boolean)
    fun logout()
    fun editProfile()
    fun addAddress()
    fun editAddress()
    fun removeAddress()

    // Purchase
    fun addToCart(sku: String, amount: Long, addToCartType: AddToCartType)
    fun removeFromCart(sku: String, amount: Long)
    fun startCheckout(basketValue: Long)
    fun purchase(amount: Long, paymentType: PaymentType, succeed: Boolean)
    fun cancelOrder()
    fun conductSurvey()

    // Navigation
    fun openApp(openAppType: OpenAppType)
    fun inviteFriends()
    fun search(query: String)

    // Product
    fun contentView(sku: String, category: String)
    fun share(sku: String)
    fun addToWishList(sku: String)
    fun removeFromWishList(sku: String)
    fun addReview(sku: String)
    fun notifyMe(sku: String)

    // Screens
    fun openHomeScreen()
    fun openProductDetailsScreen()
    fun openProductListScreen()
    fun openCartScreen()

    // Other (from iOS!)
    fun searchFiltered()
    fun searchBarSearched()
    fun purchaseBehaviour()
    fun itemTapped()
    fun catalogViewChanged()
    fun catalogSortChanged()
    fun callToOrderTapped()
    fun buyNowTapped()
    fun searchSuggestionTapped()

    object EventsKeys {
        const val REGISTER = "register"
        const val LOGIN = "login"
        const val LOGOUT = "logout"
        const val EDIT_PROFILE = "edit_profile"
        const val ADD_ADDRESS = "add_address"
        const val EDIT_ADDRESS = "add_address"
        const val REMOVE_ADDRESS = "remove_address"
        const val CONDUCT_SURVEY = "conduct_survey"
        const val OPEN_APP = "open_app"
        const val SEARCH = "search"
        const val CONTENT_VIEW = "content_view"
        const val INVITE_FRIENDS = "invite_friends"
        const val SHARE = "share"
        const val ADD_TO_CART = "add_to_cart"
        const val REMOVE_FROM_CART = "remove_from_cart"
        const val START_CHECKOUT = "start_checkout"
        const val PURCHASE = "purchase"
        const val CANCEL_ORDER = "cancel_order"
        const val ADD_TO_WISH_LIST = "add_to_wish_list"
        const val REMOVE_FROM_WISH_LIST = "remove_from_wish_list"
        const val ADD_REVIEW = "add_review"
        const val NOTIFY_ME = "notify_me"
        const val SEARCH_FILTERED = "search_filtered"
        const val CATALOG_VIEW_CHANGED = "catalog_view_changed"
        const val CATALOG_SORT_CHANGED = "catalog_sort_changed"
    }

    object ParamsKeys {
        const val USER_ID = "user_id"
        const val USER_EMAIL_ADDRESS = "user_email_address"
        const val USER_PHONE_NUMBER = "user_phone_number"
        const val SUCCEED = "succeed"
        const val QUERY = "query"
        const val SKU = "sku"
        const val CATEGORY = "category"
        const val AMOUNT = "amount"
        const val BASKET_VALUE = "basket_value"
        const val REGISTRATION_TYPE = "registration_type"
        const val LOGIN_TYPE = "login_type"
        const val ADD_TO_CART_TYPE = "add_to_cart_type"
        const val PAYMENT_TYPE = "payment_type"
        const val OPEN_APP_TYPE = "open_app_type"
    }

    // Param Enumerations:

    enum class RegistrationType(val value: String) : CharSequence by value {
        REGISTER_WITH_EMAIL("REGISTER_WITH_EMAIL"),
        REGISTER_WITH_PHONE("REGISTER_WITH_PHONE");

        override fun toString() = value
    }

    enum class LoginType(val value: String) : CharSequence by value {
        LOGIN_WITH_EMAIL("LOGIN_WITH_EMAIL"),
        LOGIN_WITH_PHONE("LOGIN_WITH_PHONE");

        override fun toString() = value
    }

    enum class AddToCartType(val value: String) : CharSequence by value {
        ADD_TO_CART_BTN("ADD_TO_CART_BTN"),
        BUY_NOW_BTN("BUY_NOW_BTN");

        override fun toString() = value
    }

    enum class PaymentType(val value: String) : CharSequence by value {
        IPG("IPG"),
        MPG("MPG"),
        COD("COD");

        override fun toString() = value
    }

    enum class OpenAppType(val value: String) : CharSequence by value {
        LAUNCHER("LAUNCHER"),
        DEEP_LINK("DEEP_LINK"),
        PUSH_NOTIFICATION("PUSH_NOTIFICATION");

        override fun toString() = value
    }

}