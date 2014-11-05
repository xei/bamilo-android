package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.TipsPagerAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.database.RelatedItemsTableHelper;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.LastViewed;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.AdjustTracker;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.cart.GetShoppingCartAddItemHelper;
import pt.rocket.helpers.products.GetProductHelper;
import pt.rocket.helpers.search.GetSearchProductHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.FragmentCommunicatorForProduct;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.ScrollViewWithHorizontal;
import pt.rocket.utils.TipsOnPageChangeListener;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.deeplink.DeepLinkManager;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.utils.dialogfragments.WizardPreferences;
import pt.rocket.utils.dialogfragments.WizardPreferences.WizardType;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
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
public class ProductDetailsFragment extends BaseFragment implements OnClickListener, OnDialogListListener {

    private final static String TAG = LogTagHelper.create(ProductDetailsFragment.class);

    private final static int NO_SIMPLE_SELECTED = -1;

    private final static String VARIATION_PICKER_ID = "variation_picker";

    private static String SELECTED_SIMPLE_POSITION = "selected_simple_position";

    public final static String LOADING_PRODUCT_KEY = "loading_product_key";

    public final static String LOADING_PRODUCT = "loading_product";

    public final static String PRODUCT_COMPLETE = "complete_product";

    public final static String PRODUCT_CATEGORY = "product_category";

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

    private Fragment productVariationsFragment;

    private Fragment productImagesViewPagerFragment;

    public static String VARIATION_LIST_POSITION = "variation_list_position";

    private int mVariationsListPosition = -1;

    private String mPhone2Call = "";

    private SharedPreferences sharedPreferences;

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

    private View mRelatedHorizontalScroll;

    private ViewGroup mRelatedHorizontalGroup;
    
    private RelativeLayout mProductFeaturesContainer;
    
    private TextView mProductFeaturesText;
    
    private TextView mProductDescriptionText;
    
    private LinearLayout mProductFeaturesMore;
    
    private LinearLayout mProductDescriptionMore;
    
    private RelativeLayout mProductDescriptionContainer;

    private boolean isRelatedItem = false;
    
    private static String categoryTree = "";
    
