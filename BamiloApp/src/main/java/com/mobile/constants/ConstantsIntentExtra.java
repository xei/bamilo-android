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

    // ########### Search ###########
    public static final String SEARCH_QUERY = mPackage + "SearchStr";

    // ########### Products ###########
    public static final String PRODUCT_SKU = mPackage + "ProductSku";
    public static final String IS_ZOOM_AVAILABLE = mPackage + "ZoomAvailable";
    public static final String INFINITE_SLIDE_SHOW = mPackage + "InfiniteScroll";
    public static final String SHOW_HORIZONTAL_LIST_VIEW = mPackage + "ShowHorizontalListView";
    public static final String SHOW_RELATED_ITEMS = mPackage + "ShowRelatedItems";
    public static final String SIZE_GUIDE_URL = mPackage + "SizeGuideUrl";
    public static final String PRODUCT_NAME = mPackage + "ProductName";
    public static final String IMAGE_LIST = mPackage + "ProductImages";
    public static final String PRODUCT = mPackage + "Product";
    public static final String PRODUCT_BRAND = mPackage + "brand";
    public static final String OUT_OF_STOCK = mPackage + "OutOfStock";

    // ########### REVIEW ##############
    public static final String REVIEW_TITLE = mPackage + "ReviewTitle";
    public static final String REVIEW_COMMENT = mPackage + "ReviewComment";
    public static final String REVIEW_RATING = mPackage + "ReviewRating";
    public static final String REVIEW_NAME = mPackage + "ReviewName";
    public static final String REVIEW_DATE = mPackage + "ReviewDate";

    public static final String NAVIGATION_PATH = mPackage + "NavigationPath";
    public static final String NAVIGATION_SOURCE = mPackage + "NavigationPrefix";

    // ########### Google Analytics: Campaign ##############
    public static final String UTM_STRING = "UTM";

    // ########### Categories ##############
    public static final String CATEGORY_TREE_NAME = mPackage + "CategoryTreeName";

    // ########### Catalog ##############
    public static final String CATALOG_PAGE = mPackage + "catalogPage";
    public static final String CATALOG_QUERY_VALUES = mPackage + "queryValues";
    public static final String CATALOG_FILTER_VALUES = mPackage + "catalogFilter";
    public static final String CATALOG_SORT = mPackage + "catalogSort";
    public static final String CATALOG_CHANGES_APPLIED = mPackage + "catalogChangesApplied";
    public static final String CATALOG_PAGE_POSITION = mPackage + "catalogPagePosition";

    // ########### Home ##############
    public static final String FRAGMENT_TYPE = mPackage + "FragmentType";
    public static final String FRAGMENT_INITIAL_COUNTRY = mPackage + "FragmentInitialCountry";
    public static final String FRAGMENT_BUNDLE = mPackage + "FragmentBundle";

    // ########### Login ##############
    public static final String NEXT_FRAGMENT_TYPE = mPackage + "NextFragmentType";
    public static final String PARENT_FRAGMENT_TYPE = mPackage + "ParentFragmentType";
    public static final String GET_NEXT_STEP_FROM_MOB_API = mPackage + "InCheckoutProcess";

    // ########### Terms ##############

    // ########### DEEP LINK ##############    
    public static final String DEEP_LINK_TAG = "u";
    public static final String DEEP_LINK_ORIGIN = "deepLinkOrigin";
    public static final String TARGET_TYPE = mPackage + "TargetType";

    // ########### MAINTANCE ##############
    public static final String IN_MAINTANCE = "is_in_maintance";

    // ########### BACKSTACK ##############
    public static final String BACK_STACK = mPackage + "backstack";
    public static final String REMOVE_OLD_BACK_STACK_ENTRIES = mPackage + "removeEntries";

    // ########### MY ORDER ##############
    public static final String ORDER_FINISH = mPackage + "OrderFinish";
    public static final String COMPLETE_ORDER = mPackage + "CompleteOrder";
    public static final String ORDER_CANCELLATION_REQUEST_BODY = mPackage + "CancellationRequestBody";
    public static final String CANCELING_ORDER_ITEM_ID = mPackage + "CancelingOrderItemId";

    // ############ BANNER TRACKING ###############
    public static final String TRACKING_ORIGIN_TYPE = "bannerGroupType";

    // ########### MY ORDER ##############
    public static final String PRODUCT_INFO_POS = "ProductInfoPosition";

    // TODO :: Use BaseFragmentSwitcher and UISwitcher
    public static final String DATA = mPackage + "data";
    public static final String FLAG_1 = mPackage + "flag1";
    public static final String ARG_1 = mPackage + "arg1";
    public static final String ARG_2 = mPackage + "arg2";
    public static final String ARG_3 = mPackage + "arg3";
    @Deprecated
    public static final String CONTENT_ID = mPackage + "ContentId";
    @Deprecated
    public static final String CONTENT_TITLE = mPackage + "ContentTitle";

    // ########## RICH RELEVANCE ############
    public static final String RICH_RELEVANCE_HASH = mPackage + "RichRelevanceHash";

    public static final String SUB_CATEGORY_FILTER = "SubCategoryFilter";

    public static final String CATEGORY_URL = "category_url";
    public static final String ORDER_NUMBER = "order_number";
    public static final String ADDRESS = "address";
    public static final String REDIRECT_TO_REVIEWS = "redirect_to_reviews";
    public static final String PHONE_NUMBER = "phone_number";
}
