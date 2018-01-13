package com.mobile.service.tracking;

import com.mobile.framework.R;

/**
 * @author nunocastro
 * @modified sergiopereira
 */
public enum TrackingPage {


    HOME(R.string.ghomepage),
    MY_BAMILO(R.string.gMyBamilo),
    PRODUCT_LIST(R.string.gproductlist),
    PDV(R.string.gPDV),
    PRODUCT_DETAIL(R.string.gproductdetail),
    LOGIN_SIGN_UP(R.string.gsignup),
    FAVORITES(R.string.gfavourites),
    CART(R.string.gshoppingcart),
    REGISTRATION(R.string.gregister),
    MY_ORDERS(R.string.gOrderList),
    ORDER_LIST(R.string.gOrderList),
    ORDER_DETAIL(R.string.gOrderDetail),
    PROFILE_CREATE_ADDRESS(R.string.gProfileCreateAddress),
    CATEGORIES(R.string.gCategories),
    CATEGORY_MENU(R.string.gCategoryMenu),
    CATALOG(R.string.gcatalog),
    FILTER_VIEW(R.string.gFilterView),
    MY_ADDRESSES(R.string.gMyAddresses),
    CHECKOUT_ADDRESSES(R.string.gCheckoutAddresses),
    USER_LOGIN(R.string.gUserLogin),
    USER_SIGNUP(R.string.gUserSignup),
    HOME_PAGE(R.string.gHomePage),
    EDIT_ADDRESS(R.string.gEditAddress),
    FORGOT_PASSWORD(R.string.gForgotPassword),
    USER_PROFILE(R.string.gUserProfile),
    RECENTLY_VIEWED_PAGE(R.string.gRecentlyviewedPage),
    WRITE_REVIEW(R.string.gWriteReview),
    WISH_LIST(R.string.gWishList),
    CHECKOUT_CONFIRMATION(R.string.gCheckoutConfirmation),
    CHECKOUT_PAYMENT_FAILURE(R.string.gCheckoutPaymentFailure),
    CHECKOUT_FINISH(R.string.gCheckoutFinish),
    CHECKOUT_PAYMENT_METHOD(R.string.gCheckoutPaymentMethod),


    PRODUCT_LIST_SORTED(-1),
    CHECKOUT_THANKS(R.string.gcheckoutfinal),
    PRODUCT_DETAIL_LOADED(-1),
    CART_LOADED(-1),
    EMPTY_CART(R.string.gcartempty),
    FILLED_CART(R.string.gcartwithitems),
    CAMPAIGNS(R.string.gcampaignpage),
    CAMPAIGN(R.string.gCampaign),
    CAMPAIGN_PAGE(R.string.gCampaignPage),
    STATIC_PAGE(R.string.gStaticPage),
    RECENTLY_VIEWED(R.string.grecentlyviewed),
    NEWSLETTER_SUBS(R.string.gnewslettersubs),
    RECENT_SEARCHES(R.string.grecentsearches),
    ORDER_CONFIRM(R.string.gtrackconfirmorder),
    PAYMENT_SCREEN(R.string.gtrackpayment),
    NEW_ADDRESS(R.string.gtracknewaddress),
    ADD_ADDRESS(R.string.gtrackaddaddress),
    ADDRESS_SCREEN(R.string.gtrackaddress),
    MYORDERS_SCREEN(R.string.gtrackmyorders);
    /**
     * ############## CLASS ##############
     */

    private int mNameId;

    /**
     * Constructor
     *
     * @author sergiopereira
     */
    TrackingPage(int name) {
        this.mNameId = name;
    }

    /**
     * Get the string id for page
     *
     * @return int
     * @author sergiopereira
     */
    public int getName() {
        return mNameId;
    }

}
