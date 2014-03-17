package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetProductHelper;
import pt.rocket.helpers.GetShoppingCartAddItemHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.FragmentCommunicatorForProduct;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.ScrollViewWithHorizontal;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.fragments.BaseFragment;
import pt.rocket.view.fragments.ProductBasicInfoFragment;
import pt.rocket.view.fragments.ProductDetailsDescriptionFragment;
import pt.rocket.view.fragments.ProductImageGalleryFragment;
import pt.rocket.view.fragments.ProductSpecificationsFragment;
import pt.rocket.view.fragments.ProductVariationsFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
public class ProductDetailsActivityFragment extends BaseFragment implements
        OnClickListener, OnDialogListListener {
    private final static String TAG = LogTagHelper.create(ProductDetailsActivityFragment.class);
    private final static int NO_SIMPLE_SELECTED = -1;
    private final static String VARIATION_PICKER_ID = "variation_picker";

    private Context mContext;
    private DialogFragment mDialogAddedToCart;
    private DialogListFragment dialogListFragment;

    private CompleteProduct mCompleteProduct;

    private Button mAddToCartButton;

    private ViewGroup mDetailsContainer;

    private ViewGroup mVarianceContainer;

    private String mCompleteProductUrl;

    public static String SELECTED_SIMPLE_POSITION = "selected_simple_position";
    public static String LOAD_FROM_SCRATCH = "load_from_scratch";
    private int mSelectedSimple = NO_SIMPLE_SELECTED;

    private ViewGroup mProductRatingContainer;

    private RatingBar mProductRating;

    private TextView mProductRatingCount;

    private Button mCallToOrderButton;

    private Button mVarianceButton;

    private TextView mVariantNormPrice;

    private TextView mVariantSpecPrice;
    private boolean mHideVariationSelection;
    private TextView mVarianceText;
    private ViewGroup mProductBasicInfoContainer;

    private long mBeginRequestMillis;
    private ArrayList<String> mSimpleVariants;
    private ArrayList<String> mSimpleVariantsAvailable;
    private TextView mVariantChooseError;

    private View mVariantPriceContainer;
    private String mNavigationPath;
    private int mNavigationSource;

    private RelativeLayout loadingRating;

    private Fragment productVariationsFragment;
    private Fragment productImagesViewPagerFragment;
    private Fragment productSpecificationFragment;
    private Fragment productBasicInfoFragment;

    public static String VARIATION_LIST_POSITION = "variation_list_position";
    private int mVariationsListPosition = -1;

    private String mPhone2Call = "";
    private SharedPreferences sharedPreferences;
    public static String KEY_CALL_TO_ORDER = "call_to_order";

    public final static String LOADING_PRODUCT_KEY = "loading_product_key";
    public final static String LOADING_PRODUCT = "loading_product";
    public final static String PRODUCT_COMPLETE = "complete_product";
    public final static String BASIC_INFO_UPDATE = "update";
    public final static String VARIATIONS_UPDATE = "update";
    public final static String PRODUCT_CATEGORY = "product_category";

    private static View mainView;

    private static String category = "";

    private static ProductDetailsActivityFragment mProductDetailsActivityFragment;

    boolean isAddingProductToCart = false;
    private String mLastSelectedVariance;
    
    public ProductDetailsActivityFragment() {
        super(EnumSet.of(EventType.GET_PRODUCT_EVENT), EnumSet
                .of(EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT), EnumSet.of(MyMenuItem.SHARE),
                NavigationAction.Products, 0);
    }

    public static ProductDetailsActivityFragment getInstance(Bundle bundle) {
        ProductDetailsActivityFragment.mProductDetailsActivityFragment = new ProductDetailsActivityFragment();
        if (bundle.containsKey(PRODUCT_CATEGORY)) {
            category = bundle.getString(PRODUCT_CATEGORY);
        }
        return ProductDetailsActivityFragment.mProductDetailsActivityFragment;
    }

    public void onVariationElementSelected(int position) {
        mVariationsListPosition = position;
        Editor eD = sharedPreferences.edit();
        eD.putInt(VARIATION_LIST_POSITION, mVariationsListPosition);
        eD.putBoolean(LOAD_FROM_SCRATCH, false);
        eD.commit();
        /**
         * Send LOADING_PRODUCT to show loading views.
         */

        loadingRating.setVisibility(View.VISIBLE);
        if (mCompleteProduct.getVariations() == null
                || (mCompleteProduct.getVariations().size() <= position))
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
        sharedPreferences = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mVariationsListPosition = sharedPreferences.getInt(VARIATION_LIST_POSITION, -1);
        mSelectedSimple = sharedPreferences.getInt(SELECTED_SIMPLE_POSITION, NO_SIMPLE_SELECTED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mainView = inflater.inflate(R.layout.productdetailsnew_fragments, container, false);

        // mSelectedSimple = NO_SIMPLE_SELECTED;
        // mVariationsListPosition = -1;
        setAppContentLayout();
        init();
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPhone2Call = sharedPrefs.getString(ProductDetailsActivityFragment.KEY_CALL_TO_ORDER, "");
        if (mPhone2Call.equalsIgnoreCase("")) {
            mPhone2Call = getString(R.string.call_to_order_number);
        }
        boolean showProductDetailsTips = sharedPrefs.getBoolean(ConstantsSharedPrefs.KEY_SHOW_PRODUCT_DETAILS_TIPS, true);
        if (showProductDetailsTips) {
            ViewPager viewPagerTips = (ViewPager) mainView.findViewById(R.id.viewpager_tips);
            viewPagerTips.setVisibility(View.VISIBLE);
            viewPagerTips.setAdapter(new TipsPagerAdapter(getActivity().getLayoutInflater()));
            viewPagerTips.setOnPageChangeListener(tipsPageChangeListener);
            ((LinearLayout) mainView.findViewById(R.id.viewpager_tips_btn_indicator)).setVisibility(View.VISIBLE);
            ((LinearLayout) mainView.findViewById(R.id.viewpager_tips_btn_indicator)).setOnClickListener(this);
        }

        return mainView;
    }

    private void init() {
        Log.d(TAG, "INIT");
        mContext = getActivity();
        Bundle bundle = getArguments();
        mCompleteProductUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        if (mCompleteProductUrl == null) {
            getActivity().onBackPressed();
            return;
        }

        mNavigationSource = bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);
        mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
        
        // Validate the current product
        mCompleteProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        if (mCompleteProduct == null || sharedPreferences.getBoolean(LOAD_FROM_SCRATCH, true)) {
            loadProduct();
        } else {
            displayProduct(mCompleteProduct);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        isAddingProductToCart = false;
        AnalyticsGoogle.get().trackPage(R.string.gproductdetail);
    }

    @Override
    public void onPause() {
        super.onPause();
        dialogListFragment = null;
    }

    @Override
    public void onDestroy() {
        // mSelectedSimple = NO_SIMPLE_SELECTED;
        // unbindDrawables(getView().findViewById(R.id.gallery_container));
        // unbindDrawables(mDetailsContainer);
        // releaseFragments();
        // releaseVars();
        super.onDestroy();
        System.gc();
    }

    private void releaseVars() {
        mContext = null;
        mDialogAddedToCart = null;
        dialogListFragment = null;
        mCompleteProduct = null;

        mAddToCartButton = null;

        mDetailsContainer = null;

        mVarianceContainer = null;

        mCompleteProductUrl = null;

        mProductRatingContainer = null;

        mProductRating = null;

        mProductRatingCount = null;

        mCallToOrderButton = null;

        mVarianceButton = null;

        mVariantNormPrice = null;

        mVariantSpecPrice = null;

        mVarianceText = null;
        mProductBasicInfoContainer = null;

        mSimpleVariants = null;
        mSimpleVariantsAvailable = null;
        mVariantChooseError = null;

        mVariantPriceContainer = null;
        mNavigationPath = null;

        loadingRating = null;

    }

    private void releaseFragments() {
        productVariationsFragment = null;
        productImagesViewPagerFragment = null;
        productSpecificationFragment = null;
        productBasicInfoFragment = null;

    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {
        if (mainView == null)
            mainView = getView();

        mDetailsContainer = (ViewGroup) mainView.findViewById(R.id.details_container);

        mProductBasicInfoContainer = (ViewGroup) mainView.findViewById(
                R.id.product_basicinfo_container);
        mProductBasicInfoContainer.setOnClickListener(this);

        mProductRatingContainer = (ViewGroup) mainView.findViewById(R.id.product_rating_container);
        mProductRatingContainer.setOnClickListener(this);
        mProductRating = (RatingBar) mProductRatingContainer.findViewById(R.id.product_rating);
        mProductRating.setEnabled(false);
        mProductRatingCount = (TextView) mProductRatingContainer
                .findViewById(R.id.product_rating_count);
        loadingRating = (RelativeLayout) mProductRatingContainer.findViewById(R.id.loading_rating);

        mVarianceContainer = (ViewGroup) mainView.findViewById(R.id.product_variant_container);
        mVarianceText = (TextView) mainView.findViewById(R.id.product_variant_text);
        mVarianceButton = (Button) mainView.findViewById(R.id.product_variant_button);
        mVarianceButton.setOnClickListener(this);
        mVariantPriceContainer = mainView.findViewById(R.id.product_variant_price_container);
        mVariantNormPrice = (TextView) mainView.findViewById(R.id.product_variant_normprice);
        mVariantSpecPrice = (TextView) mainView.findViewById(R.id.product_variant_specprice);
        mVariantChooseError = (TextView) mainView.findViewById(R.id.product_variant_choose_error);

        mAddToCartButton = (Button) mainView.findViewById(R.id.shop);
        mAddToCartButton.setSelected(true);
        mAddToCartButton.setOnClickListener(this);

        mCallToOrderButton = (Button) mainView.findViewById(R.id.call_to_order);
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            mCallToOrderButton.setSelected(true);
            mCallToOrderButton.setOnClickListener(this);
        } else {
            mCallToOrderButton.setVisibility(View.GONE);
        }

    }

    private void startFragmentCallbacks() {
//        Log.i(TAG, "code1 starting callbacks!!!");
        CompleteProduct cProduct = null;
        if(!sharedPreferences.getBoolean(LOAD_FROM_SCRATCH, true)){
            cProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        }
        FragmentCommunicatorForProduct.getInstance().destroyInstance();
        FragmentCommunicatorForProduct.getInstance().startFragmentsCallBacks(this,
                productVariationsFragment, productImagesViewPagerFragment,
                productSpecificationFragment, productBasicInfoFragment);
        if(cProduct == null) {
            FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(mCompleteProduct);
        } else {
            FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(cProduct);
        }

    }

    private void setContentInformation() {
        Log.d(TAG, "SET DATA");
        ((BaseActivity) getActivity()).setTitle(mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
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
        triggerContentEvent(new GetProductHelper(), bundle, responseCallback);
        ((BaseActivity) getActivity()).setProcessShow(false);
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
        // Editor eD = sharedPreferences.edit();
        // eD.putInt(VARIATION_LIST_POSITION, mVariationsListPosition);
        // eD.commit();
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

        for (ProductSimple simple : simples) {
            scanSimpleForKnownVariants(simple, foundVariations);
        }

        return foundVariations;
    }

    private void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations) {
        String variations[] = { "size", "color", "variation" };
        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            // Log.d(TAG, "scanSimpleForKnownVariations: variation = " +
            // variation + " attr = " + attr);
            if (attr == null)
                continue;
            foundVariations.add(variation);
        }
    }

    private ArrayList<String> createSimpleVariants() {
        ArrayList<ProductSimple> simples = (ArrayList<ProductSimple>) mCompleteProduct.getSimples()
                .clone();
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(simples);

        mSimpleVariantsAvailable = new ArrayList<String>();
        ArrayList<String> variationValues = new ArrayList<String>();
        for (ProductSimple simple : simples) {
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

    private HashMap<String, String> createVariantAttributesHashMap(ProductSimple simple) {
        ArrayList<ProductSimple> simples = mCompleteProduct.getSimples();
        if (simples.size() <= 1)
            return null;

        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(simples);

        HashMap<String, String> variationsMap = new HashMap<String, String>();
        for (String key : foundKeys) {
            String value = simple.getAttributeByKey(key);

            if (value == null)
                continue;
            if (value.equals("\u2026"))
                continue;

            variationsMap.put(key, value);
        }

        return variationsMap;
    }

    private ProductSimple getSelectedSimple() {
        if (mSelectedSimple >= mCompleteProduct.getSimples().size())
            return null;
        if (mSelectedSimple == NO_SIMPLE_SELECTED)
            return null;
//        Log.i(TAG, "code1tag : "+mLastSelectedVariance+ " "+mCompleteProduct.getSimples().get(mSelectedSimple).getAttributes().get(mSelectedSimple));
        return mCompleteProduct.getSimples().get(mSelectedSimple);
    }

    private void displayPriceInfoOverallOrForSimple() {
        ProductSimple simple = getSelectedSimple();

        if (mSelectedSimple == NO_SIMPLE_SELECTED || simple == null) {
            String unitPrice = mCompleteProduct.getPrice();
            String specialPrice = mCompleteProduct.getSpecialPrice();
            if (specialPrice == null)
                specialPrice = mCompleteProduct.getMaxSpecialPrice();
            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();
//            Log.i(TAG, "code1basic unitPrice : " + unitPrice);
//            Log.i(TAG, "code1basic specialPrice : " + specialPrice);
//            Log.i(TAG, "code1basic discountPercentage : " + discountPercentage);
            Bundle bundle = new Bundle();
            bundle.putString(ProductBasicInfoFragment.DEFINE_UNIT_PRICE, unitPrice);
            bundle.putString(ProductBasicInfoFragment.DEFINE_SPECIAL_PRICE, specialPrice);
            bundle.putInt(ProductBasicInfoFragment.DEFINE_DISCOUNT_PERCENTAGE, discountPercentage);
            bundle.putLong(ProductBasicInfoFragment.DEFINE_STOCK, -1);
            FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment, bundle, 4);

        } else {
            // Simple Products prices dont come with currency preformatted
            String unitPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specialPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);

            unitPrice = currencyFormatHelper(unitPrice);
            if (specialPrice != null)
                specialPrice = currencyFormatHelper(specialPrice);

            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();
            Bundle bundle = new Bundle();
            bundle.putString(ProductBasicInfoFragment.DEFINE_UNIT_PRICE, unitPrice);
            bundle.putString(ProductBasicInfoFragment.DEFINE_SPECIAL_PRICE, specialPrice);
            bundle.putInt(ProductBasicInfoFragment.DEFINE_DISCOUNT_PERCENTAGE, discountPercentage);
            FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment, bundle, 4);

        }
    }

    private long getPriceForTrackingAsLong(ProductSimple simple) {
        String price;

        price = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);
        if (price == null)
            price = simple.getAttributeByKey(ProductSimple.PRICE_TAG);

        long priceLong;
        try {
            priceLong = (long) Double.parseDouble(price);
        } catch (NumberFormatException e) {
            priceLong = 0l;
        }

        return priceLong;
    }

    private void updateStockInfo() {
        
//        Log.d(TAG, "code1stock : "+mSelectedSimple);
        
        // TextView stockInfo = (TextView) findViewById(R.id.product_instock);
        
        /**
         * No simple selected show the stock for the current product
         */
        ProductSimple currentSimple = mCompleteProduct.getSimples().get(0);
        if (mSelectedSimple == NO_SIMPLE_SELECTED && currentSimple != null) {
            try {
                long stockQuantity = -1;
                stockQuantity = Long.valueOf(currentSimple.getAttributeByKey(ProductSimple.QUANTITY_TAG));
//                Log.d(TAG, "code1stock STOCK:  NO SIMPLE SELECTED " + stockQuantity);
                Bundle bundle = new Bundle();
                bundle.putLong(ProductBasicInfoFragment.DEFINE_STOCK, stockQuantity);
                FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment, bundle, 4);
                return;
            } catch (NumberFormatException e) {
                Log.w(TAG, "code1stock STOCK INFO: quantity in simple is null: ", e);
            }
        }
        
        /**
         * Simple selected but is null
         */
        if (getSelectedSimple() == null) {
//            Log.d(TAG, "code1stock STOCK:  SIMPLE IS NULL " + mSelectedSimple);
            Bundle bundle = new Bundle();
            bundle.putLong(ProductBasicInfoFragment.DEFINE_STOCK, -1);
            FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment, bundle, 4);
            return;
        }
        
        /**
         * Simple selected
         */
