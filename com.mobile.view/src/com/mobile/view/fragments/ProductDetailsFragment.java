package com.mobile.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.HorizontalListView;
import com.mobile.components.HorizontalListView.OnViewSelectedListener;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.controllers.BundleItemsListAdapter;
import com.mobile.controllers.BundleItemsListAdapter.OnItemChecked;
import com.mobile.controllers.BundleItemsListAdapter.OnItemSelected;
import com.mobile.controllers.BundleItemsListAdapter.OnSimplePressed;
import com.mobile.controllers.ProductVariationsListAdapter;
import com.mobile.controllers.RelatedItemsListAdapter;
import com.mobile.controllers.TipsPagerAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.ViewGroupFactory;
import com.mobile.framework.Darwin;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.database.FavouriteTableHelper;
import com.mobile.framework.database.LastViewedTableHelper;
import com.mobile.framework.database.RelatedItemsTableHelper;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.Errors;
import com.mobile.framework.objects.LastViewed;
import com.mobile.framework.objects.ProductBundle;
import com.mobile.framework.objects.ProductBundleProduct;
import com.mobile.framework.objects.ProductBundleSimple;
import com.mobile.framework.objects.ProductSimple;
import com.mobile.framework.objects.Variation;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.GTMEvents.GTMValues;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.DeviceInfoHelper;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.GetShoppingCartAddBundleHelper;
import com.mobile.helpers.cart.GetShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetProductBundleHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.helpers.search.GetSearchProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.FragmentCommunicatorForProduct;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TipsOnPageChangeListener;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.dialogfragments.WizardPreferences;
import com.mobile.utils.dialogfragments.WizardPreferences.WizardType;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.imageloader.RocketImageLoader.ImageHolder;
import com.mobile.utils.imageloader.RocketImageLoader.RocketImageLoaderLoadImagesListener;
import com.mobile.utils.ui.CompleteProductUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * 
 * <p>
 * This class displays the product detail screen
 * </p>
 * <p>
 * Its uses the HorizontalListView to display the variations for that product
 * </p>
 * 
 * <p>
 * Copyright (C) 2013 Smart Mobile Factory GmbH - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential.
 * </p>
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.00
 * 
 * @author Michael Kroez
 * @modified Manuel Silva
 * 
 * @date 4/1/2013
 * 
 * @description This class displays the product detail screen
 * 
 */
