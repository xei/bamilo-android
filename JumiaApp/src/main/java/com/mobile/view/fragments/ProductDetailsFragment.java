package com.mobile.view.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.components.recycler.HorizontalListView.OnViewSelectedListener;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.BundleItemsListAdapter;
import com.mobile.controllers.BundleItemsListAdapter.OnItemChecked;
import com.mobile.controllers.BundleItemsListAdapter.OnItemSelected;
import com.mobile.controllers.ProductVariationsListAdapter;
import com.mobile.controllers.RelatedItemsListAdapter;
import com.mobile.controllers.TipsPagerAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.ViewGroupFactory;
import com.mobile.helpers.cart.GetShoppingCartAddBundleHelper;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetProductBundleHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.database.BrandsTableHelper;
import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.Variation;
import com.mobile.newFramework.objects.product.pojo.ProductBase;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;
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
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

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
public class ProductDetailsFragment extends BaseFragment implements IResponseCallback, OnDialogListListener, OnItemChecked, OnItemSelected, RocketImageLoaderLoadImagesListener {

    private final static String TAG = ProductDetailsFragment.class.getSimpleName();

    private final static int NO_SIMPLE_SELECTED = -1;

    private final static String VARIATION_PICKER_ID = "variation_picker";

    private static String SELECTED_SIMPLE_POSITION = "selected_simple_position";

    public final static String PRODUCT_BUNDLE = "product_bundle";

    private Context mContext;

    private DialogFragment mDialogAddedToCart;

    private DialogListFragment dialogListFragment;

    private ProductComplete mCompleteProduct;

    private Button mAddToCartButton;

    private ViewGroup mVarianceContainer;

    private String mCompleteProductSku;

    private int mSelectedSimple = NO_SIMPLE_SELECTED;

    private RatingBar mProductRating;

    private TextView mProductRatingCount;

    private Button mVarianceButton;

    private TextView mVarianceText;

    private ImageView mImageFavourite;

    private long mBeginRequestMillis;

    private ArrayList<String> mSimpleVariants;

    private ArrayList<String> mSimpleVariantsAvailable;

    private String mNavigationPath;

    private String mNavigationSource;

    private RelativeLayout loadingRating;

    private String mPhone2Call = "";

    boolean isAddingProductToCart = false;

    //private ArrayList<String> variations;

    private String mDeepLinkSimpleSize;

    private TextView mSpecialPriceText;

    private TextView mPriceText;

    private TextView mTitleText;

    private TextView mDiscountPercentageText;

    private View mRelatedLoading;

    private View mRelatedContainer;

    private boolean isRelatedItem = false;

    private HorizontalListView mRelatedListView;

    private static String categoryTree = "";

    private BundleList mProductBundle;

    private View mBundleContainer;

    private HorizontalListView mBundleListView;

    private View mBundleLoading;

    private View mDividerBundle;

    private TextView mBundleTextTotal;

    private Button mBundleButton;

    private View mVariationsContainer;

    private HorizontalListView mVariationsListView;

    private RelativeLayout sellerView;

    private TextView mSellerName;

    private TextView mSellerRatingValue;

    private TextView mSellerDeliveryTime;

    private RatingBar mSellerRating;

    private RelativeLayout offersContainer;

    private RelativeLayout offersContent;

    private TextView numOffers;

    private TextView minOffers;

    private TextView mDetailsSection;

    private View mDetailsSectionLine;

    private ViewGroupFactory mGalleryViewGroupFactory;

    public static final String SELLER_ID = "sellerId";

    private View mWizardContainer;

    private View mSellerDeliveryContainer;

    private boolean mHideWizard = false;

