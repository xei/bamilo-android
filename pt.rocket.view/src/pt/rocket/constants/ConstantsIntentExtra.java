package pt.rocket.constants;

/**
 * The purpose of this class is to provide a central point to identify the intent extras used within
 * the activities This constants are user mainly in the ActivitiesWorkflow activity
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author sergiopereira
 * 
 * @version 1.00
 * 
 */
public class ConstantsIntentExtra {
    //
    private final static String mPackage                    = "pt.rocket.view.";

    //
    public static final String CATEGORY_INDEX               = mPackage + "CategoryIndex";

    // ########### Search ###########
    //
    public static final String SEARCH_QUERY                 = mPackage + "SearchStr";

    // ########### Products ###########
    //
    public static final String CONTENT_URL                  = mPackage + "ContentUrl";

    public static final String PRODUCT_URL                  = mPackage + "ProductUrl";
    public static final String CURRENT_LISTPOSITION         = mPackage + "CurrentListPosition";
    public static final String VARIATION_LISTPOSITION       = mPackage + "VariationListPosition";
    public static final String IS_ZOOM_AVAILABLE            = mPackage + "ZoomAvailable";
    public static final String SHOW_HORIZONTAL_LIST_VIEW    = mPackage + "ShowHorizontalListView";
    public static final String SHOW_RELATED_ITEMS           = mPackage + "ShowRelatedItems";
    public static final String IS_RELATED_ITEM              = mPackage + "RelatedItem";
    public static final String SIZE_GUIDE_URL               = mPackage + "SizeGuideUrl";
    
    // ########### Checkout ###########
    //
    public static final String CHECKOUT_PROCESS             = mPackage + "CheckoutProcess";
    public static final String CONTENT_TITLE                = mPackage + "ContentTitle";
    public static final String SUCESS_INFORMATION           = mPackage + "SucessInformation";
    public static final String CUSTOMER_EMAIL               = mPackage + "CustomerEmail";
    public static final String IS_SIGNUP                    = mPackage + "isSignup";

    // ########### REVIEW ##############
    public static final String REVIEW_TITLE                 = mPackage + "ReviewTitle";
    public static final String REVIEW_COMMENT               = mPackage + "ReviewComment";
    public static final String REVIEW_RATING                = mPackage + "ReviewRating";
    public static final String REVIEW_NAME                  = mPackage + "ReviewName";
    public static final String REVIEW_DATE                  = mPackage + "ReviewDate";

    public static final String NAVIGATION_PATH              = mPackage + "NavigationPath";
    public static final String NAVIGATION_SOURCE            = mPackage + "NavigationPrefix";

    // ########### Google Analytics: Campaign ##############
    public static final String UTM_STRING                   = "UTM";

    // ########### Categories ##############
    public static final String CATEGORY_LEVEL               = mPackage + "CategoryLevel";
    public static final String CATEGORY_URL                 = mPackage + "CategoryUrl";
    public static final String CATEGORY_ID                  = mPackage + "Category";
    public static final String SELECTED_CATEGORY_INDEX      = mPackage + "CategoryIdx";
    public static final String SELECTED_SUB_CATEGORY_INDEX  = mPackage + "SubCategoryIdx";
    public static final String CATEGORY_TREE_PATH           = mPackage + "CategoryTreePath";
    public static final String CATEGORY_PARENT_NAME         = mPackage + "CategoryParentName";
    public static final String CATEGORY_TREE_NAME         = mPackage + "CategoryTreeName";

    // ########### Catalog ##############
    public static final String CATALOG_SORT_PAGE            = mPackage + "catalogPage";
    
    // ########### Home ##############
    public static final String FRAGMENT_TYPE                = mPackage + "FragmentType";
    public static final String FRAGMENT_INITIAL_COUNTRY     = mPackage + "FragmentInitialCountry";
    public static final String FRAGMENT_FROM_WEBCHECKOU     = mPackage + "FragmentFromWebCheckout";
    public static final String FRAGMENT_BUNDLE              = mPackage + "FragmentBundle";

    // ########### Login ##############
    public static final String NEXT_FRAGMENT_TYPE           = mPackage + "NextFragmentType";

    // ########### Terms ##############
    public static final String TERMS_CONDITIONS             = mPackage + "TermsConditions";
    
    // ########### Change Country ##############
    public static final String CHANGED_COUNTRY              = mPackage + "ChangedCountry";
    
    // ########### DEEP LINK ##############    
    public static final String DEEP_LINK_TAG                = "u";
    public static final String CATALOG_DEEP_LINK_TAG        = "c";
    public static final String CART_DEEP_LINK_TAG           = "cart";

    public static final String ORDER_FINISH                 = mPackage + "OrderFinish";

    // ########### MAINTANCE ##############
    public static final String IN_MAINTANCE                 = "is_in_maintance";
    
    // ########### BACKSTACK ##############
    public static final String BACK_STACK                     = mPackage + "backstack";
    
    // ########### MY ORDER ##############
    public static final String MY_ORDER_POS                 = "position";
}
