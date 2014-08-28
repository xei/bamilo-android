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
	
	SHOW_PRODUCT_DETAIL(R.string.gcatalog, R.string.gpdv),
	
	// ### ACTION BAR EVENTS
	
	AB_MENU_SIGN_IN(R.string.goverflowmenu, R.string.gsignin),
	
	AB_MENU_FAVORITE(R.string.goverflowmenu, R.string.gfavourites),
	
	AB_MENU_RECENT_SEARCHES(R.string.goverflowmenu, R.string.grecentsearches),
	
	AB_MENU_RECENTLY_VIEW(R.string.goverflowmenu, R.string.grecentlyviewed),
	
	AB_MENU_MY_ACCOUNT(R.string.goverflowmenu, R.string.gaccount),
	
	AB_MENU_TRACK_ORDER(R.string.goverflowmenu, R.string.gtrackorder),
	
	//	### CATALOG EVENTS
	
	CATALOG_SWITCH_LAYOUT(R.string.gcatalog, R.string.gcatalogswitchlayout),
	
	CATALOG_FILTER(R.string.gcatalog, R.string.gfilters),
	
	CATALOG_SEARCH(R.string.gcatalog, R.string.gsearch),
	
	CATALOG_FROM_CATEGORIES(R.string.gcatalog, R.string.gcategories),
	
	CATALOG_FROM_NAVIGATION(R.string.gcatalog, R.string.gnavigation),
	
	//	### ACCOUNT EVENTS
	
	LOGIN_FB_SUCCESS(R.string.gaccount, R.string.gfacebookloginsuccess),
	
	LOGIN_AUTO_SUCCESS(R.string.gaccount, R.string.gautologinsuccess),
	
	LOGIN_SUCCESS(R.string.gaccount, R.string.gloginsuccess),
	
	LOGIN_FAIL(R.string.gaccount, R.string.gloginfailed),
	
	LOGIN_AUTO_FAIL(R.string.gaccount, R.string.gautologinfailed),
	
	LOGOUT_SUCCESS(R.string.gaccount, R.string.glogoutsuccess),
	
	SIGNUP(R.string.gsignup, R.string.gsignup),
	
	SIGNUP_SUCCESS(R.string.gaccount, R.string.gcreatesuccess),
	
	SIGNUP_FAIL(R.string.gaccount, R.string.gcreatefailed),
	
	// ### NATIVE CHECKOUT EVENTS
	
	CHECKOUT_STARTED(R.string.gcheckout, R.string.gcheckoutstart),
	
	CHECKOUT_FINISHED(R.string.gcheckout, R.string.gfinished),
	
	CHECKOUT_CONTINUE(R.string.gcheckout, R.string.gcheckoutcontinueshopping),
	
	CHECKOUT_STEP_ABOUT_YOU(R.string.gNativeCheckout, R.string.gcheckoutAboutYou),
	
	CHECKOUT_STEP_CREATE_ADDRESS(R.string.gNativeCheckout, R.string.gcheckoutCreateAddress),
	
	CHECKOUT_STEP_EDIT_ADDRESS(R.string.gNativeCheckout, R.string.gcheckoutEditAddress),
	
	CHECKOUT_STEP_ADDRESSES(R.string.gNativeCheckout, R.string.gcheckoutMyAddresses),
	
	CHECKOUT_STEP_QUESTION(R.string.gNativeCheckout, R.string.gcheckoutPollQuestion),
	
	CHECKOUT_STEP_ORDER(R.string.gNativeCheckout, R.string.gcheckoutMyOrder),
	
	CHECKOUT_STEP_SHIPPING(R.string.gNativeCheckout, R.string.gcheckoutShippingMethods),
	
	CHECKOUT_STEP_PAYMENT(R.string.gNativeCheckout, R.string.gcheckoutPaymentMethods),
	
	CHECKOUT_STEP_EXTERNAL_PAYMENT(R.string.gNativeCheckout, R.string.gcheckoutExternalPayment)
	
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
