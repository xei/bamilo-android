package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.AddItemToShoppingCartEvent;
import pt.rocket.framework.event.events.GetProductEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.ProductBasicInfoFragment;
import pt.rocket.view.fragments.ProductImageShowOffFragment;
import pt.rocket.view.fragments.ProductSpecificationsFragment;
import pt.rocket.view.fragments.ProductVariationsFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
public class ProductDetailsActivityFragment extends BaseActivity implements
        OnClickListener, OnDialogListListener {
    private final static String TAG = LogTagHelper.create(ProductDetailsActivityFragment.class);
    private final static int NO_SIMPLE_SELECTED = -1;
    private final static String VARIATION_PICKER_ID = "variation_picker";

    private LayoutInflater mInflater;

    private Context mContext;
    private DialogFragment mDialogAddedToCart;

    private CompleteProduct mCompleteProduct;

    private Button mAddToCartButton;

    private ViewGroup mDetailsContainer;

    private ViewGroup mVarianceContainer;

    private String mCompleteProductUrl;

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
    private TextView mVariantChooseError;

    private View mVariantPriceContainer;
    private String mNavigationPath;
    private int mNavigationSource;

    private RelativeLayout loadingRating;
    
    private Fragment productVariationsFragment;
    private Fragment productImageShowOffFragment;
    private Fragment productSpecificationFragment;
    private Fragment productBasicInfoFragment;
    
    private OnActivityFragmentInteraction mCallbackProductVariationsFragment;
    private OnActivityFragmentInteraction mCallbackProductImageShowOffFragment;
    private OnActivityFragmentInteraction mCallbackProductSpecificationFragment;
    private OnActivityFragmentInteraction mCallbackProductBasicInfoFragment;
    
    private int mVariationsListPosition = -1;
    
    
    private final int LOADING_PRODUCT = -1;
    public ProductDetailsActivityFragment() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SHARE),
                EnumSet.of(EventType.GET_PRODUCT_EVENT),
                EnumSet.of(EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT),
                0, R.layout.productdetailsnew_fragments);
    }
    
    @Override
    public void onFragmentElementSelected(int position){
        mVariationsListPosition = position;
        /**
         * Send LOADING_PRODUCT to show loading views.
         */
        mCallbackProductImageShowOffFragment.sendPositionToFragment(LOADING_PRODUCT);
        mCallbackProductSpecificationFragment.sendPositionToFragment(LOADING_PRODUCT);
        mCallbackProductBasicInfoFragment.sendPositionToFragment(LOADING_PRODUCT);
        loadingRating.setVisibility(View.VISIBLE);
        
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
    public void onFragmentSelected(FragmentType type){
        if(type == FragmentType.PRODUCT_SHOWOFF){
            showGallery();
        } else if(type == FragmentType.PRODUCT_SPECIFICATION){
            ActivitiesWorkFlow.descriptionActivity(this, mCompleteProduct.getUrl());    
        }
        
        
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppContentLayout();
        init(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }
    
    private void init(Intent intent) {
        mContext = getApplicationContext();
        mCompleteProductUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
        mNavigationSource = intent.getIntExtra(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);
        mNavigationPath = intent.getStringExtra(ConstantsIntentExtra.NAVIGATION_PATH);
        loadProduct();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage(R.string.gproductdetail);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(mDetailsContainer);
        System.gc();
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {

        mDetailsContainer = (ViewGroup) findViewById(R.id.details_container);

        mProductBasicInfoContainer = (ViewGroup) findViewById(R.id.product_basicinfo_container);
        mProductBasicInfoContainer.setOnClickListener(this);



        mProductRatingContainer = (ViewGroup) findViewById(R.id.product_rating_container);
        mProductRatingContainer.setOnClickListener(this);
        mProductRating = (RatingBar) mProductRatingContainer.findViewById(R.id.product_rating);
        mProductRating.setEnabled(false);
        mProductRatingCount = (TextView) mProductRatingContainer.findViewById(R.id.product_rating_count);
        loadingRating = (RelativeLayout) mProductRatingContainer.findViewById(R.id.loading_rating);


        mVarianceContainer = (ViewGroup) findViewById(R.id.product_variant_container);
        mVarianceText = (TextView) findViewById(R.id.product_variant_text);
        mVarianceButton = (Button) findViewById(R.id.product_variant_button);
        mVarianceButton.setOnClickListener(this);
        mVariantPriceContainer = findViewById(R.id.product_variant_price_container);
        mVariantNormPrice = (TextView) findViewById(R.id.product_variant_normprice);
        mVariantSpecPrice = (TextView) findViewById(R.id.product_variant_specprice);
        mVariantChooseError = (TextView) findViewById(R.id.product_variant_choose_error);

        mAddToCartButton = (Button) findViewById(R.id.shop);
        mAddToCartButton.setOnClickListener(this);
        mCallToOrderButton = (Button) findViewById(R.id.call_to_order);
        mCallToOrderButton.setOnClickListener(this);
    }

    private void startFragmentCallbacks() {
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackProductVariationsFragment = (OnActivityFragmentInteraction) productVariationsFragment;
            mCallbackProductImageShowOffFragment = (OnActivityFragmentInteraction) productImageShowOffFragment;
            mCallbackProductSpecificationFragment = (OnActivityFragmentInteraction) productSpecificationFragment;
            mCallbackProductBasicInfoFragment = (OnActivityFragmentInteraction) productBasicInfoFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(productVariationsFragment.toString()
                    + " must implement OnActivityFragmentInteraction");
        }

    }

    private void setContentInformation() {
        setTitle(mCompleteProduct.getName());
        preselectASimpleItem();

        // displayVariations();
//        mProductName.setText(mCompleteProduct.getName());
        displayPriceInfoOverallOrForSimple();
        updateStockInfo();
        displayRatingInfo();
        displayVariantsContainer();
        updateVariants();
    }

    private void loadProduct() {
        mBeginRequestMillis = System.currentTimeMillis();
        triggerContentEvent(new GetProductEvent(mCompleteProductUrl));
    }

    private void loadProductPartial() {
        mBeginRequestMillis = System.currentTimeMillis();
        triggerContentEventWithNoLoading(new GetProductEvent(mCompleteProductUrl));
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
            } else
                mSelectedSimple = NO_SIMPLE_SELECTED;
        } else
            mSelectedSimple = NO_SIMPLE_SELECTED;

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
        ArrayList<ProductSimple> simples = mCompleteProduct.getSimples();
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(simples);

        ArrayList<String> variationValues = new ArrayList<String>();
        for (ProductSimple simple : simples) {
            String value = calcVariationStringForSimple(simple, foundKeys);
            variationValues.add(value);
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
            // Log.d( TAG, "createVariationHashMap: key = *" + key +
            // "* value = *" + value + "*");

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

        return mCompleteProduct.getSimples().get(mSelectedSimple);
    }


    private void displayPriceInfoOverallOrForSimple() {
        ProductSimple simple = getSelectedSimple();

        if (mSelectedSimple == NO_SIMPLE_SELECTED || simple == null) {
            String unitPrice = mCompleteProduct.getPrice();
            String specialPrice = mCompleteProduct.getMaxSpecialPrice();
            if (specialPrice == null)
                specialPrice = mCompleteProduct.getSpecialPrice();

            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();
            mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_UNIT_PRICE, unitPrice);
            mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_SPECIAL_PRICE, specialPrice);
            mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_DISCOUNT_PERCENTAGE, discountPercentage);