    /**
     * Empty constructor
     */
    public ProductDetailsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.product_details_fragment_main,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    public static ProductDetailsFragment getInstance(Bundle bundle) {
        ProductDetailsFragment.mProductDetailsActivityFragment = new ProductDetailsFragment();
        
        if(null != bundle && bundle.containsKey(ConstantsIntentExtra.CATEGORY_TREE_NAME)){
            categoryTree = bundle.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME)+",PDV";
        } else {
            categoryTree = "";
        }
        // Clean current product
        JumiaApplication.INSTANCE.setCurrentProduct(null);
        return ProductDetailsFragment.mProductDetailsActivityFragment;
    }

    public void onVariationElementSelected(int position) {
        mVariationsListPosition = position;

        /**
         * Send LOADING_PRODUCT to show loading views.
         */

        loadingRating.setVisibility(View.VISIBLE);
        if (mCompleteProduct.getVariations() == null || (mCompleteProduct.getVariations().size() <= position))
            return;

        String url = mCompleteProduct.getVariations().get(position).getLink();
        if (TextUtils.isEmpty(url))
            return;

        if (url.equals(mCompleteProduct.getUrl()))
            return;

        Log.d(TAG, "onItemClick: loading url = " + url);
        mCompleteProductUrl = url;
        loadProductPartial();

    }

    @Override
    public void onFragmentSelected(FragmentType type) {
        if (type == FragmentType.PRODUCT_SHOWOFF) {
            showGallery();
        } else if (type == FragmentType.PRODUCT_SPECIFICATION) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE");
        sharedPreferences = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mVariationsListPosition = sharedPreferences.getInt(VARIATION_LIST_POSITION, -1);
        // mSelectedSimple = sharedPreferences.getInt(SELECTED_SIMPLE_POSITION, NO_SIMPLE_SELECTED);
        //
        if (savedInstanceState != null)
            mSelectedSimple = savedInstanceState.getInt(SELECTED_SIMPLE_POSITION, NO_SIMPLE_SELECTED);
        Log.d(TAG, "CURRENT SELECTED SIMPLE: " + mSelectedSimple);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.product_detail_view.View,
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
     * @see pt.rocket.view.fragments.BaseFragment#onResume()
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
        //removed nested fragments
        removeNestedFragments();
        // Save the current fragment type on orientation change
        if (!mHideVariationSelection)
            outState.putInt(SELECTED_SIMPLE_POSITION, mSelectedSimple);
    }

    
    /**
     * method to remove nested fragments from product detail
     */
    private void removeNestedFragments() {
        
        fragmentManagerTransition(R.id.product_detail_variations_container, new Fragment(), false, true);
        fragmentManagerTransition(R.id.product_detail_image_gallery_container, new Fragment(), false, true);
        
//        if (productImagesViewPagerFragment != null ) {
//            FragmentManager fm = getChildFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.remove(productImagesViewPagerFragment);
//            ft.commit();
//        }
//        if (productVariationsFragment != null ) {
//            FragmentManager fm = getChildFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.remove(productVariationsFragment);
//            ft.commit();
//        }
         
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        dialogListFragment = null;
        // Destroy variations
        productVariationsFragment = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ON DESTROY");
    }

    /**
     * 
     */
    private void init() {
        Log.d(TAG, "INIT");
        // Get arguments
        Bundle bundle = getArguments();
        // Validate deep link arguments
        if (hasArgumentsFromDeepLink(bundle)) return;

        restoreParams(bundle);

        // Get url
        mCompleteProductUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        // Validate url and load product
        if (mCompleteProductUrl == null) getBaseActivity().onBackPressed();
        else loadProduct();
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
            mNavigationSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix));
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
        mRelatedHorizontalScroll = view.findViewById(R.id.product_detail_horizontal_scroll);
        mRelatedHorizontalGroup = (ViewGroup) view.findViewById(R.id.product_detail_horizontal_group_container);
        mRelatedLoading = view.findViewById(R.id.loading_related);        
        // Bottom Button
        mAddToCartButton = (Button) view.findViewById(R.id.product_detail_shop);
        mAddToCartButton.setSelected(true);
        mAddToCartButton.setOnClickListener(this);
        //
        mCallToOrderButton = (Button) view.findViewById(R.id.product_detail_call_to_order);
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            mCallToOrderButton.setSelected(true);
            mCallToOrderButton.setOnClickListener(this);
        } else {
            mCallToOrderButton.setVisibility(View.GONE);
        }


      
      
      if(BaseActivity.isTabletInLandscape(getBaseActivity())){
          mProductFeaturesContainer = (RelativeLayout) mainView.findViewById(R.id.features_container);
          mProductDescriptionContainer = (RelativeLayout) mainView.findViewById(R.id.description_container);
          mProductFeaturesText = (TextView) mainView.findViewById(R.id.product_features_text);
          mProductDescriptionText = (TextView) mainView.findViewById(R.id.product_description_text);
          mProductFeaturesMore = (LinearLayout) mainView.findViewById(R.id.features_more_container);
          mProductFeaturesMore.setOnClickListener(this);
          mProductDescriptionMore = (LinearLayout) mainView.findViewById(R.id.description_more_container);
          mProductDescriptionMore.setOnClickListener(this);
          
          // Title
          mTitleText = (TextView) view.findViewById(R.id.product_detail_name);
          mTitleText.setOnClickListener((OnClickListener) this);
      } else {
          
          // Title
          mTitleText = (TextView) view.findViewById(R.id.product_name);
          mTitleText.setOnClickListener((OnClickListener) this);
          // Button specification
          view.findViewById(R.id.product_detail_specifications).setOnClickListener((OnClickListener) this);
      }
     
        
    }

    private void startFragmentCallbacks() {
        // Log.i(TAG, "code1 starting callbacks!!!");

        CompleteProduct cProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();

        FragmentCommunicatorForProduct.getInstance().destroyInstance();
        FragmentCommunicatorForProduct.getInstance().startFragmentsCallBacks(this, productVariationsFragment, productImagesViewPagerFragment);
        if (cProduct == null) {
            FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(mCompleteProduct);
        } else {
            FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(cProduct);
        }

    }

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
    }

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

    private void loadProductPartial() {
        mBeginRequestMillis = System.currentTimeMillis();
        Bundle bundle = new Bundle();
        bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
        triggerContentEventWithNoLoading(new GetProductHelper(), bundle, responseCallback);
    }

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

    private Set<String> scanSimpleAttributesForKnownVariants(ArrayList<ProductSimple> simples) {
        Set<String> foundVariations = new HashSet<String>();
        Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants");
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants in");
            scanSimpleForKnownVariants(simple, foundVariations);
        }

        return foundVariations;
    }

    private void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations) {

        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            Log.i(TAG, "scanSimpleForKnownVariations: variation = " + variation + " attr = " + attr);
            if (attr == null)
                continue;
            foundVariations.add(variation);
        }
    }

    private ArrayList<String> createSimpleVariants() {
        Log.i(TAG,"scanSimpleForKnownVariations : createSimpleVariants" + mCompleteProduct.getName());
        ArrayList<ProductSimple> simples = (ArrayList<ProductSimple>) mCompleteProduct.getSimples().clone();
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

//    private HashMap<String, String> createVariantAttributesHashMap(ProductSimple simple) {
//        ArrayList<ProductSimple> simples = mCompleteProduct.getSimples();
//        if (simples.size() <= 1)
//            return null;
//
//        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(simples);
//
//        HashMap<String, String> variationsMap = new HashMap<String, String>();
//        for (String key : foundKeys) {
//            String value = simple.getAttributeByKey(key);
//
//            if (value == null)
//                continue;
//            if (value.equals("\u2026"))
//                continue;
//
//            variationsMap.put(key, value);
//        }
//
//        return variationsMap;
//    }

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

    private void displayPriceInfoOverallOrForSimple() {
        ProductSimple simple = getSelectedSimple();

        if (mSelectedSimple == NO_SIMPLE_SELECTED || simple == null) {
            String unitPrice = mCompleteProduct.getPrice();
            String specialPrice = mCompleteProduct.getSpecialPrice();
            /*--if (specialPrice == null) specialPrice = mCompleteProduct.getMaxSpecialPrice();*/
            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();

            displayPriceInfo(unitPrice, specialPrice, discountPercentage);

        } else {
            // Simple Products prices dont come with currency preformatted
            String unitPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specialPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);

            unitPrice = currencyFormatHelper(unitPrice);
            if (specialPrice != null)
                specialPrice = currencyFormatHelper(specialPrice);

            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();

            displayPriceInfo(unitPrice, specialPrice, discountPercentage);
        }
    }

    private void displayPriceInfo(String unitPrice, String specialPrice, int discountPercentage) {
        Log.d(TAG, "displayPriceInfo: unitPrice = " + unitPrice + " specialPrice = " + specialPrice);
        if (specialPrice == null || specialPrice.equals(unitPrice)) {
            // display only the normal price
            mSpecialPriceText.setText(unitPrice);
            mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
            mPriceText.setVisibility(View.GONE);
            // Set discount percentage value
            if (discountPercentage > 0) {
                mDiscountPercentageText.setText("-" + discountPercentage + "%");
                mDiscountPercentageText.setVisibility(View.VISIBLE);
            } else {
                mDiscountPercentageText.setVisibility(View.GONE);
            }
        } else {
            // display reduced and special price
            mSpecialPriceText.setText(specialPrice);
            mSpecialPriceText.setTextColor(getResources().getColor(R.color.red_basic));
            mPriceText.setText(unitPrice);
            mPriceText.setPaintFlags(mPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mPriceText.setVisibility(View.VISIBLE);
            // Set discount percentage value
            if (discountPercentage > 0) {
                mDiscountPercentageText.setText("-" + discountPercentage + "%");
                mDiscountPercentageText.setVisibility(View.VISIBLE);
            } else {
                mDiscountPercentageText.setVisibility(View.GONE);
            }
        }

    }

//    private long getPriceForTrackingAsLong(ProductSimple simple) {
//        String price;
//
//        price = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);
//        if (price == null)
//            price = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
//
//        long priceLong;
//        try {
//            priceLong = (long) Double.parseDouble(price);
//        } catch (NumberFormatException e) {
//            priceLong = 0l;
//        }
//
//        return priceLong;
//    }

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

        mProductRating.setRating(ratingAverage);
        
        if(ratingCount == 1){
            mProductRatingCount.setText(String.valueOf(ratingCount) + " " + getString(R.string.review) );
        } else {
            mProductRatingCount.setText(String.valueOf(ratingCount) + " " + getString(R.string.reviews) );
        }
        
//        mProductRatingCount.setText("(" + String.valueOf(ratingCount) + ")");
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

                // mSpecialPriceText.setVisibility(View.GONE);
                // mPriceText.setText(normPrice);
                // mPriceText.setPaintFlags(mPriceText.getPaintFlags() &
                // ~Paint.STRIKE_THRU_TEXT_FLAG);
                // mPriceText.setTextColor(getResources().getColor(R.color.red_basic));
                // mPriceText.setVisibility(View.VISIBLE);
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

                // mSpecialPriceText.setText(specPrice);
                // mSpecialPriceText.setVisibility(View.VISIBLE);
                // mPriceText.setText(normPrice);
                // mPriceText.setPaintFlags(mPriceText.getPaintFlags() |
                // Paint.STRIKE_THRU_TEXT_FLAG);
                // mPriceText.setTextColor(getResources().getColor(R.color.grey_light));
                // mPriceText.setVisibility(View.VISIBLE);
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
        if (simple == null && !BaseActivity.isTabletInLandscape(getBaseActivity())) {
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
            Toast.makeText(mContext, R.string.product_outof_stock, Toast.LENGTH_SHORT).show();
            isAddingProductToCart = false;
            return;
        }

        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        String priceAsString;

        priceAsString = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);
        if (priceAsString == null) {
            priceAsString = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
        }

        //Long price = getPriceForTrackingAsLong(simple);

        if (TextUtils.isEmpty(sku)) {
            isAddingProductToCart = false;
            return;
        }

        // Add one unity to cart 
        triggerAddItemToCart(mCompleteProduct.getSku(), sku);

        //Log.i(TAG, "code1price : " + price);

        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, sku);
        bundle.putDouble(TrackerDelegator.PRICE_KEY, mCompleteProduct.getPriceForTracking());
        bundle.putString(TrackerDelegator.NAME_KEY, mCompleteProduct.getName());
        bundle.putString(TrackerDelegator.BRAND_KEY, mCompleteProduct.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, mCompleteProduct.getRatingsAverage());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, mCompleteProduct.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
        if(null != mCompleteProduct && mCompleteProduct.getCategories().size() > 0){
            bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories().get(0));
            if(null != mCompleteProduct && mCompleteProduct.getCategories().size() > 1){
                bundle.putString(TrackerDelegator.SUBCATEGORY_KEY, mCompleteProduct.getCategories().get(1));
            }
        } else {
            bundle.putString(TrackerDelegator.CATEGORY_KEY, "");
        }
        
        TrackerDelegator.trackProductAddedToCart(bundle);
    }

    /**
     * Add one item to cart
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
        ScrollViewWithHorizontal scrollView = (ScrollViewWithHorizontal) getView().findViewById(R.id.product_detail_scrollview);
        scrollView.scrollTo(0, (getView().findViewById(R.id.product_detail_variations_container).getBottom() + 10));
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
        mTitleText.setText(mCompleteProduct.getBrand() != null ? mCompleteProduct.getBrand() + " " + mCompleteProduct.getName() : "");

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

        if (productVariationsFragment == null) {
            productVariationsFragment = ProductVariationsFragment.getInstance();
            Bundle args = new Bundle();
            args.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProductUrl);
            args.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, mSelectedSimple);
            args.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
            args.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            // Instantiate a nested fragment of ProductImageGalleryFragment
            productImagesViewPagerFragment = ProductImageGalleryFragment.getInstanceAsNested(args);

            startFragmentCallbacks();

            // Validate variations
            if (isNotValidVariation(mCompleteProduct.getVariations())){
                if (mainView != null){
                    mainView.findViewById(R.id.product_detail_variations_container).setVisibility(View.GONE);
                    mainView.findViewById(R.id.variation_bottom_line).setVisibility(View.GONE);
                }
            }
            // Containers
            fragmentManagerTransition(R.id.product_detail_variations_container, productVariationsFragment, false, true);
            fragmentManagerTransition(R.id.product_detail_image_gallery_container, productImagesViewPagerFragment, false, true);

            if (mShowRelatedItems) {
                Log.d(TAG, "ON GET RELATED ITEMS FOR: " + product.getSku());
                getRelatedItems(product.getSku());
            }

            FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(mCompleteProduct);
            Bundle bundle = new Bundle();
            bundle.putBoolean(PRODUCT_COMPLETE, true);
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProductUrl);
            bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
            bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, mSelectedSimple);
            bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            FragmentCommunicatorForProduct.getInstance().notifyOthers(0, bundle);
        } else {
            mSelectedSimple = NO_SIMPLE_SELECTED;
            FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(mCompleteProduct);
            Bundle bundle = new Bundle();
            bundle.putBoolean(PRODUCT_COMPLETE, true);
            bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
            bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, mSelectedSimple);
            bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            FragmentCommunicatorForProduct.getInstance().notifyOthers(0, bundle);

            displayPriceInfoOverallOrForSimple();
        }

        setContentInformation();

        if(BaseActivity.isTabletInLandscape(getBaseActivity())){
            displayDescription();
            displaySpecification();   
        }
        // Tracking
        TrackerDelegator.trackProduct(createBundleProduct());
    }
    
    private boolean isNotValidVariation(ArrayList<Variation> variations) {
        if (variations == null || variations.size() == 0) {
            return true;
        } else if (variations.size() == 1 && variations.get(0).getSKU().equals(mCompleteProduct.getSku())) {
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * ################# IMAGE GALLERY #################
     */
    // TODO
    
    
    
    /**
     * ################# RELATED ITEMS #################
     */
    
    /**
     * Method used to get the related products
     */
    private void getRelatedItems(String sku){
        Log.d(TAG, "ON GET RELATED ITEMS FOR: " + sku);
        ArrayList<LastViewed> relatedItemsList = RelatedItemsTableHelper.getRelatedItemsList();
        if(relatedItemsList != null && relatedItemsList.size() > 1){
            for(int i = 0; i< relatedItemsList.size(); i++){
                String itemSku = relatedItemsList.get(i).getProductSku();
                if(!TextUtils.isEmpty(itemSku) && itemSku.equalsIgnoreCase(sku)){
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
     * @param relatedItemsList
     * @modified sergiopereira
     */
    private void showRelatedItemsLayout(ArrayList<LastViewed> relatedItemsList){
        mRelatedContainer.setVisibility(View.VISIBLE);
        // Get Layout inflator
        LayoutInflater inflator = LayoutInflater.from(getBaseActivity());
        // For each related item
        for (LastViewed lastViewed : relatedItemsList) {
            // Create view for related item
            View itemView = createRelatedItemView(inflator, lastViewed);
            // Add view
            mRelatedHorizontalGroup.addView(itemView);
        }
        // Show container
        mRelatedHorizontalScroll.setVisibility(View.VISIBLE);
        mRelatedLoading.setVisibility(View.GONE);
    }
    
    /**
     * Create a related item with respective layout
     * @param inflator
     * @param lastViewed
     * @return View
     * @author sergiopereira
     */
    private View createRelatedItemView(LayoutInflater inflator, LastViewed lastViewed) {
        // Inflate
        View view = inflator.inflate(R.layout.product_item_small, mRelatedHorizontalGroup, false);
        // Get clickable view
        RelativeLayout mElement1 = (RelativeLayout) view.findViewById(R.id.item_container);
        mElement1.setTag(lastViewed.getProductUrl());
        mElement1.setOnClickListener(new OnClickListener() {
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
        });
        // Set data
        TextView brand = (TextView) mElement1.findViewById(R.id.item_brand);
        TextView name = (TextView) mElement1.findViewById(R.id.item_title);
        TextView price = (TextView) mElement1.findViewById(R.id.item_price);
        ImageView image = (ImageView) mElement1.findViewById(R.id.image_view);
        View progress = mElement1.findViewById(R.id.image_loading_progress);
        brand.setText(lastViewed.getProductBrand());
        name.setText(lastViewed.getProductName());
        price.setText(lastViewed.getProductPrice());
        // Load image
        RocketImageLoader.instance.loadImage(lastViewed.getImageUrl(), image, progress, R.drawable.no_image_large);
        // Return the current view
        return view;
    }
    

    /**
     * Validate if is to show the pager wizard
     */
    private void isToShowWizard() {
        if (WizardPreferences.isFirstTime(getBaseActivity(), WizardType.PRODUCT_DETAIL)) {
            Log.d(TAG, "Show Wizard");
            mainView.findViewById(R.id.product_detail_tips_container).setVisibility(View.VISIBLE);
            // boolean hasVariations = (mCompleteProduct != null && mCompleteProduct.getVariations() != null && mCompleteProduct.getVariations().size() > 1) ? true : false;
            ViewPager viewPagerTips = (ViewPager) mainView.findViewById(R.id.viewpager_tips);
            viewPagerTips.setVisibility(View.VISIBLE);
            int[] tips_pages = { R.layout.tip_swipe_layout, R.layout.tip_tap_layout, R.layout.tip_favourite_layout, R.layout.tip_share_layout };
            TipsPagerAdapter mTipsPagerAdapter = new TipsPagerAdapter(getActivity().getApplicationContext(), getActivity().getLayoutInflater(), mainView, tips_pages);
            // mTipsPagerAdapter.setAddVariationsPadding(hasVariations);
            viewPagerTips.setAdapter(mTipsPagerAdapter);
            viewPagerTips.setOnPageChangeListener(new TipsOnPageChangeListener(mainView, tips_pages));
            viewPagerTips.setCurrentItem(0);
            mainView.findViewById(R.id.viewpager_tips_btn_indicator).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.tips_got_it_img).setOnClickListener(this);
        }
    }

    /**
     * 
     */
    private void setCallPhone() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, "");
        if (mPhone2Call.equalsIgnoreCase("")) mPhone2Call = getString(R.string.call_to_order_number);
    }

    /**
     * Create a bundle for tracking
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
        
        if(null != mCompleteProduct && mCompleteProduct.getCategories().size() > 0){
            bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories().get(0));
            if(null != mCompleteProduct && mCompleteProduct.getCategories().size() > 1){
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

    private void executeAddToShoppingCartCompleted() {

        String msgText = "1 " + getResources().getString(R.string.added_to_shop_cart_dialog_text);

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
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.product_detail_product_rating_container) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
            getBaseActivity().onSwitchFragment(FragmentType.POPULARITY,
                    bundle, FragmentController.ADD_TO_BACK_STACK);

        } else if (id == R.id.product_detail_specifications || id == R.id.product_name || id == R.id.product_detail_name ||
                id == R.id.features_more_container || id == R.id.description_more_container) {
            if (null != mCompleteProduct) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DESCRIPTION, bundle, FragmentController.ADD_TO_BACK_STACK);
            }

        } else if (id == R.id.product_detail_product_variant_button) {
            showVariantsDialog();

        } else if (id == R.id.product_detail_shop) {
            if (!isAddingProductToCart) {
                isAddingProductToCart = true;
                executeAddProductToCart();
            }
        } else if (id == R.id.product_detail_call_to_order) {
            String user_id = "";
            if (JumiaApplication.CUSTOMER != null
                    && JumiaApplication.CUSTOMER.getIdAsString() != null) {
                user_id = JumiaApplication.CUSTOMER.getIdAsString();
            }
            
            TrackerDelegator.trackCall(getActivity().getApplicationContext(), user_id,
                    JumiaApplication.SHOP_NAME);

            makeCall();

        } else if (id == R.id.tips_got_it_img) {
            WizardPreferences.changeState(getBaseActivity(), WizardType.PRODUCT_DETAIL);
            try {
                getView().findViewById(R.id.viewpager_tips).setVisibility(View.GONE);
                ((LinearLayout) getView().findViewById(R.id.viewpager_tips_btn_indicator)).setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Log.w(TAG, "WARNING: NPE ON HIDE WIZARD");
            }

        } else if (id == R.id.product_detail_image_is_favourite) {
            boolean isFavourite = false;
            if (mCompleteProduct != null && mCompleteProduct.getAttributes() != null) {
                Object isFavoriteObject = mCompleteProduct.getAttributes().get(RestConstants.JSON_IS_FAVOURITE_TAG);
                if (isFavoriteObject != null && isFavoriteObject instanceof String) {
                    isFavourite = Boolean.parseBoolean((String) isFavoriteObject);
                }
            } else {
                Log.w(TAG, "mCompleteProduct is null or doesn't have attributes");
                return;
            }
            int fragmentMessage = 0;
            
            String sku = mCompleteProduct.getSku();
            if(getSelectedSimple() != null)
                sku = getSelectedSimple().getAttributeByKey(RestConstants.JSON_SKU_TAG);
            
            if (!isFavourite) {
                fragmentMessage = BaseFragment.FRAGMENT_VALUE_SET_FAVORITE;
//                mCompleteProduct.setSimpleSkuPosition(mSelectedSimple);
                FavouriteTableHelper.insertFavouriteProduct(mCompleteProduct);
                mCompleteProduct.getAttributes().put(RestConstants.JSON_IS_FAVOURITE_TAG, Boolean.TRUE.toString());
                imageIsFavourite.setSelected(true);
                
                TrackerDelegator.trackAddToFavorites(sku, mCompleteProduct.getBrand(),mCompleteProduct.getPriceForTracking(),
                        mCompleteProduct.getRatingsAverage(),mCompleteProduct.getMaxSavingPercentage(),false, mCompleteProduct.getCategories());
                Toast.makeText(mContext, getString(R.string.products_added_favourite), Toast.LENGTH_SHORT).show();
            } else {
                fragmentMessage = BaseFragment.FRAGMENT_VALUE_REMOVE_FAVORITE;
                FavouriteTableHelper.removeFavouriteProduct(mCompleteProduct.getSku());
                mCompleteProduct.getAttributes().put(RestConstants.JSON_IS_FAVOURITE_TAG, Boolean.FALSE.toString());
                imageIsFavourite.setSelected(false);
                

                TrackerDelegator.trackRemoveFromFavorites(sku, mCompleteProduct.getPriceForTracking(),mCompleteProduct.getRatingsAverage());
                Toast.makeText(mContext, getString(R.string.products_removed_favourite), Toast.LENGTH_SHORT).show();
            }

            BaseFragment catalogFragment = (BaseFragment) getBaseActivity()
                    .getSupportFragmentManager().
                    findFragmentByTag(FragmentType.PRODUCT_LIST.toString());
            if (null != catalogFragment) {
                catalogFragment.sendValuesToFragment(fragmentMessage, mCompleteProduct.getSku());
            }

        } else if (id == R.id.product_detail_product_image_share) {
            try {
                Intent shareIntent = getBaseActivity().createShareIntent();
                startActivity(shareIntent);
                //GTM
                String category = "";
                if(JumiaApplication.INSTANCE.getCurrentProduct() != null &&
                        JumiaApplication.INSTANCE.getCurrentProduct().getCategories() != null &&
                        JumiaApplication.INSTANCE.getCurrentProduct().getCategories().size() > 0)
                    category = JumiaApplication.INSTANCE.getCurrentProduct().getCategories().get(0);
                TrackerDelegator.trackItemShared(shareIntent,category);
            } catch (NullPointerException e) {
                Log.w(TAG, "WARNING: NPE ON CLICK SHARE");
            }
        } else if(id == R.id.fragment_root_retry_button){
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, getArguments(), FragmentController.ADD_TO_BACK_STACK);
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

    private void showGallery() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
        bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
        bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, mSelectedSimple);
        bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        bundle.putBoolean(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, true);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_GALLERY, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    private void showVariantsDialog() {
        getBaseActivity().showWarningVariation(false);
        String title = getString(R.string.product_variance_choose);
        dialogListFragment = DialogListFragment.newInstance(this, VARIATION_PICKER_ID, title,
                mSimpleVariants, mSimpleVariantsAvailable, mSelectedSimple);
        dialogListFragment.show(getFragmentManager(), null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener#onDialogListItemSelect
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
        
        if (getBaseActivity() == null) return;
        
        getBaseActivity().handleSuccessEvent(bundle);
        
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            getBaseActivity().updateCartInfo();
            hideActivityProgress();
            mAddToCartButton.setEnabled(true);
            executeAddToShoppingCartCompleted();
            break;
        case SEARCH_PRODUCT:
        case GET_PRODUCT_EVENT:
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getBaseActivity().onBackPressed();
                return;
            } else {
                mCompleteProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(mCompleteProduct);
                Bundle params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gproductdetail);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                TrackerDelegator.trackLoadTiming(params);
                displayProduct(mCompleteProduct);
                
                params = new Bundle();
                params.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);                
                if (JumiaApplication.CUSTOMER != null) {
                    params.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER); 
                }                
                params.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
                params.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
                params.putParcelable(AdjustTracker.PRODUCT, mCompleteProduct);
                params.putString(AdjustTracker.TREE, categoryTree);    
                TrackerDelegator.trackPage(TrackingPage.PRODUCT_DETAIL_LOADED, params, getLoadTime(), false);
            }

            // Waiting for the fragment comunication
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFragmentContentContainer();
                }
            }, 300);

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

        if (getBaseActivity().handleErrorEvent(bundle)) {
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            hideActivityProgress();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

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
                    dialog = DialogGenericFragment.newInstance(true, true, false,
                            getString(titleRes),
                            message,
                            getString(R.string.ok_label), "", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dialog.dismiss();
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
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();

                showFragmentContentContainer();

                try {
                    getBaseActivity().onBackPressed();
                } catch (IllegalStateException e) {
                    getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
                }
                return;
            }
        default:
            break;
        }
    }

    @Override
    public void notifyFragment(Bundle bundle) {
        // Log.i(TAG,
        // "code1 loading product on position : "
        // + bundle.getInt(ProductDetailsActivityFragment.LOADING_PRODUCT_KEY));
        onVariationElementSelected(bundle.getInt(ProductDetailsFragment.LOADING_PRODUCT_KEY));
        bundle.putBoolean(LOADING_PRODUCT, true);
        FragmentCommunicatorForProduct.getInstance().notifyOthers(0, bundle);
    }

    protected void fragmentManagerTransition(int container, Fragment fragment, Boolean addToBackStack, Boolean animated) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Animations
        if (animated) fragmentTransaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out, R.anim.pop_in, R.anim.pop_out);
        // fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment);
        // BackStack
        if (addToBackStack) fragmentTransaction.addToBackStack(null);
        // Commit
        // fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * display product specification on landscape
     */
    private void displaySpecification() {
        String shortDescription = mCompleteProduct.getShortDescription();
        // Don't show the features box if there is no content for it
        if (TextUtils.isEmpty(shortDescription)) {
            Log.i(TAG, "shortDescription : empty");
            if(mProductFeaturesContainer!=null){
                mProductFeaturesContainer.setVisibility(View.GONE);
            }
            return;
        } else {
            mProductFeaturesContainer.setVisibility(View.VISIBLE);
        
        String translatedDescription = shortDescription.replace("\r", "<br>");
        Log.d(TAG, "displaySpecification: *" + translatedDescription + "*");
        
        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);
            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
            for (MetricAffectingSpan span : spans) {
                htmlText.removeSpan(span);
            }
        }
        mProductFeaturesText.setText(htmlText);
        
        showMoreButton(mProductFeaturesText,mProductFeaturesMore);
        
        }
    }
    
    /**
     * display product description on landscape
     */
    private void displayDescription() {
        String longDescription = mCompleteProduct.getDescription();
        if(longDescription.isEmpty()){
            mProductDescriptionContainer.setVisibility(View.GONE); 
        } else {
            mProductDescriptionContainer.setVisibility(View.VISIBLE);
        }
        String translatedDescription = longDescription.replace("\r", "<br>");
        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);                
            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
            for (MetricAffectingSpan span: spans) {
                htmlText.removeSpan(span);                
            }
        }
        mProductDescriptionText.setText(htmlText);
        
        showMoreButton(mProductDescriptionText,mProductDescriptionMore);
        
    }
    
    /**
     * function used to calculate if text is all visible or not, on order to show the show more buttom
     * 
     * @param textView, view to count the lines
     * @param moreView, view to hide or show
     */
    private void showMoreButton(final TextView textView, final LinearLayout moreView){
        
        try {
            ViewTreeObserver observer = textView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    int maxLines = (int) textView.getHeight() / textView.getLineHeight();

                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    Log.d("COUNT LINE",":"+maxLines);
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
    
}
