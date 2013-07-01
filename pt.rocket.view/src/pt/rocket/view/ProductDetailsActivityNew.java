package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import pt.rocket.app.ImageLoaderComponent;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.ProductImagesAdapter;
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
import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.DialogList;
import pt.rocket.utils.DialogList.OnDialogListListener;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.LazadaApplication;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
 * 
 * 
 * @date 4/1/2013
 * 
 * @description This class displays the product detail screen
 * 
 */
public class ProductDetailsActivityNew extends MyActivity implements
        OnClickListener, OnItemClickListener, OnDialogListListener {
    private final static String TAG = LogTagHelper.create(ProductDetailsActivityNew.class);
    private final static int NO_SIMPLE_SELECTED = -1;
    private final static int CURRENT_IMAGE_INDEX = 0;
    private final static String VARIATION_PICKER_ID = "variation_picker";

    private Context mContext;
    private Dialog mDialogAddedToCart;

    private CompleteProduct mCompleteProduct;

    private ViewGroup mProductDiscountContainer;

    private Button mAddToCartButton;

    private TextView mProductDiscountPercentage;

    private TextView mProductName;

    private TextView mProductResultPrice;

    private TextView mProductNormalPrice;

    private ViewGroup mDetailsContainer;

    private ViewGroup mVarianceContainer;

    private String mCompleteProductUrl;

    private ViewGroup mProductImageShowOffContainer;

    private ImageView mProductImage;

    private ProgressBar mProductImageLoading;

    private View mVariationsContainer;

    private int mSelectedSimple = NO_SIMPLE_SELECTED;

    private ViewGroup mProductRatingContainer;

    private RatingBar mProductRating;

    private TextView mProductRatingCount;

    private Button mCallToOrderButton;

    private TextView mProductSpecText;

    private View mProductSpecContainer;

    private TextView mProductSpecSku;

    private Button mVarianceButton;

    private TextView mVariantNormPrice;

    private TextView mVariantSpecPrice;
    private boolean mHideVariationSelection;
    private TextView mVarianceText;
    private ViewGroup mProductBasicInfoContainer;

    private long mBeginRequestMillis;
    private ArrayList<String> mSimpleVariants;
    private TextView mVariantChooseError;
    private ProductImagesAdapter mAdapter;
    private HorizontalListView mList;
    private int mVariationsListPosition = -1;
    private View mVariantPriceContainer;
    private String mNavigationPath;
    private int mNavigationSource;

    public ProductDetailsActivityNew() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SHARE),
                EnumSet.of(EventType.GET_PRODUCT_EVENT),
                EnumSet.of(EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT),
                0, R.layout.productdetailsnew);
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

        mVariationsContainer = findViewById(R.id.variations_container);

        mProductImageShowOffContainer = (ViewGroup) findViewById(R.id.product_showoff_container);
        mProductImageShowOffContainer.setOnClickListener(this);
        mProductImage = (ImageView) mProductImageShowOffContainer.findViewById(R.id.product_image);
        mProductImageLoading = (ProgressBar) mProductImageShowOffContainer
                .findViewById(R.id.progressBar2);

        mProductDiscountContainer = (ViewGroup) mProductImageShowOffContainer
                .findViewById(R.id.product_discount_container);

        mProductDiscountPercentage = (TextView) mProductDiscountContainer
                .findViewById(R.id.product_discount_percentage);

        mProductBasicInfoContainer = (ViewGroup) findViewById(R.id.product_basicinfo_container);
        mProductBasicInfoContainer.setOnClickListener(this);

        mProductName = (TextView) findViewById(R.id.product_name);
        mProductResultPrice = (TextView) findViewById(R.id.product_price_result);
        mProductNormalPrice = (TextView) findViewById(R.id.product_price_normal);
        mProductNormalPrice.setPaintFlags(mProductNormalPrice.getPaintFlags()
                | Paint.STRIKE_THRU_TEXT_FLAG);

        mProductRatingContainer = (ViewGroup) findViewById(R.id.product_rating_container);
        mProductRatingContainer.setOnClickListener(this);
        mProductRating = (RatingBar) mProductRatingContainer.findViewById(R.id.product_rating);
        mProductRating.setEnabled(false);
        mProductRatingCount = (TextView) mProductRatingContainer
                .findViewById(R.id.product_rating_count);

        mProductSpecContainer = findViewById(R.id.product_specifications_container);
        mProductSpecContainer.setOnClickListener(this);
        mProductSpecText = (TextView) findViewById(R.id.product_specifications_text);
        mProductSpecSku = (TextView) findViewById(R.id.product_sku_text);

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

    private void setContentInformation() {
        setTitle(mCompleteProduct.getName());
        preselectASimpleItem();

        displayVariations();
        loadProductImage(CURRENT_IMAGE_INDEX);
        mProductName.setText(mCompleteProduct.getName());
        displayPriceInfoOverallOrForSimple();
        updateStockInfo();
        displayRatingInfo();
        displaySpecification();
        displayVariantsContainer();
        updateVariants();
    }

    private void loadProduct() {
        mBeginRequestMillis = System.currentTimeMillis();
        triggerContentEvent(new GetProductEvent(mCompleteProductUrl));
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

    private void displayVariations() {

        if (isNotValidVariation(mCompleteProduct.getVariations())) {
            mVariationsContainer.setVisibility(View.GONE);
            return;
        }

        mList = (HorizontalListView) findViewById(R.id.variations_list);
        if (mAdapter == null) {
            mAdapter = new ProductImagesAdapter(this,
                    ProductImagesAdapter.createImageList(mCompleteProduct.getVariations()));
            mList.setAdapter(mAdapter);
        } else {
            mAdapter.replaceAll(ProductImagesAdapter.createImageList(mCompleteProduct
                    .getVariations()));
        }

        int indexOfSelectionVariation = findIndexOfSelectedVariation();

        mList.setOnItemClickListener(this);
        mList.setSelectedItem(indexOfSelectionVariation, HorizontalListView.MOVE_TO_DIRECTLY);
        Log.d(TAG, "displayVariations: list position = " + mVariationsListPosition);
        mList.setPosition(mVariationsListPosition);
    }

    private boolean isNotValidVariation(ArrayList<Variation> variations) {
        if (variations.size() == 0)
            return true;
        else if (variations.size() == 1
                && variations.get(0).getSKU().equals(mCompleteProduct.getSku()))
            return true;
        else
            return false;
    }

    private int findIndexOfSelectedVariation() {
        ArrayList<Variation> var = mCompleteProduct.getVariations();
        int idx;
        for (idx = 0; idx < var.size(); idx++) {
            if (var.get(idx).getSKU().equals(mCompleteProduct.getSku()))
                return idx;
        }

        return -1;
    }

    private void loadProductImage(int indexInImageList) {
        if (indexInImageList > mCompleteProduct.getImageList().size() - 1) {
            Log.w(TAG,
                    "loadProductImage: index for image is out of range of imagelist. Cant display anything");
            return;
        }

        Log.d(TAG, "loadProductImage: loading product image");
        final ImageView image = mProductImage;
        image.setImageResource(R.drawable.no_image_large);

        final ProgressBar loadingImage = mProductImageLoading;
        loadingImage.setVisibility(View.VISIBLE);
        String imageURL = mCompleteProduct.getImageList().get(indexInImageList);
        ImageLoader.getInstance().displayImage(imageURL, image,
                LazadaApplication.COMPONENTS.get(ImageLoaderComponent.class).largeLoaderOptions,
                new SimpleImageLoadingListener() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#
                     * onLoadingComplete(java.lang.String, android.view.View,
                     * android.graphics.Bitmap)
                     */
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        image.setVisibility(View.VISIBLE);
                        loadingImage.setVisibility(View.GONE);
                        Log.d(TAG, "loadProductImage: onLoadingComplete");
                    }

                });
    }

    private void displayPriceInfoOverallOrForSimple() {
        ProductSimple simple = getSelectedSimple();

        if (mSelectedSimple == NO_SIMPLE_SELECTED || simple == null) {
            String unitPrice = mCompleteProduct.getPrice();
            String specialPrice = mCompleteProduct.getMaxSpecialPrice();
            if (specialPrice == null)
                specialPrice = mCompleteProduct.getSpecialPrice();

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

    private void displayPriceInfo(String unitPrice, String specialPrice, int discountPercentage) {
        Log.d(TAG, "displayPriceInfo: unitPrice = " + unitPrice + " specialPrice = " + specialPrice);
        if (specialPrice == null || specialPrice.equals(unitPrice)) {
            // display only the normal price
            mProductResultPrice.setText(unitPrice);
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setVisibility(View.GONE);
            mProductDiscountContainer.setVisibility(View.GONE);
        } else {
            // display reduced and special price
            mProductResultPrice.setText(specialPrice);
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setText(unitPrice);
            mProductNormalPrice.setVisibility(View.VISIBLE);
            mProductDiscountPercentage.setText("-" + String.valueOf(discountPercentage) + "%");
            mProductDiscountContainer.setVisibility(View.VISIBLE);
        }
    }

    private void updateStockInfo() {
        TextView stockInfo = (TextView) findViewById(R.id.product_instock);
        if (getSelectedSimple() == null) {
            stockInfo.setVisibility(View.GONE);
            return;
        } else
            stockInfo.setVisibility(View.VISIBLE);

        int stockQuantity = 0;
        try {
            stockQuantity = Integer.valueOf(getSelectedSimple().getAttributeByKey(
                    ProductSimple.QUANTITY_TAG));
        } catch (NumberFormatException e) {
            Log.w(TAG, "updateStockInfo: quantity in simple is not a number:", e);
        }

        if (stockQuantity > 0) {
            stockInfo.setText(mContext.getString(R.string.shoppingcart_instock));
            stockInfo.setTextColor(mContext.getResources().getColor(R.color.green_stock));
            mAddToCartButton.setBackgroundResource(R.drawable.btn_orange);
        } else {
            stockInfo.setText(mContext.getString(R.string.shoppingcart_notinstock));
            stockInfo.setTextColor(mContext.getResources().getColor(R.color.red_basic));
            mAddToCartButton.setBackgroundResource(R.drawable.btn_grey);
        }
    }

    private void displayRatingInfo() {
        float ratingAverage = mCompleteProduct.getRatingsAverage().floatValue();
        Integer ratingCount = mCompleteProduct.getRatingsCount();

        mProductRating.setRating(ratingAverage);
        mProductRatingCount.setText("(" + String.valueOf(ratingCount) + ")");
    }

    private void displaySpecification() {
        String shortDescription = mCompleteProduct.getShortDescription();
        mProductSpecSku.setText(mCompleteProduct.getSku());
        if (TextUtils.isEmpty(shortDescription)) {
            mProductSpecText.setVisibility(View.GONE);
        } else {
            mProductSpecText.setVisibility(View.VISIBLE);
            String translatedDescription = shortDescription.replace("\r", "<br>");
            mProductSpecText.setText(Html.fromHtml(translatedDescription));
        }
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
        setShareIntent(createShareIntent());
        setContentInformation();

        AnalyticsGoogle.get().trackProduct(mNavigationSource, mNavigationPath,
                mCompleteProduct.getName(), mCompleteProduct.getSku(), mCompleteProduct.getUrl());
    }

    private void executeAddToShoppingCartCompleted() {
        String msgText = "1 " + getResources().getString(R.string.added_to_shop_cart_dialog_text);

        mDialogAddedToCart = new DialogGeneric(
                this,
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
                            ActivitiesWorkFlow.shoppingCartActivity(ProductDetailsActivityNew.this);
                            mDialogAddedToCart.dismiss();
                        } else if (id == R.id.button2) {
                            mDialogAddedToCart.dismiss();
                        }
                    }
                });

        mDialogAddedToCart.show();
    }

    private void addToShoppingCartFailed() {
        mDialogAddedToCart = new DialogGeneric(this, false, false, true, null,
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

        mDialogAddedToCart.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.product_rating_container) {
            ActivitiesWorkFlow.popularityActivity(this);
        } else if (id == R.id.product_specifications_container
                || id == R.id.product_basicinfo_container) {
            ActivitiesWorkFlow.descriptionActivity(this, mCompleteProduct.getUrl());
        } else if (id == R.id.product_showoff_container) {
            showGallery();
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
        int variationsListPosition;
        if (mList != null)
            variationsListPosition = mList.getPosition();
        else
            variationsListPosition = mVariationsListPosition;

        ActivitiesWorkFlow.productsGalleryActivity(ProductDetailsActivityNew.this,
                mCompleteProduct.getUrl(), variationsListPosition);
    }

    private void showVariantsDialog() {
        String title = getString(R.string.product_variance_choose);
        DialogList dialog = new DialogList(this, VARIATION_PICKER_ID, title, mSimpleVariants,
                mSelectedSimple);
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String url = mCompleteProduct.getVariations().get(position).getLink();
        if (TextUtils.isEmpty(url))
            return;

        if (url.equals(mCompleteProduct.getUrl()))
            return;

        Log.d(TAG, "onItemClick: loading url = " + url);
        mCompleteProductUrl = url;
        mVariationsListPosition = mList.getPosition();
        loadProduct();
    }

    @Override
    public void onDialogListItemSelect(String id, int position, String value) {
        mSelectedSimple = position;
        updateVariants();
        updateStockInfo();
        displayPriceInfoOverallOrForSimple();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ProductsGalleryActivityNew.REQUEST_CODE_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                String productUrl = data
                        .getStringExtra(ProductsGalleryActivityNew.EXTRA_CURRENT_VARIANT);
                if (productUrl == null)
                    return;

                if (mCompleteProduct == null || !mCompleteProduct.getUrl().equals(productUrl)) {
                    mSelectedSimple = NO_SIMPLE_SELECTED;
                    mCompleteProductUrl = productUrl;
                    mVariationsListPosition = data.getIntExtra(
                            ProductsGalleryActivityNew.EXTRA_CURRENT_LISTPOSITION, 0);
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

                    dialog = new DialogGeneric(this, true, true, false,
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
                    dialog.show();
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

}
