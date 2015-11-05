package com.mobile.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;

import com.mobile.app.JumiaApplication;
import com.mobile.components.ExpandableGridViewComponent;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.helpers.products.GetProductBundleHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.helpers.wishlist.AddToWishListHelper;
import com.mobile.helpers.wishlist.RemoveFromWishListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.database.BrandsTableHelper;
import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.ImageUrls;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment.OnDialogListListener;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.imageloader.RocketImageLoader.ImageHolder;
import com.mobile.utils.imageloader.RocketImageLoader.RocketImageLoaderLoadImagesListener;
import com.mobile.utils.pdv.RelatedProductsAdapter;
import com.mobile.utils.ui.ProductUtils;
import com.mobile.utils.ui.ToastManager;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * This class displays the product detail screen.
 *
 * @author Michael Kroez
 * @modified spereira
 */
public class ProductDetailsFragment extends BaseFragment implements IResponseCallback, RocketImageLoaderLoadImagesListener, AdapterView.OnItemClickListener, OnDialogListListener {

    private final static String TAG = ProductDetailsFragment.class.getSimpleName();

    public static int sSharedSelectedPosition = IntConstants.DEFAULT_POSITION;

    private ProductComplete mProduct;

    private String mCompleteProductSku;

    private RatingBar mProductRating;

    private TextView mProductRatingCount;

    private ImageView mWishListButton;

    private long mBeginRequestMillis;

    private String mNavPath;

    private String mNavSource;

    private TextView mSpecialPriceText;

    private TextView mPriceText;

    private TextView mDiscountPercentageText;

    private TextView mSaveForLater;

    private TextView mBuyButton;

    private boolean isRelatedItem = false;

    private static String categoryTree = "";

    private ViewGroup mTableBundles;

    private ViewGroup mSellerContainer;

    private ViewGroup mDescriptionView;

    private ViewGroup mSpecificationsView;

    private ViewGroup mOtherVariationsLayout;

    private ViewGroup mSizeLayout;

    private ViewGroup mRelatedProductsView;

    private ViewGroup mComboProductsLayout;

    private RatingBar mProductFashionRating;

    private ViewGroup mTitleContainer;

    private ViewGroup mTitleFashionContainer;

    private View mGlobalButton;

    private View mOffersContainer;

    boolean isFromBuyButton;

    /**
     * Empty constructor
     */
    public ProductDetailsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.pdv_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /**
     * Get a new instance.
     */
    public static ProductDetailsFragment getInstance(Bundle bundle) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        // Reset the share
        sSharedSelectedPosition = IntConstants.DEFAULT_POSITION;
        fragment.setArguments(bundle);
        return fragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.d(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (arguments != null) {
            // Get sku
            mCompleteProductSku = arguments.getString(ConstantsIntentExtra.PRODUCT_SKU);
            // Categories
            categoryTree = arguments.containsKey(ConstantsIntentExtra.CATEGORY_TREE_NAME) ? arguments.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME) + ",PDV" : "";

            restoreParams(arguments);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.product_detail_view.View,
     * android.product_detail_os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Print.d(TAG, "ON VIEW CREATED");
        super.onViewCreated(view, savedInstanceState);
        // Title
        mTitleContainer = (ViewGroup) view.findViewById(R.id.pdv_title_container);
        mTitleFashionContainer = (ViewGroup) view.findViewById(R.id.pdv_title_fashion_container);
        // Slide show
        // created a new ProductImageGalleryFragment
        // Wish list
        mWishListButton = (ImageView) view.findViewById(R.id.pdv_button_wish_list);
        mWishListButton.setOnClickListener(this);
        // Prices
        mSpecialPriceText = (TextView) view.findViewById(R.id.pdv_text_special_price);
        mPriceText = (TextView) view.findViewById(R.id.pdv_text_price);
        mDiscountPercentageText = (TextView) view.findViewById(R.id.pdv_text_discount);
        // Rating
        view.findViewById(R.id.pdv_rating_container).setOnClickListener(this);
        mProductRating = (RatingBar) view.findViewById(R.id.pdv_rating_bar);
        mProductFashionRating = (RatingBar) view.findViewById(R.id.pdv_rating_bar_fashion);
        mProductRatingCount = (TextView) view.findViewById(R.id.pdv_rating_bar_count);
        // Specifications
        mSpecificationsView = (ViewGroup) view.findViewById(R.id.pdv_specs_container);
        // Variations
        mOtherVariationsLayout = (ViewGroup) view.findViewById(R.id.pdv_variations_container);
        mSizeLayout = (ViewGroup) view.findViewById(R.id.pdv_simples_container);
        // Seller
        mSellerContainer = (ViewGroup) view.findViewById(R.id.pdv_seller_container);
        mGlobalButton = view.findViewById(R.id.pdv_button_global_seller);
        // Product Description
        mDescriptionView = (ViewGroup) view.findViewById(R.id.pdv_desc_container);
        // Product Combos
        mTableBundles = (ViewGroup) view.findViewById(R.id.pdv_bundles_container);
        mComboProductsLayout = (ViewGroup) view.findViewById(R.id.pdv_combos_container);
        mComboProductsLayout.setVisibility(View.GONE);
        // Related Products
        mRelatedProductsView = (ViewGroup) view.findViewById(R.id.pdv_related_container);
        // Offers
        mOffersContainer = view.findViewById(R.id.pdv_other_sellers_button);
        // Bottom Buy Bar
        view.findViewById(R.id.pdv_button_share).setOnClickListener(this);
        view.findViewById(R.id.pdv_button_call).setOnClickListener(this);
        mBuyButton = (TextView) view.findViewById(R.id.pdv_button_buy);
        mBuyButton.setOnClickListener(this);
        // Save for later
        mSaveForLater = (TextView) view.findViewById(R.id.pdv_button_add_to_save);
        mSaveForLater.setOnClickListener(this);
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
        // Validate current data product
        onValidateDataState();
        // Tracking
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
        outState.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProductSku);
        outState.putParcelable(ProductComplete.class.getSimpleName(), mProduct);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
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
        getBaseActivity().mConfirmationCartMessageView.hideMessage();
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
     * Method used to validate the current data state.<br>
     * Case has product object<br>
     * Case has product sku to get object<br>
     * Case product not retrieved<br>
     */
    private void onValidateDataState() {
        Print.d(TAG, "INIT");
        // Get arguments
        Bundle bundle = getArguments();
        // Validate deep link arguments
        if (hasArgumentsFromDeepLink(bundle)) {
            return;
        }
        // Validate current product
        if (mProduct != null) {
            displayProduct(mProduct);
        }
        // Case get product
        else if (TextUtils.isNotEmpty(mCompleteProductSku)) {
            triggerLoadProduct(mCompleteProductSku);
        }
        // Case error
        else {
            ToastManager.show(getBaseActivity(), ToastManager.ERROR_PRODUCT_NOT_RETRIEVED);
            getBaseActivity().onBackPressed();
        }
    }

