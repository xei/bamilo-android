package com.bamilo.android.appmodule.modernbamilo.tracking

import android.content.Context
import android.content.Intent
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
//
            WebEngageEventsTracker.let {
                it.initialize(context)
                add(it)
            }
        }
    }


    /**
     * This method will send [intent] as the install referer to analytics platforms.
     */
    override fun install(intent: Intent) {
        for (observer in mTrackingObservers) {
            observer.install(intent)
        }

        Logger.log(
                StringBuilder("Event \"Install App\" is called.")
                        .append(" | Data: ").append(intent.dataString)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method determines the entry point to the app with [appOpenMethod].
     * Call it when the Main screen appears.
     */
    override fun appOpen(appOpenMethod: TrackingEvents.AppOpenMethod) {
        for (observer in mTrackingObservers) {
            observer.appOpen(appOpenMethod)
        }

        Logger.log(
                StringBuilder("Event \"Open App\" is called.")
                        .append(" | openAppType: ").append(appOpenMethod)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will hit when the user invites someone to install the app.
     */
    override fun inviteFriends() {
        for (observer in mTrackingObservers) {
            observer.inviteFriends()
        }

        Logger.log(
                StringBuilder("Event \"Invite Friends\" is called.").toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will hit while the user signed up successfully.
     */
    override fun signUp(userId: String?, emailAddress: String?, phoneNumber: String?, signUpMethod: TrackingEvents.SignUpMethod) {
        for (observer in mTrackingObservers) {
            observer.signUp(userId, emailAddress, phoneNumber, signUpMethod)
        }

        Logger.log(
                StringBuilder("Event \"Sign Up\" is called.")
                        .append(" | userId: ").append(userId)
                        .append(" | emailAddress: ").append(emailAddress)
                        .append(" | phoneNumber: ").append(phoneNumber)
                        .append(" | signUpMethod: ").append(signUpMethod.value)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will hit while the user logged in successfully.
     */
    override fun login(userId: String?, emailAddress: String?, phoneNumber: String?, loginMethod: TrackingEvents.LoginMethod) {
        for (observer in mTrackingObservers) {
            observer.login(userId, emailAddress, phoneNumber, loginMethod)
        }

        Logger.log(
                StringBuilder("Event \"Login\" is called.")
                        .append(" | userId: ").append(userId)
                        .append(" | loginMethod: ").append(loginMethod.value)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will hit while the user logged out successfully.
     */
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

    /**
     * This method will call when the user adds some numbers of a product to their cart successfully.
     * [id]
     * [sku]
     * [title] Is the main title of the added product.
     * [categoryId]
     * [categoryUrl]
     * [amount] Is the final amount of each number of added product in Tomans.
     * [quantity] Is the quantity of product which you added.
     */
    override fun addToCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int) {
        for (observer in mTrackingObservers) {
            observer.addToCart(id, sku, title, categoryId, categoryUrl, amount, quantity)
        }

        Logger.log(
                StringBuilder("Event \"Add To Cart\" is called.")
                        .append(" | id: ").append(id)
                        .append(" | sku: ").append(sku)
                        .append(" | title: ").append(title)
                        .append(" | categoryId: ").append(categoryId)
                        .append(" | categoryUrl: ").append(categoryUrl)
                        .append(" | amount: ").append(amount)
                        .append(" | quantity: ").append(quantity)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will call when the user remove some numbers of a product from their cart successfully.
     * [id]
     * [sku]
     * [title] Is the main title of the added product.
     * [categoryId]
     * [categoryUrl]
     * [amount] Is the final amount of each number of added product in Tomans.
     * [quantity] Is the quantity of product which you added.
     */
    override fun removeFromCart(id: String, sku: String, title: String, categoryId: String, categoryUrl: String, amount: Long, quantity: Int) {
        for (observer in mTrackingObservers) {
            observer.removeFromCart(id, sku, title, categoryId, categoryUrl, amount, quantity)
        }

        Logger.log(
                StringBuilder("Event \"Remove From Cart\" is called.")
                        .append(" | id: ").append(id)
                        .append(" | sku: ").append(sku)
                        .append(" | title: ").append(title)
                        .append(" | categoryId: ").append(categoryId)
                        .append(" | categoryUrl: ").append(categoryUrl)
                        .append(" | amount: ").append(amount)
                        .append(" | quantity: ").append(quantity)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user starts a purchase process.
     * * [basketValue] is the total amount that user have to pay.
     * [numberOfBasketItems] is the number of items the user is going to buy.
     */
    override fun beginCheckout(basketValue: Long, numberOfBasketItems: Int) {
        for (observer in mTrackingObservers) {
            observer.beginCheckout(basketValue, numberOfBasketItems)
        }

        Logger.log(
                StringBuilder("Event \"Start Checkout\" is called.")
                        .append(" | basketValue: ").append(basketValue)
                        .append(" | numberOfBasketItems: ").append(numberOfBasketItems)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user submits their purchase.
     * [value] is the total amount that user has payed or has supposed to pay.
     * [numberOfItems] the total number of items that user submitted to buy.
     * [coupon] the coupon code (voucher) that user applied to their basket during purchase process.
     * [transactionId] is the order number.
     * [paymentMethod] can be MPG (Mobile Payment Gateway), IPG (Internet Payment Gateway) or COD (Cash on Delivery)
     * [cityName] is the name of city where order is submitted.
     */
    override fun purchase(value: Long, numberOfItems: Int, coupon: String?, transactionId: String, paymentMethod: TrackingEvents.PaymentMethod, cityName: String) {
        for (observer in mTrackingObservers) {
            observer.purchase(value, numberOfItems, coupon, transactionId, paymentMethod, cityName)
        }

        Logger.log(
                StringBuilder("Event \"Purchase\" is called.")
                        .append(" | value: ").append(value)
                        .append(" | numberOfItems: ").append(numberOfItems)
                        .append(" | coupon: ").append(coupon)
                        .append(" | transactionId: ").append(transactionId)
                        .append(" | paymentMethod: ").append(paymentMethod.value)
                        .append(" | cityName: ").append(cityName)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be called when the user submits a purchase refund.
     * [transaction_id] is the order id.
     * [value] is the total amount of an order.
     * [quantity] is the number of items in the order.
     */
    override fun cancelOrder(transaction_id: String, value: Long, quantity: Int) {
        for (observer in mTrackingObservers) {
            observer.cancelOrder(transaction_id, value, quantity)
        }

        Logger.log(
                StringBuilder("Event \"Cancel Order\" is called.")
                        .append(" | transaction_id: ").append(transaction_id)
                        .append(" | value: ").append(value)
                        .append(" | quantity: ").append(quantity)
                        .toString(),
                TAG_DEBUG
        )
    }

//    override fun conductSurvey() {
//        for (observer in mTrackingObservers) {
//            observer.conductSurvey()
//        }
//
//        Logger.log(
//                StringBuilder("Event \"Conduct Survey\" is called.").toString(),
//                TAG_DEBUG
//        )
//    }

    /**
     * This method will be called when the user searches for a query.
     * [searchTerm] is the searched query.
     */
    override fun search(searchTerm: String) {
        for (observer in mTrackingObservers) {
            observer.search(searchTerm)
        }

        Logger.log(
                StringBuilder("Event \"Search\" is called.")
                        .append(" | searchTerm: ").append(searchTerm)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked whe the user tries to change the sorting in the product list screen.
     * [sortingMethodKey] is the key we send to API to sort the list properly.
     */
    override fun sortProductsList(sortingMethodKey: String) {
        for (observer in mTrackingObservers) {
            observer.sortProductsList(sortingMethodKey)
        }

        Logger.log(
                StringBuilder("Event \"Sort Products List\" is called.")
                        .append(" | sortingMethodKey: ").append(sortingMethodKey)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user opens the product details screen to check a product out.
     * [id] is product id.
     * [sku] is sku string of the product.
     * [title] is the main title of the product.
     * [amount] is the final price of the product.
     * [categoryId] is the ID of the category which the product belongs to.
     * [categoryUrl] is the category URL of the category which the product belongs to.
     * [brandId] is the id of brand which the product belongs to.
     * [brandTitle] is the brand title the product belongs to.
     */
    override fun viewProduct(id: String, sku: String, title: String, amount: Long, categoryId: String, categoryUrl: String, brandId: String?, brandTitle: String?) {
        for (observer in mTrackingObservers) {
            observer.viewProduct(id, sku, title, amount, categoryId, categoryUrl, brandId, brandTitle)
        }

        Logger.log(
                StringBuilder("Event \"View Product\" is called.")
                        .append(" | id: ").append(id)
                        .append(" | sku: ").append(sku)
                        .append(" | title: ").append(title)
                        .append(" | amount: ").append(amount)
                        .append(" | categoryId: ").append(categoryId)
                        .append(" | categoryUrl: ").append(categoryUrl)
                        .append(" | brandId: ").append(brandId)
                        .append(" | brandTitle: ").append(brandTitle)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will inform the recommendation system about user interaction with Gallery of products.
     * So no need to implement it in another platforms.
     * [id] is the product id
     */
    override fun viewProductGallery(id: String) {
        for (observer in mTrackingObservers) {
            observer.viewProductGallery(id)
        }

        Logger.log(
                StringBuilder("Event \"View Product Gallery\" is called.")
                        .append(" | id: ").append(id)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user tries to share a product in social networks.
     * [id] is the product id
     */
    override fun shareProduct(id: String) {
        for (observer in mTrackingObservers) {
            observer.shareProduct(id)
        }

        Logger.log(
                StringBuilder("Event \"Share Product\" is called.")
                        .append(" | id: ").append(id)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user adds some products to their wish list successfully.
     * based on Wikipedia's definition, a wish list is an itemization of goods that the user desires to buy.
     * [id] is desired product id.
     * [title] is the main title of the desired product.
     * [amount] is the price of each number of the desired product.
     * [categoryId] is the category ID of the desired product.
     * [quantity] is the number of items the user is going to buy.
     */
    override fun addToWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int) {
        for (observer in mTrackingObservers) {
            observer.addToWishList(id, title, amount, categoryId, quantity)
        }

        Logger.log(
                StringBuilder("Event \"Add To WishList\" is called.")
                        .append(" | id: ").append(id)
                        .append(" | title: ").append(title)
                        .append(" | amount: ").append(amount)
                        .append(" | categoryId: ").append(categoryId)
                        .append(" | quantity: ").append(quantity)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user removes some products from their wish list successfully.
     * based on Wikipedia's definition, a wish list is an itemization of goods that the user desires to buy.
     * [id] is removed product id.
     * [title] is the main title of the removed product.
     * [amount] is the price of each number of the removed product.
     * [categoryId] is the category ID of the removed product.
     * [quantity] is the number of items the user has been removing.
     */
    override fun removeFromWishList(id: String, title: String, amount: Long, categoryId: String, quantity: Int) {
        for (observer in mTrackingObservers) {
            observer.removeFromWishList(id, title, amount, categoryId, quantity)
        }

        Logger.log(
                StringBuilder("Event \"Remove From WishList\" is called.")
                        .append(" | id: ").append(id)
                        .append(" | title: ").append(title)
                        .append(" | amount: ").append(amount)
                        .append(" | categoryId: ").append(categoryId)
                        .append(" | quantity: ").append(quantity)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user submits a review successfully.
     * [id] is the product id.
     * [title] is the main title of the product.
     * [amount] is the price of the product.
     * [categoryId] is the ID of the category which product belongs to.
     */
    override fun addProductReview(id: String, title: String, amount: Long, categoryId: String) {
        for (observer in mTrackingObservers) {
            observer.addProductReview(id, title, amount, categoryId)
        }

        Logger.log(
                StringBuilder("Event \"Add Review\" is called.")
                        .append(" | id: ").append(id)
                        .append(" | title: ").append(title)
                        .append(" | amount: ").append(amount)
                        .append(" | categoryId: ").append(categoryId)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method will be invoked when the user subscribes to notify when the product comes to stock back.
     * [id] is the product id
     * [title] is the main title of the product.
     * [categoryId] is the id of the category which the product belongs to.
     */
    override fun notifyMe(id: String, title: String, categoryId: String) {
        for (observer in mTrackingObservers) {
            observer.notifyMe(id, title, categoryId)
        }

        Logger.log(
                StringBuilder("Event \"Notify Me\" is called.")
                        .append(" | id: ").append(id)
                        .append(" | title: ").append(title)
                        .append(" | categoryId: ").append(categoryId)
                        .toString(),
                TAG_DEBUG
        )
    }

    /**
     * This method is a temporary event call to track an issue reported by Product department.
     */
    override fun failRequest(request: String, errorCode: Int, errorMessage: String, ipAddress: String, connectionMethod: String, operatorName: String, vpn: Boolean, apiLevel: Int, apiVersion: String) {
        for (observer in mTrackingObservers) {
            observer.failRequest(request, errorCode, errorMessage, ipAddress, connectionMethod, operatorName, vpn, apiLevel, apiVersion)
        }
    }

}