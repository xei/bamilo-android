package com.bamilo.android.appmodule.modernbamilo.tracking.platformbasedimplementation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bamilo.android.appmodule.modernbamilo.tracking.TAG_DEBUG
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * This class implements event hits in Firebase platform.
 *
 */
object FirebaseEventsTracker : TrackingEvents {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun initialize(context: Context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)

        Logger.log("Firebase Events Tracker is initialized.", TAG_DEBUG)
    }

    /**
     * Don't call this method!
     */
    override fun install(intent: Intent) {
        // This event is measured automatically by Firebase SDK
    }

    override fun appOpen(appOpenMethod: TrackingEvents.AppOpenMethod) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle().apply {
                putString(TrackingEvents.ParamsKeys.APP_OPEN_METHOD, appOpenMethod.value)
            })
        }
    }

    override fun inviteFriends() {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseCustomEventKeys.INVITE_FRIENDS, null)
        }
    }

    override fun signUp(userId: String?, emailAddress: String?, phoneNumber: String?, signUpMethod: TrackingEvents.SignUpMethod) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.SIGN_UP, Bundle().apply {
                userId?.run { putString(TrackingEvents.ParamsKeys.USER_ID, userId) }
                emailAddress.run { putString(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
                phoneNumber.run { putString(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
                putString(TrackingEvents.ParamsKeys.SIGN_UP_METHOD, signUpMethod.value)
            })

            // Set User's Properties:
            // at first: register properties in panel
//            mFirebaseAnalytics.setUserProperty("favorite_food", mFavoriteFood);
//            mFirebaseAnalytics.setUserProperty("gender", mGender);
        }
    }

    override fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginMethod: TrackingEvents.LoginMethod) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                userId?.run { putString(TrackingEvents.ParamsKeys.USER_ID, userId) }
                emailAddress.run { putString(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
                phoneNumber.run { putString(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
                putString(TrackingEvents.ParamsKeys.LOGIN_METHOD, loginMethod.value)
            })
        }
    }

    override fun logout() {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseCustomEventKeys.LOGOUT, null)
        }
    }

//    override fun editProfile() {
//        mFirebaseAnalytics?.run {
//            logEvent(FirebaseCustomEventKeys.EDIT_PROFILE, null)
//        }
//    }
//
//    override fun addAddress() {
//        mFirebaseAnalytics?.run {
//            logEvent(FirebaseCustomEventKeys.ADD_ADDRESS, null)
//        }
//    }
//
//    override fun editAddress() {
//        mFirebaseAnalytics?.run {
//            logEvent(FirebaseCustomEventKeys.EDIT_ADDRESS, null)
//        }
//    }
//
//    override fun removeAddress() {
//        mFirebaseAnalytics?.run {
//            logEvent(FirebaseCustomEventKeys.REMOVE_ADDRESS, null)
//        }
//    }

    override fun addToCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.ADD_TO_CART, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(TrackingEvents.ParamsKeys.ITEM_SKU, sku)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryId)
                putString(TrackingEvents.ParamsKeys.ITEM_CATEGORY_URL, categoryUrl)
                putLong(FirebaseAnalytics.Param.PRICE, amount)
                putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
            })
        }
    }

    override fun removeFromCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(TrackingEvents.ParamsKeys.ITEM_SKU, sku)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryId)
                putString(TrackingEvents.ParamsKeys.ITEM_CATEGORY_URL, categoryUrl)
                putLong(FirebaseAnalytics.Param.PRICE, amount)
                putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
            })
        }
    }

    override fun beginCheckout(basketValue: Long, numberOfBasketItems: Int) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, Bundle().apply {
                putLong(FirebaseAnalytics.Param.VALUE, basketValue)
                putInt(TrackingEvents.ParamsKeys.NO_OF_ITEMS, numberOfBasketItems)
            })
        }
    }

    override fun purchase(value: Long, numberOfItems: Int, coupon: String?, transactionId: String, paymentMethod: TrackingEvents.PaymentMethod, cityName: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, Bundle().apply {
                putLong(FirebaseAnalytics.Param.VALUE, value)
                putInt(TrackingEvents.ParamsKeys.NO_OF_ITEMS, numberOfItems)
                coupon?.let { putString(FirebaseAnalytics.Param.COUPON, it) }
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, coupon)
                putString(TrackingEvents.ParamsKeys.PAYMENT_METHOD, paymentMethod.value)
                putString(TrackingEvents.ParamsKeys.CITY_NAME, cityName)
            })
        }
    }

    override fun cancelOrder(transaction_id: String, value: Long, quantity: Int) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.PURCHASE_REFUND, Bundle().apply {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, transaction_id)
                putLong(FirebaseAnalytics.Param.VALUE, value)
                putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
            })
        }
    }