    /**
     * Empty constructor
     */
    public ProductDetailsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.product_details_fragment_main,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /**
     *
     */
    public static ProductDetailsFragment getInstance(android.os.Bundle bundle) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.d(TAG, "ON CREATE");
        // Get arguments
        android.os.Bundle arguments = getArguments();
        if(arguments != null) {
            // Get sku
            mCompleteProductSku = arguments.getString(ConstantsIntentExtra.PRODUCT_SKU);
            // Categories
            if (arguments.containsKey(ConstantsIntentExtra.CATEGORY_TREE_NAME)) {
                categoryTree = arguments.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME) + ",PDV";
            } else {
                categoryTree = "";
            }
            // Get wizard flag
            mHideWizard = arguments.getBoolean(ConstantsIntentExtra.TO_SHOW_WIZARD);
        }
        // Get data from saved instance
        if (savedInstanceState != null) {
            mSelectedSimple = savedInstanceState.getInt(SELECTED_SIMPLE_POSITION, NO_SIMPLE_SELECTED);
            mProductBundle = savedInstanceState.getParcelable(PRODUCT_BUNDLE);
        }
        Print.d(TAG, "CURRENT SELECTED SIMPLE: " + mSelectedSimple);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.product_detail_view.View,
     * android.product_detail_os.Bundle)
     */
    @Override
    public void onViewCreated(View view, android.os.Bundle savedInstanceState) {
        Print.d(TAG, "ON VIEW CREATED");
        super.onViewCreated(view, savedInstanceState);
        // Context
        mContext = getBaseActivity();
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
        Print.d(TAG, "ON RESUME");

        if (mCompleteProduct == null) {
            init();
        } else {
            // Must get other params other than currentProduct
            // Get arguments
            android.os.Bundle bundle = getArguments();
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
    public void onSaveInstanceState(android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save product bundle
        if (mProductBundle != null) {
            outState.putParcelable(PRODUCT_BUNDLE, mProductBundle);
            outState.putInt(SELECTED_SIMPLE_POSITION, mSelectedSimple);
        }
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
        // Hide dialog progress
        hideActivityProgress();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.d(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.d(TAG, "ON DESTROY");
    }

    /**
     *
     */
    private void init() {
        Print.d(TAG, "INIT");
        // Get arguments
        android.os.Bundle bundle = getArguments();
        // Validate deep link arguments
        if (hasArgumentsFromDeepLink(bundle))
            return;

        restoreParams(bundle);

        // Validate url and load product
        if (TextUtils.isEmpty(mCompleteProductSku)) {
            getBaseActivity().onBackPressed();
        } else {
            // Url and parameters
            triggerLoadProduct(mCompleteProductSku);
        }
    }

    private void restoreParams(android.os.Bundle bundle) {
        // Get source and path
        mNavigationSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcatalog));
        mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
        // Determine if related items should be shown
        isRelatedItem = bundle.getBoolean(ConstantsIntentExtra.IS_RELATED_ITEM);
    }

    /**
     * Validate and loads the received arguments comes from deep link process.
     */
    private boolean hasArgumentsFromDeepLink(android.os.Bundle bundle) {
        // Get the sku
        String sku = bundle.getString(GetProductHelper.SKU_TAG);
        // Get the simple size
        mDeepLinkSimpleSize = bundle.getString(DeepLinkManager.PDV_SIZE_TAG);
        // Validate
        if (sku != null) {
            Print.i(TAG, "DEEP LINK GET PDV: " + sku + " " + mDeepLinkSimpleSize);
            mNavigationSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix));
            mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
            triggerLoadProduct(sku);
            return true;
        }
        return false;
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout(View view) {
        // Wizard container
        mWizardContainer = view.findViewById(R.id.product_detail_tips_container);
        // Favourite
        mImageFavourite = (ImageView) view.findViewById(R.id.product_detail_image_is_favourite);
        mImageFavourite.setOnClickListener(this);
        // Share
        view.findViewById(R.id.product_detail_product_image_share).setOnClickListener(this);
        // Discount percentage
        mDiscountPercentageText = (TextView) view.findViewById(R.id.product_detail_discount_percentage);
        // Prices
        mSpecialPriceText = (TextView) view.findViewById(R.id.product_price_special);
        mPriceText = (TextView) view.findViewById(R.id.product_price_normal);
        // Variations
        mVarianceContainer = (ViewGroup) view.findViewById(R.id.product_detail_product_variant_container);
        mVarianceText = (TextView) view.findViewById(R.id.product_detail_product_variant_text); // TODO
        mVarianceButton = (Button) view.findViewById(R.id.product_detail_product_variant_button); // TODO
        mVarianceButton.setOnClickListener(this);
        // Rating
        ViewGroup mProductRatingContainer = (ViewGroup) view.findViewById(R.id.product_detail_product_rating_container);
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
        //in order to marquee work on 2.3+ devices
        mBundleTextTotal.setSelected(true);
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
        Button mCallToOrderButton = (Button) view.findViewById(R.id.product_detail_call_to_order);
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            mCallToOrderButton.setSelected(true);
            mCallToOrderButton.setOnClickListener(this);
        } else {
            mCallToOrderButton.setVisibility(View.GONE);
        }
        // Get title from portrait or landscape
        mTitleText = (TextView) view.findViewById(R.id.product_detail_name);
        mTitleText.setOnClickListener(this);
        // Seller info
        sellerView = (RelativeLayout) view.findViewById(R.id.seller_info);
        RelativeLayout mSellerNameContainer = (RelativeLayout) view.findViewById(R.id.seller_name_container);
        mSellerNameContainer.setOnClickListener(this);
        mSellerName = (TextView) view.findViewById(R.id.product_detail_seller_name);
        RelativeLayout mSellerRatingContainer = (RelativeLayout) view.findViewById(R.id.product_detail_product_seller_rating_container);
        mSellerRatingContainer.setOnClickListener(this);
        mSellerRatingValue = (TextView) view.findViewById(R.id.product_detail_product_seller_rating_count);
        mSellerDeliveryTime = (TextView) view.findViewById(R.id.product_detail_seller_delivery_time);
        mSellerDeliveryContainer = view.findViewById(R.id.delivery_time_container);
        mSellerRating = (RatingBar) view.findViewById(R.id.product_detail_product_seller_rating);
        sellerView.setVisibility(View.GONE);
        //Details section
        mDetailsSection = (TextView) view.findViewById(R.id.product_detail_specifications);
        mDetailsSectionLine = view.findViewById(R.id.review_details_line);

        mGalleryViewGroupFactory = new ViewGroupFactory((ViewGroup) view.findViewById(R.id.product_image_layout));
    }

    /**
     * Validate its to show specifications or not
     */
    private void checkProductDetailsVisibility() {
        if (mCompleteProduct != null && mDetailsSection != null && mDetailsSectionLine != null) {
            if (CollectionUtils.isEmpty(mCompleteProduct.getProductSpecifications()) &&
                    TextUtils.isEmpty(mCompleteProduct.getShortDescription()) &&
                    TextUtils.isEmpty(mCompleteProduct.getDescription())) {
                mDetailsSection.setVisibility(View.GONE);
                mDetailsSectionLine.setVisibility(View.GONE);
            }
            mDetailsSection.setOnClickListener(this);
        }
    }

    private void setOffersInfo() {
        if (mCompleteProduct != null && mCompleteProduct.getTotalOffers() > 0) {
            offersContainer.setVisibility(View.VISIBLE);
            numOffers.setText(" (" + mCompleteProduct.getTotalOffers() + ")");
            minOffers.setText(CurrencyFormatter.formatCurrency(mCompleteProduct.getMinPriceOffer()));
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
     * @return
     */
    private ArrayList<String> createSimpleVariants() {
        Print.i(TAG, "CREATE VALID SIMPLE VARIATIONS");
        // Available simples
        mSimpleVariantsAvailable = new ArrayList<>();
        ArrayList<String> variationValues = new ArrayList<>();
        for (ProductSimple simple : mCompleteProduct.getSimples()) {
            String value = simple.getVariationValue();
            int quantity = simple.getQuantity();
            Print.i(TAG, "SIMPLE VARIATION: " + value + " " + quantity);
            if (quantity > 0) {
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
     * @return
     */
    private @Nullable
    ProductSimple getSelectedSimple() {
        // Case Own simple variation
        if(mCompleteProduct.hasOwnSimpleVariation()) {
            return  mCompleteProduct.getSimples().get(0);
        }
        // Case Multi simple variations
        else if(mCompleteProduct.hasMultiSimpleVariations() && mSelectedSimple != NO_SIMPLE_SELECTED && mSelectedSimple < mCompleteProduct.getSimples().size()) {
            return mCompleteProduct.getSimples().get(mSelectedSimple);
        }
        // Case invalid
        else {
            return null;
        }
    }

    /**
     *
     */
    private void setPriceInfoOverallOrForSimple() {
        // Get selected simple variation
        ProductSimple simple = getSelectedSimple();
        // Case valid simple
        if (simple != null) {
            setProductPriceInfo(simple);
        } else {
            setProductPriceInfo(mCompleteProduct);
        }
    }

    /**
     *
     */
    private void setProductPriceInfo(ProductBase productBase) {
        Print.d(TAG, "SHOW PRICE INFO: " + productBase.getPrice() + " " + productBase.getSpecialPrice());
        if (productBase.hasDiscount()) {
            // display reduced and special price
            mSpecialPriceText.setText(CurrencyFormatter.formatCurrency(productBase.getSpecialPrice()));
            mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
            mPriceText.setText(CurrencyFormatter.formatCurrency(productBase.getPrice()));
            mPriceText.setPaintFlags(mPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mPriceText.setVisibility(View.VISIBLE);
        } else {
            // display only the normal price
            mSpecialPriceText.setText(CurrencyFormatter.formatCurrency(productBase.getPrice()));
            mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
            mPriceText.setVisibility(View.GONE);
        }
        // Set discount percentage value
        if (productBase.getMaxSavingPercentage() > 0) {
            String discount = String.format(getString(R.string.format_discount_percentage), productBase.getMaxSavingPercentage());
            mDiscountPercentageText.setText(discount);
            mDiscountPercentageText.setVisibility(View.VISIBLE);
        } else {
            mDiscountPercentageText.setVisibility(View.GONE);
        }
    }

    /**
     * TODO: Use Placeholders
     */
    private void setRatingInfo() {
        float ratingAverage = (float) mCompleteProduct.getAvgRating();
        Integer ratingCount = mCompleteProduct.getTotalRatings();
        Integer reviewsCount = mCompleteProduct.getTotalReviews();
        mProductRating.setRating(ratingAverage);
        String rating = getString(R.string.string_ratings).toLowerCase();
        if (ratingCount == 1)
            rating = getString(R.string.string_rating).toLowerCase();
        String review = getString(R.string.reviews).toLowerCase();
        if (reviewsCount == 1)
            review = getString(R.string.review).toLowerCase();
        mProductRatingCount.setText("( " + String.valueOf(ratingCount) + " " + rating + " / " + String.valueOf(reviewsCount) + " " + review + ")");
        loadingRating.setVisibility(View.GONE);
    }

    public void setSimpleVariationsContainer() {
        Print.d(TAG, "PRODUCT HAS SIMPLE VARIATIONS: " + mCompleteProduct.hasMultiSimpleVariations());
        if (mCompleteProduct.hasMultiSimpleVariations()) {
            mVarianceContainer.setVisibility(View.VISIBLE);
            updateSelectedProductSimple();
        } else {
            mVarianceContainer.setVisibility(View.GONE);
        }
    }

    /**
     * function responsible for showing the seller info
     */
    public void setSellerInfo() {
        if (mCompleteProduct.hasSeller()) {
            sellerView.setVisibility(View.VISIBLE);
            mSellerName.setText(mCompleteProduct.getSeller().getName());
            String rating = getString(R.string.string_ratings).toLowerCase();
            if (mCompleteProduct.getSeller().getRatingCount() == 1)
                rating = getString(R.string.string_rating).toLowerCase();
            mSellerRatingValue.setText(mCompleteProduct.getSeller().getRatingCount() + " " + rating);
            mSellerRating.setRating(mCompleteProduct.getSeller().getRatingValue());
            //
            int visibility = View.GONE;
            // TODO placeholder
            if(CollectionUtils.isNotEmpty(mCompleteProduct.getSimples()) &&
                    mCompleteProduct.getSimples().get(0).getMinDeliveryTime() > 0 &&
                    mCompleteProduct.getSimples().get(0).getMaxDeliveryTime() > 0) {
                //
                String min = "" + mCompleteProduct.getSimples().get(0).getMinDeliveryTime();
                String max = "" + mCompleteProduct.getSimples().get(0).getMaxDeliveryTime();
                mSellerDeliveryTime.setText(min + " - " + max + " " + getString(R.string.product_delivery_days));
                visibility = View.VISIBLE;
            }
            mSellerDeliveryContainer.setVisibility(visibility);
        } else {
            sellerView.setVisibility(View.GONE);
        }
    }

    public void updateSelectedProductSimple() {
        Log.i(TAG, "UPDATE THE SELECTED PRODUCT SIMPLE: " + mSelectedSimple);

        if (mSelectedSimple == NO_SIMPLE_SELECTED) {
            mVarianceButton.setText("...");
        }

        mSimpleVariants = createSimpleVariants();
        Print.i(TAG, "scanSimpleForKnownVariations : updateVariants " + mSimpleVariants.size());
        ProductSimple selectedSimple = getSelectedSimple();
        // Log.i(TAG, "code1stock size selected!" + mSelectedSimple);

        if (selectedSimple != null) {
            // Set variation label
            mVarianceText.setText(mCompleteProduct.getVariationName());
            mVarianceText.setTextColor(getResources().getColor(R.color.grey_middle));
            // Set variation value
            Print.i(TAG, "size is : " + mSimpleVariants.get(mSelectedSimple));
            mVarianceButton.setText(mSimpleVariants.get(mSelectedSimple));
            // Set product price
            if (selectedSimple.hasDiscount()) {
                mSpecialPriceText.setText(CurrencyFormatter.formatCurrency(selectedSimple.getPrice()));
                mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
                mPriceText.setVisibility(View.GONE);
            } else {
                mSpecialPriceText.setText(CurrencyFormatter.formatCurrency(selectedSimple.getSpecialPrice()));
                mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
                mPriceText.setText(CurrencyFormatter.formatCurrency(selectedSimple.getPrice()));
                mPriceText.setPaintFlags(mPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mPriceText.setVisibility(View.VISIBLE);
            }
        }
    }


    private void executeAddProductToCart() {
        // Get selected simple
        ProductSimple simple = getSelectedSimple();
        // Validate simple
        if (simple == null) {
            showVariantsDialog();
            return;
        }
        // Validate quantity
        if (simple.getQuantity() == 0) {
            Toast.makeText(getBaseActivity(), R.string.product_outof_stock, Toast.LENGTH_SHORT).show();
            isAddingProductToCart = false;
            return;
        }
        // Validate simple sku
        String simpleSku = simple.getSku();
        // Add one unity to cart
        triggerAddItemToCart(mCompleteProduct.getSku(), simpleSku);
        // Tracking
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, simpleSku);
        bundle.putDouble(TrackerDelegator.PRICE_KEY, mCompleteProduct.getPriceForTracking());
        bundle.putString(TrackerDelegator.NAME_KEY, mCompleteProduct.getName());
        bundle.putString(TrackerDelegator.BRAND_KEY, mCompleteProduct.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, mCompleteProduct.getAvgRating());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, mCompleteProduct.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories());
        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
        TrackerDelegator.trackProductAddedToCart(bundle);
    }

    private void displayProduct(ProductComplete product) {
        Print.d(TAG, "SHOW PRODUCT");
        // Show wizard
        if(!mHideWizard){
            isToShowWizard();
        }
        // Call phone
        setCallPhone();
        // validate specifications layout
        checkProductDetailsVisibility();
        // Get simple position from deep link value
        if (mDeepLinkSimpleSize != null) {
            locateSimplePosition(mDeepLinkSimpleSize, product);
        }

        try {
            LastViewedTableHelper.insertLastViewedProduct(product);
            BrandsTableHelper.updateBrandCounter(product.getBrand());
        } catch (IllegalStateException | SQLiteException e) {
            // ...
        }

        mCompleteProduct = product;
        mCompleteProductSku = product.getSku();

        // Set Title
        // #RTL
        if (ShopSelector.isRtl()) {
            mTitleText.setText(mCompleteProduct.getBrand() != null ? mCompleteProduct.getName() + " " + mCompleteProduct.getBrand() : "");
        } else {
            mTitleText.setText(mCompleteProduct.getBrand() != null ? mCompleteProduct.getBrand() + " " + mCompleteProduct.getName() : "");
        }

        // Set favourite
        mImageFavourite.setSelected(mCompleteProduct.isWishList());
        // Validate gallery
        setProductGallery(product);
        // Validate related items
        ArrayList<ProductRegular> RelatedItemsArrayList = new ArrayList<>(mCompleteProduct.getRelatedProducts());
        showRelatedItemsLayout(RelatedItemsArrayList);
        // Validate variations
        setProductVariations();
        // Show product info
        setSimpleVariationsContainer();
        setPriceInfoOverallOrForSimple();
        setRatingInfo();
        setSellerInfo();
        setOffersInfo();
        setBundles(product);
        // Tracking
        TrackerDelegator.trackProduct(createBundleProduct());
        // Show container
        showFragmentContentContainer();
    }

    /**
     * Set the gallery
     * @param completeProduct
     * @author sergiopereira
     */
    private void setProductGallery(ProductComplete completeProduct) {
        // Show loading
        mGalleryViewGroupFactory.setViewVisible(R.id.image_loading_progress);
        // Case product without images
        if(CollectionUtils.isEmpty(completeProduct.getImageList())) mGalleryViewGroupFactory.setViewVisible(R.id.image_place_holder);
            // Case product with images
        else RocketImageLoader.getInstance().loadImages(completeProduct.getImageList(), this);
    }

    /**
     *
     * @param product
     */
    private void setBundles(ProductComplete product) {
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
        } else return variations.size() == 1 && variations.get(0).getSKU().equals(mCompleteProduct.getSku());
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
        Print.i(TAG, "ON DISPLAY VARIATIONS");

        // Validate complete product
        if (mCompleteProduct == null) {
            Print.i(TAG, "mCompleteProduct is null -- verify and fix!!!");
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
            Print.i(TAG, "NEW ADAPTER");
            int position = CompleteProductUtils.findIndexOfSelectedVariation(mCompleteProduct);
            ProductVariationsListAdapter adapter = new ProductVariationsListAdapter(mCompleteProduct.getVariations());
            mVariationsListView.setHasFixedSize(true);
            mVariationsListView.enableRtlSupport(ShopSelector.isRtl());
            mVariationsListView.setAdapter(adapter);
            mVariationsListView.setSelectedItem(position);
            mVariationsListView.setOnItemSelectedListener(new OnViewSelectedListener() {
                @Override
                public void onViewSelected(View view, int position, String sku) {
                    Print.i(TAG, "ON SELECTED ITEM: " + position + " " + sku);
                    // Validate if current product has variations and if is invalid URL and if is the current product
                    if (CollectionUtils.isEmpty(mCompleteProduct.getVariations()) ||
                            mCompleteProduct.getVariations().size() <= position ||
                            TextUtils.isEmpty(sku) || sku.equals(mCompleteProduct.getSku()))
                        return;
                    // Saved the selected URL
                    mCompleteProductSku = sku;
                    // Show loading rating
                    loadingRating.setVisibility(View.VISIBLE);
                    // Hide bundle container
                    hideBundle();
                    // Get product to update partial data
                    triggerLoadProductPartial(mCompleteProductSku);
                }
            });
        }
        // Show container
        mVariationsContainer.setVisibility(View.VISIBLE);
    }

    /**
     * ################# RELATED ITEMS #################
     */

    /**
     * Method used to create the view
     * @modified sergiopereira
     */
    private void showRelatedItemsLayout(ArrayList<ProductRegular> relatedItemsList) {
        if(relatedItemsList == null){
            mRelatedContainer.setVisibility(View.GONE);
            return;
        }

        if(!relatedItemsList.isEmpty()){
            mRelatedContainer.setVisibility(View.VISIBLE);
            // Use this setting to improve performance if you know that changes in content do not change
            // the layout size of the RecyclerView
            mRelatedListView.setHasFixedSize(true);
            mRelatedListView.enableRtlSupport(ShopSelector.isRtl());
            mRelatedListView.setAdapter(new RelatedItemsListAdapter(relatedItemsList,
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Show related item
                            android.os.Bundle bundle = new android.os.Bundle();
                            bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, (String) v.getTag(R.id.target_sku));
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
    }

    /**
     * Validate if is to show the pager wizard
     */
    private void isToShowWizard() {
        if (WizardPreferences.isFirstTime(getBaseActivity(), WizardType.PRODUCT_DETAIL)) {
            Print.d(TAG, "Show Wizard");
            mWizardContainer.setVisibility(View.VISIBLE);
            ViewPager viewPagerTips = (ViewPager) mWizardContainer.findViewById(R.id.viewpager_tips);
            viewPagerTips.setVisibility(View.VISIBLE);
            int[] tips_pages = { R.layout.tip_swipe_layout, R.layout.tip_tap_layout, R.layout.tip_favourite_layout, R.layout.tip_share_layout };
            TipsPagerAdapter mTipsPagerAdapter = new TipsPagerAdapter(getActivity().getApplicationContext(), getActivity().getLayoutInflater(), mWizardContainer, tips_pages);
            viewPagerTips.setAdapter(mTipsPagerAdapter);
            viewPagerTips.setOnPageChangeListener(new TipsOnPageChangeListener(mWizardContainer, tips_pages));
            viewPagerTips.setCurrentItem(0);
            mWizardContainer.findViewById(R.id.viewpager_tips_btn_indicator).setVisibility(View.VISIBLE);
            mWizardContainer.findViewById(R.id.tips_got_it_img).setOnClickListener(this);
        }
    }

    /**
     *
     */
    private void setCallPhone() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, "");
    }

    /**
     * Create a bundle for tracking
     *
     * @author sergiopereira
     */
    private android.os.Bundle createBundleProduct() {
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString(TrackerDelegator.SOURCE_KEY, mNavigationSource);
        bundle.putString(TrackerDelegator.PATH_KEY, mNavigationPath);
        bundle.putString(TrackerDelegator.NAME_KEY, mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
        bundle.putString(TrackerDelegator.SKU_KEY, mCompleteProduct.getSku());
        bundle.putDouble(TrackerDelegator.PRICE_KEY, mCompleteProduct.getPriceForTracking());
        bundle.putBoolean(TrackerDelegator.RELATED_ITEM, isRelatedItem);
        bundle.putString(TrackerDelegator.BRAND_KEY, mCompleteProduct.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, mCompleteProduct.getAvgRating());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, mCompleteProduct.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories());
        return bundle;
    }

    /**
     * Locate the simple size from deep link and save that position
     *
     * @param simpleSize
     * @param product
     * @author sergiopereira
     */
    private void locateSimplePosition(String simpleSize, ProductComplete product) {
        Print.d(TAG, "DEEP LINK SIMPLE SIZE: " + simpleSize);
        if (product != null && product.getSimples() != null && product.getSimples().size() > 0)
            for (int i = 0; i < product.getSimples().size(); i++) {
                ProductSimple simple = product.getSimples().get(i);
                if (simple != null && simple.getVariationValue() != null && simple.getVariationValue().equals(simpleSize))
                    mSelectedSimple = i;
            }
        Print.d(TAG, "DEEP LINK SIMPLE POSITION: " + mSelectedSimple);
    }

    private void executeAddToShoppingCartCompleted(boolean isBundle) {

        if (!isBundle) {
            getBaseActivity().warningFactory.showWarning(WarningFactory.ADDED_ITEM_TO_CART);
        } else {
            getBaseActivity().warningFactory.showWarning(WarningFactory.ADDED_ITEMS_TO_CART);
        }
    }

    private void addToShoppingCartFailed() {
        mDialogAddedToCart = DialogGenericFragment.newInstance(false, true, null,
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
        super.onClick(view);
        // Get id
        int id = view.getId();
        // Case rating
        if (id == R.id.product_detail_product_rating_container ) onClickRating();
            // Case description
        else if (id == R.id.product_detail_specifications || id == R.id.product_detail_name) onClickShowDescription();
            // Case variation button
        else if (id == R.id.product_detail_product_variant_button) onClickVariationButton();
            // Case shop product
        else if (id == R.id.product_detail_shop) onClickShopProduct();
            // Case call to order
        else if (id == R.id.product_detail_call_to_order) onClickCallToOrder();
            // Case wizard
        else if (id == R.id.tips_got_it_img) onClickWizardButton();
            // Case favourite
        else if (id == R.id.product_detail_image_is_favourite) onClickWishListButton();
            // Case share
        else if (id == R.id.product_detail_product_image_share) onClickShare(mCompleteProduct);
            // Case size guide
        else if (id == R.id.dialog_list_size_guide_button) onClickSizeGuide(view);
            // seller link
        else if (id == R.id.seller_name_container) goToSellerCatalog();
            // seller rating
        else if (id == R.id.product_detail_product_seller_rating_container) goToSellerRating();
            // product offers
        else if (id == R.id.offers_container || id == R.id.product_detail_product_offers_container) goToProductOffers();

    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    /**
     * function that sends the user to the product offers view
     */
    private void goToProductOffers() {
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_NAME, mCompleteProduct.getName());
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProduct.getSku());
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_OFFERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     *
     */
    private void onClickRating() {
        JumiaApplication.cleanRatingReviewValues();
        JumiaApplication.cleanSellerReviewValues();
        JumiaApplication.INSTANCE.setFormReviewValues(null);
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProduct.getSku());
        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mCompleteProduct);
        bundle.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, true);
        getBaseActivity().onSwitchFragment(FragmentType.POPULARITY, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Show the product description
     */
    private void onClickShowDescription() {
        if (null != mCompleteProduct && (!CollectionUtils.isEmpty(mCompleteProduct.getProductSpecifications()) ||
                (!TextUtils.isEmpty(mCompleteProduct.getShortDescription()) && !TextUtils.isEmpty(mCompleteProduct.getDescription())) )) {
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mCompleteProduct);
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_INFO, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     *
     */
    private void onClickVariationButton() {
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
        TrackerDelegator.trackCall(getBaseActivity());
        makeCall();
    }

    /**
     *
     */
    private void onClickWizardButton() {
        WizardPreferences.changeState(getBaseActivity(), WizardType.PRODUCT_DETAIL);
        try {
            getView().findViewById(R.id.viewpager_tips).setVisibility(View.GONE);
            getView().findViewById(R.id.viewpager_tips_btn_indicator).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON HIDE WIZARD");
        }
    }

    /**
     *
     */
    private void onClickWishListButton() {
        try {
            // Get item
            if (mCompleteProduct.isWishList()) {
                triggerRemoveFromWishList(mCompleteProduct.getSku());
            } else {
                triggerAddToWishList(mCompleteProduct.getSku());
            }
            // Update value
            mImageFavourite.setSelected(!mCompleteProduct.isWishList());
            mCompleteProduct.setIsWishList(!mCompleteProduct.isWishList());
        } catch (NullPointerException e) {
            Log.w(TAG, "NPE ON ADD ITEM TO WISH LIST", e);
        }
    }

    /**
     * Process the click on share.
     *
     * @param completeProduct
     * @author sergiopereira
     */
    private void onClickShare(ProductComplete completeProduct) {
        try {
            String extraSubject = getString(R.string.share_subject, getString(R.string.app_name_placeholder));
            String extraMsg = getString(R.string.share_checkout_this_product) + "\n" + mCompleteProduct.getShareUrl();
            Intent shareIntent = getBaseActivity().createShareIntent(extraSubject, extraMsg);
            shareIntent.putExtra(RestConstants.JSON_SKU_TAG, mCompleteProduct.getSku());
            startActivity(shareIntent);
            // Track share
            TrackerDelegator.trackItemShared(shareIntent, completeProduct.getCategories());
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SHARE");
        } catch (IndexOutOfBoundsException e) {
            Print.w(TAG, "WARNING: IOB ON CLICK SHARE");
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
                android.os.Bundle bundle = new android.os.Bundle();
                bundle.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, url);
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_SIZE_GUIDE, bundle,
                        FragmentController.ADD_TO_BACK_STACK);
            } else
                Print.w(TAG, "WARNING: SIZE GUIDE URL IS EMPTY");
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
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
            getBaseActivity().warningFactory.hideWarning();
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
            Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
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
    public void onDialogListItemSelect(int position, String value) {
        mSelectedSimple = position;
        Print.i(TAG, "size selected! onDialogListItemSelect : " + mSelectedSimple);
        updateSelectedProductSimple();
//        updateStockInfo();
        setPriceInfoOverallOrForSimple();

        if(isAddingProductToCart) {
            executeAddProductToCart();
        }

    }

    /*
     * ############## TODO TRIGGERS ##############
     */

    private void triggerLoadProduct(String sku) {
        mBeginRequestMillis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(GetProductHelper.SKU_TAG, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new GetProductHelper(), bundle, this);
    }

    private void triggerLoadProductPartial(String sku) {
        mBeginRequestMillis = System.currentTimeMillis();
        mGalleryViewGroupFactory.setViewVisible(R.id.image_loading_progress);
        ContentValues values = new ContentValues();
        values.put(GetProductHelper.SKU_TAG, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventNoLoading(new GetProductHelper(), bundle, this);
    }

    private void triggerAddItemToCart(String sku, String simpleSKU) {
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_TAG, sku);
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, simpleSKU);
        values.put(ShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), bundle, this);
    }

    private void triggerAddToWishList(String sku) {
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        //triggerContentEventProgress(new AddToWishListHelper(), bundle, this);
    }

    private void triggerRemoveFromWishList(String sku) {
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        //triggerContentEventProgress(new RemoveFromWishListHelper(), bundle, this);
    }


    private void triggerAddBundleToCart(BundleList mProductBundle) {
        mBundleButton.setEnabled(false);
        int count = 0;
        ContentValues values = new ContentValues();
        values.put(GetShoppingCartAddBundleHelper.BUNDLE_ID, mProductBundle.getBundleId());
        // TODO use palceholders
        for (ProductBundle item  : mProductBundle.getBundleProducts()) {
            if(item.isChecked()) {
                //
                values.put(GetShoppingCartAddBundleHelper.PRODUCT_SKU_TAG + count + "]", item.getSku());
                if(item.hasOwnSimpleVariation()) {
                    values.put(GetShoppingCartAddBundleHelper.PRODUCT_SIMPLE_SKU_TAG + count + "]", item.getOwnSimpleVariation().getSku());
                } else {
                    values.put(GetShoppingCartAddBundleHelper.PRODUCT_SIMPLE_SKU_TAG + count + "]", item.getSelectedSimpleVariation().getSku());
                }
                // GA BUNDLE TRACKING
                TrackerDelegator.trackAddBundleToCart(item.getSku(), item.getPriceForTracking());
                //
                count++;
            }
        }
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new GetShoppingCartAddBundleHelper(), bundle, this);
    }

    @Override
    public void onDismiss() {
        isAddingProductToCart = false;
    }

    /*
     * ############## RESPONSE ##############
     */

    @Override
    public void onRequestComplete(android.os.Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();

        if (getBaseActivity() == null)
            return;

        super.handleSuccessEvent(bundle);

        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                executeAddToShoppingCartCompleted(false);
                isAddingProductToCart = false;
                mAddToCartButton.setEnabled(true);
                break;
            case GET_PRODUCT_DETAIL:
                if (((ProductComplete) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                    Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                    getBaseActivity().onBackPressed();
                    return;
                } else {
                    mCompleteProduct = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                    // Show product or update partial
                    ProductImageGalleryFragment.sSharedSelectedPosition = 0;
                    // Show product or update partial
                    displayProduct(mCompleteProduct);
                    //
                    android.os.Bundle params = new android.os.Bundle();
                    params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gproductdetail);
                    params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                    TrackerDelegator.trackLoadTiming(params);

                    params = new android.os.Bundle();
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

                // TODO: create a method
                if (mCompleteProduct.hasBundle()) {
                    android.os.Bundle arg = new android.os.Bundle();
                    arg.putString(GetProductBundleHelper.PRODUCT_SKU, mCompleteProduct.getSku());
                    triggerContentEventNoLoading(new GetProductBundleHelper(), arg, this);
                }
                break;
            case GET_PRODUCT_BUNDLE:
                mProductBundle = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                if (mProductBundle != null)
                    displayBundle(mProductBundle);
                else
                    hideBundle();
                break;
            case ADD_PRODUCT_BUNDLE:
                isAddingProductToCart = false;
                getBaseActivity().updateCartInfo();
                mBundleButton.setEnabled(true);
                mAddToCartButton.setEnabled(true);
                executeAddToShoppingCartCompleted(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(android.os.Bundle bundle) {
        Print.i(TAG, "ON ERROR EVENT");
        // Validate fragment visibility

        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();

        // Specific errors
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (eventType == EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT) {
            isAddingProductToCart = false;
        }

        // Generic errors
        if (super.handleErrorEvent(bundle)) {
            mBundleButton.setEnabled(true);
            return;
        }

        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
            case ADD_PRODUCT_BUNDLE:
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                mBundleButton.setEnabled(true);
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

                    if (errorMessages != null) {
                        int titleRes = R.string.error_add_to_cart_failed;
                        int msgRes = -1;

                        String message = null;
                        if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                            msgRes = R.string.product_outof_stock;
                        } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_PRODUCT_ADD_OVERQUANTITY)) {
                            msgRes = R.string.error_add_to_shopping_cart_quantity;
                        } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
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
                        dialog = DialogGenericFragment.newInstance(true, false,
                                getString(titleRes),
                                message,
                                getString(R.string.ok_label), "", new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        int id = v.getId();
                                        if (id == R.id.button1) {
                                            dismissDialogFragment();
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
            case GET_PRODUCT_DETAIL:
                showContinueShopping();
            case GET_PRODUCT_BUNDLE:
                hideBundle();
                break;
            default:
                break;
        }
    }

    /**
     *
     * Function responsible for showing the product bundle if it exists
     *
     * @param bundle
     */
    private void displayBundle(BundleList bundle) {
        mBundleContainer.setVisibility(View.VISIBLE);
        mCompleteProduct.setProductBundle(bundle);
        // calculate the bundle total without the plead product
        double total = 0.0;
        // validate if any product has simples do adjust item size
        boolean hasSimples = false;
        ArrayList<ProductBundle> bundleProducts = bundle.getBundleProducts();
        for (ProductBundle item : bundleProducts) {
            if (item.hasMultiSimpleVariations()) {
                hasSimples = true;
            }
            if (item.isChecked() && item.hasDiscount()) {
                total = total + item.getSpecialPrice();
            } else {
                total = total + item.getPrice();
            }
        }

        validateBundleButton();

        mBundleTextTotal.setText(CurrencyFormatter.formatCurrency(total));

        mBundleTextTotal.setTag(total);

        if (hasSimples) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.teaser_product_bundle_item_width));
            params.addRule(RelativeLayout.BELOW, mDividerBundle.getId());
            mBundleListView.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.teaser_product_item_width));
            params.addRule(RelativeLayout.BELOW, mDividerBundle.getId());
            mBundleListView.setLayoutParams(params);
        }

        // Use this setting to improve performance
        mBundleListView.setHasFixedSize(true);
        // RTL
        mBundleListView.enableRtlSupport(ShopSelector.isRtl());
        // Content
        Print.e("BUNDLE", "bundleProducts size:" + bundleProducts.size());
        mBundleListView.setAdapter(new BundleItemsListAdapter(bundleProducts, this, this));
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
     */
    @Override
    public void SelectedItem() {
        Print.d("BUNDLE", "GO TO PDV");
    }

    /**
     *
     * function responsible for handling on item of the bundle
     *
     * @param selectedProduct
     * @param isChecked
     * @param pos
     */
    @Override
    public void checkItem(ProductBundle selectedProduct, boolean isChecked, int pos) {
        // if isChecked is false then item was deselected
        double priceChange = selectedProduct.getSpecialPrice();
        if (priceChange == 0) {
            priceChange = selectedProduct.getPrice();
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
     * function responsible for showing the the pop with the simples
     *
     */
    private void showBundleSimples() {

    }


    /**
     * function responsible for calling the catalog with the products from a specific seller
     */
    private void goToSellerCatalog() {
        Print.d("SELLER", "GO TO CATALOG");
        if (mCompleteProduct.hasSeller()) {
            android.os.Bundle bundle = new android.os.Bundle();
            String targetUrl = mCompleteProduct.getSeller().getUrl();
            String targetTitle = mCompleteProduct.getSeller().getName();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, true);
        }
    }

    /**
     * function responsible for showing the rating and reviews of a specific seller
     */
    private void goToSellerRating() {
        JumiaApplication.cleanRatingReviewValues();
        JumiaApplication.cleanSellerReviewValues();
        JumiaApplication.INSTANCE.setFormReviewValues(null);

        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProduct.getSku());
        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mCompleteProduct);
        bundle.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, false);
        bundle.putString(SELLER_ID, mCompleteProduct.getSeller().getSellerId());
        getBaseActivity().onSwitchFragment(FragmentType.POPULARITY, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.utils.imageloader.RocketImageLoader.RocketImageLoaderLoadImagesListener#onCompleteLoadingImages(java.util.ArrayList)
     */
    @Override
    public void onCompleteLoadingImages(ArrayList<ImageHolder> successUrls) {
        Print.i(TAG, "ON COMPLETE LOADING IMAGES");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Gets all urls with success
        ArrayList<String> urls = new ArrayList<>();
        for(ImageHolder imageHolder : successUrls) urls.add(imageHolder.url);

        // Validate the number of cached images
        if (!successUrls.isEmpty()) {
            // Match the cached image list with the current image list order
            ArrayList<String> orderCachedImageList = (ArrayList<String>) CollectionUtils.retainAll(mCompleteProduct.getImageList(), urls);
            // Set the cached images
            mCompleteProduct.setImageList(orderCachedImageList);
            // Create bundle with arguments
            android.os.Bundle args = new android.os.Bundle();
            args.putStringArrayList(ConstantsIntentExtra.IMAGE_LIST, orderCachedImageList);
            args.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);

            // Validate the ProductImageGalleryFragment
            ProductImageGalleryFragment productImagesViewPagerFragment = (ProductImageGalleryFragment) getChildFragmentManager().findFragmentByTag(ProductImageGalleryFragment.TAG);
            // CASE CREATE
            if (productImagesViewPagerFragment == null) {
                Print.i(TAG, "SHOW GALLERY: first time position = " + ProductImageGalleryFragment.sSharedSelectedPosition);
                productImagesViewPagerFragment = ProductImageGalleryFragment.getInstanceAsNested(args);
                fragmentManagerTransition(R.id.product_detail_image_gallery_container, productImagesViewPagerFragment);
            }
            // CASE UPDATE
            else {
                Print.i(TAG, "SHOW GALLERY: second time position = " + ProductImageGalleryFragment.sSharedSelectedPosition);
                productImagesViewPagerFragment.notifyFragment(args);
            }
            // Show container
            mGalleryViewGroupFactory.setViewVisible(R.id.product_detail_image_gallery_container);
        } else {
            Print.i(TAG, "SHOW PLACE HOLDER");
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
