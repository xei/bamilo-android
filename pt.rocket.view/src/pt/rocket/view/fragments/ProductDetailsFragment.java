/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetProductHelper;
import pt.rocket.helpers.GetShoppingCartAddItemHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class ProductDetailsFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnDialogListListener {

    private static final String TAG = LogTagHelper.create(ProductDetailsFragment.class);
    private final static int NO_SIMPLE_SELECTED = -1;
    private final static int CURRENT_IMAGE_INDEX = 0;
    private final static String VARIATION_PICKER_ID = "variation_picker";
    private static ProductDetailsFragment productDetailsFragment;

    private Context mContext;
    private DialogFragment mDialogAddedToCart;

    private static CompleteProduct mCompleteProduct;

    private ViewGroup mProductDiscountContainer;

    private Button mAddToCartButton;

    private TextView mProductDiscountPercentage;

    private TextView mProductName;

    private TextView mProductResultPrice;

    private TextView mProductNormalPrice;

    protected ViewGroup mDetailsContainer;

    private ViewGroup mVarianceContainer;

    private static String mCompleteProductUrl;

    private ViewGroup mProductImageShowOffContainer;

    private ImageView mProductImage;

    private ProgressBar mProductImageLoading;

    private View mVariationsContainer;

    private static int mSelectedSimple = NO_SIMPLE_SELECTED;

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
    private static boolean mHideVariationSelection;
    private TextView mVarianceText;
    private ViewGroup mProductBasicInfoContainer;

    private long mBeginRequestMillis;
    private ArrayList<String> mSimpleVariants;
    private TextView mVariantChooseError;
    private HorizontalListView mList;
    private static int mVariationsListPosition = -1;
    private View mVariantPriceContainer;
    private String mNavigationPath;
    private int mNavigationSource;
    private TextView stockInfo;
    private ProductImagesAdapter mAdapter;

    /**
     * Get instance
     * 
     * @return
     */
    public static ProductDetailsFragment getInstance() {
        // Validate instance
        if (productDetailsFragment == null)
            productDetailsFragment = new ProductDetailsFragment();
       
        return productDetailsFragment;
    }
    
    /**
     * Method used to reset some variables to recreate the view
     */
    private static void reset(){
        mCompleteProduct = null;
        mSelectedSimple = NO_SIMPLE_SELECTED;
        mHideVariationSelection = false;
    }


    /**
     * Empty constructor
     */
    public ProductDetailsFragment() {
        super(EnumSet.of(EventType.GET_PRODUCT_EVENT), 
                EnumSet.of(EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT),
                EnumSet.of(MyMenuItem.SHARE), 
                NavigationAction.Products, 
                0, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        Bundle bundle = this.getArguments();
        // Get Extras
        mCompleteProductUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        productDetailsFragment.mNavigationSource = bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);
        productDetailsFragment.mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
        // Reset some variables
        reset();
        // Reutrns intance
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        
        mContext = getActivity().getApplicationContext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.productdetailsnew, viewGroup, false);
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        
        setAppContentLayout();
        
        if(mCompleteProduct == null)
            init();
        else
            displayProduct(mCompleteProduct);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        ((BaseActivity) getActivity()).dismissProgress();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        mCompleteProduct = null;
        mCompleteProductUrl = null;
        mNavigationSource =  -1;
        mNavigationPath = null;
        mSelectedSimple = NO_SIMPLE_SELECTED;
        mVariationsListPosition = -1;
        productDetailsFragment = null; 
        mList = null;
        mAdapter = null;
        System.gc();
    }
    
    /**
     * 
     */
    private void init() {
        Log.d(TAG, "INIT");
        mAdapter = null;
        loadProduct();
    }
    
    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {
        Log.d(TAG, "SET APP CONTENT");

        mDetailsContainer = (ViewGroup) getView().findViewById(R.id.details_container);

        mVariationsContainer = getView().findViewById(R.id.variations_container);
        mList = (HorizontalListView) getView().findViewById(R.id.variations_list);

        mProductImageShowOffContainer = (ViewGroup) getView().findViewById(R.id.product_showoff_container);
        mProductImageShowOffContainer.setOnClickListener(this);
        mProductImage = (ImageView) mProductImageShowOffContainer.findViewById(R.id.product_image);
        mProductImageLoading = (ProgressBar) mProductImageShowOffContainer.findViewById(R.id.progressBar2);

        mProductDiscountContainer = (ViewGroup) mProductImageShowOffContainer.findViewById(R.id.product_discount_container);

        mProductDiscountPercentage = (TextView) mProductDiscountContainer.findViewById(R.id.product_discount_percentage);

        mProductBasicInfoContainer = (ViewGroup) getView().findViewById(R.id.product_basicinfo_container);
        mProductBasicInfoContainer.setOnClickListener(this);

        mProductName = (TextView) getView().findViewById(R.id.product_name);
        mProductResultPrice = (TextView) getView().findViewById(R.id.product_price_result);
        mProductNormalPrice = (TextView) getView().findViewById(R.id.product_price_normal);
        mProductNormalPrice.setPaintFlags(mProductNormalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mProductRatingContainer = (ViewGroup) getView().findViewById(R.id.product_rating_container);
        mProductRatingContainer.setOnClickListener(this);
        mProductRating = (RatingBar) mProductRatingContainer.findViewById(R.id.product_rating);
        mProductRating.setEnabled(false);
        mProductRatingCount = (TextView) mProductRatingContainer.findViewById(R.id.product_rating_count);

        mProductSpecContainer = getView().findViewById(R.id.product_specifications_container);
        mProductSpecContainer.setOnClickListener(this);
        mProductSpecText = (TextView) getView().findViewById(R.id.product_specifications_text);
        mProductSpecSku = (TextView) getView().findViewById(R.id.product_sku_text);

        mVarianceContainer = (ViewGroup) getView().findViewById(R.id.product_variant_container);
        mVarianceText = (TextView) getView().findViewById(R.id.product_variant_text);
        mVarianceButton = (Button) getView().findViewById(R.id.product_variant_button);
        mVarianceButton.setOnClickListener(this);
        
        mVariantPriceContainer = getView().findViewById(R.id.product_variant_price_container);
        mVariantNormPrice = (TextView) getView().findViewById(R.id.product_variant_normprice);
        mVariantSpecPrice = (TextView) getView().findViewById(R.id.product_variant_specprice);
        mVariantChooseError = (TextView) getView().findViewById(R.id.product_variant_choose_error);

        mAddToCartButton = (Button) getView().findViewById(R.id.shop);
        mAddToCartButton.setOnClickListener(this);
        mCallToOrderButton = (Button) getView().findViewById(R.id.call_to_order);
        mCallToOrderButton.setOnClickListener(this);
        
        stockInfo = (TextView) getView().findViewById(R.id.product_instock);
    }

    private void setContentInformation() {
        Log.d(TAG, "SET INFORMATION");

        getActivity().setTitle(mCompleteProduct.getName());
        
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
        Log.d(TAG, "LOAD PRODUCT");

        /**
         * Validate the product URL
         * If null is assumed that the system clean some data
         */
        if(mCompleteProductUrl != null) {
            mBeginRequestMillis = System.currentTimeMillis();
            
            /**
             * TRIGGERS
             * @author sergiopereira
             */
            triggerProduct(mCompleteProductUrl);
            //triggerContentEvent(new GetProductEvent(mCompleteProductUrl));
            
        } else {
            if(getBaseActivity() != null){
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_SHORT).show();
            }
            restartAllFragments();
        }
    }

    private void preselectASimpleItem() {
        Log.d(TAG, "PRESELECT A SIMPLE ITEM: " + mSelectedSimple);
        
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
        if ( null == mCompleteProduct ) {
            return null;
        }
        if (mSelectedSimple >= mCompleteProduct.getSimples().size())
            return null;
        if (mSelectedSimple == NO_SIMPLE_SELECTED)
            return null;

        return mCompleteProduct.getSimples().get(mSelectedSimple);
    }

    private void displayVariations() {
        Log.d(TAG, "DISPLAY VARIANTIONS");

        if (isNotValidVariation(mCompleteProduct.getVariations())) {
            mVariationsContainer.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "AFTER VALIDATION");

        if (mAdapter == null) {
            Log.d(TAG, "ADAPTER IS NULL");
            mAdapter = new ProductImagesAdapter(getActivity(), ProductImagesAdapter.createImageList(mCompleteProduct.getVariations()));
            mList.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "ADAPTER IS NOT NULL");
            //mAdapter.replaceAll(ProductImagesAdapter.createImageList(mCompleteProduct.getVariations()));
            mList.setAdapter(mAdapter);
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
        AQuery aq = new AQuery(mContext);
        aq.id(image).image(imageURL, true, true, 0, 0, new BitmapAjaxCallback() {

                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                        image.setImageBitmap(bm);
                        image.setVisibility(View.VISIBLE);
                        loadingImage.setVisibility(View.GONE);
                    }
                });
        
