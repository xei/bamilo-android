package com.mobile.controllers.fragments;


/**
 * Enum used to set the fragment types
 *
 * @author sergiopereira
 */
public enum FragmentType {
    UNKNOWN,
    LOGIN,
    REGISTER,
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
    PRODUCT_DESCRIPTION,
    PRODUCT_GALLERY,
    SHOPPING_CART,
    CHECKOUT_BASKET,
    CHECKOUT_THANKS,
    TERMS,
    MY_ACCOUNT,
    MY_ACCOUNT_MY_ADDRESSES,
    MY_ACCOUNT_CREATE_ADDRESS,
    MY_ACCOUNT_EDIT_ADDRESS,
    MY_USER_DATA,
    CHOOSE_COUNTRY,
    MY_ADDRESSES,
    CREATE_ADDRESS,
    EDIT_ADDRESS,
    SHIPPING_METHODS,
    PAYMENT_METHODS,
    ABOUT_YOU,
    MY_ORDER,
    POLL,
    CHECKOUT_EXTERNAL_PAYMENT,
    HEADLESS_CART,
    CAMPAIGNS,
    EMAIL_NOTIFICATION,
    NAVIGATION_MENU,
    NAVIGATION_CATEGORIES_ROOT_LEVEL,
    NAVIGATION_CATEGORIES_SUB_LEVEL,
    FAVORITE_LIST,
    RECENT_SEARCHES_LIST,
    RECENTLY_VIEWED_LIST,
    MY_ORDERS,
    PRODUCT_SIZE_GUIDE,
    PRODUCT_OFFERS,
    INNER_SHOP,
    WRITE_REVIEW_SELLER;

    private int id;

    @Override
    public String toString() {
        return name() + ((id != 0) ? "_"+getId() : "");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
