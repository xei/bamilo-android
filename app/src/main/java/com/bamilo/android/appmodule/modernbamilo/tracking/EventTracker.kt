package com.bamilo.android.appmodule.modernbamilo.tracking

import android.content.Context
import com.bamilo.android.appmodule.modernbamilo.tracking.platformbasedimplementation.FirebaseEventsTracker
import com.bamilo.android.appmodule.modernbamilo.tracking.platformbasedimplementation.WebEngageEventsTracker
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger

const val TAG_DEBUG = "EventTracker"

/**
 * This object is an observable singleton that you can call it's methods to notify
 * all the analytics platforms automatically.
 */
object EventTracker : TrackingEvents {

    private val mTrackingObservers = ArrayList<TrackingEvents>()

    override fun initialize(context: Context) {
        mTrackingObservers.run {
            FirebaseEventsTracker.let {
                it.initialize(context)
                add(it)
            }

//            AdjustEventsTracker.let {
//                it.initialize(context)
//                add(it)
//            }
//
            WebEngageEventsTracker.let {
                it.initialize(context)
                add(it)
            }
        }
    }

    override fun register(userId: String?, emailAddress: String?, phoneNumber: String?, registrationType: TrackingEvents.RegistrationType, succeed: Boolean) {
        for (observer in mTrackingObservers) {
            observer.register(userId, emailAddress, phoneNumber, registrationType, succeed)
        }

        Logger.log(
                StringBuilder("Event \"Register\" is called.")
                        .append(" | userId: ").append(userId)
                        .append(" | registrationType: ").append(registrationType.value)
                        .append(" | succeed: ").append(succeed.toString())
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginType: TrackingEvents.LoginType, succeed: Boolean) {
        for (observer in mTrackingObservers) {
            observer.login(userId, emailAddress, phoneNumber, loginType, succeed)
        }

        Logger.log(
                StringBuilder("Event \"Login\" is called.")
                        .append(" | userId: ").append(userId)
                        .append(" | loginType: ").append(loginType.value)
                        .append(" | succeed: ").append(succeed.toString())
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun logout() {
        for (observer in mTrackingObservers) {
            observer.logout()
        }

        Logger.log(
                StringBuilder("Event \"Logout\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun editProfile() {
        for (observer in mTrackingObservers) {
            observer.editProfile()
        }

        Logger.log(
                StringBuilder("Event \"Edit Profile\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun addAddress() {
        for (observer in mTrackingObservers) {
            observer.addAddress()
        }

        Logger.log(
                StringBuilder("Event \"Add Address\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun editAddress() {
        for (observer in mTrackingObservers) {
            observer.editAddress()
        }

        Logger.log(
                StringBuilder("Event \"Edit Address\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun removeAddress() {
        for (observer in mTrackingObservers) {
            observer.removeAddress()
        }

        Logger.log(
                StringBuilder("Event \"Remove Address\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun addToCart(sku: String, amount: Long, addToCartType: TrackingEvents.AddToCartType) {
        for (observer in mTrackingObservers) {
            observer.addToCart(sku, amount, addToCartType)
        }

        Logger.log(
                StringBuilder("Event \"Add To Cart\" is called.")
                        .append(" |sku: ").append(sku)
                        .append(" |amount: ").append(amount)
                        .append(" |addToCartType: ").append(addToCartType.value)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun removeFromCart(sku: String, amount: Long) {
        for (observer in mTrackingObservers) {
            observer.removeFromCart(sku, amount)
        }

        Logger.log(
                StringBuilder("Event \"Remove From Cart\" is called.")
                        .append(" | sku: ").append(sku)
                        .append(" | amount: ").append(amount)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun startCheckout(basketValue: Long) {
        for (observer in mTrackingObservers) {
            observer.startCheckout(basketValue)
        }

        Logger.log(
                StringBuilder("Event \"Start Checkout\" is called.")
                        .append(" | basketValue: ").append(basketValue)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun purchase(amount: Long, paymentType: TrackingEvents.PaymentType, succeed: Boolean) {
        for (observer in mTrackingObservers) {
            observer.purchase(amount, paymentType, succeed)
        }

        Logger.log(
                StringBuilder("Event \"Purchase\" is called.")
                        .append(" | amount: ").append(amount)
                        .append(" | paymentType: ").append(paymentType.value)
                        .append(" | succeed: ").append(succeed)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun cancelOrder() {
        for (observer in mTrackingObservers) {
            observer.cancelOrder()
        }

        Logger.log(
                StringBuilder("Event \"Cancel Order\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun conductSurvey() {
        for (observer in mTrackingObservers) {
            observer.conductSurvey()
        }

        Logger.log(
                StringBuilder("Event \"Conduct Survey\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun openApp(openAppType: TrackingEvents.OpenAppType) {
        for (observer in mTrackingObservers) {
            observer.openApp(openAppType)
        }

        Logger.log(
                StringBuilder("Event \"Open App\" is called.")
                        .append(" | openAppType: ").append(openAppType)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun inviteFriends() {
        for (observer in mTrackingObservers) {
            observer.inviteFriends()
        }

        Logger.log(
                StringBuilder("Event \"Invite Friends\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun search(query: String) {
        for (observer in mTrackingObservers) {
            observer.search(query)
        }

        Logger.log(
                StringBuilder("Event \"Search\" is called.")
                        .append(" | query: ").append(query)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun contentView(sku: String, category: String) {
        for (observer in mTrackingObservers) {
            observer.contentView(sku, category)
        }

        Logger.log(
                StringBuilder("Event \"Content View\" is called.")
                        .append(" | sku: ").append(sku)
                        .append(" | category: ").append(category)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun share(sku: String) {
        for (observer in mTrackingObservers) {
            observer.share(sku)
        }

        Logger.log(
                StringBuilder("Event \"Share\" is called.")
                        .append(" | sku: ").append(sku)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun addToWishList(sku: String) {
        for (observer in mTrackingObservers) {
            observer.addToWishList(sku)
        }

        Logger.log(
                StringBuilder("Event \"Add To WishList\" is called.")
                        .append(" | sku: ").append(sku)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun removeFromWishList(sku: String) {
        for (observer in mTrackingObservers) {
            observer.removeFromWishList(sku)
        }

        Logger.log(
                StringBuilder("Event \"Remove From WishList\" is called.")
                        .append(" | sku: ").append(sku)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun addReview(sku: String) {
        for (observer in mTrackingObservers) {
            observer.addReview(sku)
        }

        Logger.log(
                StringBuilder("Event \"Add Review\" is called.")
                        .append(" | sku: ").append(sku)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun notifyMe(sku: String) {
        for (observer in mTrackingObservers) {
            observer.notifyMe(sku)
        }

        Logger.log(
                StringBuilder("Event \"Notify Me\" is called.")
                        .append(" | sku: ").append(sku)
                        .toString(),
                TAG_DEBUG
        )
    }

    override fun openHomeScreen() {
        for (observer in mTrackingObservers) {
            observer.openHomeScreen()
        }

        Logger.log(
                StringBuilder("Event \"Open Home Screen\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun openProductDetailsScreen() {
        for (observer in mTrackingObservers) {
            observer.openProductDetailsScreen()
        }

        Logger.log(
                StringBuilder("Event \"Open ProductDetailsScreen Screen\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun openProductListScreen() {
        for (observer in mTrackingObservers) {
            observer.openProductListScreen()
        }

        Logger.log(
                StringBuilder("Event \"Open ProductList Screen\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun openCartScreen() {
        for (observer in mTrackingObservers) {
            observer.openCartScreen()
        }

        Logger.log(
                StringBuilder("Event \"Open Cart Screen\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun searchFiltered() {
        for (observer in mTrackingObservers) {
            observer.searchFiltered()
        }

        Logger.log(
                StringBuilder("Event \"SearchFiltered\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun searchBarSearched() {
        for (observer in mTrackingObservers) {
            observer.searchBarSearched()
        }

        Logger.log(
                StringBuilder("Event \"SearchBarSearched\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun purchaseBehaviour() {
        for (observer in mTrackingObservers) {
            observer.purchaseBehaviour()
        }

        Logger.log(
                StringBuilder("Event \"PurchaseBehavior\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun itemTapped() {
        for (observer in mTrackingObservers) {
            observer.itemTapped()
        }

        Logger.log(
                StringBuilder("Event \"ItemTapped\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun catalogViewChanged() {
        for (observer in mTrackingObservers) {
            observer.catalogViewChanged()
        }

        Logger.log(
                StringBuilder("Event \"CatalogViewChanged\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun catalogSortChanged() {
        for (observer in mTrackingObservers) {
            observer.catalogSortChanged()
        }

        Logger.log(
                StringBuilder("Event \"CatalogSortChanged\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun callToOrderTapped() {
        for (observer in mTrackingObservers) {
            observer.callToOrderTapped()
        }

        Logger.log(
                StringBuilder("Event \"CallToOrderTapped\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun buyNowTapped() {
        for (observer in mTrackingObservers) {
            observer.buyNowTapped()
        }

        Logger.log(
                StringBuilder("Event \"BuyNowTapped\" is called.").toString(),
                TAG_DEBUG
        )
    }

    override fun searchSuggestionTapped() {
        for (observer in mTrackingObservers) {
            observer.searchSuggestionTapped()
        }

        Logger.log(
                StringBuilder("Event \"SearchSuggestionTapped\" is called.").toString(),
                TAG_DEBUG
        )
    }

}