public class ProductDetailsFragment extends BaseFragment implements OnClickListener,
        OnDialogListListener, OnItemChecked, OnItemSelected, OnSimplePressed,
        OnItemSelectedListener, RocketImageLoaderLoadImagesListener {

    private final static String TAG = LogTagHelper.create(ProductDetailsFragment.class);

    private final static int NO_SIMPLE_SELECTED = -1;

    private final static String VARIATION_PICKER_ID = "variation_picker";

    private static String SELECTED_SIMPLE_POSITION = "selected_simple_position";

    public final static String LOADING_PRODUCT_KEY = "loading_product_key";

    public final static String LOADING_PRODUCT = "loading_product";

    public final static String PRODUCT_COMPLETE = "complete_product";

    public final static String PRODUCT_CATEGORY = "product_category";

    public final static String PRODUCT_BUNDLE = "product_bundle";

    private Context mContext;

    private DialogFragment mDialogAddedToCart;

    private DialogListFragment dialogListFragment;

    private CompleteProduct mCompleteProduct;

    private Button mAddToCartButton;

    private ViewGroup mVarianceContainer;

    private String mCompleteProductUrl;

    private int mSelectedSimple = NO_SIMPLE_SELECTED;

    private ViewGroup mProductRatingContainer;

    private RatingBar mProductRating;

    private TextView mProductRatingCount;

    private Button mCallToOrderButton;

    private Button mVarianceButton;

    private boolean mHideVariationSelection;

    private TextView mVarianceText;

    private ImageView imageIsFavourite;

    private ImageView imageShare;

    private long mBeginRequestMillis;

    private ArrayList<String> mSimpleVariants;

    private ArrayList<String> mSimpleVariantsAvailable;

    private String mNavigationPath;

    private String mNavigationSource;

    private boolean mShowRelatedItems;

    private RelativeLayout loadingRating;

    public static String VARIATION_LIST_POSITION = "variation_list_position";

    private String mPhone2Call = "";

    private static View mainView;

    private static ProductDetailsFragment mProductDetailsActivityFragment;

    boolean isAddingProductToCart = false;

    private ArrayList<String> variations;

    private String mDeepLinkSimpleSize;

    private TextView mSpecialPriceText;

    private TextView mPriceText;

    private TextView mTitleText;

    private TextView mDiscountPercentageText;

    private View mRelatedLoading;

    private View mRelatedContainer;

    private RelativeLayout mProductFeaturesContainer;

    private TextView mProductFeaturesText;

    private TextView mProductDescriptionText;

    private LinearLayout mProductFeaturesMore;

    private LinearLayout mProductDescriptionMore;

    private RelativeLayout mProductDescriptionContainer;

    private boolean isRelatedItem = false;

    private HorizontalListView mRelatedListView;

    private static String categoryTree = "";

    private ProductBundle mProductBundle;

    private View mBundleContainer;

    private HorizontalListView mBundleListView;

    private View mBundleLoading;

    private View mDividerBundle;

    private TextView mBundleTextTotal;

    private Button mBundleButton;

    private View mVariationsContainer;

    private HorizontalListView mVariationsListView;

    private RelativeLayout sellerView;

    private RelativeLayout mSellerNameContainer;

    private TextView mSellerName;

    private RelativeLayout mSellerRatingContainer;

    private TextView mSellerRatingValue;

    private TextView mSellerDeliveryTime;

    private RatingBar mSellerRating;

    private RelativeLayout offersContainer;

    private RelativeLayout offersContent;

    private TextView numOffers;

    private TextView minOffers;

    private ViewGroupFactory mGalleryViewGroupFactory;

    /**
     * Empty constructor
     */
    public ProductDetailsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET,
                MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.product_details_fragment_main,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /**
     * 
     * @param bundle
     * @return
     */
    public static ProductDetailsFragment getInstance(Bundle bundle) {
        ProductDetailsFragment.mProductDetailsActivityFragment = new ProductDetailsFragment();
        if (null != bundle && bundle.containsKey(ConstantsIntentExtra.CATEGORY_TREE_NAME)) {
            categoryTree = bundle.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME) + ",PDV";
        } else {
            categoryTree = "";
        }
        // Clean current product
        JumiaApplication.INSTANCE.setCurrentProduct(null);
        return ProductDetailsFragment.mProductDetailsActivityFragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE");
        // Get data from saved instance
        if (savedInstanceState != null) {
            mSelectedSimple = savedInstanceState.getInt(SELECTED_SIMPLE_POSITION, NO_SIMPLE_SELECTED);
            mProductBundle = savedInstanceState.getParcelable(PRODUCT_BUNDLE);
        }
        Log.d(TAG, "CURRENT SELECTED SIMPLE: " + mSelectedSimple);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.product_detail_view.View,
     * android.product_detail_os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "ON VIEW CREATED");
        super.onViewCreated(view, savedInstanceState);
        // Context
        mContext = getBaseActivity();
        // Save view
        mainView = view;
        // Set layout
        setAppContentLayout(view);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ON RESUME");
        // Validate the current product
        mCompleteProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        if (mCompleteProduct == null) {
            init();
        } else {
            // Must get other params other than currentProduct
            // Get arguments
            Bundle bundle = getArguments();
            restoreParams(bundle);
            displayProduct(mCompleteProduct);
        }
        isAddingProductToCart = false;
        TrackerDelegator.trackPage(TrackingPage.PRODUCT_DETAIL, getLoadTime(), false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.product_detail_support.v4.app.Fragment#onSaveInstanceState(android.product_detail_os
     * .Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current fragment type on orientation change
        if (!mHideVariationSelection)
            outState.putInt(SELECTED_SIMPLE_POSITION, mSelectedSimple);
        // Save product bundle
        if (mProductBundle != null)
            outState.putParcelable(PRODUCT_BUNDLE, mProductBundle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        dialogListFragment = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ON DESTROY");
        // Garbage
        FragmentCommunicatorForProduct.getInstance().destroyInstance();
    }

    /**
     * 
     */
    private void init() {
        Log.d(TAG, "INIT");
        // Get arguments
        Bundle bundle = getArguments();
        // Validate deep link arguments
        if (hasArgumentsFromDeepLink(bundle))
            return;

        restoreParams(bundle);

        // Get url
        mCompleteProductUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        // Validate url and load product
        if (mCompleteProductUrl == null)
            getBaseActivity().onBackPressed();
        else
            loadProduct();
    }

    private void restoreParams(Bundle bundle) {
        // Get source and path
        mNavigationSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcatalog));
        mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
        // Determine if related items should be shown
        mShowRelatedItems = bundle.getBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS);
        isRelatedItem = bundle.getBoolean(ConstantsIntentExtra.IS_RELATED_ITEM);
    }

    /**
     * Validate and loads the received arguments comes from deep link process.
     * 
     * @param bundle
     * @return boolean
     * @author sergiopereira
     */
    private boolean hasArgumentsFromDeepLink(Bundle bundle) {
        // Get the sku
        String sku = bundle.getString(GetSearchProductHelper.SKU_TAG);
        // Get the simple size
        mDeepLinkSimpleSize = bundle.getString(DeepLinkManager.PDV_SIZE_TAG);
        // Validate
        if (sku != null) {
            Log.i(TAG, "DEEP LINK GET PDV: " + sku + " " + mDeepLinkSimpleSize);
            mNavigationSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                    R.string.gpush_prefix));
            mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
            mBeginRequestMillis = System.currentTimeMillis();
            triggerContentEvent(new GetSearchProductHelper(), bundle, responseCallback);
            return true;
        }
        return false;
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout(View view) {
        // Favourite
        imageIsFavourite = (ImageView) view.findViewById(R.id.product_detail_image_is_favourite);
        imageIsFavourite.setOnClickListener((OnClickListener) this);
        // Share
        imageShare = (ImageView) view.findViewById(R.id.product_detail_product_image_share);
        imageShare.setOnClickListener((OnClickListener) this);
        // Discount percentage
        mDiscountPercentageText = (TextView) view.findViewById(R.id.product_detail_discount_percentage);
        // Prices
        mSpecialPriceText = (TextView) view.findViewById(R.id.product_price_special);
        mPriceText = (TextView) view.findViewById(R.id.product_price_normal);
        // Variations
        mVarianceContainer = (ViewGroup) view.findViewById(R.id.product_detail_product_variant_container);
        mVarianceText = (TextView) view.findViewById(R.id.product_detail_product_variant_text);
        mVarianceButton = (Button) view.findViewById(R.id.product_detail_product_variant_button);
        mVarianceButton.setOnClickListener(this);
        // Rating
        mProductRatingContainer = (ViewGroup) view.findViewById(R.id.product_detail_product_rating_container);
        mProductRatingContainer.setOnClickListener(this);
        mProductRating = (RatingBar) view.findViewById(R.id.product_detail_product_rating);
        mProductRatingCount = (TextView) view.findViewById(R.id.product_detail_product_rating_count);
        loadingRating = (RelativeLayout) view.findViewById(R.id.product_detail_loading_rating);
        // Related
        mRelatedContainer = view.findViewById(R.id.product_detail_product_related_container);
        mRelatedListView = (HorizontalListView) view.findViewById(R.id.product_detail_horizontal_list_view);
        mRelatedLoading = view.findViewById(R.id.loading_related);
        // Variations
        mVariationsContainer = view.findViewById(R.id.variations_container);
        mVariationsListView = (HorizontalListView) view.findViewById(R.id.variations_list);
        // BUNDLE
        mBundleContainer = view.findViewById(R.id.product_detail_product_bundle_container);
        mBundleListView = (HorizontalListView) view.findViewById(R.id.product_detail_horizontal_bundle_list_view);
        mBundleLoading = view.findViewById(R.id.loading_related_bundle);
        mBundleButton = (Button) view.findViewById(R.id.bundle_add_cart);
        mBundleTextTotal = (TextView) view.findViewById(R.id.bundle_total_value);
        mDividerBundle = view.findViewById(R.id.divider_bundle);
        mBundleButton.setSelected(true);
        // OFFERS
        offersContainer = (RelativeLayout) view.findViewById(R.id.product_detail_product_offers_container);
        offersContent = (RelativeLayout) view.findViewById(R.id.offers_container);
        numOffers = (TextView) view.findViewById(R.id.offers_value);
        minOffers = (TextView) view.findViewById(R.id.from_value);
        // Bottom Button
        mAddToCartButton = (Button) view.findViewById(R.id.product_detail_shop);
        mAddToCartButton.setSelected(true);
        mAddToCartButton.setOnClickListener(this);
        // Call to order
        mCallToOrderButton = (Button) view.findViewById(R.id.product_detail_call_to_order);
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            mCallToOrderButton.setSelected(true);
            mCallToOrderButton.setOnClickListener(this);
        } else {
            mCallToOrderButton.setVisibility(View.GONE);
        }
        // Get title from portrait or landscape
        mTitleText = (TextView) view.findViewById(R.id.product_name);
        if (mTitleText == null)
            mTitleText = (TextView) view.findViewById(R.id.product_detail_name);
        mTitleText.setOnClickListener((OnClickListener) this);
        // Seller info
        sellerView = (RelativeLayout) view.findViewById(R.id.seller_info);
        mSellerNameContainer = (RelativeLayout) view.findViewById(R.id.seller_name_container);
        mSellerNameContainer.setOnClickListener(this);
        mSellerName = (TextView) view.findViewById(R.id.product_detail_seller_name);
        mSellerRatingContainer = (RelativeLayout) view.findViewById(R.id.product_detail_product_seller_rating_container);
        mSellerRatingContainer.setOnClickListener(this);
        mSellerRatingValue = (TextView) view.findViewById(R.id.product_detail_product_seller_rating_count);
        mSellerDeliveryTime = (TextView) view.findViewById(R.id.product_detail_seller_delivery_time);
        mSellerRating = (RatingBar) view.findViewById(R.id.product_detail_product_seller_rating);
        sellerView.setVisibility(View.GONE);

        // Get and set portrait views
        checkPortraitLayout(view);
        // Get and set landscape views
        checkLanscapeLayout(view);

        mGalleryViewGroupFactory = new ViewGroupFactory((ViewGroup) view.findViewById(R.id.product_image_layout));
    }

    /**
     * Get and set the portrait views
     * 
     * @param view
     * @author sergiopereira
     */
    private void checkPortraitLayout(View view) {
        View specs = view.findViewById(R.id.product_detail_specifications);
        if (specs != null)
            specs.setOnClickListener((OnClickListener) this);
    }

    /**
     * Get and set the landscape views
     * 
     * @param view
     * @author sergiopereira
     */
    private void checkLanscapeLayout(View view) {
        // Get landscape views
        mProductFeaturesContainer = (RelativeLayout) view.findViewById(R.id.features_container);
        mProductDescriptionContainer = (RelativeLayout) view
                .findViewById(R.id.description_container);
        mProductFeaturesText = (TextView) view.findViewById(R.id.product_features_text);
        mProductDescriptionText = (TextView) view.findViewById(R.id.product_description_text);
        mProductFeaturesMore = (LinearLayout) view.findViewById(R.id.features_more_container);
        mProductDescriptionMore = (LinearLayout) view.findViewById(R.id.description_more_container);
        // Set listeners
        if (mProductDescriptionMore != null)
            mProductDescriptionMore.setOnClickListener(this);
        if (mProductFeaturesMore != null)
            mProductFeaturesMore.setOnClickListener(this);
    }

