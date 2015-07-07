/**
 * 
 */
package com.mobile.newFramework.tracking;

import com.mobile.framework.R;

/**
 * @author nunocastro
 * @modified sergiopereira
 */
public enum TrackingPage {
	
	/**
	 * ############## ENUMS ##############
	 */
	
    NAVIGATION(R.string.gnavigation),
    
    PRODUCT_LIST(R.string.gproductlist),
    
    PRODUCT_LIST_SORTED(-1),
        
    CHECKOUT_THANKS(R.string.gcheckoutfinal),
    
    HOME(R.string.ghomepage),
    
    PRODUCT_DETAIL(R.string.gproductdetail),
    
    PRODUCT_DETAIL_LOADED(-1),
    
    CART(R.string.gshoppingcart),
    
    CART_LOADED(-1),
    
    EMPTY_CART(R.string.gcartempty),
    
    FILLED_CART(R.string.gcartwithitems),
    
    LOGIN_SIGNUP(R.string.gsignup),
    
    FAVORITES(R.string.gfavourites),
    
    REGISTRATION(R.string.gregister),
    
    CAMPAIGNS(R.string.gcampaignpage),
    
    RECENTLY_VIEWED(R.string.grecentlyviewed),
    
    NEWSLETTER_SUBS(R.string.gnewslettersubs),
    
    RECENT_SEARCHES(R.string.grecentsearches),
    
    ORDER_CONFIRM(R.string.gtrackconfirmorder),
    
    PAYMENT_SCREEN(R.string.gtrackpayment),
    
    NEW_ADDRESS(R.string.gtrackaddaddress),
    
    ADDRESS_SCREEN(R.string.gtrackaddress),
    
    SEARCH_SCREEN(R.string.gtracksearch),
    
    MYORDERS_SCREEN(R.string.gtrackmyorders)
    
    ;
    
	/**
	 * ############## CLASS ##############
	 */
    
	private int mNameId;
    
	/**
	 * Constructor
	 * @param name
	 * @author sergiopereira
	 */
    TrackingPage(int name) {
		this.mNameId = name;
	}

    /**
     * Get the string id for page
     * @return int
     * @author sergiopereira
     */
    public int getName() {
		return mNameId;
	}
    
}
