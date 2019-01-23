package com.bamilo.android.appmodule.modernbamilo.tracking.platformbasedimplementation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.bamilo.android.BuildConfig
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.tracking.TAG_DEBUG
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents
import com.bamilo.android.appmodule.modernbamilo.util.logging.LogType
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger
import com.webengage.sdk.android.*


object WebEngageEventsTracker : TrackingEvents {

    private var mAnalytics: Analytics? = null
    private var mUser: User? = null

    override fun initialize(context: Context) {
        val webEngageConfig = WebEngageConfig.Builder()
                .setWebEngageKey(context.getString(R.string.webEngage_licence_code))
                .setDebugMode(BuildConfig.DEBUG)    // enable/disable logs
                .setLocationTrackingStrategy(LocationTrackingStrategy.ACCURACY_CITY)    // might need to check PackageManager().hasSystemFeature() because of the permission in the Manifest.xml
                .setPushLargeIcon(R.mipmap.ic_launcher)
                .setPushSmallIcon(R.drawable.ic_stat_notifications)
                .setPushAccentColor(ContextCompat.getColor(context, R.color.colorAccent))
                .build()

        try {
            (context as Application).registerActivityLifecycleCallbacks(WebEngageActivityLifeCycleCallbacks(context, webEngageConfig))
        } catch (cce: ClassCastException) {
            Logger.log(message = "WebEngage must initialize in the Application class!", logType = LogType.ERROR)
            return
        }


        mAnalytics = WebEngage.get().analytics()
        mUser = WebEngage.get().user()

        Logger.log(
                StringBuilder("WebEngage Events Tracker is initialized.")
                        .append(" licenceCode: ")
                        .append(context.getString(R.string.webEngage_licence_code))
                        .toString(),
                TAG_DEBUG)
    }

    /**
     * hit an [intent] as the install referer to measure user attribution.
     */
    override fun install(intent: Intent) {
        mAnalytics?.installed(intent)
    }