//            displayPriceInfo(unitPrice, specialPrice, discountPercentage);
        } else {
            // Simple Products prices dont come with currency preformatted
            String unitPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specialPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);
            unitPrice = currencyFormatHelper(unitPrice);
            if (specialPrice != null)
                specialPrice = currencyFormatHelper(specialPrice);
            int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();
            mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_UNIT_PRICE, unitPrice);
            mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_SPECIAL_PRICE, specialPrice);
            mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_DISCOUNT_PERCENTAGE, discountPercentage);
//            displayPriceInfo(unitPrice, specialPrice, discountPercentage);

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
//        TextView stockInfo = (TextView) findViewById(R.id.product_instock);
        if (getSelectedSimple() == null) {
            mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_STOCK, -1);
            return;
        }
        int stockQuantity = 0;
        try {
            stockQuantity = Integer.valueOf(getSelectedSimple().getAttributeByKey(
                    ProductSimple.QUANTITY_TAG));
        } catch (NumberFormatException e) {
            Log.w(TAG, "updateStockInfo: quantity in simple is not a number:", e);
        }

        if (stockQuantity > 0) {        
            mAddToCartButton.setBackgroundResource(R.drawable.btn_orange);
        } else {
            mAddToCartButton.setBackgroundResource(R.drawable.btn_grey);
        }
        
        mCallbackProductBasicInfoFragment.sendValuesToFragment(ProductBasicInfoFragment.DEFINE_STOCK, stockQuantity);
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
            mVarianceContainer.setVisibility(View.GONE);
        } else {
            mVarianceContainer.setVisibility(View.VISIBLE);
        }
    }

    public void updateVariants() {
        mSimpleVariants = createSimpleVariants();

        ProductSimple simple = getSelectedSimple();
        mVariantChooseError.setVisibility(View.GONE);

        if (simple == null) {
            mVariantPriceContainer.setVisibility(View.GONE);
        } else {
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

            mVarianceButton.setText(mSimpleVariants.get(mSelectedSimple));
            mVarianceText.setTextColor(getResources().getColor(R.color.grey_middle));
        }

    }

    private String currencyFormatHelper(String number) {
        return CurrencyFormatter.formatCurrency(Double.parseDouble(number));
    }

    private void executeAddProductToCart() {
        ProductSimple simple = getSelectedSimple();
        if (simple == null) {
            showChooseReminder();
            return;
        }

        int quantity = 0;
        try {
            quantity = Integer.valueOf(simple.getAttributeByKey(ProductSimple.QUANTITY_TAG));
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
                mCompleteProduct.getUrl(), mCompleteProduct.getName(), quantity,
                mCompleteProduct.getSpecialPrice(), mCompleteProduct.getPrice(), 1);

        showProgress();
        EventManager.getSingleton().triggerRequestEvent(new AddItemToShoppingCartEvent(item));

        AnalyticsGoogle.get().trackAddToCart(sku, price);
    }

    private void showChooseReminder() {
        // mVarianceText.setTextColor(getResources().getColor(R.color.red_basic));
        mVariantChooseError.setVisibility(View.VISIBLE);
    }

    private void displayProduct(CompleteProduct product) {
        mCompleteProduct = product;
        mCompleteProductUrl = product.getUrl();
        
        if(productVariationsFragment == null){
            productVariationsFragment = ProductVariationsFragment.getInstance();
            productImageShowOffFragment = ProductImageShowOffFragment.getInstance();
            productSpecificationFragment = ProductSpecificationsFragment.getInstance();
            productBasicInfoFragment = ProductBasicInfoFragment.getInstance();
            startFragmentCallbacks();
            mCallbackProductVariationsFragment.sendValuesToFragment(0, mCompleteProduct);
            mCallbackProductVariationsFragment.sendPositionToFragment(-1);
            mCallbackProductImageShowOffFragment.sendValuesToFragment(0, mCompleteProduct);
            mCallbackProductSpecificationFragment.sendValuesToFragment(0, mCompleteProduct);
            mCallbackProductBasicInfoFragment.sendValuesToFragment(0, mCompleteProduct);
            fragmentManagerTransition(R.id.variations_container, productVariationsFragment, false, false);
            fragmentManagerTransition(R.id.product_showoff_container, productImageShowOffFragment, false, false);
            fragmentManagerTransition(R.id.product_specifications_container, productSpecificationFragment, false, false);
            fragmentManagerTransition(R.id.product_basicinfo_container, productBasicInfoFragment, false, false);
        } else {
            mCallbackProductVariationsFragment.sendValuesToFragment(1, mCompleteProduct);
            mCallbackProductImageShowOffFragment.sendValuesToFragment(1, mCompleteProduct);
            mCallbackProductSpecificationFragment.sendValuesToFragment(1, mCompleteProduct);
            mCallbackProductBasicInfoFragment.sendValuesToFragment(1, mCompleteProduct);
        }
        setShareIntent(createShareIntent());
        setContentInformation();

        AnalyticsGoogle.get().trackProduct(mNavigationSource, mNavigationPath,
                mCompleteProduct.getName(), mCompleteProduct.getSku(), mCompleteProduct.getUrl());
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
                            ActivitiesWorkFlow.shoppingCartActivity(ProductDetailsActivityFragment.this);
                            mDialogAddedToCart.dismiss();
                        } else if (id == R.id.button2) {
                            mDialogAddedToCart.dismiss();
                        }
                    }
                });

        mDialogAddedToCart.show(getSupportFragmentManager(), null);
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

        mDialogAddedToCart.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.product_rating_container) {
            ActivitiesWorkFlow.popularityActivity(this);
        } else if (id == R.id.product_basicinfo_container) {
            ActivitiesWorkFlow.descriptionActivity(this, mCompleteProduct.getUrl());
        } else if (id == R.id.product_variant_button) {
            showVariantsDialog();
        } else if (id == R.id.shop) {
            executeAddProductToCart();
        } else if (id == R.id.call_to_order) {
            Toast.makeText(this, "Hello! I want to order. Can you hear me?", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void showGallery() {
        ActivitiesWorkFlow.productsGalleryActivity(ProductDetailsActivityFragment.this,
                mCompleteProduct.getUrl(), mVariationsListPosition);
    }
    
    private void showVariantsDialog() {
        String title = getString(R.string.product_variance_choose);
        DialogListFragment dialog = DialogListFragment.newInstance(this, VARIATION_PICKER_ID, title, mSimpleVariants,
                mSelectedSimple);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onDialogListItemSelect(String id, int position, String value) {
        mSelectedSimple = position;
        updateVariants();
        Log.i(TAG, "code1 passou aqui primeiro!");
        updateStockInfo();
        displayPriceInfoOverallOrForSimple();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ProductsGalleryActivityFragment.REQUEST_CODE_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                String productUrl = data
                        .getStringExtra(ProductsGalleryActivityFragment.EXTRA_CURRENT_VARIANT);
                if (productUrl == null)
                    return;

                if (mCompleteProduct == null || !mCompleteProduct.getUrl().equals(productUrl)) {
                    mSelectedSimple = NO_SIMPLE_SELECTED;
                    mCompleteProductUrl = productUrl;
                    mCallbackProductVariationsFragment.sendPositionToFragment(data.getIntExtra(
                            ProductsGalleryActivityFragment.EXTRA_CURRENT_LISTPOSITION, 0));
                    loadProduct();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        Log.d(TAG, "onSuccessEvent: type = " + event.getType());
        switch (event.getType()) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            mAddToCartButton.setEnabled(true);
            executeAddToShoppingCartCompleted();
            break;
        case GET_PRODUCT_EVENT:
            AnalyticsGoogle.get().trackLoadTiming(R.string.gproductdetail, mBeginRequestMillis);
            displayProduct((CompleteProduct) event.result);
            break;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        Log.d(TAG, "onErrorEvent: type = " + event.getType());
        switch (event.getType()) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            dismissProgress();
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                List<String> errorMessages = event.errorMessages.get(Errors.JSON_ERROR_TAG);
                if (errorMessages != null) {
                    int titleRes = R.string.error_add_to_cart_failed;
                    int msgRes = -1;

                    String message = null;
                    if (errorMessages.contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                        msgRes = R.string.product_outof_stock;
                    } else if (errorMessages.contains(Errors.CODE_PRODUCT_ADD_OVERQUANTITY)) {
                        msgRes = R.string.error_add_to_shopping_cart_quantity;
                    } else if (errorMessages.contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                        List<String> validateMessages = event.errorMessages
                                .get(Errors.JSON_VALIDATE_TAG);
                        if (validateMessages != null && validateMessages.size() > 0) {
                            message = validateMessages.get(0);
                        } else {
                            msgRes = R.string.error_add_to_cart_failed;
                        }
                    }

                    if (msgRes != -1) {
                        message = getString(msgRes);
                    } else if (message == null) {
                        return false;
                    }
                    
                    FragmentManager fm = getSupportFragmentManager();
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
                    return true;
                }
            }
            if (!event.errorCode.isNetworkError()) {
                addToShoppingCartFailed();
                return true;
            }
        case GET_PRODUCT_EVENT:
            if (!event.errorCode.isNetworkError()) {
                Toast.makeText(this, getString(R.string.product_could_not_retrieved),
                        Toast.LENGTH_LONG).show();
                finish();
                return true;
            }
        }
        return super.onErrorEvent(event);
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
    }

}
