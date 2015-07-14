package com.mobile.utils;

import android.text.TextUtils;

/**
 * Defines the navigation action enum.
 * 
 * @author ManuelSilva
 * 
 */
public enum NavigationAction {
    LoginOut("loginout"),
    Basket("cartdata"),
    // Brands("brands"),
    // Search("search"),
    Categories("categories"),
    Home("shop"),
    MyAccount("account"),
    MyAccountUserData("user_data"),
    MyAccountMyAddresses("addresses"),
    MyAccountEmailNotification("email_notification"),
    MyProfile("profile"),
    Favorite("favourite"),
    RecentSearch("recentsearch"),
    RecentlyView("recentlyview"),
    // Sales("sales"),
    // Configurator("configurator"),
    Products("products"),
    Product("product"),
    Country("language"),
    MyOrders("trackingorder"),
    Unknown("unknown"),
    Checkout("checkout"),
    Offers("offers"),
    Seller("seller"),
    ForgotPassword("forgotpassword"),
    Terms("terms");

	private final String action;

	/**
		 * 
		 */
	private NavigationAction(String action) {
		this.action = action;
	}

	public static NavigationAction byAction(String action) {
	    if(TextUtils.isEmpty(action)) {
	        return Unknown;
	    }
		for (NavigationAction navigationAction : values()) {
			if (navigationAction.action.equalsIgnoreCase(action)) {
				return navigationAction;
			}
		}
		return Unknown;

	}
}