//    override fun conductSurvey() {
//        mFirebaseAnalytics?.run {
//            logEvent(TrackingEvents.EventsKeys.CONDUCT_SURVEY, null)
//        }
//    }

    override fun search(searchTerm: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
                putString(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm)
            })
        }
    }

    override fun sortProductsList(sortingMethodKey: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseCustomEventKeys.SORT_PRODUCT_LIST, Bundle().apply {
                putString(TrackingEvents.ParamsKeys.SORTING_METHOD_KEY, sortingMethodKey)
            })
        }
    }

    override fun viewProduct(id: String, sku: String, title: String, amount: Long, categoryId: String, categoryUrl: String, brandId: String?, brandTitle: String?) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.VIEW_ITEM, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(TrackingEvents.ParamsKeys.ITEM_SKU, sku)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putLong(FirebaseAnalytics.Param.PRICE, amount)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryId)
                putString(TrackingEvents.ParamsKeys.ITEM_CATEGORY_URL, categoryUrl)
                putString(TrackingEvents.ParamsKeys.ITEM_BRAND, brandId)
                putString(TrackingEvents.ParamsKeys.ITEM_BRAND_NAME, brandTitle)
            })
        }
    }

    override fun viewProductGallery(id: String) {}

    override fun shareProduct(id: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.SHARE, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
            })
        }
    }

    override fun addToWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putLong(FirebaseAnalytics.Param.PRICE, amount)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryId)
                putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
            })
        }
    }

    override fun removeFromWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseCustomEventKeys.REMOVE_FROM_WISH_LIST, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putLong(FirebaseAnalytics.Param.PRICE, amount)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryId)
                putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
            })
        }
    }

    override fun addProductReview(id: String, title: String, amount: Long, categoryId: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseCustomEventKeys.ADD_REVIEW, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putLong(FirebaseAnalytics.Param.PRICE, amount)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryId)
            })
        }
    }

    override fun notifyMe(id: String, title: String, categoryId: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseCustomEventKeys.NOTIFY_ME, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryId)
            })
        }
    }

    override fun failRequest(request: String, errorCode: Int, errorMessage: String, ipAddress: String, connectionMethod: String, operatorName: String, vpn: Boolean, apiLevel: Int, apiVersion: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseCustomEventKeys.FAIL_REQUEST, Bundle().apply {
                putString("request", request)
                putInt("error_code", errorCode)
                putString("error_message", errorMessage)
                putString("ip_address", ipAddress)
                putString("connection_method", connectionMethod)
                putString("operator_name", operatorName)
                putBoolean("vpn", vpn)
                putInt("api_level", apiLevel)
                putString("api_version", apiVersion)
            })
        }
    }

    private object FirebaseCustomEventKeys {
        const val INVITE_FRIENDS = "invite_friends"
        const val LOGOUT = "logout"
//        const val EDIT_PROFILE = "edit_profile"
//        const val ADD_ADDRESS = "add_address"
//        const val EDIT_ADDRESS = "edit_address"
//        const val REMOVE_ADDRESS = "remove_address"
//        const val CONDUCT_SURVEY = "conduct_survey"
        const val REMOVE_FROM_WISH_LIST = "remove_from_wishlist"
        const val ADD_REVIEW = "add_product_review"
        const val NOTIFY_ME = "subscribe_to_product_stock"
        const val SORT_PRODUCT_LIST = "sort_product_list"
        const val FAIL_REQUEST = "fail_request"
    }

}