package com.mobile.constants;

/**
 * The purpose of this class is to provide a central point to identify the intent extras used within the activities This constants are user mainly in the
 * ActivitiesWorkflow activity
 * <p/>
 * <br>
 * <p/>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited <br> Proprietary and confidential.
 *
 * @author sergiopereira
 * @version 1.00
 */
public class ConstantsIntentExtra {

    //
    private final static String mPackage = "com.mobile.view.";

    // ########### BASE ###########
    public static final String IS_NESTED_FRAGMENT = mPackage + "isNestedFragment";
    public static final String CONTENT_DATA = mPackage + "ContentData";

    // ########### Search ###########
    public static final String SEARCH_QUERY = mPackage + "SearchStr";

    // ########### Products ###########
    public static final String CONTENT_URL = mPackage + "ContentUrl";
    public static final String PRODUCT_SKU = mPackage + "ProductSku";
    public static final String IS_ZOOM_AVAILABLE = mPackage + "ZoomAvailable";
    public static final String INFINITE_SLIDE_SHOW = mPackage + "InfiniteScroll";
    public static final String SHOW_HORIZONTAL_LIST_VIEW = mPackage + "ShowHorizontalListView";
    public static final String SHOW_RELATED_ITEMS = mPackage + "ShowRelatedItems";
    public static final String IS_RELATED_ITEM = mPackage + "RelatedItem";
    public static final String SIZE_GUIDE_URL = mPackage + "SizeGuideUrl";
    public static final String PRODUCT_NAME = mPackage + "ProductName";
    public static final String IMAGE_LIST = mPackage + "ProductImages";
    public static final String PRODUCT = mPackage + "Product";
    public static final String PRODUCT_GALLERY_POS = mPackage + "ProductPosition";
    public static final String PRODUCT_BRAND = mPackage + "brand";
    public static final String OUT_OF_STOCK = mPackage + "OutOfStock";

    // ########### Checkout ###########
    public static final String CONTENT_TITLE = mPackage + "ContentTitle";
    public static final String SUCCESS_INFORMATION = mPackage + "SuccessInformation";
    public static final String CUSTOMER_EMAIL = mPackage + "CustomerEmail";

    // ########### REVIEW ##############
    public static final String REVIEW_TITLE = mPackage + "ReviewTitle";
    public static final String REVIEW_COMMENT = mPackage + "ReviewComment";
    public static final String REVIEW_RATING = mPackage + "ReviewRating";
    public static final String REVIEW_NAME = mPackage + "ReviewName";
    public static final String REVIEW_DATE = mPackage + "ReviewDate";

    public static final String NAVIGATION_PATH = mPackage + "NavigationPath";
    public static final String NAVIGATION_SOURCE = mPackage + "NavigationPrefix";
    public static final String REVIEW_TYPE = mPackage + "ReviewType";

    // ########### Google Analytics: Campaign ##############
    public static final String UTM_STRING = "UTM";

    // ########### Categories ##############
    public static final String CATEGORY_LEVEL = mPackage + "CategoryLevel";
    public static final String CATEGORY_URL = mPackage + "CategoryUrl";
    public static final String CATEGORY_ID = mPackage + "Category";
    public static final String SELECTED_SUB_CATEGORY_INDEX = mPackage + "SubCategoryIdx";
    public static final String CATEGORY_PARENT_NAME = mPackage + "CategoryParentName";
    public static final String CATEGORY_TREE_NAME = mPackage + "CategoryTreeName";
    public static final String CATALOG_SOURCE = mPackage + "isFromNavigationDrawer";

    // ########### Catalog ##############
    public static final String CATALOG_PAGE = mPackage + "catalogPage";
    public static final String CATALOG_QUERY_VALUES = mPackage + "queryValues";
    public static final String CATALOG_FILTER_VALUES = mPackage + "catalogFilter";
    public static final String CATALOG_FILTER_BRAND = mPackage + "catalogBrand";
    public static final String CATALOG_SORT = mPackage + "catalogSort";
    public static final String CATALOG_CHANGES_APPLIED = mPackage + "catalogChangesApplied";
    public static final String CATALOG_QUERIE = mPackage + "catalogQuerie";

    // ########### Home ##############
    public static final String FRAGMENT_TYPE = mPackage + "FragmentType";
    public static final String FRAGMENT_INITIAL_COUNTRY = mPackage + "FragmentInitialCountry";
    public static final String FRAGMENT_BUNDLE = mPackage + "FragmentBundle";

    // ########### Login ##############
    public static final String NEXT_FRAGMENT_TYPE = mPackage + "NextFragmentType";
    public static final String PARENT_FRAGMENT_TYPE = mPackage + "ParentFragmentType";
    public static final String IS_IN_CHECKOUT_PROCESS = mPackage + "InCheckoutProcess";

    // ########### Terms ##############
    public static final String TERMS_CONDITIONS = mPackage + "TermsConditions";

    // ########### DEEP LINK ##############    
    public static final String DEEP_LINK_TAG = "u";
    public static final String CART_DEEP_LINK_TAG = "cart";
    public static final String DEEP_LINK_ORIGIN = "deepLinkOrigin";

    // ########### MAINTANCE ##############
    public static final String IN_MAINTANCE = "is_in_maintance";

    // ########### BACKSTACK ##############
    public static final String BACK_STACK = mPackage + "backstack";
    public static final String REMOVE_OLD_BACK_STACK_ENTRIES = mPackage + "removeEntries";

    // ########### MY ORDER ##############
    public static final String MY_ORDER_POS = "position";
    public static final String ORDER_FINISH = mPackage + "OrderFinish";
    public static final String ORDER_SUMMARY = mPackage + "OrderSummary";

    // ############ BANNER TRACKING ###############
    public static final String BANNER_TRACKING_TYPE = "bannerGroupType";

    // ########### MY ORDER ##############
    public static final String PRODUCT_INFO_POS = "ProductInfoPosition";

    public static final String DATA = mPackage + "data";
    public static final String FLAG_1 = mPackage + "flag1";
    public static final String FLAG_2 = mPackage + "flag2";


}
