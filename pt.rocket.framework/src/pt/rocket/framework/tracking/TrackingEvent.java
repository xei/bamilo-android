/**
 * 
 */
package pt.rocket.framework.tracking;

import pt.rocket.framework.R;

/**
 * Represent all events available.
 * @author sergiopereira
 */
public enum TrackingEvent {
	
	SEARCH_SUGGESTIONS(R.string.gaccount, R.string.gsearchsuggestions),
	
	ADD_TO_WISHLIST(R.string.gfavourites, R.string.gaddtofavorites),
	
	ADD_TO_CART(R.string.gcatalog, R.string.gaddtocart),
	
	REMOVE_FROM_WISHLIST(R.string.gfavourites, R.string.gremovefromfavorites),
	
	SUBSCRIBE_NEWSLETTER(R.string.gaccount, R.string.gremovefromfavorites),
	
	UNSUBSCRIBE_NEWSLETTER(R.string.gaccount, R.string.gunsubscribenewsletter),
	
	SHOW_CAMPAIGN(R.string.ghomepage, R.string.gcampaign),
	
	AB_MENU_SIGN_IN(R.string.goverflowmenu, R.string.gsignin),
	
	AB_MENU_FAVORITE(R.string.goverflowmenu, R.string.gfavourites),
	
	AB_MENU_RECENT_SEARCHES(R.string.goverflowmenu, R.string.grecentsearches),
	
	AB_MENU_RECENTLY_VIEW(R.string.goverflowmenu, R.string.grecentlyviewed),
	
	AB_MENU_MY_ACCOUNT(R.string.goverflowmenu, R.string.gaccount),
	
	AB_MENU_TRACK_ORDER(R.string.goverflowmenu, R.string.gtrackorder),
	
	CATALOG_SWITCH_LAYOUT(R.string.gcatalog, R.string.gcatalogswitchlayout),
	
	CATALOG_FILTER(R.string.gcatalog, R.string.gfilters),
	
	CHECKOUT_STARTED(R.string.gcheckout, R.string.gcheckoutstart),
	
	CHECKOUT_FINISHED(R.string.gcheckout, R.string.gfinished),
	
	CHECKOUT_CONTINUE(R.string.gcheckout, R.string.gcheckoutcontinueshopping),
	
	SIGNUP(R.string.gsignup, R.string.gsignup),
	
	SEARCH(R.string.gcatalog, R.string.gsearch)
	
	;
	
	/**
	 * ############## CLASS ##############
	 */
	
	private int mCategoryId;
	
    private int mActionId;
    
    /**
     * Contstrutor
     * @param category
     * @param action
     * @author sergiopereira
     */
    TrackingEvent(int category, int action) {
		this.mCategoryId = category;
		this.mActionId = action;
	}
    
    /**
     * Get the string id for category
     * @return int
     * @author sergiopereira
     */
    public int getCategory() {
		return mCategoryId;
	}
    
    /**
     * Get the string id for action
     * @return int
     * @author sergiopereira
     */
    public int getAction() {
		return mActionId;
	}
}
