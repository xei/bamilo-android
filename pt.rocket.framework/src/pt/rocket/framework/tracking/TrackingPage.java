/**
 * 
 */
package pt.rocket.framework.tracking;

import pt.rocket.framework.R;

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
    
    CHECKOUT_THANKS(R.string.gcheckoutfinal),
    
    HOME(R.string.ghomepage),
    
    PRODUCT_DETAIL(R.string.gproductdetail),
    
    CART(R.string.gshoppingcart),
    
    EMPTY_CART(R.string.gcartempty),
    
    FILLED_CART(R.string.gcartwithitems),
    
    LOGIN_SIGNUP(R.string.gsignup),
    
    FAVORITES(R.string.gfavourites),
    
    REGISTRATION(R.string.gregister),
    
    CAMPAIGNS(R.string.gcampaignpage),
    
    RECENTLY_VIEWED(R.string.grecentlyviewed),
    
    NEWSLETTER_SUBS(R.string.gnewslettersubs),
    
    RECENT_SEARCHES(R.string.grecentsearches)
    
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
