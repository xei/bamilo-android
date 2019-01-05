package com.bamilo.android.appmodule.modernbamilo.tracking.platformbasedimplementation

import android.app.Application
import android.content.Context
import android.graphics.Color
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
     * The String phoneNumber must be in E.164 format, eg. +551155256325, +917850009678.
     */
    override fun register(userId: String?, emailAddress: String?, phoneNumber: String?, registrationType: TrackingEvents.RegistrationType, succeed: Boolean) {
        mUser?.run {
            userId?.run { login(userId) }
            emailAddress?.run { mUser?.setEmail(emailAddress) }
            phoneNumber?.run { mUser?.setPhoneNumber(phoneNumber) }
        }

        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.REGISTER,
                    HashMap<String, Any>().apply {
                        userId?.run { put(TrackingEvents.ParamsKeys.USER_ID, userId) }
                        emailAddress?.run { put(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
                        phoneNumber?.run { put(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
                        put(TrackingEvents.ParamsKeys.REGISTRATION_TYPE, registrationType.value)
                        put(TrackingEvents.ParamsKeys.SUCCEED, succeed)
                    }
            )
        }
    }

    /**
     * The String phoneNumber must be in E.164 format, eg. +551155256325, +917850009678.
     */
    override fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginType: TrackingEvents.LoginType, succeed: Boolean) {
        mUser?.run {
            userId?.run { login(userId) }
            emailAddress?.run { mUser?.setEmail(emailAddress) }
            phoneNumber?.run { mUser?.setPhoneNumber(phoneNumber) }
        }

        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.LOGIN,
                    HashMap<String, Any>().apply {
                        userId?.run { put(TrackingEvents.ParamsKeys.USER_ID, userId) }
                        emailAddress?.run { put(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
                        phoneNumber?.run { put(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
                        put(TrackingEvents.ParamsKeys.LOGIN_TYPE, loginType.value)
                        put(TrackingEvents.ParamsKeys.SUCCEED, succeed)
            })
        }
    }

    override fun logout() {
        mUser?.logout()
        mAnalytics?.track(TrackingEvents.EventsKeys.LOGOUT)
    }

    override fun editProfile() {
        mAnalytics?.track(TrackingEvents.EventsKeys.EDIT_PROFILE)
    }

    override fun addAddress() {
        mAnalytics?.track(TrackingEvents.EventsKeys.ADD_ADDRESS)
    }

    override fun editAddress() {
        mAnalytics?.track(TrackingEvents.EventsKeys.EDIT_ADDRESS)
    }

    override fun removeAddress() {
        mAnalytics?.track(TrackingEvents.EventsKeys.REMOVE_ADDRESS)
    }

    override fun addToCart(sku: String, amount: Long, addToCartType: TrackingEvents.AddToCartType) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.ADD_TO_CART,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                        put(TrackingEvents.ParamsKeys.AMOUNT, amount)
                        put(TrackingEvents.ParamsKeys.ADD_TO_CART_TYPE, addToCartType.value)
                    })
        }
    }

    override fun removeFromCart(sku: String, amount: Long) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.REMOVE_FROM_CART,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                        put(TrackingEvents.ParamsKeys.AMOUNT, amount)
                    })
        }
    }

    override fun startCheckout(basketValue: Long) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.START_CHECKOUT,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.BASKET_VALUE, basketValue)
                    })
        }
    }

    override fun purchase(amount: Long, paymentType: TrackingEvents.PaymentType, succeed: Boolean) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.PURCHASE,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.AMOUNT, amount)
                        put(TrackingEvents.ParamsKeys.PAYMENT_TYPE, paymentType.value)
                        put(TrackingEvents.ParamsKeys.SUCCEED, succeed)
                    })
        }
    }

    override fun cancelOrder() {
        mAnalytics?.track(TrackingEvents.EventsKeys.CANCEL_ORDER)
    }

    override fun conductSurvey() {
        mAnalytics?.track(TrackingEvents.EventsKeys.CONDUCT_SURVEY)
    }

    override fun openApp(openAppType: TrackingEvents.OpenAppType) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.OPEN_APP,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.OPEN_APP_TYPE, openAppType.value)
                    })
        }
    }

    override fun inviteFriends() {
        mAnalytics?.track(TrackingEvents.EventsKeys.INVITE_FRIENDS)
    }

    override fun search(query: String) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.SEARCH,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.QUERY, query)
                    })
        }
    }

    override fun contentView(sku: String, category: String) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.CONTENT_VIEW,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                        put(TrackingEvents.ParamsKeys.CATEGORY, category)
                    })
        }
    }

    override fun share(sku: String) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.SHARE,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                    })
        }
    }

    override fun addToWishList(sku: String) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.ADD_TO_WISH_LIST,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                    })
        }
    }

    override fun removeFromWishList(sku: String) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.REMOVE_FROM_WISH_LIST,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                    })
        }
    }

    override fun addReview(sku: String) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.ADD_REVIEW,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                    })
        }
    }

    override fun notifyMe(sku: String) {
        mAnalytics?.run {
            track(TrackingEvents.EventsKeys.NOTIFY_ME,
                    HashMap<String, Any>().apply {
                        put(TrackingEvents.ParamsKeys.SKU, sku)
                    })
        }
    }

    override fun openHomeScreen() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openProductDetailsScreen() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openProductListScreen() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openCartScreen() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchFiltered() {
        mAnalytics?.track(TrackingEvents.EventsKeys.SEARCH_FILTERED)
    }

    override fun searchBarSearched() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun purchaseBehaviour() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun itemTapped() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun catalogViewChanged() {
        mAnalytics?.track(TrackingEvents.EventsKeys.CATALOG_VIEW_CHANGED)
    }

    override fun catalogSortChanged() {
        mAnalytics?.track(TrackingEvents.EventsKeys.CATALOG_SORT_CHANGED)
    }

    override fun callToOrderTapped() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buyNowTapped() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchSuggestionTapped() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}