    override fun appOpen(appOpenMethod: TrackingEvents.AppOpenMethod) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.OPEN_APP,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.APP_OPEN_METHOD, appOpenMethod.value)
                    })
        }
    }

    override fun inviteFriends() {
        mAnalytics?.track(WebEngageCustomEventKeys.INVITE_FRIENDS)
    }

    /**
     * The [userId] is needed to login in WebEngage.
     * The [phoneNumber] must be a string in E.164 format, eg. +551155256325, +917850009678.
     */
    override fun signUp(userId: String?, emailAddress: String?, phoneNumber: String?, signUpMethod: TrackingEvents.SignUpMethod) {
        mUser?.run {
            userId?.run {
                (userId) }
            emailAddress?.run { mUser?.setEmail(emailAddress) }
            phoneNumber?.run { mUser?.setPhoneNumber(phoneNumber) }
        }

        mAnalytics?.run {
            track(WebEngageCustomEventKeys.SIGN_UP,
                    HashMap<String, Any>().apply {
                        userId?.run { put(TrackingEvents.ParamsKeys.USER_ID, userId) }
                        emailAddress?.run { put(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
                        phoneNumber?.run { put(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
                        put(TrackingEvents.ParamsKeys.SIGN_UP_METHOD, signUpMethod.value)
                    }
            )
        }
    }

    /**
     * The [userId] is needed to login in WebEngage.
     * The String phoneNumber must be in E.164 format, eg. +551155256325, +917850009678.
     */
    override fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginMethod: TrackingEvents.LoginMethod) {
        mUser?.run {
            userId?.run { login(userId) }
            emailAddress?.run { mUser?.setEmail(emailAddress) }
            phoneNumber?.run { mUser?.setPhoneNumber(phoneNumber) }
        }

//        mAnalytics?.run {
//            track(WebEngageCustomEventKeys.LOGIN,
//                    HashMap<String, Any>().apply {
//                        userId?.run { put(TrackingEvents.ParamsKeys.USER_ID, userId) }
//                        emailAddress?.run { put(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
//                        phoneNumber?.run { put(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
//                        put(TrackingEvents.ParamsKeys.LOGIN_METHOD, loginMethod.value)
//            })
//        }
    }

    override fun logout() {
        mUser?.logout()
//        mAnalytics?.track(WebEngageCustomEventKeys.LOGOUT)
    }

    override fun editProfile() {
        mAnalytics?.track(WebEngageCustomEventKeys.EDIT_PROFILE)
    }

    override fun addAddress() {
        mAnalytics?.track(WebEngageCustomEventKeys.ADD_ADDRESS)
    }

    override fun editAddress() {
        mAnalytics?.track(WebEngageCustomEventKeys.EDIT_ADDRESS)
    }

    override fun removeAddress() {
        mAnalytics?.track(WebEngageCustomEventKeys.REMOVE_ADDRESS)
    }

    override fun addToCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.ADD_TO_CART,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                        put(TrackingEvents.ParamsKeys.ITEM_SKU, sku)
                        put(TrackingEvents.ParamsKeys.ITEM_NAME, title)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY, categoryId)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY_URL, categoryUrl)
                        put(TrackingEvents.ParamsKeys.PRICE, amount)
                        put(TrackingEvents.ParamsKeys.QUANTITY, quantity)
                    })
        }
    }

    override fun removeFromCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.REMOVE_FROM_CART,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                        put(TrackingEvents.ParamsKeys.ITEM_SKU, sku)
                        put(TrackingEvents.ParamsKeys.ITEM_NAME, title)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY, categoryId)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY_URL, categoryUrl)
                        put(TrackingEvents.ParamsKeys.PRICE, amount)
                        put(TrackingEvents.ParamsKeys.QUANTITY, quantity)
                    })
        }
    }

    override fun beginCheckout(basketValue: Long, numberOfBasketItems: Int) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.START_CHECKOUT,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.BASKET_VALUE, basketValue)
                        put(TrackingEvents.ParamsKeys.NO_OF_ITEMS, numberOfBasketItems)
                    })
        }
    }

    override fun purchase(value: Long, numberOfItems: Int, coupon: String?, transactionId: String, paymentMethod: TrackingEvents.PaymentMethod, cityName: String) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.PURCHASE,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.VALUE, value)
                        put(TrackingEvents.ParamsKeys.NO_OF_ITEMS, numberOfItems)
                        coupon?.let { put(TrackingEvents.ParamsKeys.COUPON, it) }
                        put(TrackingEvents.ParamsKeys.TRANSACTION_ID, transactionId)
                        put(TrackingEvents.ParamsKeys.PAYMENT_METHOD, paymentMethod.value)
                        put(TrackingEvents.ParamsKeys.CITY_NAME, cityName)
                    })
        }
    }

    override fun cancelOrder(transaction_id: String, value: Long, quantity: Int) {
        mAnalytics?.track(WebEngageCustomEventKeys.CANCEL_ORDER,
                HashMap<String, Any>().apply {
                    put(TrackingEvents.ParamsKeys.TRANSACTION_ID, transaction_id)
                    put(TrackingEvents.ParamsKeys.VALUE, value)
                    put(TrackingEvents.ParamsKeys.QUANTITY, quantity)
                })
    }

