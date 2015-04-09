/**
 * 
 */
package com.mobile.framework.tracking;

import com.mobile.framework.R;

/**
 * Represent all events available.
 * @author sergiopereira
 */
public enum TrackingEvent {
	
	SEARCH_SUGGESTIONS(R.string.gaccount, R.string.gsearchsuggestions),
	
	SEARCH(R.string.adjust_token_search, -1),
	
	VIEW_WISHLIST(R.string.adjust_token_fb_view_wishlist, -1),
	
	ADD_TO_WISHLIST(R.string.gfavourites, R.string.gaddtofavorites),
	
	ADD_TO_CART(R.string.gcatalog, R.string.gaddtocart),
	
	REMOVE_FROM_CART(R.string.adjust_token_remove_from_cart, -1),	
	
	REMOVE_FROM_WISHLIST(R.string.gfavourites, R.string.gremovefromfavorites),
	
	SUBSCRIBE_NEWSLETTER(R.string.gaccount, R.string.gsubscribenewsletter),
	
	UNSUBSCRIBE_NEWSLETTER(R.string.gaccount, R.string.gunsubscribenewsletter),
	
	SHOW_CAMPAIGN(R.string.ghomepage, R.string.gcampaign),
	
	SHOW_PRODUCT_DETAIL(R.string.gcatalog, R.string.gpdv),
	
	SHOW_RELATED_PRODUCT_DETAIL(R.string.gproductdetail, R.string.gRelatedProduct),
	
	APP_OPEN(R.string.adjust_token_launch, -1),
	
	SHARE(R.string.adjust_token_social_share, -1),
	
	CALL(R.string.adjust_token_call, -1),
	
	ADD_REVIEW(R.string.adjust_token_product_rate, -1),
	
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
	
	CATALOG_SORT_RATING(R.string.gcatalog, R.string.gCatalogSortBestRating),
	
	CATALOG_SORT_POPULARIRY(R.string.gcatalog, R.string.gCatalogSortPopularity),
	
	CATALOG_SORT_NEW_IN(R.string.gcatalog, R.string.gCatalogSortNewIn),
	
	CATALOG_SORT_PRICE_UP(R.string.gcatalog, R.string.gCatalogSortPriceUp),
	
	CATALOG_SORT_PRICE_DOWN(R.string.gcatalog, R.string.gCatalogSortPriceDown),
	
	CATALOG_SORT_NAME(R.string.gcatalog, R.string.gCatalogSortName),
	
	CATALOG_SORT_BRAND(R.string.gcatalog, R.string.gCatalogSortBrand),
	
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
	
	CHECKOUT_STEP_EXTERNAL_PAYMENT(R.string.gNativeCheckout, R.string.gcheckoutExternalPayment),
	
	ADD_BUNDLE_TO_CART(R.string.gcatalog, R.string.gAddBundleToCart),
	
	ADD_OFFER_TO_CART(R.string.gcatalog, R.string.gAddSellerToCart),
	
	SHARE_APP(R.string.gaccount,R.string.gShareApp),

    	ACCOUNT_CREATE_ADDRESS(R.string.gaccount, R.string.gAccountCreateAddress),

	BANNER_CLICK(R.string.gbanner, R.string.gPurchase);
	
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
