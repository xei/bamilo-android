package com.mobile.controllers.fragments;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * Enum used to set the fragment types
 *
 * @author sergiopereira
 */
public enum FragmentType {
    UNKNOWN,
    LOGIN,
    ABOUT_US,
    REGISTER,
    MOBILE_VERIFICATION,
    CHANGE_PHONE_NUMBER_FRAGMENT,
    FORGOT_PASSWORD,
    POPULARITY,
    MAIN_ONE_SLIDE_TEASER,
    PRODUCT_TEASER_LIST,
    CATEGORY_TEASER,
    STATIC_BANNER_TEASER,
    PRODUCT_VARIATION,
    PRODUCT_SHOWOFF,
    PRODUCT_SPECIFICATION,
    PRODUCT_BASIC_INFO,
    CATEGORIES,
    HOME,
    SEARCH,
    CATALOG,
    PRODUCT_DETAILS,
    WRITE_REVIEW,
    REVIEW,
    PRODUCT_INFO,
    PRODUCT_GALLERY,
    SHOPPING_CART,
    CHECKOUT_BASKET,
    CHECKOUT_THANKS,
    STATIC_PAGE,
    MY_ACCOUNT,
    MY_ACCOUNT_MY_ADDRESSES,
    MY_ACCOUNT_CREATE_ADDRESS,
    MY_ACCOUNT_EDIT_ADDRESS,
    MY_USER_DATA,
    CHOOSE_COUNTRY,
    CHECKOUT_MY_ADDRESSES,
    CHECKOUT_CREATE_ADDRESS,
    CHECKOUT_EDIT_ADDRESS,
    CHECKOUT_SHIPPING,
    CHECKOUT_CONFIRMATION,
    CHECKOUT_PAYMENT,
    CHECKOUT_FINISH,
    CHECKOUT_EXTERNAL_PAYMENT,
    HEADLESS_CART,
    CAMPAIGNS,
    EMAIL_NOTIFICATION,
    NAVIGATION_MENU,
    NAVIGATION_CATEGORIES_ROOT_LEVEL,
    NAVIGATION_CATEGORIES_SUB_LEVEL,
    WISH_LIST,
    RECENT_SEARCHES_LIST,
    RECENTLY_VIEWED_LIST,
    MY_ORDERS,
    ORDER_STATUS,
    ORDER_CANCELLATION,
    ORDER_CANCELLATION_SUCCESS,
    PRODUCT_SIZE_GUIDE,
    PRODUCT_OFFERS,
    INNER_SHOP,
    VARIATIONS,
    WRITE_REVIEW_SELLER,
    FILTERS,
    COMBO_PAGE,
    LOGIN_EMAIL,
    CATALOG_SELLER,
    CATALOG_BRAND,
    CATALOG_DEEP_LINK,
    CATALOG_CATEGORY,
    CATALOG_FILTER,
    CATALOG_NOFILTER,
    ORDER_RETURN_CONDITIONS,
    ORDER_RETURN_STEPS,
    ORDER_RETURN_CALL,
    STATIC_WEBVIEW_PAGE,
    Sub_CATEGORY_FILTERS,
    MORE_RELATED_PRODUCTS;

    private int id;
    private final static String DIVIDER = ":";

    @Override
    public String toString() {
        return name() + (id != 0 ? DIVIDER + id : "");
    }

    /**
     * Get id associated to type.
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * Associate an id to type.
     * @param id The fragment id
     */
    private FragmentType setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Return the last value (FragmentType) of the tag.
     *
     * @param tag The fragment tag.
     * @author ricardosoares
     */
    public static FragmentType getValue(String tag){
        String[] strings = tag.split(DIVIDER);
        return FragmentType.valueOf(strings[0]);
    }

    /**
     * Get the unique identifier for back stack.
     */
    public static FragmentType getUniqueIdentifier(@NonNull FragmentType type, @NonNull Fragment fragment) {
        return type.setId(fragment.hashCode());
    }

}