//    override fun conductSurvey() {
//        mAnalytics?.track(TrackingEvents.EventsKeys.CONDUCT_SURVEY)
//    }

    override fun search(searchTerm: String) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.SEARCH,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SEARCH_TERM, searchTerm)
                    })
        }
    }

    override fun sortProductsList(sortingMethodKey: String) {
        mAnalytics?.track(WebEngageCustomEventKeys.SORT_PRODUCT_LIST,
                HashMap<String, Any>().apply {
                    put(TrackingEvents.ParamsKeys.SORTING_METHOD_KEY, sortingMethodKey)
                })
    }

    override fun viewProduct(id: String, sku: String, title: String, amount: Long, categoryId: String, categoryUrl: String, brandId: String?, brandTitle: String?) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.CONTENT_VIEW,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                        put(TrackingEvents.ParamsKeys.ITEM_SKU, sku)
                        put(TrackingEvents.ParamsKeys.ITEM_NAME, title)
                        put(TrackingEvents.ParamsKeys.PRICE, amount)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY, categoryId)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY_URL, categoryUrl)
                        brandId?.let { put(TrackingEvents.ParamsKeys.ITEM_BRAND, brandId) }
                        brandTitle?.let { put(TrackingEvents.ParamsKeys.ITEM_BRAND_NAME, brandTitle) }
                    })
        }
    }

    override fun viewProductGallery(id: String) {}

    override fun shareProduct(id: String) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.SHARE,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                    })
        }
    }

    override fun addToWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.ADD_TO_WISH_LIST,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                        put(TrackingEvents.ParamsKeys.ITEM_NAME, title)
                        put(TrackingEvents.ParamsKeys.PRICE, amount)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY, categoryId)
                        put(TrackingEvents.ParamsKeys.QUANTITY, quantity)
                    })
        }
    }

    override fun removeFromWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.REMOVE_FROM_WISH_LIST,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                        put(TrackingEvents.ParamsKeys.ITEM_NAME, title)
                        put(TrackingEvents.ParamsKeys.PRICE, amount)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY, categoryId)
                        put(TrackingEvents.ParamsKeys.QUANTITY, quantity)
                    })
        }
    }

    override fun addProductReview(id: String, title: String, amount: Long, categoryId: String) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.ADD_REVIEW,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                        put(TrackingEvents.ParamsKeys.ITEM_NAME, title)
                        put(TrackingEvents.ParamsKeys.PRICE, amount)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY, categoryId)
                    })
        }
    }

    override fun notifyMe(id: String, title: String, categoryId: String) {
        mAnalytics?.run {
            track(WebEngageCustomEventKeys.NOTIFY_ME,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.ITEM_ID, id)
                        put(TrackingEvents.ParamsKeys.ITEM_NAME, title)
                        put(TrackingEvents.ParamsKeys.ITEM_CATEGORY, categoryId)
                    })
        }
    }

    fun hitScreenNavigationForInAppMessaging(screenName: String, screenData: Map<String, Any>?) {
        if (screenData != null) {
            mAnalytics?.screenNavigated(screenName, screenData)
        } else {
            mAnalytics?.screenNavigated(screenName)
        }

    }

    /**
     * We are not going to measure this in WebEngage.
     */
    override fun failRequest(request: String, errorCode: Int, errorMessage: String, ipAddress: String, connectionMethod: String, operatorName: String, vpn: Boolean, apiLevel: Int, apiVersion: String) {
    }

    private object WebEngageCustomEventKeys {
        const val OPEN_APP = "app_opened"
        const val INVITE_FRIENDS = "user_friends_invited"
        const val SIGN_UP = "user_signed_up"
//        const val LOGIN = "user_logged_in"
//        const val LOGOUT = "user_logged_out"
        const val EDIT_PROFILE = "user_profile_edited"
        const val ADD_ADDRESS = "user_address_added"
        const val EDIT_ADDRESS = "user_address_edited"
        const val REMOVE_ADDRESS = "user_address_removed"
        const val ADD_TO_CART = "cart_item_added"
        const val REMOVE_FROM_CART = "cart_item_removed"
        const val START_CHECKOUT = "cart_checkout_started"
        const val PURCHASE = "cart_checkout_completed"
//        const val CONDUCT_SURVEY = "survey_conducted"
        const val SEARCH = "searched"
        const val CONTENT_VIEW = "product_viewed"
        const val CANCEL_ORDER = "purchase_refunded"
        const val SHARE = "product_shared"
        const val ADD_TO_WISH_LIST = "product_added_to_wishlist"
        const val REMOVE_FROM_WISH_LIST = "product_removed_from_wishlist"
        const val ADD_REVIEW = "product_review_added"
        const val NOTIFY_ME = "product_stock_subscribed"
        const val SORT_PRODUCT_LIST = "product_list_sorted"
    }

}