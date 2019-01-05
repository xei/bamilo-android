package com.bamilo.android.appmodule.modernbamilo.tracking.platformbasedimplementation

import android.content.Context
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

    override fun register(userId: String?, emailAddress: String?, phoneNumber: String?, registrationType: TrackingEvents.RegistrationType, succeed: Boolean) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.SIGN_UP, Bundle().apply {
                userId?.run { putString(TrackingEvents.ParamsKeys.USER_ID, userId) }
                emailAddress.run { putString(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
                phoneNumber.run { putString(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
                putString(TrackingEvents.ParamsKeys.REGISTRATION_TYPE, registrationType.value)
                putBoolean(FirebaseAnalytics.Param.SUCCESS, succeed)
            })

            // Set User's Properties:
            // at first: register properties in panel
//            mFirebaseAnalytics.setUserProperty("favorite_food", mFavoriteFood);
//            mFirebaseAnalytics.setUserProperty("gender", mGender);
        }
    }

    override fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginType: TrackingEvents.LoginType, succeed: Boolean) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                userId?.run { putString(TrackingEvents.ParamsKeys.USER_ID, userId) }
                emailAddress.run { putString(TrackingEvents.ParamsKeys.USER_EMAIL_ADDRESS, emailAddress) }
                phoneNumber.run { putString(TrackingEvents.ParamsKeys.USER_PHONE_NUMBER, phoneNumber) }
                putString(TrackingEvents.ParamsKeys.LOGIN_TYPE, loginType.value)
                putBoolean(FirebaseAnalytics.Param.SUCCESS, succeed)
            })
        }
    }

    override fun logout() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.LOGOUT, null)
        }
    }

    override fun editProfile() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.EDIT_PROFILE, null)
        }
    }

    override fun addAddress() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.ADD_ADDRESS, null)
        }
    }

    override fun editAddress() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.EDIT_ADDRESS, null)
        }
    }

    override fun removeAddress() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.REMOVE_ADDRESS, null)
        }
    }

    override fun addToCart(sku: String, amount: Long, addToCartType: TrackingEvents.AddToCartType) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.ADD_TO_CART, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
                putLong(TrackingEvents.ParamsKeys.AMOUNT, amount)
                putString(TrackingEvents.ParamsKeys.ADD_TO_CART_TYPE, addToCartType.value)
            })
        }
    }

    override fun removeFromCart(sku: String, amount: Long) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
                putLong(TrackingEvents.ParamsKeys.AMOUNT, amount)
            })
        }
    }

    override fun startCheckout(basketValue: Long) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, Bundle().apply {
                putLong(TrackingEvents.ParamsKeys.BASKET_VALUE, basketValue)
            })
        }
    }

    override fun purchase(amount: Long, paymentType: TrackingEvents.PaymentType, succeed: Boolean) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, Bundle().apply {
                putLong(TrackingEvents.ParamsKeys.AMOUNT, amount)
                putString(TrackingEvents.ParamsKeys.PAYMENT_TYPE, paymentType.value)
                putBoolean(FirebaseAnalytics.Param.SUCCESS, succeed)
            })
        }
    }

    override fun cancelOrder() {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.PURCHASE_REFUND, null)
        }
    }

    override fun conductSurvey() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.CONDUCT_SURVEY, null)
        }
    }

    override fun openApp(openAppType: TrackingEvents.OpenAppType) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle().apply {
                putString(TrackingEvents.ParamsKeys.OPEN_APP_TYPE, openAppType.value)
            })
        }
    }

    override fun inviteFriends() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.INVITE_FRIENDS, null)
        }
    }

    override fun search(query: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
                putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
            })
        }
    }

    override fun contentView(sku: String, category: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.VIEW_ITEM, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
                putString(TrackingEvents.ParamsKeys.CATEGORY, category)
            })
        }
    }

    override fun share(sku: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.SHARE, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
            })
        }
    }

    override fun addToWishList(sku: String) {
        mFirebaseAnalytics?.run {
            logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
            })
        }
    }

    override fun removeFromWishList(sku: String) {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.REMOVE_FROM_WISH_LIST, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
            })
        }
    }

    override fun addReview(sku: String) {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.ADD_REVIEW, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
            })
        }
    }

    override fun notifyMe(sku: String) {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.NOTIFY_ME, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, sku)
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
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.SEARCH_FILTERED, null)
        }
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
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.CATALOG_VIEW_CHANGED, null)
        }
    }

    override fun catalogSortChanged() {
        mFirebaseAnalytics?.run {
            logEvent(TrackingEvents.EventsKeys.CATALOG_SORT_CHANGED, null)
        }
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