//        ImageLoader.getInstance().displayImage(imageURL, image,
//                JumiaApplication.COMPONENTS.get(ImageLoaderComponent.class).largeLoaderOptions,
//                new SimpleImageLoadingListener() {
//
//                    /*
//                     * (non-Javadoc)
//                     * 
//                     * @see
//                     * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#
//                     * onLoadingComplete(java.lang.String, android.view.View,
//                     * android.graphics.Bitmap)
//                     */
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        image.setVisibility(View.VISIBLE);
//                        loadingImage.setVisibility(View.GONE);
//                        Log.d(TAG, "loadProductImage: onLoadingComplete");
//                    }
//
//                });
    }

    // XXX
    private void displayPriceInfoOverallOrForSimple() {
        ProductSimple simple = getSelectedSimple();

        if (mSelectedSimple == NO_SIMPLE_SELECTED || simple == null) {
            String unitPrice = mCompleteProduct.getPrice();
            String specialPrice = mCompleteProduct.getSpecialPrice();
            if (specialPrice == null)
                specialPrice = mCompleteProduct.getMaxSpecialPrice();

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

        if(stockInfo == null)
            return;
        
        if (getSelectedSimple() == null) {
            stockInfo.setVisibility(View.GONE);
            return;
        } else
            stockInfo.setVisibility(View.VISIBLE);

        int stockQuantity = 0;
        try {
            stockQuantity = Integer.valueOf(getSelectedSimple().getAttributeByKey(ProductSimple.QUANTITY_TAG));
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
            String translatedDescription = shortDescription.replace("\r", "<br>").replaceAll("<img(.*?)\\>", "");
            // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
            Log.d(TAG, "REMOVE IMG: " + translatedDescription);
            
            Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);                
                MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
                for (MetricAffectingSpan span: spans) {
                    htmlText.removeSpan(span);                
                }
            }
            mProductSpecText.setText(htmlText);
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
        Log.d(TAG, "UPDATE VARIANTS");
        
        mSimpleVariants = createSimpleVariants();

        ProductSimple simple = getSelectedSimple();
        mVariantChooseError.setVisibility(View.GONE);

        if (simple == null) {
            Log.d(TAG, "SIMPLE IS NULL");
            mVariantPriceContainer.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "SIMPLE IS NOT NULL");
            mVariantPriceContainer.setVisibility(View.VISIBLE);
            String normPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);
            if (TextUtils.isEmpty(specPrice)) {
                normPrice = currencyFormatHelper(normPrice);
                mVariantSpecPrice.setVisibility(View.GONE);
                mVariantNormPrice.setText(normPrice);
                mVariantNormPrice.setPaintFlags(mVariantNormPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                mVariantNormPrice.setTextColor(getResources().getColor(R.color.red_basic));
                mVariantNormPrice.setVisibility(View.VISIBLE);
                
            } else {
                normPrice = currencyFormatHelper(normPrice);
                specPrice = currencyFormatHelper(specPrice);
                mVariantSpecPrice.setText(specPrice);
                mVariantSpecPrice.setVisibility(View.VISIBLE);
                mVariantNormPrice.setText(normPrice);
                mVariantNormPrice.setPaintFlags(mVariantNormPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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

        ((BaseActivity) getActivity()).showProgress();
        
        triggerAddItemToCart(item);
//        EventManager.getSingleton().triggerRequestEvent(new AddItemToShoppingCartEvent(item));

        AnalyticsGoogle.get().trackAddToCart(sku, price);
    }

    private void showChooseReminder() {
        // mVarianceText.setTextColor(getResources().getColor(R.color.red_basic));
        mVariantChooseError.setVisibility(View.VISIBLE);
    }
    
    private void displayProduct(CompleteProduct product) {
        Log.d(TAG, "DISPLAY PRODUCT");
        
        mCompleteProduct = product;
        mCompleteProductUrl = product.getUrl();
        ((BaseActivity) getActivity()).setShareIntent(((BaseActivity) getActivity()).createShareIntent());
        
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
                        if (id == R.id.button1 && null != getActivity()) {
                            ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                            if (null != mDialogAddedToCart)
                                mDialogAddedToCart.dismiss();
                            
                        } else if (id == R.id.button2 && null != mDialogAddedToCart) {
                            mDialogAddedToCart.dismiss();
                        }
                    }
                });

        mDialogAddedToCart.show(((BaseActivity) getActivity()).getSupportFragmentManager(), null);
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

        mDialogAddedToCart.show(getActivity().getSupportFragmentManager(), null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (null == mCompleteProduct ) {
            restartAllFragments();
            return;
        }
        
        if (id == R.id.product_rating_container) {
            ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.POPULARITY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            
            
        } else if (id == R.id.product_specifications_container || id == R.id.product_basicinfo_container) {
            
            if ( null != mCompleteProduct ) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
                BaseActivity activity = ((BaseActivity) getActivity());
                if ( null == activity ) {
                    activity = mainActivity;
                }            
                activity.onSwitchFragment(FragmentType.PRODUCT_DESCRIPTION, bundle, FragmentController.ADD_TO_BACK_STACK);
            }
             
        } else if (id == R.id.product_showoff_container) {
            showGallery();
        } else if (id == R.id.product_variant_button) {
            showVariantsDialog();
        } else if (id == R.id.shop) {
            executeAddProductToCart();
        } else if (id == R.id.call_to_order) {
            Toast.makeText(getActivity(), "Hello! I want to order. Can you hear me?", Toast.LENGTH_LONG).show();
        }
    }    

    
    private void showGallery() {
        Log.d(TAG, "SHOW GALLERY");
        // Validate product
        if(mCompleteProduct == null){
            Log.w(TAG, "COMPLETE PRODUCT IS NULL");
            Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_SHORT).show();
        } 
        else{
            int variationsListPosition;
            if (mList != null)
                variationsListPosition = mList.getPosition();
            else
                variationsListPosition = mVariationsListPosition;
            
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
            bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, variationsListPosition);
            bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            bundle.putBoolean(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, true);
            ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_GALLERY, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    private void showVariantsDialog() {
        String title = getString(R.string.product_variance_choose);
//        DialogList dialog = new DialogList(getActivity(), VARIATION_PICKER_ID, title, mSimpleVariants, mSelectedSimple);
//        dialog.show();
//        
        DialogListFragment dialog = DialogListFragment.newInstance(getActivity(), (OnDialogListListener) this, VARIATION_PICKER_ID, title, mSimpleVariants, mSelectedSimple);
        dialog.show(getActivity().getSupportFragmentManager(), null);
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
    
    
    public static void updateVariantionListPosition(String productUrl, int position){
        Log.d(TAG, "UPDATE VARIATION POSITION: " + position);
        
        if (productUrl == null)
            return;
        
        if (mCompleteProduct == null || !mCompleteProduct.getUrl().equals(productUrl)) {
            mSelectedSimple = NO_SIMPLE_SELECTED;
            mCompleteProductUrl = productUrl;
            mVariationsListPosition = position;
            
            // Force reload on resume fragment
            mCompleteProduct = null;
        }
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        // Validate if fragment is on the screen
        if (!isVisible()) {
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        
        Log.d(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            mAddToCartButton.setEnabled(true);
            executeAddToShoppingCartCompleted();
            break;
        case GET_PRODUCT_EVENT:
            AnalyticsGoogle.get().trackLoadTiming(R.string.gproductdetail, mBeginRequestMillis);
            displayProduct((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
            break;
        }
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        
        if(!isVisible()){
            return true;
        }
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        mBeginRequestMillis = System.currentTimeMillis();
        // Validate dialog
        if(dialog != null && dialog.isVisible()){
            return true;
        }
        
        // Get activity
        BaseActivity act = (BaseActivity) getActivity(); 
        
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            if ( null != act ) {
                act.dismissProgress();
            }
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
                        return false;
                    }

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
                    dialog.show(act.getSupportFragmentManager(), null);
                    return true;
                }
            }
            if (!errorCode.isNetworkError()) {
                addToShoppingCartFailed();
                return true;
            }
        case GET_PRODUCT_EVENT:
            if (!errorCode.isNetworkError()) {
                try {

                    //Hide content
                    hideContentOnError();
                    // Show dialog
                    dialog = DialogGenericFragment.newInstance(true, true, false,
                            getString(R.string.product_details_title),
                            getString(R.string.product_could_not_retrieved),
                            getString(R.string.ok_label), "", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dialog.dismiss();
                                        finishFragment();
                                    }
                                }
                            });
                    dialog.show(act.getSupportFragmentManager(), null);
                    dialog.setCancelable(false);
                    
                } catch (Exception e) {
                    Log.d(TAG, "onErrorEvent: ERROR going to previous activity");
                    e.printStackTrace();
                }
                return true;
            }
        }
        return true;
    }
    
    /**
     * Finish the current fragment
     */
    private void finishFragment(){
        BaseActivity act = (BaseActivity) getActivity();
        if(act != null){
            act.onBackPressed();
        }
    }
    
    /**
     * Hide main content
     */
    private void hideContentOnError(){
        // Hide content
        View view = getView();
        if(view != null)
            view.findViewById(R.id.details_container).setVisibility(View.GONE);
    }
    
    
    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerProduct(String mCompleteProductUrl){
        Bundle bundle = new Bundle();
        bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
        triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
    }
    
    private void triggerAddItemToCart(ShoppingCartItem item){
//        ShoppingCartItem item = event.value;

        ContentValues values = new ContentValues();

        // add the simple data to the registry
        if (item.getSimpleData() != null) {
            JumiaApplication.INSTANCE.getItemSimpleDataRegistry().put(item.getConfigSKU(), item.getSimpleData());
        }

        values.put("p", item.getConfigSKU());
        values.put("sku", item.getConfigSimpleSKU());
        values.put("quantity", "" + item.getQuantity());
        Bundle bundle = new Bundle();
        bundle.putString(GetShoppingCartAddItemHelper.ADD_ITEM, mCompleteProductUrl);
        triggerContentEvent(new GetShoppingCartAddItemHelper(), bundle, mCallBack);
    }
    
    /**
     * CALLBACK
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            // TODO
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

}
