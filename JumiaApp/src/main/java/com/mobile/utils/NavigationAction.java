package com.mobile.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines the navigation action enum.
 */
public class NavigationAction {

    public static final int LOGIN_OUT = 0;
    public static final int BASKET = 1;
    public static final int CATEGORIES = 2;
    public static final int HOME = 3;
    public static final int MY_ACCOUNT = 4;
    public static final int MY_ACCOUNT_USER_DATA = 5;
    public static final int MY_ACCOUNT_MY_ADDRESSES = 6;
    public static final int MY_ACCOUNT_EMAIL_NOTIFICATION = 7;
    public static final int MY_PROFILE = 8;
    public static final int SAVED = 9;
    public static final int RECENT_SEARCHES = 10;
    public static final int RECENTLY_VIEWED = 11;
    public static final int CATALOG = 12;
    public static final int PRODUCT = 13;
    public static final int COUNTRY = 14;
    public static final int MY_ORDERS = 15;
    public static final int CHECKOUT = 16;
    public static final int OFFERS = 17;
    public static final int SELLER = 18;
    public static final int FORGOT_PASSWORD = 19;
    public static final int TERMS = 20;
    public static final int FILTERS = 21;
    public static final int COMBOS = 22;
    public static final int VARIATIONS = 23;
    public static final int UNKNOWN = 24;

    @IntDef({
            LOGIN_OUT,
            BASKET,
            CATEGORIES,
            HOME,
            MY_ACCOUNT,
            MY_ACCOUNT_USER_DATA,
            MY_ACCOUNT_MY_ADDRESSES,
            MY_ACCOUNT_EMAIL_NOTIFICATION,
            MY_PROFILE,
            SAVED,
            RECENT_SEARCHES,
            RECENTLY_VIEWED,
            CATALOG,
            PRODUCT,
            COUNTRY,
            MY_ORDERS,
            CHECKOUT,
            OFFERS,
            SELLER,
            FORGOT_PASSWORD,
            TERMS,
            FILTERS,
            COMBOS,
            VARIATIONS,
            UNKNOWN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{}

}
