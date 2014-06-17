package pt.rocket.utils;

import android.text.TextUtils;

/**
 * Defines the navigation action enum.
 * 
 * @author ManuelSilva
 * 
 */
public enum NavigationAction {
	
	LoginOut("loginout"), Basket("cartdata"), Brands("brands"), Search("search"), Categories(
			"categories"), Home("shop"), MyAccount("account"), MyProfile("profile"), Favourite(
			"favourite"), RecentSearch("recentsearch"), RecentlyView("recentlyview"),Sales("sales"), 
			Configurator("configurator"), Products("products"), Settings("settings"), Country(
			"language"), TrackOrder("trackingorder"), Unknown("unknown"), Checkout("checkout");

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