//    /**
//     * function responsible for handling the size of the image according to existence of other
//     * sections of the PDV
//     * 
//     * @param hasBundle
//     *            // TODO : Other approach
//     */
//    private void updateImageSize(boolean hasBundle) {
//
//        if (getActivity() == null)
//            return;
//
//        if (DeviceInfoHelper.isTabletInLandscape(getActivity().getApplicationContext())) {
//            RelativeLayout mLeftContainerInfo = (RelativeLayout) getView().findViewById(
//                    R.id.image_container);
//
//            LayoutParams params = mLeftContainerInfo.getLayoutParams();
//
//            if (imageHeight == -1) {
//
//                // set default size like the product does not have any more component on the left
//                // besides the image
//                imageHeight = (int) getResources().getDimension(R.dimen.pdv_image_alone);
//
//                // set image size if product has variations and simples
//                if (mVarianceContainer.isShown()
//                        && getView().findViewById(R.id.variations_container).isShown()) {
//                    imageHeight = (int) getResources().getDimension(
//                            R.dimen.pdv_image_with_var_simple);
//                    // set image size if product has variations or simples
//                } else if (mVarianceContainer.isShown()
//                        || getView().findViewById(R.id.variations_container).isShown()) {
//                    imageHeight = (int) getResources().getDimension(R.dimen.pdv_image_with_var);
//                }
//
//                int decreaseSize = 0;
//                // decrease image size if product has bundle container
//                if (hasBundle) {
//                    decreaseSize = (int) getResources().getDimension(R.dimen.pdv_image_with_bundle);
//                }
//                // decrease image size if product has seller info
//                if (sellerView.isShown()) {
//                    decreaseSize = decreaseSize
//                            + (int) getResources().getDimension(R.dimen.pdv_image_with_seller);
//                }
//
//                params.height = imageHeight - decreaseSize;
//
//            } else {
//                params.height = imageHeight;
//            }
//
//            mLeftContainerInfo.setLayoutParams(params);
//        }
//
//    }

    /**
     * 
     */
    private void setContentInformation() {
        Log.d(TAG, "SET DATA");
        updateVariants();
        updateStockInfo();
        preselectASimpleItem();
        displayPriceInfoOverallOrForSimple();
        displayRatingInfo();
        displayVariantsContainer();
        displaySellerInfo();
        displayOffersInfo();
    }

    private void displayOffersInfo() {
        if (mCompleteProduct != null && mCompleteProduct.getTotalOffers() > 0) {
            offersContainer.setVisibility(View.VISIBLE);

            numOffers.setText(" (" + mCompleteProduct.getTotalOffers() + ")");
            minOffers.setText(mCompleteProduct.getMinPriceOffer());

            if (DeviceInfoHelper.isTabletInLandscape(getActivity().getApplicationContext())) {
                offersContent.setOnClickListener(this);
            } else {
                offersContainer.setOnClickListener(this);
            }
        } else {
            offersContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 
     */
    private void loadProduct() {
        Log.d(TAG, "LOAD PRODUCT");
        mBeginRequestMillis = System.currentTimeMillis();
        Bundle bundle = new Bundle();
        bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);

        if (JumiaApplication.mIsBound) {
            triggerContentEvent(new GetProductHelper(), bundle, responseCallback);
        } else {
            showFragmentRetry(this);
        }
    }

    /**
     * 
     */
    private void loadProductPartial() {
        mBeginRequestMillis = System.currentTimeMillis();
        mGalleryViewGroupFactory.setViewVisible(R.id.image_loading_progress);
        Bundle bundle = new Bundle();
        bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
        triggerContentEventWithNoLoading(new GetProductHelper(), bundle, responseCallback);
    }

    /**
     * 
     */
    private void preselectASimpleItem() {
        if (mSelectedSimple != NO_SIMPLE_SELECTED)
            return;

        ArrayList<ProductSimple> ps = mCompleteProduct.getSimples();
        Set<String> knownVariations = scanSimpleAttributesForKnownVariants(ps);

        if (ps.size() == 1) {
            if (knownVariations.size() <= 1) {
                mSelectedSimple = 0;
                mHideVariationSelection = true;
            } else {
                mSelectedSimple = NO_SIMPLE_SELECTED;
            }
        } else {
            mHideVariationSelection = false;
            mSelectedSimple = NO_SIMPLE_SELECTED;
        }

    }

    /**
     * 
     * @param simples
     * @return
     */
    private Set<String> scanSimpleAttributesForKnownVariants(ArrayList<ProductSimple> simples) {
        Set<String> foundVariations = new HashSet<String>();
        Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants");
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants in");
            scanSimpleForKnownVariants(simple, foundVariations);
        }
        return foundVariations;
    }

    /**
     * 
     * @param simple
     * @param foundVariations
     */
    private void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations) {
        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            Log.i(TAG, "scanSimpleForKnownVariations: variation = " + variation + " attr = " + attr);
            if (attr == null)
                continue;
            foundVariations.add(variation);
        }
    }

    /**
     * 
     * @return
     */
    private ArrayList<String> createSimpleVariants() {
        Log.i(TAG,
                "scanSimpleForKnownVariations : createSimpleVariants" + mCompleteProduct.getName());
        ArrayList<ProductSimple> simples = new ArrayList<ProductSimple>(
                mCompleteProduct.getSimples());
        variations = mCompleteProduct.getKnownVariations();
        if (variations == null || variations.size() == 0) {
            variations = new ArrayList<String>();
            variations.add("size");
            variations.add("color");
            variations.add("variation");
        }
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(simples);

        mSimpleVariantsAvailable = new ArrayList<String>();
        ArrayList<String> variationValues = new ArrayList<String>();
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants in");
            String value = calcVariationStringForSimple(simple, foundKeys);
            String quantity = simple.getAttributeByKey(ProductSimple.QUANTITY_TAG);

            if (quantity != null && Long.parseLong(quantity) > 0) {
                variationValues.add(value);
                mSimpleVariantsAvailable.add(value);
            } else {
                variationValues.add(value);
            }

        }
        return variationValues;
    }

    /**
     * 
     * @param simple
     * @param keys
     * @return
     */
    private String calcVariationStringForSimple(ProductSimple simple, Set<String> keys) {
        String delim = ";";
        String loopDelim = "";
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = simple.getAttributeByKey(key);
            if (value != null) {
                sb.append(loopDelim);
                sb.append(value);
                loopDelim = delim;
            }
        }

        return sb.toString();
    }

    // private HashMap<String, String> createVariantAttributesHashMap(ProductSimple simple) {
    // ArrayList<ProductSimple> simples = mCompleteProduct.getSimples();
    // if (simples.size() <= 1)
    // return null;
    //
    // Set<String> foundKeys = scanSimpleAttributesForKnownVariants(simples);
    //
    // HashMap<String, String> variationsMap = new HashMap<String, String>();
    // for (String key : foundKeys) {
    // String value = simple.getAttributeByKey(key);
    //
    // if (value == null)
    // continue;
    // if (value.equals("\u2026"))
    // continue;
    //
    // variationsMap.put(key, value);
    // }
    //
    // return variationsMap;
    // }

    /**
     * 
     * @return
     */
    private ProductSimple getSelectedSimple() {
        ProductSimple simple = null;
        try {
            // Case invalid selection
            if (mSelectedSimple >= mCompleteProduct.getSimples().size())
                ;
            // Case no selected
            else if (mSelectedSimple == NO_SIMPLE_SELECTED)
                ;
            // Case success
            else
                simple = mCompleteProduct.getSimples().get(mSelectedSimple);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON GET SELECTED SIMPLE");
        }
        return simple;
    }

    /**
     * 
     */
    private void displayPriceInfoOverallOrForSimple() {
        ProductSimple simple = getSelectedSimple();

        if (mSelectedSimple == NO_SIMPLE_SELECTED || simple == null) {
            String unitPrice = mCompleteProduct.getPrice();
            String specialPrice = mCompleteProduct.getSpecialPrice();
            /*--if (specialPrice == null) specialPrice = mCompleteProduct.getMaxSpecialPrice();*/
            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();

            displayPriceInfo(unitPrice, specialPrice, discountPercentage);

        } else {
            // Simple Products prices don't come with currency formatted
            String unitPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specialPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);

            unitPrice = currencyFormatHelper(unitPrice);
            if (specialPrice != null)
                specialPrice = currencyFormatHelper(specialPrice);

            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();

            displayPriceInfo(unitPrice, specialPrice, discountPercentage);
        }
    }

    /**
     * 
     * @param unitPrice
     * @param specialPrice
     * @param discountPercentage
     */
    private void displayPriceInfo(String unitPrice, String specialPrice, int discountPercentage) {
        Log.d(TAG, "displayPriceInfo: unitPrice = " + unitPrice + " specialPrice = " + specialPrice);
        if (specialPrice == null || specialPrice.equals(unitPrice)) {
            // display only the normal price
            mSpecialPriceText.setText(unitPrice);
            mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
            mPriceText.setVisibility(View.GONE);
        } else {
            // display reduced and special price
            mSpecialPriceText.setText(specialPrice);
            mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
            mPriceText.setText(unitPrice);
            mPriceText.setPaintFlags(mPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mPriceText.setVisibility(View.VISIBLE);
        }
        // Set discount percentage value
        if (discountPercentage > 0) {
            String discount = String.format(getString(R.string.format_discount_percentage),
                    discountPercentage);
            Log.i(TAG, "displayPriceInfo:" + discount);
            mDiscountPercentageText.setText(discount);
            mDiscountPercentageText.setVisibility(View.VISIBLE);
        } else {
            mDiscountPercentageText.setVisibility(View.GONE);
        }
    }

    private void updateStockInfo() {
        /**
         * No simple selected show the stock for the current product
         */
        ProductSimple currentSimple = mCompleteProduct.getSimples().get(0);
        if (mSelectedSimple == NO_SIMPLE_SELECTED && currentSimple != null) {
            // try {
            // long stockQuantity = -1;
            // stockQuantity =
            // Long.valueOf(currentSimple.getAttributeByKey(ProductSimple.QUANTITY_TAG));
            // // Log.d(TAG, "code1stock STOCK:  NO SIMPLE SELECTED " + stockQuantity);
            // Bundle bundle = new Bundle();
            // bundle.putLong(ProductBasicInfoFragment.DEFINE_STOCK, stockQuantity);
            // FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment,
            // bundle, 4);
            return;
            // } catch (NumberFormatException e) {
            // Log.w(TAG, "code1stock STOCK INFO: quantity in simple is null: ", e);
            // }
        }

        /**
         * Simple selected but is null
         */
        if (getSelectedSimple() == null) {
            // Log.d(TAG, "code1stock STOCK:  SIMPLE IS NULL " + mSelectedSimple);
            // Bundle bundle = new Bundle();
            // bundle.putLong(ProductBasicInfoFragment.DEFINE_STOCK, -1);
            // FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment,
            // bundle, 4);
            return;
        }

        /**
         * Simple selected
         */
        // Log.d(TAG, "code1stock SIMPLE " + mSelectedSimple);
        /*-long stockQuantity = 0;
        try {
            stockQuantity = Long.valueOf(getSelectedSimple().getAttributeByKey(
                    ProductSimple.QUANTITY_TAG));
        } catch (NumberFormatException e) {
            Log.w(TAG, "code1stock: quantity in simple is not a number:", e);
        }

        if (stockQuantity > 0) {
            mAddToCartButton.setBackgroundResource(R.drawable.btn_orange);
        } else {
            mAddToCartButton.setBackgroundResource(R.drawable.btn_grey);
        }*/

        // Log.d(TAG, "code1stock UPDATE STOCK INFO: " + stockQuantity);

        // Bundle bundle = new Bundle();
        // bundle.putLong(ProductBasicInfoFragment.DEFINE_STOCK, stockQuantity);
        // FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment,
        // bundle, 4);

    }

    private void displayRatingInfo() {

        float ratingAverage = mCompleteProduct.getRatingsAverage().floatValue();
        Integer ratingCount = mCompleteProduct.getRatingsCount();
        Integer reviewsCount = mCompleteProduct.getReviewsCount();

        mProductRating.setRating(ratingAverage);

        String rating = getString(R.string.string_ratings).toLowerCase();
        if (ratingCount == 1)
            rating = getString(R.string.string_rating).toLowerCase();

        String review = getString(R.string.reviews).toLowerCase();
        if (reviewsCount == 1)
            review = getString(R.string.review).toLowerCase();

        mProductRatingCount.setText("( " + String.valueOf(ratingCount) + " " + rating + " / "
                + String.valueOf(reviewsCount) + " " + review + " )");

        // if(ratingCount == 1){
        // mProductRatingCount.setText(String.valueOf(ratingCount) + " " +
        // getString(R.string.review) );
        // } else {
        // mProductRatingCount.setText(String.valueOf(ratingCount) + " " +
        // getString(R.string.reviews) );
        // }
        //
        // mProductRatingCount.setText("(" + String.valueOf(ratingCount) + ")");
        loadingRating.setVisibility(View.GONE);
    }

    public void displayVariantsContainer() {
        if (mHideVariationSelection) {
            Log.d(TAG, "HIDE VARIATIONS");
            mVarianceContainer.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "SHOW VARIATIONS");
            mVarianceContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * function responsible for showing the seller info
     */
    public void displaySellerInfo() {

        if (mCompleteProduct.isHasSeller()) {
            sellerView.setVisibility(View.VISIBLE);
            mSellerName.setText(mCompleteProduct.getSeller().getName());
            String rating = getString(R.string.string_ratings).toLowerCase();
            if (mCompleteProduct.getSeller().getRatingCount() == 1)
                rating = getString(R.string.string_rating).toLowerCase();
            mSellerRatingValue
                    .setText(mCompleteProduct.getSeller().getRatingCount() + " " + rating);
            mSellerRating.setRating(mCompleteProduct.getSeller().getRatingValue());
            mSellerDeliveryTime.setText(mCompleteProduct.getSeller().getMinDeliveryTime() + " - "
                    + mCompleteProduct.getSeller().getMinDeliveryTime()
                    + " " + getResources().getString(R.string.product_delivery_days));

        } else {
            sellerView.setVisibility(View.GONE);
        }

    }

    public void updateVariants() {
        // Log.i(TAG, "code1stock size selected : "+mSelectedSimple);
        if (mSelectedSimple == NO_SIMPLE_SELECTED) {
            mVarianceButton.setText("...");
        }

        mSimpleVariants = createSimpleVariants();
        Log.i(TAG, "scanSimpleForKnownVariations : updateVariants " + mSimpleVariants.size());
        ProductSimple simple = getSelectedSimple();

        // mVariantChooseError.setVisibility(View.GONE);

        // Log.i(TAG, "code1stock size selected!" + mSelectedSimple);
        if (simple != null) {
            Log.i(TAG, "size is : " + mSimpleVariants.get(mSelectedSimple));

            // mVariantPriceContainer.setVisibility(View.VISIBLE);
            String normPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);

            if (TextUtils.isEmpty(specPrice)) {
                normPrice = currencyFormatHelper(normPrice);
                // display only the normal price
                mSpecialPriceText.setText(normPrice);
                mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
                mPriceText.setVisibility(View.GONE);
            }
            else {
                normPrice = currencyFormatHelper(normPrice);
                specPrice = currencyFormatHelper(specPrice);
                // display reduced and special price
                mSpecialPriceText.setText(specPrice);
                mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
                mPriceText.setText(normPrice);
                mPriceText.setPaintFlags(mPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mPriceText.setVisibility(View.VISIBLE);
            }

            mVarianceButton.setText(mSimpleVariants.get(mSelectedSimple));
            mVarianceText.setTextColor(getResources().getColor(R.color.grey_middle));
        }

    }

    private String currencyFormatHelper(String number) {
        return CurrencyFormatter.formatCurrency(number);
    }

    private void executeAddProductToCart() {
        ProductSimple simple = getSelectedSimple();
        if (simple == null && !DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) {
            showChooseReminder();
            isAddingProductToCart = false;
            return;
        } else if (simple == null) {
            getBaseActivity().showWarningVariation(true);
            isAddingProductToCart = false;
            return;
        }

        long quantity = 0;
        try {
            quantity = Long.valueOf(simple.getAttributeByKey(ProductSimple.QUANTITY_TAG));
        } catch (NumberFormatException e) {
            Log.e(TAG, "executeAddProductToCart: quantity in simple is not a quantity", e);
        }

        if (quantity == 0) {
            Toast.makeText(getBaseActivity(), R.string.product_outof_stock, Toast.LENGTH_SHORT)
                    .show();
            isAddingProductToCart = false;
            return;
        }

        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        String priceAsString;

        priceAsString = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);
        if (priceAsString == null) {
            priceAsString = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
        }

        // Long price = getPriceForTrackingAsLong(simple);

        if (TextUtils.isEmpty(sku)) {
            isAddingProductToCart = false;
            return;
        }

        // Add one unity to cart
        triggerAddItemToCart(mCompleteProduct.getSku(), sku);

        // Log.i(TAG, "code1price : " + price);

        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, sku);
        bundle.putDouble(TrackerDelegator.PRICE_KEY, mCompleteProduct.getPriceForTracking());
        bundle.putString(TrackerDelegator.NAME_KEY, mCompleteProduct.getName());
        bundle.putString(TrackerDelegator.BRAND_KEY, mCompleteProduct.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, mCompleteProduct.getRatingsAverage());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, mCompleteProduct.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
        if (null != mCompleteProduct && mCompleteProduct.getCategories().size() > 0) {
            bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories().get(0));
            if (null != mCompleteProduct && mCompleteProduct.getCategories().size() > 1) {
                bundle.putString(TrackerDelegator.SUBCATEGORY_KEY, mCompleteProduct.getCategories()
                        .get(1));
            }
        } else {
            bundle.putString(TrackerDelegator.CATEGORY_KEY, "");
        }

        TrackerDelegator.trackProductAddedToCart(bundle);
    }

    /**
     * Add one item to cart
     * 
     * @param sku
     * @param simpleSKU
     * @author sergiopereira
     */
    private void triggerAddItemToCart(String sku, String simpleSKU) {
        ContentValues values = new ContentValues();
        values.put("p", sku);
        values.put("sku", simpleSKU);
        values.put("quantity", "1");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        triggerContentEventProgress(new GetShoppingCartAddItemHelper(), bundle, responseCallback);
    }

    private void showChooseReminder() {
        getBaseActivity().showWarningVariation(true);
    }

    private void displayProduct(CompleteProduct product) {
        Log.d(TAG, "SHOW PRODUCT");
        // Show wizard
        isToShowWizard();
        // Call phone
        setCallPhone();

        // Get simple position from deep link value
        if (mDeepLinkSimpleSize != null) {
            locateSimplePosition(mDeepLinkSimpleSize, product);
        }

        JumiaApplication.INSTANCE.setCurrentProduct(product);
        LastViewedTableHelper.insertLastViewedProduct(product);
        mCompleteProduct = product;
        mCompleteProductUrl = product.getUrl();

        // Set Title
        // #RTL
        if (getResources().getBoolean(R.bool.is_bamilo_specific)) {
            mTitleText.setText(mCompleteProduct.getBrand() != null ? mCompleteProduct.getName() + " " + mCompleteProduct.getBrand() : "");
        } else {
            mTitleText.setText(mCompleteProduct.getBrand() != null ? mCompleteProduct.getBrand() + " " + mCompleteProduct.getName() : "");
        }

        // Set favourite
        try {
            if (FavouriteTableHelper.verifyIfFavourite(mCompleteProduct.getSku())) {
                mCompleteProduct.getAttributes().put(RestConstants.JSON_IS_FAVOURITE_TAG, Boolean.TRUE.toString());
                imageIsFavourite.setSelected(true);
            } else {
                mCompleteProduct.getAttributes().put(RestConstants.JSON_IS_FAVOURITE_TAG, Boolean.FALSE.toString());
                imageIsFavourite.setSelected(false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            mCompleteProduct.getAttributes().put(RestConstants.JSON_IS_FAVOURITE_TAG, Boolean.FALSE.toString());
            imageIsFavourite.setSelected(false);
        }
        
        // Validate gallery
        setProductGallery(product);
        // Validate related items
        setRelatedItems(product.getSku());
        // Validate variations
        setProductVariations();
        // Show product info
        setContentInformation();
        // Bundles
        setBundles(product);

        if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) {
            displayDescription();
            displaySpecification();
        }
        // Tracking
        TrackerDelegator.trackProduct(createBundleProduct());
    }
    
    /**
     * Set the gallery
     * @param mCompleteProduct
     * @author sergiopereira
     */
    private void setProductGallery(CompleteProduct mCompleteProduct) {
        mGalleryViewGroupFactory.setViewVisible(R.id.image_loading_progress);
        RocketImageLoader.getInstance().loadImages(mCompleteProduct.getImageList(), this);
    }
    
    /**
     * 
     * @param product
     */
    private void setBundles(CompleteProduct product) {
        if (product.getProductBundle() != null && product.getProductBundle().getBundleProducts().size() > 0) {
            displayBundle(product.getProductBundle());
        } else
            hideBundle();
    }
    
    /**
     * 
     * @param variations
     * @return
     */
    private boolean isNotValidVariation(ArrayList<Variation> variations) {
        if (variations == null || variations.size() == 0) {
            return true;
        } else if (variations.size() == 1
                && variations.get(0).getSKU().equals(mCompleteProduct.getSku())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ################# VARIATIONS ITEMS #################
     */
    /**
     * Set the variation container. (Colors)
     * 
     * @author manuel
     * @modified sergiopereira
     */
    private void setProductVariations() {
        Log.i(TAG, "ON DISPLAY VARIATIONS");

        // Validate complete product
        if (mCompleteProduct == null) {
            Log.i(TAG, "mCompleteProduct is null -- verify and fix!!!");
            return;
        }

        // Validate variations
        if (isNotValidVariation(mCompleteProduct.getVariations())) {
            if (mVariationsContainer != null)
                mVariationsContainer.setVisibility(View.GONE);
            return;
        }

        // Validate adapter
        if (mVariationsListView.getAdapter() == null) {
            Log.i(TAG, "NEW ADAPTER");
            int position = CompleteProductUtils.findIndexOfSelectedVariation(mCompleteProduct);
            ProductVariationsListAdapter adapter = new ProductVariationsListAdapter(mCompleteProduct.getVariations());
            mVariationsListView.setHasFixedSize(true);
            Boolean isRTL = mContext.getResources().getBoolean(R.bool.is_bamilo_specific);
            if(isRTL) mVariationsListView.enableReverseLayout();
            mVariationsListView.setAdapter(adapter);
            mVariationsListView.setSelecetedItem(position);
            mVariationsListView.setOnItemSelectedListener(new OnViewSelectedListener() {
                @Override
                public void onViewSelected(View view, int position, String url) {
                    Log.i(TAG, "ON SECLECTED ITEM: " + position + " " + url);
                    // Validate if current product has variations
                    if (mCompleteProduct.getVariations() == null
                            || (mCompleteProduct.getVariations().size() <= position))
                        return;
                    // Validate is invalid URL
                    if (TextUtils.isEmpty(url))
                        return;
                    // Validate if is the current product
                    if (url.equals(mCompleteProduct.getUrl()))
                        return;
                    // Saved the selected URL
                    mCompleteProductUrl = url;
                    // Show loading rating
                    loadingRating.setVisibility(View.VISIBLE);
                    // Hide bundle container
                    hideBundle();
                    // Get product to update partial data
                    loadProductPartial();
                }
            });
        }
        // Show container
        mVariationsContainer.setVisibility(View.VISIBLE);
    }

    /**
     * ################# IMAGE GALLERY #################
     */

    /**
     * ################# RELATED ITEMS #################
     */

    /**
     * Method used to get the related products
     */
    private void setRelatedItems(String sku) {
        // Validate if is to show
        if (!mShowRelatedItems) return;
        // Show related items
        Log.d(TAG, "ON GET RELATED ITEMS FOR: " + sku);
        ArrayList<LastViewed> relatedItemsList = RelatedItemsTableHelper.getRelatedItemsList();
        if (relatedItemsList != null && relatedItemsList.size() > 1) {
            for (int i = 0; i < relatedItemsList.size(); i++) {
                String itemSku = relatedItemsList.get(i).getProductSku();
                if (!TextUtils.isEmpty(itemSku) && itemSku.equalsIgnoreCase(sku)) {
                    relatedItemsList.remove(i);
                    break;
                }
            }
            showRelatedItemsLayout(relatedItemsList);
        } else {
            Log.w(TAG, "ONLY OWN PRODUCT ON RELATED ITEMS FOR: " + sku);
        }
    }

    /**
     * Method used to create the view
     * 
     * @param relatedItemsList
     * @modified sergiopereira
     */
    private void showRelatedItemsLayout(ArrayList<LastViewed> relatedItemsList) {
        mRelatedContainer.setVisibility(View.VISIBLE);
        // Use this setting to improve performance if you know that changes in content do not change
        // the layout size of the RecyclerView
        mRelatedListView.setHasFixedSize(true);
        Boolean isRTL = mContext.getResources().getBoolean(R.bool.is_bamilo_specific);
        if(isRTL) mRelatedListView.enableReverseLayout();
        mRelatedListView.setAdapter(new RelatedItemsListAdapter(mContext, relatedItemsList,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show related item
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsIntentExtra.CONTENT_URL, (String) v.getTag());
                        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.grelateditem_prefix);
                        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                        // For tracking as a related item
                        bundle.putBoolean(ConstantsIntentExtra.IS_RELATED_ITEM, true);
                        // inform PDV that Related Items should be shown
                        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
                        ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
                    }
                }));
        // Hide loading
        mRelatedLoading.setVisibility(View.GONE);
    }

    /**
     * Validate if is to show the pager wizard
     */
    private void isToShowWizard() {
        if (WizardPreferences.isFirstTime(getBaseActivity(), WizardType.PRODUCT_DETAIL)) {
            Log.d(TAG, "Show Wizard");
            mainView.findViewById(R.id.product_detail_tips_container).setVisibility(View.VISIBLE);
            ViewPager viewPagerTips = (ViewPager) mainView.findViewById(R.id.viewpager_tips);
            viewPagerTips.setVisibility(View.VISIBLE);
            int[] tips_pages = { R.layout.tip_swipe_layout, R.layout.tip_tap_layout,
                    R.layout.tip_favourite_layout, R.layout.tip_share_layout };
            TipsPagerAdapter mTipsPagerAdapter = new TipsPagerAdapter(getActivity()
                    .getApplicationContext(), getActivity().getLayoutInflater(), mainView,
                    tips_pages);
            viewPagerTips.setAdapter(mTipsPagerAdapter);
            viewPagerTips
                    .setOnPageChangeListener(new TipsOnPageChangeListener(mainView, tips_pages));
            viewPagerTips.setCurrentItem(0);
            mainView.findViewById(R.id.viewpager_tips_btn_indicator).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.tips_got_it_img).setOnClickListener(this);
        }
    }

    /**
     * 
     */
    private void setCallPhone() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, "");
        if (mPhone2Call.equalsIgnoreCase(""))
            mPhone2Call = getString(R.string.call_to_order_number);
    }

    /**
     * Create a bundle for tracking
     * 
     * @author sergiopereira
     */
    private Bundle createBundleProduct() {
        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SOURCE_KEY, mNavigationSource);
        bundle.putString(TrackerDelegator.PATH_KEY, mNavigationPath);
        bundle.putString(TrackerDelegator.NAME_KEY, mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
        bundle.putString(TrackerDelegator.SKU_KEY, mCompleteProduct.getSku());
        bundle.putDouble(TrackerDelegator.PRICE_KEY, mCompleteProduct.getPriceForTracking());
        bundle.putBoolean(TrackerDelegator.RELATED_ITEM, isRelatedItem);
        bundle.putString(TrackerDelegator.BRAND_KEY, mCompleteProduct.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, mCompleteProduct.getRatingsAverage());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, mCompleteProduct.getMaxSavingPercentage());

        if (null != mCompleteProduct && mCompleteProduct.getCategories().size() > 0) {
            bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories().get(0));
            if (null != mCompleteProduct && mCompleteProduct.getCategories().size() > 1) {
                bundle.putString(TrackerDelegator.SUBCATEGORY_KEY, mCompleteProduct.getCategories().get(1));
            }
        }
        return bundle;
    }

    /**
     * Locate the simple size from deep link and save that position
     * 
     * @param simpleSize
     * @param product
     * @author sergiopereira
     */
    private void locateSimplePosition(String simpleSize, CompleteProduct product) {
        Log.d(TAG, "DEEP LINK SIMPLE SIZE: " + simpleSize);
        if (product != null && product.getSimples() != null && product.getSimples().size() > 0)
            for (int i = 0; i < product.getSimples().size(); i++) {
                ProductSimple simple = product.getSimples().get(i);
                if (simple != null && simple.getAttributeByKey("size") != null
                        && simple.getAttributeByKey("size").equals(simpleSize))
                    mSelectedSimple = i;
            }
        Log.d(TAG, "DEEP LINK SIMPLE POSITION: " + mSelectedSimple);
    }

    private void executeAddToShoppingCartCompleted(boolean isBundle) {

        String msgText = "1 " + getResources().getString(R.string.added_to_shop_cart_dialog_text);
        if (isBundle)
            msgText = getResources().getString(R.string.added_bundle_to_shop_cart_dialog_text);

        mDialogAddedToCart = DialogGenericFragment.newInstance(
                false,
                false,
                true,
                getString(R.string.your_cart),
                msgText,
                getString(R.string.go_to_cart), getString(R.string.continue_shopping),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            if (getBaseActivity() != null) {
                                getBaseActivity().onSwitchFragment(
                                        FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE,
                                        FragmentController.ADD_TO_BACK_STACK);
                            }
                            if (mDialogAddedToCart != null) {
                                mDialogAddedToCart.dismiss();
                            }

                        } else if (id == R.id.button2) {
                            showFragmentContentContainer();
                            mDialogAddedToCart.dismiss();
                        }
                    }
                });

        mDialogAddedToCart.show(getFragmentManager(), null);
    }

    private void addToShoppingCartFailed() {
        mDialogAddedToCart = DialogGenericFragment.newInstance(false, false, true, null,
                getResources().getString(R.string.error_add_to_shopping_cart), getResources()
                        .getString(R.string.ok_label), "", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            mDialogAddedToCart.dismiss();
                        } else if (id == R.id.button2) {

                        }
                    }
                });

        mDialogAddedToCart.show(getFragmentManager(), null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case rating
        if (id == R.id.product_detail_product_rating_container)
            onClickRating();
        // Case description
        else if (id == R.id.product_detail_specifications || id == R.id.product_name ||
                id == R.id.product_detail_name || id == R.id.features_more_container ||
                id == R.id.description_more_container) {
            onClickShowDescription();
        }
        // Case variation button
        else if (id == R.id.product_detail_product_variant_button)
            onClickVariantionButton();
        // Case shop product
        else if (id == R.id.product_detail_shop)
            onClickShopProduct();
        // Case call to order
        else if (id == R.id.product_detail_call_to_order)
            onClickCallToOrder();
        // Case wizard
        else if (id == R.id.tips_got_it_img)
            onClickWizardButton();
        // Case favourite
        else if (id == R.id.product_detail_image_is_favourite)
            onClickFavouriteButton();
        // Case retry
        else if (id == R.id.fragment_root_retry_button)
            onClickRetry();
        // Case share
        else if (id == R.id.product_detail_product_image_share)
            onClickShare(mCompleteProduct);
        // Case size guide
        else if (id == R.id.dialog_list_size_guide_button)
            onClickSizeGuide(view);
        // seller link
        else if (id == R.id.seller_name_container)
            goToSellerCatalog();
        // seller rating
        else if (id == R.id.product_detail_product_seller_rating_container)
            goToSellerRating();
        // product offers
        else if (id == R.id.offers_container || id == R.id.product_detail_product_offers_container)
            goToProductOffers();
    }

    /**
     * function that sends the user to the product offers view
     */
    private void goToProductOffers() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_NAME, mCompleteProduct.getName());
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_OFFERS, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * 
     */
    private void onClickRating() {

        JumiaApplication.cleanRatingReviewValues();

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
        bundle.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, true);
        getBaseActivity().onSwitchFragment(FragmentType.POPULARITY,
                bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * 
     */
    private void onClickShowDescription() {
        if (null != mCompleteProduct) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DESCRIPTION, bundle,
                    FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * 
     */
    private void onClickVariantionButton() {
        showVariantsDialog();
    }

    /**
     * 
     */
    private void onClickShopProduct() {
        if (!isAddingProductToCart) {
            isAddingProductToCart = true;
            executeAddProductToCart();
        }
    }

    /**
     * 
     */
    private void onClickCallToOrder() {
        String user_id = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            user_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        TrackerDelegator.trackCall(getActivity().getApplicationContext(), user_id,
                JumiaApplication.SHOP_NAME);
        makeCall();
    }

    /**
     * 
     */
    private void onClickWizardButton() {
        WizardPreferences.changeState(getBaseActivity(), WizardType.PRODUCT_DETAIL);
        try {
            getView().findViewById(R.id.viewpager_tips).setVisibility(View.GONE);
            ((LinearLayout) getView().findViewById(R.id.viewpager_tips_btn_indicator))
                    .setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON HIDE WIZARD");
        }
    }

    /**
     * 
     */
    private void onClickFavouriteButton() {

        boolean isFavourite = false;
        if (mCompleteProduct != null && mCompleteProduct.getAttributes() != null) {
            Object isFavoriteObject = mCompleteProduct.getAttributes().get(
                    RestConstants.JSON_IS_FAVOURITE_TAG);
            if (isFavoriteObject != null && isFavoriteObject instanceof String) {
                isFavourite = Boolean.parseBoolean((String) isFavoriteObject);
            }
        } else {
            Log.w(TAG, "mCompleteProduct is null or doesn't have attributes");
            return;
        }
        int fragmentMessage = 0;

        String sku = mCompleteProduct.getSku();
        if (getSelectedSimple() != null)
            sku = getSelectedSimple().getAttributeByKey(RestConstants.JSON_SKU_TAG);

        if (!isFavourite) {
            fragmentMessage = BaseFragment.FRAGMENT_VALUE_SET_FAVORITE;
            FavouriteTableHelper.insertFavouriteProduct(mCompleteProduct);
            mCompleteProduct.getAttributes().put(RestConstants.JSON_IS_FAVOURITE_TAG,
                    Boolean.TRUE.toString());
            imageIsFavourite.setSelected(true);

            TrackerDelegator.trackAddToFavorites(sku, mCompleteProduct.getBrand(),
                    mCompleteProduct.getPriceForTracking(),
                    mCompleteProduct.getRatingsAverage(),
                    mCompleteProduct.getMaxSavingPercentage(), false,
                    mCompleteProduct.getCategories());
            Log.e("TOAST", "USE SuperToast");
            Toast.makeText(getBaseActivity(), getString(R.string.products_added_favourite),
                    Toast.LENGTH_SHORT).show();
        } else {
            fragmentMessage = BaseFragment.FRAGMENT_VALUE_REMOVE_FAVORITE;
            FavouriteTableHelper.removeFavouriteProduct(mCompleteProduct.getSku());
            mCompleteProduct.getAttributes().put(RestConstants.JSON_IS_FAVOURITE_TAG,
                    Boolean.FALSE.toString());
            imageIsFavourite.setSelected(false);

            TrackerDelegator.trackRemoveFromFavorites(sku, mCompleteProduct.getPriceForTracking(),
                    mCompleteProduct.getRatingsAverage());
            Toast.makeText(getBaseActivity(), getString(R.string.products_removed_favourite),
                    Toast.LENGTH_SHORT).show();
        }

        BaseFragment catalogFragment = (BaseFragment) getBaseActivity().getSupportFragmentManager()
                .findFragmentByTag(FragmentType.PRODUCT_LIST.toString());
        if (null != catalogFragment) {
            catalogFragment.sendValuesToFragment(fragmentMessage, mCompleteProduct.getSku());
        }

    }

    /**
     * Process the click on retry
     * 
     * @author sergiopereira
     */
    private void onClickRetry() {
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, getArguments(),
                FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on share.
     * 
     * @param completeProduct
     * @author sergiopereira
     */
    private void onClickShare(CompleteProduct completeProduct) {
        try {
            Intent shareIntent = getBaseActivity().createShareIntent();
            startActivity(shareIntent);
            // Track share
            TrackerDelegator.trackItemShared(shareIntent, completeProduct.getCategories().get(0));
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK SHARE");
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING: IOB ON CLICK SHARE");
        }
    }

    /**
     * Process click on size guide.
     * 
     * @author sergiopereira
     */
    private void onClickSizeGuide(View view) {
        try {
            // Get size guide URL
            String url = (String) view.getTag();
            // Validate url
            if (!TextUtils.isEmpty(url)) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, url);
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_SIZE_GUIDE, bundle,
                        FragmentController.ADD_TO_BACK_STACK);
            } else
                Log.w(TAG, "WARNING: SIZE GUIDE URL IS EMPTY");
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
        }
    }

    private void makeCall() {
        // Displays the phone number but the user must press the Call button to begin the phone call
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone2Call));
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showVariantsDialog() {
        try {
            getBaseActivity().showWarningVariation(false);
            String title = getString(R.string.product_variance_choose);
            dialogListFragment = DialogListFragment.newInstance(this,
                    VARIATION_PICKER_ID,
                    title,
                    mSimpleVariants,
                    mSimpleVariantsAvailable,
                    mSelectedSimple,
                    mCompleteProduct.getSizeGuideUrl());
            dialogListFragment.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener#onDialogListItemSelect
     * (java.lang.String, int, java.lang.String)
     */
    @Override
    public void onDialogListItemSelect(String id, int position, String value) {
        mSelectedSimple = position;
        Log.i(TAG, "size selected! onDialogListItemSelect : " + mSelectedSimple);
        updateVariants();
        updateStockInfo();
        displayPriceInfoOverallOrForSimple();

    }

    IResponseCallback responseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    public void onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (getBaseActivity() == null)
            return;

        super.handleSuccessEvent(bundle);

        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            getBaseActivity().updateCartInfo();
            hideActivityProgress();
            mAddToCartButton.setEnabled(true);
            executeAddToShoppingCartCompleted(false);
            break;
        case SEARCH_PRODUCT:
        case GET_PRODUCT_EVENT:
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getBaseActivity().onBackPressed();
                return;
            } else {
                mCompleteProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                // Show product or update partial
                ProductImageGalleryFragment.sSharedSelectedPosition = 0;
                // Show product or update partial
                displayProduct(mCompleteProduct);
                // 
                Bundle params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gproductdetail);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                TrackerDelegator.trackLoadTiming(params);

                params = new Bundle();
                params.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
                if (JumiaApplication.CUSTOMER != null) {
                    params.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER);
                }
                params.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
                params.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
                params.putParcelable(AdjustTracker.PRODUCT, mCompleteProduct);
                params.putString(AdjustTracker.TREE, categoryTree);
                TrackerDelegator.trackPage(TrackingPage.PRODUCT_DETAIL_LOADED, getLoadTime(), false);
                TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_DETAIL_LOADED, params);
            }

            // Waiting for the fragment comunication
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFragmentContentContainer();
                }
            }, 300);

            if (mCompleteProduct.isHasBundle()) {
                Bundle arg = new Bundle();
                arg.putString(GetProductBundleHelper.PRODUCT_SKU, mCompleteProduct.getSku());
                triggerContentEventWithNoLoading(new GetProductBundleHelper(), arg, responseCallback);
            }
            break;
        case GET_PRODUCT_BUNDLE:
            mProductBundle = (ProductBundle) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if (mProductBundle != null)
                displayBundle(mProductBundle);
            else
                hideBundle();
            break;
        case ADD_PRODUCT_BUNDLE:
            isAddingProductToCart = false;
            getBaseActivity().updateCartInfo();
            hideActivityProgress();
            mBundleButton.setEnabled(true);
            mAddToCartButton.setEnabled(true);
            executeAddToShoppingCartCompleted(true);
            break;
        default:
            break;
        }
    }

    public void onErrorEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        hideActivityProgress();

        if (super.handleErrorEvent(bundle)) {
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case ADD_PRODUCT_BUNDLE:
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            mBundleButton.setEnabled(true);
            isAddingProductToCart = false;
            hideActivityProgress();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

                if (errorMessages != null) {
                    int titleRes = R.string.error_add_to_cart_failed;
                    int msgRes = -1;

                    String message = null;
                    if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(
                            Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                        msgRes = R.string.product_outof_stock;
                    } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(
                            Errors.CODE_PRODUCT_ADD_OVERQUANTITY)) {
                        msgRes = R.string.error_add_to_shopping_cart_quantity;
                    } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(
                            Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                        List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                        if (validateMessages != null && validateMessages.size() > 0) {
                            message = validateMessages.get(0);
                        } else {
                            msgRes = R.string.error_add_to_cart_failed;
                        }
                    }

                    if (msgRes != -1) {
                        message = getString(msgRes);
                    } else if (message == null) {
                        return;
                    }

                    FragmentManager fm = getFragmentManager();
                    dialog = DialogGenericFragment.newInstance(true, true, false,
                            getString(titleRes),
                            message,
                            getString(R.string.ok_label), "", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dismissDialogFragement();
                                    }
                                }
                            });
                    dialog.show(fm, null);
                    return;
                }
            }
            if (!errorCode.isNetworkError()) {
                addToShoppingCartFailed();
                return;
            }
        case SEARCH_PRODUCT:
        case GET_PRODUCT_EVENT:
            if (!errorCode.isNetworkError()) {
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved),
                        Toast.LENGTH_LONG).show();

                showFragmentContentContainer();

                try {
                    getBaseActivity().onBackPressed();
                } catch (IllegalStateException e) {
                    getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
                }
                return;
            }
        case GET_PRODUCT_BUNDLE:
            hideBundle();
            break;

        default:
            break;
        }
    }

    /**
     * display product specification on landscape
     */
    private void displaySpecification() {
        String shortDescription = mCompleteProduct.getShortDescription();
        // Don't show the features box if there is no content for it
        if (TextUtils.isEmpty(shortDescription)) {
            Log.i(TAG, "shortDescription : empty");
            if (mProductFeaturesContainer != null) {
                mProductFeaturesContainer.setVisibility(View.GONE);
            }
            return;
        } else {
            mProductFeaturesContainer.setVisibility(View.VISIBLE);

            String translatedDescription = shortDescription.replace("\r", "<br>");
            Log.d(TAG, "displaySpecification: *" + translatedDescription + "*");

            Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
            // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML
            // with bold tags
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);
                MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(),
                        MetricAffectingSpan.class);
                for (MetricAffectingSpan span : spans) {
                    htmlText.removeSpan(span);
                }
            }
            mProductFeaturesText.setText(htmlText);

            showMoreButton(mProductFeaturesText, mProductFeaturesMore);

        }
    }

    /**
     * display product description on landscape
     */
    private void displayDescription() {
        if (mProductDescriptionContainer == null) {
            Log.w(TAG, "Product details container is null on landscape");
            return;
        }
        String longDescription = mCompleteProduct.getDescription();
        if (longDescription.isEmpty()) {
            mProductDescriptionContainer.setVisibility(View.GONE);
        } else {
            mProductDescriptionContainer.setVisibility(View.VISIBLE);
        }
        String translatedDescription = longDescription.replace("\r", "<br>");
        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with
        // bold tags
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);
            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(),
                    MetricAffectingSpan.class);
            for (MetricAffectingSpan span : spans) {
                htmlText.removeSpan(span);
            }
        }
        mProductDescriptionText.setText(htmlText);

        showMoreButton(mProductDescriptionText, mProductDescriptionMore);

    }

    /**
     * function used to calculate if text is all visible or not, on order to show the show more
     * buttom
     * 
     * @param textView
     *            , view to count the lines
     * @param moreView
     *            , view to hide or show
     */
    private void showMoreButton(final TextView textView, final LinearLayout moreView) {

        try {
            ViewTreeObserver observer = textView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    int maxLines = (int) textView.getHeight() / textView.getLineHeight();

                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    Log.d("COUNT LINE", ":" + maxLines);
                    if (textView.getLineCount() >= maxLines) {
                        moreView.setVisibility(View.VISIBLE);

                    } else {
                        moreView.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            moreView.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

    }

    /**
     * 
     * Function responsible for showing the product bundle if it exists
     * 
     * @param bundle
     */
    private void displayBundle(ProductBundle bundle) {

        mBundleContainer.setVisibility(View.VISIBLE);

        CompleteProduct curProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        curProduct.setProductBundle(bundle);
        JumiaApplication.INSTANCE.setCurrentProduct(curProduct);

        // calculate the bundle total without the plead product
        double total = 0.0;
        // validate if any product has simples do adjust item size
        boolean hasSimples = false;
        ArrayList<ProductBundleProduct> bundleProducts = bundle.getBundleProducts();
        for (int i = 0; i < bundleProducts.size(); i++) {

            if (bundleProducts.get(i).getBundleSimples() != null
                    && bundleProducts.get(i).getBundleSimples().size() > 1) {
                hasSimples = true;
            }

            if (bundleProducts.get(i).isChecked()) {
                if (bundleProducts.get(i).hasDiscount()) {
                    total = total + bundleProducts.get(i).getBundleProductSpecialPriceDouble();
                } else {
                    total = total + bundleProducts.get(i).getBundleProductPriceDouble();
                }
            }
        }

        validateBundleButton();

        mBundleTextTotal.setText(CurrencyFormatter.formatCurrency(String.valueOf(total)));

        mBundleTextTotal.setTag(total);

        if (hasSimples) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension( R.dimen.teaser_product_bundle_item_width));
            params.addRule(RelativeLayout.BELOW, mDividerBundle.getId());
            mBundleListView.setLayoutParams(params);
        }

        // Use this setting to improve performance
        mBundleListView.setHasFixedSize(true);
        // RTL
        Boolean isRTL = mContext.getResources().getBoolean(R.bool.is_bamilo_specific);
        if(isRTL) mBundleListView.enableReverseLayout();
        // Content
        mBundleListView.setAdapter(new BundleItemsListAdapter(mContext, bundleProducts, (OnItemSelected) this, (OnItemChecked) this, (OnSimplePressed) this, (OnItemSelectedListener) this));
        mBundleLoading.setVisibility(View.GONE);
        mBundleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerAddBundleToCart(mProductBundle);

            }
        });

    }

    /**
     * Hide the bundle container.
     */
    private void hideBundle() {
        mBundleContainer.setVisibility(View.GONE);
    }

    /**
     * validate if theres any product selected to enable or not the buy now button
     */
    private void validateBundleButton() {
        boolean hasSomeProdSelected = false;
        if (mProductBundle != null) {

            for (int i = 0; i < mProductBundle.getBundleProducts().size(); i++) {
                if (mProductBundle.getBundleProducts().get(i).getBundleProductLeaderPos() != 0
                        && mProductBundle.getBundleProducts().get(i).isChecked()) {
                    hasSomeProdSelected = true;
                }
            }

            if (hasSomeProdSelected) {
                mBundleButton.setEnabled(true);
            } else {
                mBundleButton.setEnabled(false);
            }

        }

    }

    /**
     * function responsible for handling the item click of the bundle
     * 
     * @param selectedOrder
     */
    @Override
    public void SelectedItem(ProductBundleProduct selectedProduct) {
        // TODO TO BE IMPLEMENTED
        Log.d("BUNDLE", "GO TO PDV");
    }

    /**
     * 
     * function responsible for handling on item of the bundle
     * 
     * @param selectedOrder
     * @param productsContainer
     * @param toShow
     * @param selectedProd
     */
    @Override
    public void checkItem(ProductBundleProduct selectedProduct, boolean isChecked, int pos) {
        // if isChecked is false then item was deselected
        double priceChange = selectedProduct.getBundleProductMaxSpecialPriceDouble();
        if (priceChange == 0) {
            priceChange = selectedProduct.getBundleProductMaxPriceDouble();
        }
        double totalPrice = (Double) mBundleTextTotal.getTag();

        if (!isChecked) {
            totalPrice = totalPrice - priceChange;
            // CurrencyFormatter.formatCurrency(String.valueOf(totalPrice));
            mBundleTextTotal.setTag(totalPrice);
            mBundleTextTotal.setText(CurrencyFormatter.formatCurrency(String.valueOf(totalPrice)));
            if (mProductBundle != null)
                mProductBundle.getBundleProducts().get(pos).setChecked(false);

            validateBundleButton();

        } else {
            totalPrice = totalPrice + priceChange;
            // CurrencyFormatter.formatCurrency(String.valueOf(totalPrice));
            mBundleTextTotal.setTag(totalPrice);
            mBundleTextTotal.setText(CurrencyFormatter.formatCurrency(String.valueOf(totalPrice)));
            if (mProductBundle != null)
                mProductBundle.getBundleProducts().get(pos).setChecked(true);

            mBundleButton.setEnabled(true);
        }

    }

    /**
     * function to catch the user click on the size button within the bundle product
     */
    @Override
    public void PressedSimple(ProductBundleProduct selectedProduct) {
        showBundleSimples(selectedProduct);
    }

    /**
     * function responsible for showing the the pop with the simples
     * 
     * @param selectedProduct
     */
    private void showBundleSimples(ProductBundleProduct selectedProduct) {

    }

    private void triggerAddBundleToCart(ProductBundle mProductBundle) {
        mBundleButton.setEnabled(false);
        int count = 0;
        ContentValues values = new ContentValues();
        values.put(GetShoppingCartAddBundleHelper.BUNDLE_ID, mProductBundle.getBundleId());
        for (int i = 0; i < mProductBundle.getBundleProducts().size(); i++) {
            // if(mProductBundle.getBundleProducts().get(i).isChecked() &&
            // mProductBundle.getBundleProducts().get(i).getBundleProductLeaderPos() != 0){
            if (mProductBundle.getBundleProducts().get(i).isChecked()) {
                values.put(GetShoppingCartAddBundleHelper.PRODUCT_SKU_TAG + count + "]",
                        mProductBundle.getBundleProducts().get(i).getBundleProductSku());
                values.put(
                        GetShoppingCartAddBundleHelper.PRODUCT_SIMPLE_SKU_TAG + count + "]",
                        mProductBundle
                                .getBundleProducts()
                                .get(i)
                                .getBundleSimples()
                                .get(mProductBundle.getBundleProducts().get(i)
                                        .getSimpleSelectedPos()).getSimpleSku());
                count++;

                // GA BUNDLE TRACKING
                TrackerDelegator.trackAddBundleToCart(
                        mProductBundle
                                .getBundleProducts()
                                .get(i)
                                .getBundleSimples()
                                .get(mProductBundle.getBundleProducts().get(i)
                                        .getSimpleSelectedPos()).getSimpleSku(),
                        mProductBundle
                                .getBundleProducts()
                                .get(i)
                                .getBundleSimples()
                                .get(mProductBundle.getBundleProducts().get(i)
                                        .getSimpleSelectedPos()).getPriceForTracking());
            }
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddBundleHelper.ADD_BUNDLE, values);
        triggerContentEventProgress(new GetShoppingCartAddBundleHelper(), bundle, responseCallback);
    }

    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getItemAtPosition(position);

        if (object instanceof ProductBundleSimple) {
            ProductBundleSimple productSimple = (ProductBundleSimple) object;
            if (mProductBundle != null) {
                mProductBundle.getBundleProducts().get(productSimple.getProductParentPos())
                        .setSimpleSelectedPos(position);
            }
        }
    }

    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) {
        // ...
    }

    /**
     * function responsible for calling the catalog with the products from a specific seller
     */
    private void goToSellerCatalog() {
        Log.d("SELLER", "GO TO CATALOG");
    }

    /**
     * function responsible for showing the rating and reviews of a specific seller
     */
    private void goToSellerRating() {
        JumiaApplication.cleanRatingReviewValues();
        
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
        bundle.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, false);
        getBaseActivity().onSwitchFragment(FragmentType.POPULARITY, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.utils.imageloader.RocketImageLoader.RocketImageLoaderLoadImagesListener#onCompleteLoadingImages(java.util.ArrayList)
     */
    @Override
    public void onCompleteLoadingImages(ArrayList<ImageHolder> successUrls) {
        Log.i(TAG, "ON COMPLETE LOADING IMAGES");
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        // Gets all urls with success
        ArrayList<String> urls = new ArrayList<String>();
        for(ImageHolder imageHolder : successUrls) urls.add(imageHolder.url);
        
        // Validate the number of cached images
        if (!successUrls.isEmpty()) {
            // Match the cached image list with the current image list order
            ArrayList<String> orderCachedImageList = (ArrayList<String>) CollectionUtils.retainAll(mCompleteProduct.getImageList(), urls);
            // Set the cached images
            mCompleteProduct.setImageList(orderCachedImageList);
            // Create bundle with arguments
            Bundle args = new Bundle();
            args.putStringArrayList(ConstantsIntentExtra.IMAGE_LIST, orderCachedImageList);
            args.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            
            // Validate the ProductImageGalleryFragment
            ProductImageGalleryFragment productImagesViewPagerFragment = (ProductImageGalleryFragment) getChildFragmentManager().findFragmentByTag(ProductImageGalleryFragment.TAG);
            // CASE CREATE
            if (productImagesViewPagerFragment == null) {
                Log.i(TAG, "SHOW GALLERY: first time position = " + ProductImageGalleryFragment.sSharedSelectedPosition);
                productImagesViewPagerFragment = ProductImageGalleryFragment.getInstanceAsNested(args);
                fragmentManagerTransition(R.id.product_detail_image_gallery_container, productImagesViewPagerFragment);
            }
            // CASE UPDATE
            else {
                Log.i(TAG, "SHOW GALLERY: second time position = " + ProductImageGalleryFragment.sSharedSelectedPosition);
                productImagesViewPagerFragment.notifyFragment(args);
            }
            // Show container
            mGalleryViewGroupFactory.setViewVisible(R.id.product_detail_image_gallery_container);
        } else {
            Log.i(TAG, "SHOW PLACE HOLDER");
            // Show place holder
            mGalleryViewGroupFactory.setViewVisible(R.id.image_place_holder);
        }
    }
    
    /**
     * Add/Replace the container to show a new nested fragment.<br>
     * @param container
     * @param fragment
     * @author sergiopereira
     */
    protected void fragmentManagerTransition(int container, Fragment fragment) {
        // Transaction
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Animations
        fragmentTransaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out, R.anim.pop_in, R.anim.pop_out);
        // Replace
        fragmentTransaction.replace(container, fragment, ProductImageGalleryFragment.TAG);
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }
}