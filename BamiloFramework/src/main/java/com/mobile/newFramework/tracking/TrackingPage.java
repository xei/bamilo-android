package com.mobile.newFramework.tracking;

import com.mobile.framework.R;

/**
 * @author nunocastro
 * @modified sergiopereira
 */
public enum TrackingPage {


    HOME(R.string.ghomepage),
    PRODUCT_LIST(R.string.gproductlist),
    PRODUCT_DETAIL(R.string.gproductdetail),
    LOGIN_SIGN_UP(R.string.gsignup),
    FAVORITES(R.string.gfavourites),
    CART(R.string.gshoppingcart),
    REGISTRATION(R.string.gregister),


    PRODUCT_LIST_SORTED(-1),
    CHECKOUT_THANKS(R.string.gcheckoutfinal),
    PRODUCT_DETAIL_LOADED(-1),
    CART_LOADED(-1),
    EMPTY_CART(R.string.gcartempty),
    FILLED_CART(R.string.gcartwithitems),
    CAMPAIGNS(R.string.gcampaignpage),
    RECENTLY_VIEWED(R.string.grecentlyviewed),
    NEWSLETTER_SUBS(R.string.gnewslettersubs),
    RECENT_SEARCHES(R.string.grecentsearches),
    ORDER_CONFIRM(R.string.gtrackconfirmorder),
    PAYMENT_SCREEN(R.string.gtrackpayment),
    NEW_ADDRESS(R.string.gtrackaddaddress),
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