    private void restoreParams(Bundle bundle) {
        // Get source and path
        mNavSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcatalog));
        mNavPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
        // Determine if related items should be shown
        isRelatedItem = bundle.getBoolean(ConstantsIntentExtra.IS_RELATED_ITEM);
        mProduct = bundle.getParcelable(ProductComplete.class.getSimpleName());
    }

    /**
     * Validate and loads the received arguments comes from deep link process.
     */
    private boolean hasArgumentsFromDeepLink(Bundle bundle) {
        // Get the sku
        String sku = bundle.getString(GetProductHelper.SKU_TAG);
        // Get the simple size
        String mDeepLinkSimpleSize = bundle.getString(DeepLinkManager.PDV_SIZE_TAG);
        // Validate
        if (sku != null) {
            Print.i(TAG, "DEEP LINK GET PDV: " + sku + " " + mDeepLinkSimpleSize);
            mNavSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix));
            mNavPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
            triggerLoadProduct(sku);
            return true;
        }
        return false;
    }

    /*
     * ######## LAYOUT ########
     */

    /**
     * Fills and displays a compleet product info
     *
     * @param product - Complete product
     */
    private void displayProduct(ProductComplete product) {
        Print.d(TAG, "ON SHOW PRODUCT");
        // Save complete product
        mProduct = product;
        mCompleteProductSku = product.getSku();
        // Set layout
        setTitle();
        setWishListButton();
        setSlideShow();
        setProductPriceInfo();
        setRatingInfo();
        setSpecifications();
        setProductSize();
        setProductVariations();
        setSellerInfo();
        setOffers();
        setDescription();
        setCombos();
        setRelatedItems();
        // Show container
        showFragmentContentContainer();
        // Tracking
        TrackerDelegator.trackProduct(mProduct, mNavSource, mNavPath, isRelatedItem);
    }

    private void setOffers() {
        //show button offers with separator if has offers
        View separator = mOffersContainer.findViewById(R.id.separator);
        if (mProduct.hasOffers()) {
            TextView txOffers = (TextView) mOffersContainer.findViewById(R.id.pdv_sublist_button);
            txOffers.setText(String.format(getString(R.string.other_sellers_starting), CurrencyFormatter.formatCurrency(mProduct.getMinPriceOffer())));
            mOffersContainer.setOnClickListener(this);
            separator.setVisibility(View.VISIBLE);
        } else {
            mOffersContainer.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        }
    }

    /**
     * Set product price info
     */
    private void setProductPriceInfo() {
        Print.d(TAG, "SHOW PRICE INFO: " + mProduct.getPrice() + " " + mProduct.getSpecialPrice());
        ProductUtils.setPriceRules(mProduct, mPriceText, mSpecialPriceText);
        ProductUtils.setDiscountRules(mProduct, mDiscountPercentageText);
        if (mProduct.hasDiscount()) {
            if (!mProduct.isFashion()) {
                mDiscountPercentageText.setEnabled(true);
            } else {
                mDiscountPercentageText.setEnabled(false);
                mDiscountPercentageText.setTextColor(getResources().getColor(R.color.black_800));
            }
        }
    }

    /**
     * Set rating stars and info
     */
    private void setRatingInfo() {
        Integer ratingCount = mProduct.getTotalRatings();
        Integer reviewsCount = mProduct.getTotalReviews();

        if (ratingCount == 0 && reviewsCount == 0) {
            mProductRatingCount.setText(getResources().getString(R.string.be_first_rate));    //be the first to rate if hasn't
        } else {
            //changeFashion: rating style is changed if vertical is fashion
            if (mProduct.isFashion()) {
                mProductFashionRating.setRating((float) mProduct.getAvgRating());
                mProductRating.setVisibility(View.GONE);
                mProductFashionRating.setVisibility(View.VISIBLE);
            } else {
                mProductRating.setRating((float) mProduct.getAvgRating());
                mProductRating.setVisibility(View.VISIBLE);
            }
            String rating = getResources().getQuantityString(R.plurals.numberOfRatings, ratingCount, ratingCount);
            mProductRatingCount.setText(rating);
        }

   //     mProductRating.setVisibility(View.VISIBLE);
    }

    /**
     * Show the seller info
     */
    public void setSellerInfo() {
        // Validate seller
        if (mProduct.hasSeller()) {
            // Set seller view
            mSellerContainer.setVisibility(View.VISIBLE);
            // Name
            TextView sellerName = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_name);
            sellerName.setText(mProduct.getSeller().getName());
            sellerName.setOnClickListener(this);
            // Case global seller
            if(mProduct.getSeller().isGlobal()) {
                // Set global button
                mGlobalButton.setVisibility(View.VISIBLE);
                mGlobalButton.setOnClickListener(this);
                // Delivery Info
                mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_container).setVisibility(View.VISIBLE);
                ((TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_title)).setText(mProduct.getSeller().getDeliveryTime());
                if(TextUtils.isNotEmpty(mProduct.getSeller().getDeliveryCMSInfo())) {
                    TextView info = ((TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_text_black));
                    info.setText(mProduct.getSeller().getDeliveryCMSInfo());
                    info.setVisibility(View.VISIBLE);
                }
                if(TextUtils.isNotEmpty(mProduct.getSeller().getDeliveryShippingInfo())) {
                    TextView info2 = ((TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_text_orange));
                    info2.setText(mProduct.getSeller().getDeliveryShippingInfo());
                    info2.setVisibility(View.VISIBLE);
                }
                TextView link = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_link);
                link.setText(mProduct.getSeller().getDeliveryMoreDetailsText());
                link.setPaintFlags(link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                link.setOnClickListener(this);
            }
            // Case normal
            else if(TextUtils.isNotEmpty(mProduct.getSeller().getDeliveryTime())){
                // Delivery Info
                TextView textView = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_delivery_info);
                textView.setText(mProduct.getSeller().getDeliveryTime());
                textView.setVisibility(View.VISIBLE);
            }
            // Seller warranty
            if (TextUtils.isNotEmpty(mProduct.getSeller().getWarranty())) {
                TextView textView = ((TextView) mSellerContainer.findViewById(R.id.pdv_seller_warranty));
                String warranty = String.format(getResources().getString(R.string.warranty), mProduct.getSeller().getWarranty());
                textView.setText(warranty);
                textView.setVisibility(View.VISIBLE);
            }
        }
        // Hide seller
        else {
            mSellerContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Change and put the title in the correct position within the layout if it's fashion or not
     */
    private void setTitle() {
        if (mProduct.isFashion()) {
            mTitleContainer.setVisibility(View.GONE);
            ((TextView) mTitleFashionContainer.findViewById(R.id.pdv_product_title)).setText(mProduct.getBrand());
            ((TextView) mTitleFashionContainer.findViewById(R.id.pdv_product_subtitle)).setText(mProduct.getName());
        } else {
            mTitleFashionContainer.setVisibility(View.GONE);
            ((TextView) mTitleContainer.findViewById(R.id.pdv_product_title)).setText(mProduct.getBrand());
            ((TextView) mTitleContainer.findViewById(R.id.pdv_product_subtitle)).setText(mProduct.getName());
        }
        // Set action title
        getBaseActivity().setActionBarTitle(mProduct.getBrand());
    }


    /**
     * Get and fills the product description section if has description
     */
    private void setDescription() {
        // Validate description
        String description = mProduct.getDescription();
        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setVisibility(View.GONE);
            return;
        }
        // Title
        ((TextView) mDescriptionView.findViewById(R.id.pdv_specs_title)).setText(getString(R.string.description));
        // Multi line
        ((TextView) mDescriptionView.findViewById(R.id.pdv_specs_multi_line)).setText(description);
        // Button
        TextView button = (TextView) mDescriptionView.findViewById(R.id.pdv_specs_button);
        button.setText(getString(R.string.read_more));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowDescription(R.string.description);
            }
        });
    }

    /**
     * function that sets the state of the wishlist button
     */
    private void setWishListButton() {
        mWishListButton.setSelected(mProduct.isWishList());
        // validate if its saved to know which string to show in case of OOS
        setOutOfStockButton();
    }


    /**
     * Load specifications button if has specifications
     */
    private void setSpecifications() {
        String features = mProduct.getShortDescription();
        if (mProduct.isFashion() || TextUtils.isEmpty(features)) {
            mSpecificationsView.setVisibility(View.GONE);
            return;
        }
        // Title
        ((TextView) mSpecificationsView.findViewById(R.id.pdv_specs_title)).setText(getString(R.string.specifications));
        // Multi line
        ((TextView) mSpecificationsView.findViewById(R.id.pdv_specs_multi_line)).setText(features);
        // Button
        TextView button = (TextView) mSpecificationsView.findViewById(R.id.pdv_specs_button);
        button.setText(getString(R.string.more_specifications));
        // TODO: Move to onClick
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowDescription(R.string.product_specifications);
            }
        });
    }

    /**
     * Set combos: if has bundle get bundle list
     */
    private void setCombos() {
        if (mProduct.hasBundle()) {
            if (mProduct.getProductBundle() == null || CollectionUtils.isEmpty(mProduct.getProductBundle().getProducts())) {
                triggerGetProductBundle(mProduct.getSku());
            } else {
                mComboProductsLayout.setVisibility(View.VISIBLE);
                buildComboSection(mProduct.getProductBundle());
            }
        } else {
            mComboProductsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Set the variation container
     */
    private void setProductVariations() {
        Print.i(TAG, "ON DISPLAY VARIATIONS");
        if (mProduct.hasVariations()) {
            // Title
            String title = getResources().getString(mProduct.isFashion() ? R.string.see_other_colors : R.string.see_other_variations);
            ((TextView) mOtherVariationsLayout.findViewById(R.id.tx_single_line_text)).setText(title);
            // Listener
            mOtherVariationsLayout.setOnClickListener(this);
        } else {
            mOtherVariationsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Load size component if has sizes
     */
    private void setProductSize() {
        Print.i(TAG, "ON DISPLAY SIZE");
        // Validate simple variations
        if (mProduct.hasMultiSimpleVariations()) {
            // All Simple variations
            String textVariations = mProduct.getVariationsAvailable();
            // Get selected variation
            if(mProduct.hasSelectedSimpleVariation() && mProduct.getSelectedSimple() != null) {
                textVariations = mProduct.getSelectedSimple().getVariationValue();
            }
            // Simple variation name and value
            String text = mProduct.getVariationName() + ": " + textVariations;
            // Set text
            ((TextView) mSizeLayout.findViewById(R.id.tx_single_line_text)).setText(text);
            // Set listener
            mSizeLayout.setOnClickListener(this);
        } else {
            mSizeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Load gallery and small images
     */
    private void setSlideShow() {
        Print.i(TAG, "ON DISPLAY SLIDE SHOW");
        // Validate the ProductImageGalleryFragment
        ProductImageGalleryFragment fragment = (ProductImageGalleryFragment) getChildFragmentManager().findFragmentByTag(ProductImageGalleryFragment.TAG);
        // CASE CREATE
        if (fragment == null) {
            Print.i(TAG, "ON DISPLAY SLIDE SHOW: NEW");

            ArrayList<ImageUrls> images;
            if(ShopSelector.isRtl()){
                images = new ArrayList<>(mProduct.getImageList());
                Collections.reverse(images);
            } else {
                images = mProduct.getImageList();
            }
            // Create bundle with images
            Bundle args = new Bundle();
            args.putParcelableArrayList(ConstantsIntentExtra.IMAGE_LIST, images);
            args.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            args.putBoolean(ConstantsIntentExtra.INFINITE_SLIDE_SHOW, false);
            args.putBoolean(ConstantsIntentExtra.OUT_OF_STOCK, verifyOutOfStock());
            // Create fragment
            fragment = ProductImageGalleryFragment.getInstanceAsNested(args);
            FragmentController.addChildFragment(this, R.id.pdv_slide_show_container, fragment, ProductImageGalleryFragment.TAG);
        }
        // CASE UPDATE
        else {
            Print.i(TAG, "ON DISPLAY SLIDE SHOW: UPDATE");
            fragment.notifyFragment(null);
        }
    }

    /**
     * Method used to create the view
     */
    private void setRelatedItems() {
        if (CollectionUtils.isNotEmpty(mProduct.getRelatedProducts())) {
            ExpandableGridViewComponent relatedGridView = (ExpandableGridViewComponent) mRelatedProductsView.findViewById(R.id.pdv_related_grid_view);
            relatedGridView.setExpanded(true);
            relatedGridView.setAdapter(new RelatedProductsAdapter(getBaseActivity(), R.layout.pdv_fragment_related_item, mProduct.getRelatedProducts()));
            relatedGridView.setOnItemClickListener(this);
        } else {
            mRelatedProductsView.setVisibility(View.GONE);
        }
    }

    /**
     * Method used to update the wish list value.
     */
    private void updateWishListValue() {
        try {
            boolean value = mProduct.isWishList();
            mWishListButton.setSelected(value);
            getBaseActivity().warningFactory.showWarning(value ? WarningFactory.ADDED_TO_SAVED : WarningFactory.REMOVE_FROM_SAVED);
            setOutOfStockButton();
        } catch (NullPointerException e) {
            Log.i(TAG, "NPE ON UPDATE WISH LIST VALUE");
        }
    }


    /*
     * ####### LISTENERS #######
     */

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
        if (id == R.id.pdv_rating_container) onClickShowDescription(R.string.rat_rev);
        // Case variation button
        else if (id == R.id.pdv_variations_container) onClickVariationButton();
        // Case favourite
        else if (id == R.id.pdv_button_wish_list) onClickWishListButton(view);
        // Case size guide
        else if (id == R.id.dialog_list_size_guide_button) onClickSizeGuide(view);
        // Case simples
        else if (id == R.id.pdv_simples_container) onClickSimpleSizesButton();
        // Case share
        else if (id == R.id.pdv_button_share) onClickShare(mProduct);
        // Case call to order
        else if (id == R.id.pdv_button_call) onClickCallToOrder();
        // Case buy button
        else if (id == R.id.pdv_button_buy) onClickBuyProduct();
        // Case saved for later
        else if (id == R.id.pdv_button_add_to_save) onClickSaveForLateButton(view);
        // Case combos section
        else if (id == R.id.pdv_combos_container) onClickCombosProduct();
        // Case other offers
        else if (id == R.id.pdv_other_sellers_button) onClickOtherOffersProduct();
        // Case global seller button
        else if (id == R.id.pdv_button_global_seller) onClickGlobalSellerButton();
        // Case global delivery button
        else if (id == R.id.pdv_seller_overseas_delivery_link) onClickGlobalDeliveryLinkButton();
        // Case seller container
        else if(id == R.id.pdv_seller_name) goToSellerCatalog();

    }

    private void goToSellerCatalog() {
        Log.i(TAG, "ON CLICK SELLER NAME");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, mProduct.getSeller().getUrl());
        getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on global policy
     */
    private void onClickGlobalDeliveryLinkButton() {
        Log.i(TAG, "ON CLICK GLOBAL SELLER");
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.JSON_KEY_TAG, GetStaticPageHelper.INTERNATIONAL_PRODUCT_POLICY_PAGE);
        bundle.putString(RestConstants.JSON_TITLE_TAG, getString(R.string.policy));
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on global button
     */
    @SuppressWarnings("ConstantConditions")
    private void onClickGlobalSellerButton() {
        Log.i(TAG, "ON CLICK GLOBAL SELLER");
        try {
            ScrollView scrollView = (ScrollView) getView().findViewById(R.id.product_detail_scrollview);
            scrollView.smoothScrollTo(0, mSellerContainer.getTop());
        } catch (NullPointerException e) {
            Log.i(TAG, "WARNING: NPE ON TRY SCROLL TO SELLER VIEW");
         // ...
        }
    }


    /**
     * checks/ uncheck a bundle item from combo and updates the combo's total price
     */
    private void onClickComboItem(View bundleItemView,TextView txTotalPrice, BundleList bundleList, int position) {
        // Update combo price
        bundleList.updateTotalPriceWhenChecking(position);
        //get the bundle to update checkbox state
        ProductBundle productBundle = bundleList.getProducts().get(position);
        // Update check
        ((CheckBox) bundleItemView.findViewById(R.id.item_check)).setChecked(productBundle.isChecked());
        //get updated total price
        txTotalPrice.setText(CurrencyFormatter.formatCurrency(bundleList.getPrice()));
        //update bundleListObject in productComplete
        mProduct.setProductBundle(bundleList);
    }


    /**
     * Process the click on rating
     */
    private void onClickCombosProduct() {
        Log.i(TAG, "ON CLICK COMBOS SECTION");
        Bundle bundle = new Bundle();
        bundle.putParcelable(RestConstants.JSON_BUNDLE_PRODUCTS, mProduct.getProductBundle());
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mProduct.getSku());
        getBaseActivity().onSwitchFragment(FragmentType.COMBO_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

//    /**
//     * Process the click on rating
//     */
//    private void onClickRating() {
//        Log.i(TAG, "ON CLICK RATING");
//        JumiaApplication.cleanRatingReviewValues();
//        JumiaApplication.INSTANCE.setFormReviewValues(null);
//        Bundle bundle = new Bundle();
//        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mProduct.getSku());
//        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mProduct);
//        bundle.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, true);
//        getBaseActivity().onSwitchFragment(FragmentType.POPULARITY, bundle, FragmentController.ADD_TO_BACK_STACK);
//    }

    /**
     * Show the product description
     */
    private void onClickShowDescription(int position) {
        Log.i(TAG, "ON CLICK TO SHOW DESCRIPTION");
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mProduct);
        bundle.putInt(ConstantsIntentExtra.PRODUCT_INFO_POS, position);
        bundle.putString(ConstantsIntentExtra.FLAG_1, mProduct.getBrand());
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_INFO, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on variations
     */
    private void onClickVariationButton() {
        Log.i(TAG, "ON CLICK TO SHOW OTHER VARIATIONS");
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mProduct);
        getBaseActivity().onSwitchFragment(FragmentType.VARIATIONS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on other offers button if has offers
     */
    private void onClickOtherOffersProduct() {
        Log.i(TAG, "ON CLICK OTHER OFFERS");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mProduct.getSku());
        bundle.putString(ConstantsIntentExtra.PRODUCT_NAME, mProduct.getName());
        bundle.putString(ConstantsIntentExtra.PRODUCT_BRAND, mProduct.getBrand());
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_OFFERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on buy
     */
    private void onClickBuyProduct() {
        Log.i(TAG, "ON CLICK BUY BUTTON");
        // Validate has simple variation selected
        ProductSimple simple = mProduct.getSelectedSimple();
        // Case add item to cart
        if (simple != null) {
            triggerAddItemToCart(mProduct.getSku(), simple.getSku());
            // Tracking
            TrackerDelegator.trackProductAddedToCart(mProduct, simple.getSku(), mGroupType);
        }
        // Case select a simple variation
        else if (mProduct.hasMultiSimpleVariations()) {
            isFromBuyButton = true;
            onClickSimpleSizesButton();
        }
        // Case error unexpected
        else {
            showUnexpectedErrorWarning();
        }
    }

    /**
     * Process the click on call to buy
     */
    private void onClickCallToOrder() {
        Log.i(TAG, "ON CLICK TO CALL");
        // Tracking
        TrackerDelegator.trackCall(getBaseActivity());
        // Get phone number
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, "");
        // Make a call
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone2Call));
        if (intent.resolveActivity(getBaseActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Process the click on wish list button
     */
    private void onClickWishListButton(View view) {
        // Validate customer is logged in
        if (JumiaApplication.isCustomerLoggedIn()) {
            try {
                // Get item
                if (view.isSelected()) {
                    triggerRemoveFromWishList(mProduct.getSku());
                    TrackerDelegator.trackRemoveFromFavorites(mProduct);
                } else {
                    triggerAddToWishList(mProduct.getSku());
                    TrackerDelegator.trackAddToFavorites(mProduct);
                }
            } catch (NullPointerException e) {
                Log.w(TAG, "NPE ON ADD ITEM TO WISH LIST", e);
            }
        } else {
            // Goto login
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on saved for later button
     */
    private void onClickSaveForLateButton(View view) {
        // Validate customer is logged in
        if (JumiaApplication.isCustomerLoggedIn()) {
            try {
                // if view is selected it means that the product is currently on the saved list and user want to remove it
                if (view.isSelected()) {
                    triggerRemoveFromWishList(mProduct.getSku());
                    TrackerDelegator.trackRemoveFromFavorites(mProduct);
                } else {
                    triggerAddToWishList(mProduct.getSku());
                    TrackerDelegator.trackAddToFavorites(mProduct);
                }
            } catch (NullPointerException e) {
                Log.w(TAG, "NPE ON ADD ITEM TO SAVED", e);
            }
        } else {
            // Goto login
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on share.
     */
    private void onClickShare(ProductComplete completeProduct) {
        Log.i(TAG, "ON CLICK TO SHARE ITEM");
        try {
            String extraSubject = getString(R.string.share_subject, getString(R.string.app_name_placeholder));
            String extraMsg = getString(R.string.share_checkout_this_product) + "\n" + mProduct.getShareUrl();
            Intent shareIntent = getBaseActivity().createShareIntent(extraSubject, extraMsg);
            shareIntent.putExtra(RestConstants.SKU, mProduct.getSku());
            startActivity(shareIntent);
            // Track share
            TrackerDelegator.trackItemShared(shareIntent, completeProduct.getCategories());
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SHARE");
        }
    }

    /**
     * Process click on size guide.
     */
    private void onClickSizeGuide(View view) {
        Log.i(TAG, "ON CLICK TO SHOW SIZE GUIDE");
        try {
            // Get size guide URL
            String url = (String) view.getTag();
            // Validate url
            if (!TextUtils.isEmpty(url)) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, url);
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_SIZE_GUIDE, bundle, FragmentController.ADD_TO_BACK_STACK);
            }
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
        }
    }

    /**
     * Process the click from related item
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String sku = (String) view.getTag(R.id.target_sku);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, sku);
        bundle.putBoolean(ConstantsIntentExtra.IS_RELATED_ITEM, true);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }


    /**
     * Process the click to show simples
     */
    private void onClickSimpleSizesButton() {
        Print.i(TAG, "ON CLICK TO SHOW SIMPLE VARIATIONS");
        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    getString(R.string.product_variance_choose),
                    mProduct,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
        }
    }

    /**
     * Process the selected item from simple variations dialog
     */
    @Override
    public void onDialogListItemSelect(int position) {
        try {
            ProductSimple simple = mProduct.getSelectedSimple();
            if (simple != null) {
                // Set info
                String text = mProduct.getVariationName() + ": " + simple.getVariationValue();
                ((TextView) mSizeLayout.findViewById(R.id.tx_single_line_text)).setText(text);
                // Case from buy button
                if(isFromBuyButton) {
                    onClickBuyProduct();
                }
                ProductUtils.setPriceRules(mProduct, mPriceText, mSpecialPriceText);
            }
        } catch (NullPointerException e) {
            // ...
        }
    }

    /**
     * Process the click on size guide from simple variations dialog
     */
    @Override
    public void onDialogListClickView(View view) {
        onClick(view);
    }

    @Override
    public void onDialogListDismiss() {
        isFromBuyButton = false;
    }

    /**
     * Process the click on retry from root layout
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Log.i(TAG, "ON CLICK RETRY BUTTON");
        onResume();
    }

    /*
     * ############## TRIGGERS ##############
     */

    private void triggerLoadProduct(String sku) {
        mBeginRequestMillis = System.currentTimeMillis();
        triggerContentEvent(new GetProductHelper(), GetProductHelper.createBundle(sku), this);
    }

    private void triggerAddItemToCart(String sku, String simpleSKU) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku, simpleSKU), this);
    }

    private void triggerAddToWishList(String sku) {
        triggerContentEventProgress(new AddToWishListHelper(), AddToWishListHelper.createBundle(sku), this);
    }

    private void triggerRemoveFromWishList(String sku) {
        triggerContentEventProgress(new RemoveFromWishListHelper(), RemoveFromWishListHelper.createBundle(sku), this);
    }


//    private void triggerGetProductOffers(String sku) {
//        triggerContentEvent(new GetProductOffersHelper(), GetProductOffersHelper.createBundle(sku), this);
//    }

    /*
     * ############## RESPONSE ##############
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();
        // Validate event
        super.handleSuccessEvent(baseResponse);
        // Validate event type
        switch (eventType) {
            case REMOVE_PRODUCT_FROM_WISH_LIST:
            case ADD_PRODUCT_TO_WISH_LIST:
                // Force wish list reload for next time
                WishListFragment.sForceReloadWishListFromNetwork = true;
                // Update value
                updateWishListValue();
                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                //if has cart popup, show configurable confirmation message with cart total price
                if(CountryPersistentConfigs.hasCartPopup(getBaseActivity().getApplicationContext())){
                    PurchaseEntity purchaseEntity = ((ShoppingCartAddItemHelper.AddItemStruct) baseResponse.getMetadata().getData()).getPurchaseEntity();
                    getBaseActivity().mConfirmationCartMessageView.showMessage(purchaseEntity.getTotal());
                }
                else{
                    //show regular message add item to cart
                    showInfoAddToShoppingCartCompleted();
                }
                break;
            case GET_PRODUCT_DETAIL:
                ProductComplete product = (ProductComplete) baseResponse.getMetadata().getData();
                // Validate product
                if (product == null || product.getName() == null) {
                    ToastManager.show(getBaseActivity(), ToastManager.ERROR_PRODUCT_NOT_RETRIEVED);
                    getBaseActivity().onBackPressed();
                    return;
                }
                // Save product
                mProduct = product;

                sSharedSelectedPosition = !ShopSelector.isRtl() ? IntConstants.DEFAULT_POSITION : mProduct.getImageList().size()-1;

                // Show product or update partial
                displayProduct(mProduct);
                // Tracking
                Bundle params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gproductdetail);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                TrackerDelegator.trackLoadTiming(params);
                // Tracking
                params = new Bundle();
                params.putParcelable(AdjustTracker.PRODUCT, mProduct);
                params.putString(AdjustTracker.TREE, categoryTree);
                TrackerDelegator.trackPage(TrackingPage.PRODUCT_DETAIL_LOADED, getLoadTime(), false);
                TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_DETAIL_LOADED, params);
                // Database
                LastViewedTableHelper.insertLastViewedProduct(product);
                BrandsTableHelper.updateBrandCounter(product.getBrand());
                break;
            case GET_PRODUCT_BUNDLE:
                BundleList bundleList = (BundleList) baseResponse.getMetadata().getData();
                //keep the bundle
                mProduct.setProductBundle(bundleList);
                // build combo section from here
                buildComboSection(bundleList);
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");

        // Specific errors
        EventType eventType = baseResponse.getEventType();
        ErrorCode errorCode = baseResponse.getError().getErrorCode();

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();

        // Generic errors
        if (super.handleErrorEvent(baseResponse)) {
            //mBundleButton.setEnabled(true);
            return;
        }

        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
            case REMOVE_PRODUCT_FROM_WISH_LIST:
            case ADD_PRODUCT_TO_WISH_LIST:
                // Hide dialog progress
                hideActivityProgress();
                // Validate error
                if (!super.handleErrorEvent(baseResponse)) {
                    showUnexpectedErrorWarning();
                }
                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();

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
            case GET_PRODUCT_DETAIL:
                showContinueShopping();
            default:
                break;
        }
    }

    /**
     * Build combo section if has bundles
     * TODO: Improve
     **/
    private void buildComboSection(final BundleList bundleList) {
        // Validate content
        if (bundleList == null) {
            if (mComboProductsLayout != null) {
                mComboProductsLayout.setVisibility(View.GONE);
            }
            return;
        }
        //load header
        TextView comboHeaderTitle = (TextView) mComboProductsLayout.findViewById(R.id.pdv_bundles_title);
        //changeFashion: change title if is fashion
        String titleCombo = mProduct.isFashion() ? getResources().getString(R.string.buy_the_look) : getResources().getString(R.string.combos);
        comboHeaderTitle.setText(titleCombo);
        //set total price
        TextView txTotalPrice = (TextView) mComboProductsLayout.findViewById(R.id.txTotalComboPrice);
        txTotalPrice.setText(CurrencyFormatter.formatCurrency(bundleList.getPrice()));
        //
        ArrayList<ProductBundle> bundleProducts = bundleList.getProducts();
        LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int count = 0;
        mTableBundles.removeAllViews();

        for (int i = 0; i < bundleProducts.size(); i++) {
            ProductBundle item = bundleProducts.get(i);
            ViewGroup comboProductItem = (ViewGroup) inflater.inflate(R.layout.pdv_fragment_bundle_item, mTableBundles, false);
            fillProductBundleInfo(comboProductItem, item);
            if (!item.getSku().equals(mProduct.getSku())) {
                comboProductItem.setOnClickListener(new ComboItemClickListener(comboProductItem, txTotalPrice, bundleList, i));
            }
            mTableBundles.addView(comboProductItem);
            // Add plus separator
            if (count < bundleProducts.size() - 1) {
                mTableBundles.addView(inflater.inflate(R.layout.pdv_fragment_bundle, mTableBundles, false));
            }
            count++;
        }

        mComboProductsLayout.setOnClickListener(this);
        mComboProductsLayout.setVisibility(View.VISIBLE);
    }


    /**
     * Fill a product bundle info
     *
     * @param view - combo item view
     * @param productBundleItem    - product bundle
     */
    private void fillProductBundleInfo(View view, ProductBundle productBundleItem) {
        ImageView mImage = (ImageView) view.findViewById(R.id.image_view);
        ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
        CheckBox mCheck = (CheckBox) view.findViewById(R.id.item_check);
        mCheck.setChecked(productBundleItem.isChecked());
        RocketImageLoader.instance.loadImage(productBundleItem.getImageUrl(), mImage, mProgress, R.drawable.no_image_large);
        TextView mBrand = (TextView) view.findViewById(R.id.item_brand);
        mBrand.setText(productBundleItem.getBrand());
        TextView mTitle = (TextView) view.findViewById(R.id.item_title);
        mTitle.setText(productBundleItem.getName());
        TextView mPrice = (TextView) view.findViewById(R.id.item_price);
        if(productBundleItem.hasDiscount()){
            mPrice.setText(CurrencyFormatter.formatCurrency(productBundleItem.getSpecialPrice()));
        } else {
            mPrice.setText(CurrencyFormatter.formatCurrency(productBundleItem.getPrice()));
        }

    }

    /**
     * functions that verifies if product simple is out of stock
     */
    private boolean verifyOutOfStock(){
        return (mProduct.getSelectedSimple() != null && mProduct.getSelectedSimple().isOutOfStock());
    }

    /**
     * function that dependent of the the stock and if its saved, sets the correct string for the button
     */
    private void setOutOfStockButton(){
        if(verifyOutOfStock()){
            mSaveForLater.setVisibility(View.VISIBLE);
            mBuyButton.setVisibility(View.GONE);
            if(mProduct.isWishList()){
                mSaveForLater.setText(getString(R.string.remove_from_saved));
                mSaveForLater.setSelected(true);
            } else {
                mSaveForLater.setText(getString(R.string.save_for_later));
                mSaveForLater.setSelected(false);
            }
        } else {
            mBuyButton.setVisibility(View.VISIBLE);
            mSaveForLater.setVisibility(View.GONE);
        }
    }

    private class ComboItemClickListener implements OnClickListener {
        ViewGroup bundleItemView;
        TextView txTotalComboPrice;
        BundleList bundleList;
        int selectedPosition;


        public ComboItemClickListener(ViewGroup bundleItemView, TextView txTotalComboPrice, BundleList bundleList, int selectedPosition) {
            this.bundleItemView = bundleItemView;
            this.txTotalComboPrice = txTotalComboPrice;
            this.bundleList = bundleList;
            this.selectedPosition = selectedPosition;
        }


        public void onClick(View v) {
            onClickComboItem(bundleItemView, txTotalComboPrice, bundleList, selectedPosition);
        }

    }

    private void triggerGetProductBundle(String sku) {
        triggerContentEvent(new GetProductBundleHelper(), GetProductBundleHelper.createBundle(sku), this);
    }



//    /**
//     * function responsible for calling the catalog with the products from a specific seller
//     */
//    private void goToSellerCatalog() {
//        Print.d("SELLER", "GO TO CATALOG");
//        if (mProduct.hasSeller()) {
//            Bundle bundle = new Bundle();
//            String targetUrl = mProduct.getSeller().getUrl();
//            String targetTitle = mProduct.getSeller().getName();
//            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
//            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
//            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
//            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
//            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, true);
//        }
//    }

//    /**
//     * function responsible for showing the rating and reviews of a specific seller
//     */
//    private void goToSellerRating() {
//        JumiaApplication.cleanRatingReviewValues();
//        JumiaApplication.cleanSellerReviewValues();
//        JumiaApplication.INSTANCE.setFormReviewValues(null);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mProduct.getSku());
//        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mProduct);
//        bundle.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, false);
//        bundle.putString(SELLER_ID, mProduct.getSeller().getSellerId());
//        getBaseActivity().onSwitchFragment(FragmentType.POPULARITY, bundle, FragmentController.ADD_TO_BACK_STACK);
//    }


//    /**
//     * Set the gallery
//     */
//    private void setProductGallery(ProductComplete completeProduct) {
//        // Show loading
//        mGalleryViewGroupFactory.setViewVisible(R.id.image_loading_progress);
//        // Case product without images
//        if (CollectionUtils.isEmpty(completeProduct.getImageList())) {
//            mGalleryViewGroupFactory.setViewVisible(R.id.image_place_holder);
//        }
//        // Case product with images
//        else RocketImageLoader.getInstance().loadImages(completeProduct.getImageList(), this);
//    }

    /*
     * (non-Javadoc)
     * @see com.mobile.utils.imageloader.RocketImageLoader.RocketImageLoaderLoadImagesListener#onCompleteLoadingImages(java.util.ArrayList)
     */
    @Override
    public void onCompleteLoadingImages(ArrayList<ImageHolder> successUrls) {
        Print.i(TAG, "ON COMPLETE LOADING IMAGES");

//        // Validate fragment visibility
//        if (isOnStoppingProcess) {
//            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
//            return;
//        }
//
//        // Gets all urls with success
//        ArrayList<String> urls = new ArrayList<>();
//        for (ImageHolder imageHolder : successUrls) {
//            urls.add(imageHolder.url);
//        }
//
//        // Validate the number of cached images
//        if (!successUrls.isEmpty()) {
//            // Match the cached image list with the current image list order
//            ArrayList<String> orderCachedImageList = (ArrayList<String>) CollectionUtils.retainAll(mProduct.getImageList(), urls);
//            // Set the cached images
//            mProduct.setImageList(orderCachedImageList);
//            // Create bundle with arguments
//            Bundle args = new Bundle();
//            args.putStringArrayList(ConstantsIntentExtra.IMAGE_LIST, orderCachedImageList);
//            args.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
//
//            // Validate the ProductImageGalleryFragment
//            ProductImageGalleryFragment productImagesViewPagerFragment = (ProductImageGalleryFragment) getChildFragmentManager().findFragmentByTag(ProductImageGalleryFragment.TAG);
//            // CASE CREATE
//            if (productImagesViewPagerFragment == null) {
//                Print.i(TAG, "SHOW GALLERY: first time position = " + ProductImageGalleryFragment.sSharedSelectedPosition);
//                productImagesViewPagerFragment = ProductImageGalleryFragment.getInstanceAsNested(args);
//                fragmentManagerTransition(R.id.pdv_slide_show_container, productImagesViewPagerFragment);
//            }
//            // CASE UPDATE
//            else {
//                Print.i(TAG, "SHOW GALLERY: second time position = " + ProductImageGalleryFragment.sSharedSelectedPosition);
//                productImagesViewPagerFragment.notifyFragment(args);
//            }
//            // Show container
//            mGalleryViewGroupFactory.setViewVisible(R.id.pdv_slide_show_container);
//
//
//        } else {
//            Print.i(TAG, "SHOW PLACE HOLDER");
//            // Show place holder
//            mGalleryViewGroupFactory.setViewVisible(R.id.image_place_holder);
//        }
    }

}