//        Log.d(TAG, "code1stock SIMPLE " + mSelectedSimple);
        long stockQuantity = 0;
        try {
            stockQuantity = Long.valueOf(getSelectedSimple().getAttributeByKey(ProductSimple.QUANTITY_TAG));
        } catch (NumberFormatException e) {
            Log.w(TAG, "code1stock: quantity in simple is not a number:", e);
        }

        if (stockQuantity > 0) {
            mAddToCartButton.setBackgroundResource(R.drawable.btn_orange);
        } else {
            mAddToCartButton.setBackgroundResource(R.drawable.btn_grey);
        }
        
//        Log.d(TAG, "code1stock UPDATE STOCK INFO: " + stockQuantity);
        
        Bundle bundle = new Bundle();
        bundle.putLong(ProductBasicInfoFragment.DEFINE_STOCK, stockQuantity);
        FragmentCommunicatorForProduct.getInstance().notifyTarget(productBasicInfoFragment, bundle, 4);

    }

    private void displayRatingInfo() {

        float ratingAverage = mCompleteProduct.getRatingsAverage().floatValue();
        Integer ratingCount = mCompleteProduct.getRatingsCount();

        mProductRating.setRating(ratingAverage);
        mProductRatingCount.setText("(" + String.valueOf(ratingCount) + ")");
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
//        Log.i(TAG, "code1stock size selected : "+mSelectedSimple);
        if (mSelectedSimple == NO_SIMPLE_SELECTED) {
            mVarianceButton.setText("...");
        }

        mSimpleVariants = createSimpleVariants();

        ProductSimple simple = getSelectedSimple();
        mVariantChooseError.setVisibility(View.GONE);
//        Log.i(TAG, "code1stock size selected!" + mSelectedSimple);
        if (simple == null) {
            mVariantPriceContainer.setVisibility(View.GONE);
        } else {
            Log.i(TAG, "size is : " + mSimpleVariants.get(mSelectedSimple));
            mVariantPriceContainer.setVisibility(View.VISIBLE);
            String normPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);

            if (TextUtils.isEmpty(specPrice)) {
                normPrice = currencyFormatHelper(normPrice);
                mVariantSpecPrice.setVisibility(View.GONE);
                mVariantNormPrice.setText(normPrice);
                mVariantNormPrice.setPaintFlags(mVariantNormPrice.getPaintFlags()
                        & ~Paint.STRIKE_THRU_TEXT_FLAG);

                mVariantNormPrice.setTextColor(getResources().getColor(R.color.red_basic));
                mVariantNormPrice.setVisibility(View.VISIBLE);
            } else {
                normPrice = currencyFormatHelper(normPrice);
                specPrice = currencyFormatHelper(specPrice);
                mVariantSpecPrice.setText(specPrice);
                mVariantSpecPrice.setVisibility(View.VISIBLE);

                mVariantNormPrice.setText(normPrice);
                mVariantNormPrice.setPaintFlags(mVariantNormPrice.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                mVariantNormPrice.setTextColor(getResources().getColor(R.color.grey_light));
                mVariantNormPrice.setVisibility(View.VISIBLE);
            }
            
//            if(mLastSelectedVariance!= null && !mLastSelectedVariance.equalsIgnoreCase(mSimpleVariants.get(mSelectedSimple))){
//                mVarianceButton.setText("...");
//                mSelectedSimple = NO_SIMPLE_SELECTED;
//                mLastSelectedVariance = null;
//            } else {
                mLastSelectedVariance = mSimpleVariants.get(mSelectedSimple);
                mVarianceButton.setText(mSimpleVariants.get(mSelectedSimple));    
//            }
            
            mVarianceText.setTextColor(getResources().getColor(R.color.grey_middle));
        }

    }

    private String currencyFormatHelper(String number) {
        return CurrencyFormatter.formatCurrency(Double.parseDouble(number));
    }

    private void executeAddProductToCart() {
        ProductSimple simple = getSelectedSimple();
        if (simple == null && !BaseActivity.isTabletInLandscape(getBaseActivity())) {
            showChooseReminder();
            isAddingProductToCart = false;
            return;
        } else if (simple == null) {
            ((BaseActivity) getActivity()).showWarningVariation(true);
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
            return;
        }

        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        Long price = getPriceForTrackingAsLong(simple);

        if (TextUtils.isEmpty(sku))
            return;

        ShoppingCartItem item = new ShoppingCartItem(createVariantAttributesHashMap(simple));
        item.initialize(mCompleteProduct.getSku(), sku, mCompleteProduct.getImageList().get(0),
                mCompleteProduct.getUrl(),
                mCompleteProduct.getBrand() + " " + mCompleteProduct.getName(), quantity,
                mCompleteProduct.getSpecialPrice(), mCompleteProduct.getPrice(), 1);

        ((BaseActivity) getActivity()).showProgress();

        triggerAddItemToCart(item);

        AnalyticsGoogle.get().trackAddToCart(sku, price);
        TrackerDelegator.trackProductAddedToCart(getActivity(), mCompleteProduct, simple,
                (double) price, getString(R.string.mixprop_itemlocationdetails));

    }

    private void triggerAddItemToCart(ShoppingCartItem item) {
        // ShoppingCartItem item = event.value;

        ContentValues values = new ContentValues();

        // add the simple data to the registry
        if (item.getSimpleData() != null) {
            JumiaApplication.INSTANCE.getItemSimpleDataRegistry().put(item.getConfigSKU(),
                    item.getSimpleData());
        }

        values.put("p", item.getConfigSKU());
        values.put("sku", item.getConfigSimpleSKU());
        values.put("quantity", "" + item.getQuantity());
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        triggerContentEventWithNoLoading(new GetShoppingCartAddItemHelper(), bundle,
                responseCallback);
    }

    private void showChooseReminder() {
        ((BaseActivity) getActivity()).showWarningVariation(true);
        ScrollViewWithHorizontal scrollView = (ScrollViewWithHorizontal) getView().findViewById(
                R.id.scrollview);
        scrollView.scrollTo(0,
                (getView().findViewById(R.id.product_variant_choose).getBottom() + 10));
    }

    private void displayProduct(CompleteProduct product) {
        Log.d(TAG, "SHOW PRODUCT");
        JumiaApplication.INSTANCE.setCurrentProduct(product);
        LastViewedTableHelper.insertViewedProduct(JumiaApplication.INSTANCE.getApplicationContext(), product.getSku(), product.getBrand()+" "+product.getName(), product.getSpecialPrice(), product.getUrl(), product.getImageList().get(0));
        mCompleteProduct = product;
        mCompleteProductUrl = product.getUrl();
        ((BaseActivity) getActivity()).setTitle(mCompleteProduct.getBrand() + " "
                + mCompleteProduct.getName());
        if (productVariationsFragment == null) {
            productVariationsFragment = ProductVariationsFragment.getInstance();
            Bundle args = new Bundle();
            args.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProductUrl);
            args.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, mSelectedSimple);
            args.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
            args.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            productImagesViewPagerFragment = ProductImageGalleryFragment.getInstance(args);
            if(BaseActivity.isTabletInLandscape(getBaseActivity())){
                productSpecificationFragment = ProductDetailsDescriptionFragment.getInstance();
            } else {
                productSpecificationFragment = ProductSpecificationsFragment.getInstance();
            }
            productBasicInfoFragment = ProductBasicInfoFragment.getInstance();
            startFragmentCallbacks();

            fragmentManagerTransition(R.id.variations_container, productVariationsFragment, false,
                    true);
            fragmentManagerTransition(R.id.image_gallery_container, productImagesViewPagerFragment,
                    false, true);
            fragmentManagerTransition(R.id.product_specifications_container,
                    productSpecificationFragment, false, true);
            fragmentManagerTransition(R.id.product_basicinfo_container, productBasicInfoFragment,
                    false, true);

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
        ((BaseActivity) getActivity()).setShareIntent(((BaseActivity) getActivity()).createShareIntent());

        setContentInformation();

        AnalyticsGoogle.get().trackProduct(mNavigationSource, mNavigationPath,
                mCompleteProduct.getBrand() + " " + mCompleteProduct.getName(),
                mCompleteProduct.getSku(), mCompleteProduct.getUrl());
        TrackerDelegator.trackProduct(getActivity(), mCompleteProduct, category);
    }

    private void displayGallery(CompleteProduct product) {
        mCompleteProduct = product;
        ((BaseActivity) getActivity()).setShareIntent(((BaseActivity) getActivity())
                .createShareIntent());
        ((BaseActivity) getActivity()).setTitle(mCompleteProduct.getBrand() + " "
                + mCompleteProduct.getName());

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
                            ((BaseActivity) getActivity()).onSwitchFragment(
                                    FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE,
                                    FragmentController.ADD_TO_BACK_STACK);
                            mDialogAddedToCart.dismiss();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.product_rating_container) {
            ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.POPULARITY,
                    FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        } else if (id == R.id.product_basicinfo_container) {
            if (null != mCompleteProduct) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
                BaseActivity activity = ((BaseActivity) getActivity());
                if (null == activity) {
                    activity = mainActivity;
                }
                activity.onSwitchFragment(FragmentType.PRODUCT_DESCRIPTION, bundle,
                        FragmentController.ADD_TO_BACK_STACK);
            }
        } else if (id == R.id.product_variant_button) {
            showVariantsDialog();
        } else if (id == R.id.shop) {
            if(!isAddingProductToCart){
                isAddingProductToCart = true;
                executeAddProductToCart();
            }
        } else if (id == R.id.call_to_order) {
            // Toast.makeText(this, "Hello! I want to order. Can you hear me?", Toast.LENGTH_LONG)
            // .show();
            makeCall();

        } else if (id == R.id.viewpager_tips_btn_indicator) {
            SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                    ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            Editor eD = sharedPrefs.edit();
            eD.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PRODUCT_DETAILS_TIPS, false);
            eD.commit();
            getView().findViewById(R.id.viewpager_tips).setVisibility(View.GONE);
            ((LinearLayout) getView().findViewById(R.id.viewpager_tips_btn_indicator))
                    .setVisibility(View.GONE);
        }

    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mPhone2Call));
        startActivity(callIntent);
    }

    private void showGallery() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
        bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
        bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, mSelectedSimple);
        bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        bundle.putBoolean(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, true);
        ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_GALLERY, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    private void showVariantsDialog() {
        ((BaseActivity) getActivity()).showWarningVariation(false);
        String title = getString(R.string.product_variance_choose);
        dialogListFragment = DialogListFragment.newInstance(this, VARIATION_PICKER_ID,
                title, mSimpleVariants, mSimpleVariantsAvailable,
                mSelectedSimple);
        
        dialogListFragment.show(getFragmentManager(), null);
    }

    OnPageChangeListener tipsPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            ImageView indicator1 = (ImageView) getView().findViewById(R.id.indicator1);
            ImageView indicator2 = (ImageView) getView().findViewById(R.id.indicator2);

            switch (position) {
            case 0:
                indicator1.setImageResource(R.drawable.bullit_showing);
                indicator2.setImageResource(R.drawable.bullit);
                break;
            case 1:
                indicator1.setImageResource(R.drawable.bullit);
                indicator2.setImageResource(R.drawable.bullit_showing);
                break;

            default:
                break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

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
        if(!isVisible()){
            return;
        }
        if(getBaseActivity() == null)
            return;
        getBaseActivity().handleSuccessEvent(bundle);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            getBaseActivity().updateCartInfo();
            ((BaseActivity) getActivity()).dismissProgress();
            mAddToCartButton.setEnabled(true);
            executeAddToShoppingCartCompleted();
            break;
        case GET_PRODUCT_EVENT:
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved),
                        Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            } else {
                mCompleteProduct = (CompleteProduct) bundle
                        .getParcelable(Constants.BUNDLE_RESPONSE_KEY);

                FragmentCommunicatorForProduct.getInstance().updateCurrentProduct(mCompleteProduct);
                ((BaseActivity) getActivity()).setProcessShow(true);
                AnalyticsGoogle.get().trackLoadTiming(R.string.gproductdetail, mBeginRequestMillis);
                displayProduct(mCompleteProduct);
                displayGallery(mCompleteProduct);
            }

            getBaseActivity().showContentContainer(false);
            break;
        }
    }

    public void onErrorEvent(Bundle bundle) {
        if(!isVisible()){
            return;
        }
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            ((BaseActivity) getActivity()).dismissProgress();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

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
                        List<String> validateMessages = errorMessages
                                .get(RestConstants.JSON_VALIDATE_TAG);
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
        case GET_PRODUCT_EVENT:
            if (!errorCode.isNetworkError()) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved),
                        Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            }
        }
    }

    private class TipsPagerAdapter extends PagerAdapter {

        private int[] tips_pages = { R.layout.tip_swipe_layout, R.layout.tip_tap_layout };

        private LayoutInflater mLayoutInflater;

        public TipsPagerAdapter(LayoutInflater layoutInflater) {
            mLayoutInflater = layoutInflater;
        }

        @Override
        public int getCount() {
            return tips_pages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return view == ((View) arg1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            
            if (position != 0 && container.getChildCount() <= 0) {
                View view = mLayoutInflater.inflate(tips_pages[0], container, false);
                ((ViewPager) container).addView(view, 0);    
            }
            
            View view = mLayoutInflater.inflate(tips_pages[position], container, false);
            ((ViewPager) container).addView(view, position);    

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(getView().findViewById(tips_pages[position]));
        }

    }

    @Override
    public void notifyFragment(Bundle bundle) {
//        Log.i(TAG,
//                "code1 loading product on position : "
//                        + bundle.getInt(ProductDetailsActivityFragment.LOADING_PRODUCT_KEY));
        onVariationElementSelected(bundle
                .getInt(ProductDetailsActivityFragment.LOADING_PRODUCT_KEY));
        bundle.putBoolean(LOADING_PRODUCT, true);
        FragmentCommunicatorForProduct.getInstance().notifyOthers(0, bundle);
    }

    protected void fragmentManagerTransition(int container, Fragment fragment,
            Boolean addToBackStack, Boolean animated) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Animations
        if (animated)
            fragmentTransaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out, R.anim.pop_in,
                    R.anim.pop_out);
        // fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
        // R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment);
        // BackStack
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        // Commit
        fragmentTransaction.commit();
    }

}
