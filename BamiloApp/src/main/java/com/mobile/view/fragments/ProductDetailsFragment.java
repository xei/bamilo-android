package com.mobile.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.emarsys.predict.RecommendedItem;
import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.classes.models.EmarsysEventModel;
import com.mobile.classes.models.SimpleEventModel;
import com.mobile.classes.models.SimpleEventModelFactory;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.tracking.EventActionKeys;
import com.mobile.constants.tracking.EventConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.datamanagement.DataManager;
import com.mobile.helpers.address.GetCitiesHelper;
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetDeliveryTimeHelper;
import com.mobile.helpers.products.GetProductBundleHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.helpers.teasers.GetRichRelevanceHelper;
import com.mobile.helpers.wishlist.AddToWishListHelper;
import com.mobile.helpers.wishlist.RemoveFromWishListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.extlibraries.emarsys.predict.recommended.Item;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendManager;
import com.mobile.managers.TrackerManager;
import com.mobile.service.database.BrandsTableHelper;
import com.mobile.service.database.LastViewedTableHelper;
import com.mobile.service.objects.addresses.AddressCities;
import com.mobile.service.objects.addresses.AddressCity;
import com.mobile.service.objects.addresses.AddressRegion;
import com.mobile.service.objects.addresses.AddressRegions;
import com.mobile.service.objects.campaign.CampaignItem;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.objects.product.Brand;
import com.mobile.service.objects.product.BundleList;
import com.mobile.service.objects.product.DeliveryTime;
import com.mobile.service.objects.product.DeliveryTimeCollection;
import com.mobile.service.objects.product.ImageUrls;
import com.mobile.service.objects.product.RichRelevance;
import com.mobile.service.objects.product.pojo.ProductBundle;
import com.mobile.service.objects.product.pojo.ProductComplete;
import com.mobile.service.objects.product.pojo.ProductSimple;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.tracking.AdjustTracker;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.ApiConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment.OnDialogListListener;
import com.mobile.utils.home.holder.HomeRecommendationsGridTeaserHolder;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import de.akquinet.android.androlog.Log;

/**
 * This class displays the product detail screen.
 *
 * @author spereira
 */
public class ProductDetailsFragment extends BaseFragment implements IResponseCallback, AdapterView.OnItemClickListener, OnDialogListListener, TargetLink.OnAppendDataListener {

    private final static String TAG = ProductDetailsFragment.class.getSimpleName();

    public static int sSharedSelectedPosition = IntConstants.DEFAULT_POSITION;
    private static String categoryTree = "";
    boolean isFromBuyButton;
    private ProductComplete mProduct;
    private String mCompleteProductSku;
    private RatingBar mProductRating;
    private TextView mProductRatingCount;
    private ImageView mWishListButton;
    private long mBeginRequestMillis;
    private long mGABeginRequestMillis;
    private String mNavPath;
    private String mNavSource;
    private TextView mSaveForLater;
    private TextView mBuyButton;
    private ViewGroup mSellerContainer;
    private ViewGroup mDescriptionView;
    private ViewGroup mSpecificationsView;
    private ViewGroup mOtherVariationsLayout;
    private ViewGroup mSizeLayout;
    private ViewGroup mRelatedProductsView;
    private ViewGroup mComboProductsLayout;
    private ViewGroup mTitleContainer;
    private View mGlobalButton;
    private View mOtherSellersContainer;
    private String mRichRelevanceHash;
    private String mRelatedRichRelevanceHash;
    private ViewGroup mBrandView;
    private View mPriceContainer;
    private Spinner mRegionSpinner;
    private Spinner mCitySpinner;
    private TextView mDeliveryTimeTextView;
    private RecommendManager recommendManager;
    HomeRecommendationsGridTeaserHolder recommendationsGridTeaserHolder;
    private boolean recommendationsTeaserHolderAdded = false;
    private AddressRegions mRegions;
    private AddressCities mCities;
    private int defaultRegionId, defaultCityId;
    private static Integer selectedRegionId = null, selectedCityId = null;
    private View rootView;
    private EmarsysEventModel addToCartEventModel;
    private EmarsysEventModel addToWishListEventModel;

    /**
     * Empty constructor
     */
    public ProductDetailsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.PRODUCT,
                R.layout.pdv_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
        // Reset the share selected position
        sSharedSelectedPosition = IntConstants.DEFAULT_POSITION;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        mGABeginRequestMillis = System.currentTimeMillis();

        // Get arguments
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (arguments != null) {
            // Get sku
            mCompleteProductSku = arguments.getString(ConstantsIntentExtra.CONTENT_ID);
            // Categories
            categoryTree = arguments.containsKey(ConstantsIntentExtra.CATEGORY_TREE_NAME) ? arguments.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME) + ",PDV" : "";
            // Get source and path
            mNavSource = getString(arguments.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcatalog));
            mNavPath = arguments.getString(ConstantsIntentExtra.NAVIGATION_PATH);
            mProduct = arguments.getParcelable(ConstantsIntentExtra.DATA);

            // Track Screen
            BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.PRODUCT_DETAIL.getName()), getString(R.string.gaScreen), "", getLoadTime());
            TrackerManager.trackScreen(getContext(), screenModel, false);
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
        this.rootView = view;
        // Title
        mTitleContainer = (ViewGroup) view.findViewById(R.id.pdv_title_container);
        // Slide show
        // created a new ProductImageGalleryFragment
        // Wish list
        mWishListButton = (ImageView) view.findViewById(R.id.pdv_button_wish_list);
        mWishListButton.setOnClickListener(this);
        // Price
        mPriceContainer = view.findViewById(R.id.pdv_price_container);
        // Rating
        view.findViewById(R.id.pdv_rating_container).setOnClickListener(this);
        mProductRating = (RatingBar) view.findViewById(R.id.pdv_rating_bar);
        mProductRatingCount = (TextView) view.findViewById(R.id.pdv_rating_bar_count);
        //brand section
        mBrandView = (ViewGroup) view.findViewById(R.id.pdv_brand_section);
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
        mComboProductsLayout = (ViewGroup) view.findViewById(R.id.pdv_combo_parent);
        mComboProductsLayout.setVisibility(View.GONE);
        // Related Products
        mRelatedProductsView = (ViewGroup) view.findViewById(R.id.pdv_related_container);
        // Offers
        mOtherSellersContainer = view.findViewById(R.id.pdv_other_sellers_button);
        // Bottom Buy Bar
        view.findViewById(R.id.pdv_button_share).setOnClickListener(this);
        view.findViewById(R.id.pdv_button_call).setOnClickListener(this);
        mBuyButton = (TextView) view.findViewById(R.id.pdv_button_buy);
        mBuyButton.setOnClickListener(this);
        // Save for later
        mSaveForLater = (TextView) view.findViewById(R.id.pdv_button_add_to_save);
        mSaveForLater.setOnClickListener(this);

        mRegionSpinner = (Spinner) view.findViewById(R.id.pdv_delivery_region);
        mCitySpinner = (Spinner) view.findViewById(R.id.pdv_delivery_city);

        mDeliveryTimeTextView = (TextView) view.findViewById(R.id.pdv_seller_delivery_info);
        if (mRegions != null) {
            setRegions(mRegions);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        triggerGetDeliveryTime(selectedCityId);
        triggerGetRegions(ApiConstants.GET_REGIONS_API_PATH);
        getBaseActivity().setActionBarTitle("");
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

        // Verify if is comming from login after trying to add/remove item from cart.
        final Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(AddToWishListHelper.ADD_TO_WISHLIST)) {
                ProductComplete mClicked = args.getParcelable(AddToWishListHelper.ADD_TO_WISHLIST);
                if (BamiloApplication.isCustomerLoggedIn() && mClicked != null) {
                    TrackerDelegator.trackAddToFavorites(mClicked);

                    // Global Tracker
                    addToWishListEventModel = new EmarsysEventModel(getString(TrackingPage.PRODUCT_DETAIL.getName()),
                            EventActionKeys.ADD_TO_WISHLIST, mClicked.getSku(), (long) mClicked.getPrice(), null);

                    triggerAddToWishList(mClicked.getSku());
                }
                args.remove(AddToWishListHelper.ADD_TO_WISHLIST);
            } else if (args.containsKey(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST)) {
                ProductComplete mClicked = args.getParcelable(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST);
                if (BamiloApplication.isCustomerLoggedIn() && mClicked != null) {
                    TrackerDelegator.trackRemoveFromFavorites(mClicked);

                    // Global Tracker
                    SimpleEventModel sem = new SimpleEventModel();
                    sem.category = getString(TrackingPage.PRODUCT_DETAIL.getName());
                    sem.action = EventActionKeys.REMOVE_FROM_WISHLIST;
                    sem.label = mClicked.getSku();
                    sem.value = (long) mClicked.getPrice();
                    TrackerManager.trackEvent(getContext(), EventConstants.RemoveFromWishList, sem);

                    triggerRemoveFromWishList(mClicked.getSku());
                }
                args.remove(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST);
            }

        }

        if (mProduct != null) {
            sendRecommend();
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
        if (id == R.id.pdv_rating_container) {
            onClickShowDescription(R.string.rat_rev);
        }
        // Case variation button
        else if (id == R.id.pdv_variations_container) {
            onClickVariationButton();
        }
        // Case favourite
        else if (id == R.id.pdv_button_wish_list) {
            onClickWishListButton(view);
        }
        // Case size guide
        else if (id == R.id.dialog_list_size_guide_button) {
            onClickSizeGuide(view);
        }
        // Case simples
        else if (id == R.id.pdv_simples_container) {
            onClickSimpleSizesButton();
        }
        // Case share
        else if (id == R.id.pdv_button_share) {
            onClickShare(mProduct);
        }
        // Case call to order
        else if (id == R.id.pdv_button_call) {
            UIUtils.onClickCallToOrder(getBaseActivity());
        }
        // Case sendPurchaseRecommend button
        else if (id == R.id.pdv_button_buy) {
            onClickBuyProduct();
        }
        // Case saved for later
        else if (id == R.id.pdv_button_add_to_save) {
            onClickSaveForLateButton(view);
        }
        // Case combos section
        else if (id == R.id.pdv_combo_parent) {
            onClickCombosProduct();
        }
        // Case other offers
        else if (id == R.id.pdv_other_sellers_button) {
            onClickOtherOffersProduct();
        }
        // Case global seller button
        else if (id == R.id.pdv_button_global_seller) {
            onClickGlobalSellerButton();
        }
        // Case global delivery button
        else if (id == R.id.pdv_seller_overseas_delivery_link) {
            onClickGlobalDeliveryLinkButton();
        }
        // Case seller container
        else if (id == R.id.pdv_seller_name) {
            goToSellerCatalog();
        }
        // Case brand
        else if (id == R.id.pdv_brand_section) {
            onClickBrandButton(view);
        }
        // Case specs button
        else if (id == R.id.pdv_specs_button) {
            onClickShowDescription(R.string.product_specifications);
        }
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
     * (non-Javadoc)
     *
     * @see
     * android.product_detail_support.v4.app.Fragment#onSaveInstanceState(android.product_detail_os
     * .Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ConstantsIntentExtra.CONTENT_ID, mCompleteProductSku);
        outState.putParcelable(ConstantsIntentExtra.DATA, mProduct);
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
            triggerLoadProduct(mCompleteProductSku, mRichRelevanceHash);
            triggerGetRegions(ApiConstants.GET_REGIONS_API_PATH);
        }
        // Case error
        else {
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_could_not_retrieved));
            getBaseActivity().onBackPressed();
        }
    }

    /*
     * ######## LAYOUT ########
     */

    /**
     * Validate and loads the received arguments comes from deep link process.
     */
    private boolean hasArgumentsFromDeepLink(Bundle bundle) {
        // Get the sku
        String sku = bundle.getString(GetProductHelper.SKU_TAG);
        // Get the simple size
        String deepLinkSimpleSize = bundle.getString(DeepLinkManager.PDV_SIZE_TAG);
        // Get Rich Relevance
        mRichRelevanceHash = bundle.getString(ConstantsIntentExtra.RICH_RELEVANCE_HASH);
        // Validate
        if (sku != null) {
            Print.i(TAG, "DEEP LINK GET PDV: " + sku + " " + deepLinkSimpleSize);
            mNavSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix));
            mNavPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
            triggerLoadProduct(sku, mRichRelevanceHash);
            triggerGetRegions(ApiConstants.GET_REGIONS_API_PATH);
            return true;
        }
        return false;
    }

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

        sendRecommend();
        // Set layout
        setTitle();
        setSlideShow();
        setBuyButton();
        setWishListButton();
        setProductPriceInfo();
        setRatingInfo();
        setBrandInfo();
        setSpecifications();
        setProductSize();
        setProductVariations();
        setSellerInfo();
        setOtherSellersOffers();
        setDescription();
        setCombos();
        setRelatedItems();
        // Show container
        showFragmentContentContainer();
        // Tracking
        SimpleEventModel sem = SimpleEventModelFactory.createModelForPDV(mProduct, mNavSource);
        TrackerManager.trackEvent(getContext(), EventConstants.ViewProduct, sem);
    }

    /**
     * sets data for mBuyButton
     */
    private void setBuyButton() {
        // showcase products in the Jumia catalog flagged as "Pre-Orders" in the app
        // so that customers can pre-pay for items in the catalog before they become widely available
        if (mProduct.isPreOrder()) {
            mBuyButton.setText(R.string.pre_order);
        } else {
            mBuyButton.setText(R.string.buy_now_button);
        }
    }

    private void setOtherSellersOffers() {
        //show button offers with separator if has offers
        if (mProduct.hasOffers()) {
            TextView txOffers = (TextView) mOtherSellersContainer.findViewById(R.id.tx_single_line_text);
            txOffers.setText(String.format(getString(R.string.other_sellers_starting), CurrencyFormatter.formatCurrency(mProduct.getMinPriceOffer())));
            mOtherSellersContainer.setOnClickListener(this);
        } else {
            mOtherSellersContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Set product price info
     */
    private void setProductPriceInfo() {
        Print.d(TAG, "SHOW PRICE INFO: " + mProduct.getPrice() + " " + mProduct.getSpecialPrice());
        // Get views
        TextView special = (TextView) mPriceContainer.findViewById(R.id.pdv_price_text_special);
        TextView price = (TextView) mPriceContainer.findViewById(R.id.pdv_price_text_price);
        TextView percentage = (TextView) mPriceContainer.findViewById(R.id.pdv_price_text_discount);
        TextView shipping = (TextView) mPriceContainer.findViewById(R.id.pdv_price_text_shipping);
        // Set views
        UIProductUtils.setPriceRules(mProduct, price, special);
        UIProductUtils.setDiscountRules(mProduct, percentage);
        UIProductUtils.setFreeShippingInfo(mProduct, shipping);
    }

    /**
     * Set rating stars and info
     */
    private void setRatingInfo() {
        Integer ratingCount = mProduct.getTotalRatings();
        Integer reviewsCount = mProduct.getTotalReviews();
        // Validation
        if (ratingCount == 0 && reviewsCount == 0) {
            mProductRatingCount.setText(getResources().getString(R.string.be_first_rate));    //be the first to rate if hasn't
        } else {
            UIUtils.setProgressForRTLPreJellyMr2(mProductRating);
            mProductRating.setRating((float) mProduct.getAvgRating());
            mProductRating.setVisibility(View.VISIBLE);
            String rating = getResources().getQuantityString(R.plurals.numberOfRatings, ratingCount, ratingCount);
            mProductRatingCount.setText(rating);
        }
    }

    /**
     * Show brand/SIS section in case of grand has image and a target link
     */
    private void setBrandInfo() {
        if (!mProduct.getBrand().hasTarget()) {
            mBrandView.setVisibility(View.GONE);
        } else {
            // Set click item
            Brand brand = mProduct.getBrand();
            mBrandView.setTag(R.id.target_link, brand.getTarget());
            mBrandView.setTag(R.id.target_title, brand.getName());
            mBrandView.setOnClickListener(this);
            // Set text
            TextView button = (TextView) mBrandView.findViewById(R.id.pdv_brand_text);
            button.setText(getString(R.string.visit_the_official_brand_store, brand.getName()));
            // Set image
            ImageView brandImage = (ImageView) mBrandView.findViewById(R.id.pdv_brand_image);
            if (TextUtils.isNotEmpty(brand.getImageUrl())) {
                //RocketImageLoader.instance.loadImage(brand.getImageUrl(), brandImage, null, R.drawable.no_image_tiny);
                ImageManager.getInstance().loadImage(brand.getImageUrl(), brandImage, null, R.drawable.no_image_large, false);
            } else {
                brandImage.setVisibility(View.GONE);
            }
        }
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
            final TextView sellerName = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_name);
            // Set name
            sellerName.setText(mProduct.getSeller().getName());
            // Set shop first except B project
            if (mProduct.isShopFirst() && !ShopSelector.isRtlShop()) {
                UIProductUtils.showShopFirstOverlayMessage(this, mProduct, sellerName);
            }
            // Set listener
            if (TextUtils.isNotEmpty(mProduct.getSeller().getTarget())) {
                sellerName.setOnClickListener(this);
            }
            // Case global seller
            if (mProduct.getSeller().isGlobal()) {
                // Set global button
                mGlobalButton.setVisibility(View.VISIBLE);
                mGlobalButton.bringToFront();
                mGlobalButton.setOnClickListener(this);
                // Delivery Info
                mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_container).setVisibility(View.VISIBLE);
                /*((TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_title)).setText(mProduct.getSeller().getDeliveryTime());
                if (TextUtils.isNotEmpty(mProduct.getSeller().getDeliveryCMSInfo())) {
                    TextView info = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_text_cms);
                    info.setText(mProduct.getSeller().getDeliveryCMSInfo());
                    info.setVisibility(View.VISIBLE);
                }*/
                // Shipping Info
                if (TextUtils.isNotEmpty(mProduct.getSeller().getDeliveryShippingInfo())) {
                    TextView info2 = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_text_info);
                    info2.setText(mProduct.getSeller().getDeliveryShippingInfo());
                    info2.setVisibility(View.VISIBLE);
                }
                // Button link
                if (TextUtils.isNotEmpty(mProduct.getSeller().getDeliveryMoreDetailsText())) {
                    mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_divider).setVisibility(View.VISIBLE);
                    TextView link = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_overseas_delivery_link);
                    link.setText(mProduct.getSeller().getDeliveryMoreDetailsText());
                    link.setOnClickListener(this);
                    link.setVisibility(View.VISIBLE);
                }
            }
            // Case normal
            /*else if (TextUtils.isNotEmpty(mProduct.getSeller().getDeliveryTime())) {
                // Delivery Info
                TextView textView = (TextView) mSellerContainer.findViewById(R.id.pdv_seller_delivery_info);
                textView.setText(mProduct.getSeller().getDeliveryTime());
                textView.setVisibility(View.VISIBLE);
            }*/
            // Seller warranty
            if (TextUtils.isNotEmpty(mProduct.getSeller().getWarranty())) {
                mSellerContainer.findViewById(R.id.pdv_seller_warranty_column).setVisibility(View.VISIBLE);
                mSellerContainer.findViewById(R.id.pdv_seller_warranty_container).setVisibility(View.VISIBLE);
                TextView textView = ((TextView) mSellerContainer.findViewById(R.id.pdv_seller_warranty));
//                String warranty = String.format(getResources().getString(R.string.warranty), mProduct.getSeller().getWarranty());
                textView.setText(mProduct.getSeller().getWarranty());
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
        // Set title
        ((TextView) mTitleContainer.findViewById(R.id.pdv_product_title)).setText(mProduct.getBrandName());
        ((TextView) mTitleContainer.findViewById(R.id.pdv_product_subtitle)).setText(mProduct.getName());
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
        button.setOnClickListener(this);
    }

    /**
     * Set combos: if has bundle get bundle list
     */
    private void setCombos() {
        if (mProduct.hasBundle()) {
            if (mProduct.getProductBundle() == null || CollectionUtils.isEmpty(mProduct.getProductBundle().getProducts())) {
                triggerGetProductBundle(mProduct.getSku());
            } else {
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
            if (mProduct.hasSelectedSimpleVariation() && mProduct.getSelectedSimple() != null) {
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
        BaseFragment fragment = (BaseFragment) getChildFragmentManager().findFragmentByTag(ProductImageGalleryFragment.TAG);
        // CASE CREATE
        if (fragment == null) {
            Print.i(TAG, "ON DISPLAY SLIDE SHOW: NEW");

            ArrayList<ImageUrls> images;
            if (ShopSelector.isRtl() && CollectionUtils.isNotEmpty(mProduct.getImageList())) {
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
            fragment = BaseFragment.newInstance(getBaseActivity(), ProductImageGalleryFragment.class, args);
            FragmentController.getInstance().addChildFragment(this, R.id.pdv_slide_show_container, fragment, ProductImageGalleryFragment.TAG);
        }
        // CASE UPDATE
        else {
            Print.i(TAG, "ON DISPLAY SLIDE SHOW: UPDATE");
            fragment.notifyFragment(null);
        }
    }


    /*
     * ####### LISTENERS #######
     */

    /**
     * Method used to create the view
     */
    private void setRelatedItems() {
        /*if (CollectionUtils.isNotEmpty(mProduct.getRelatedProducts())) {
            if (mProduct.getRichRelevance() != null && TextUtils.isNotEmpty(mProduct.getRichRelevance().getTitle())) {
                ((TextView) mRelatedProductsView.findViewById(R.id.pdv_related_title)).setText(mProduct.getRichRelevance().getTitle());
            }
            ExpandedGridViewComponent relatedGridView = (ExpandedGridViewComponent) mRelatedProductsView.findViewById(R.id.pdv_related_grid_view);
            relatedGridView.setExpanded(true);
            relatedGridView.setAdapter(new RelatedProductsAdapter(getBaseActivity(), R.layout.pdv_fragment_related_item, mProduct.getRelatedProducts()));
            relatedGridView.setOnItemClickListener(this);
            mRelatedProductsView.setVisibility(View.VISIBLE);
        } else {
            mRelatedProductsView.setVisibility(View.GONE);
        }*/
    }

    /**
     * Method used to update the wish list value.
     */
    private void updateWishListValue() {
        try {
            boolean value = mProduct.isWishList();
            mWishListButton.setSelected(value);
            setOutOfStockButton();
        } catch (NullPointerException e) {
            Log.i(TAG, "NPE ON UPDATE WISH LIST VALUE");
        }
    }

    /**
     * Go to brands target link
     */
    private void onClickBrandButton(View view) {
        @TargetLink.Type String link = (String) view.getTag(R.id.target_link);
        String title = (String) view.getTag(R.id.target_title);
        new TargetLink(getWeakBaseActivity(), link).addTitle(title).retainBackStackEntries().run();
    }

    private void goToSellerCatalog() {
        Log.i(TAG, "ON CLICK SELLER NAME");
        @TargetLink.Type String target = mProduct.getSeller().getTarget();
        new TargetLink(getWeakBaseActivity(), target).retainBackStackEntries().run();
    }

    /**
     * Process the click on global policy
     */
    private static final String INTERNATIONAL_PRODUCT_POLICY_PAGE = "international-product-policy";

    private void onClickGlobalDeliveryLinkButton() {
        Log.i(TAG, "ON CLICK GLOBAL SELLER");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, INTERNATIONAL_PRODUCT_POLICY_PAGE);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, getString(R.string.policy));
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
    private void onClickComboItem(View bundleItemView, TextView txTotalPrice, BundleList bundleList, int position) {
        // Update combo price
        bundleList.updateTotalPriceWhenChecking(position);
        //get the bundle to update checkbox state
        final ProductBundle productBundle = bundleList.getProducts().get(position);

        // Update check
        final CheckBox checkBox = (CheckBox) bundleItemView.findViewById(R.id.item_check);
        checkBox.post(new Runnable() {
            @Override
            public void run() {
                checkBox.setChecked(productBundle.isChecked());
            }
        });


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
        bundle.putParcelable(RestConstants.BUNDLE_PRODUCTS, mProduct.getProductBundle());
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, mProduct.getSku());
        getBaseActivity().onSwitchFragment(FragmentType.COMBO_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Show the product description
     */
    private void onClickShowDescription(int position) {
        Log.i(TAG, "ON CLICK TO SHOW DESCRIPTION");
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mProduct);
        bundle.putInt(ConstantsIntentExtra.PRODUCT_INFO_POS, position);
        bundle.putString(ConstantsIntentExtra.FLAG_1, mProduct.getBrandName());
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
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, mProduct.getSku());
        bundle.putString(ConstantsIntentExtra.PRODUCT_NAME, mProduct.getName());
        bundle.putString(ConstantsIntentExtra.PRODUCT_BRAND, mProduct.getBrandName());
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_OFFERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on sendPurchaseRecommend
     */
    private void onClickBuyProduct() {
        Log.i(TAG, "ON CLICK BUY BUTTON");
        if (mProduct == null) {
            showUnexpectedErrorWarning();
            return;
        }
        // Validate has simple variation selected
        ProductSimple simple = mProduct.getSelectedSimple();
        // Case add item to cart
        if (simple != null) {
            triggerAddItemToCart(simple.getSku());
            // Tracking
            TrackerDelegator.trackProductAddedToCart(mProduct, mGroupType);

            // Global Tracker Event Model
            addToCartEventModel = new EmarsysEventModel(getString(TrackingPage.PDV.getName()), EventActionKeys.ADD_TO_CART,
                    mProduct.getSku(), (long) mProduct.getPrice(), null);
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
     * Process the click on wish list button
     */
    private void onClickWishListButton(View view) {
        // Validate customer is logged in
        if (BamiloApplication.isCustomerLoggedIn()) {
            try {
                // Get item
                if (view.isSelected()) {
                    triggerRemoveFromWishList(mProduct.getSku());
                    TrackerDelegator.trackRemoveFromFavorites(mProduct);

                    // Global Tracker
                    SimpleEventModel sem = new SimpleEventModel(getString(TrackingPage.PDV.getName()),
                            EventActionKeys.REMOVE_FROM_WISHLIST, mProduct.getSku(), (long) mProduct.getPrice());
                    TrackerManager.trackEvent(getContext(), EventConstants.RemoveFromWishList, sem);
                } else {
                    triggerAddToWishList(mProduct.getSku());
                    TrackerDelegator.trackAddToFavorites(mProduct);

                    // Global Tracker
                    addToWishListEventModel = new EmarsysEventModel(getString(TrackingPage.PDV.getName()),
                            EventActionKeys.ADD_TO_WISHLIST, mProduct.getSku(), (long) mProduct.getPrice(), null);
                }
            } catch (NullPointerException e) {
                Log.w(TAG, "NPE ON ADD ITEM TO WISH LIST", e);
            }
        } else {
            // Save values to end action after login
            final Bundle args = getArguments();
            if (args != null) {
                if (view.isSelected()) {
                    args.putParcelable(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST, mProduct);
                } else {
                    args.putParcelable(AddToWishListHelper.ADD_TO_WISHLIST, mProduct);
                }
            }

            // Goto login
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on saved for later button
     */
    private void onClickSaveForLateButton(View view) {
        // Validate customer is logged in
        if (BamiloApplication.isCustomerLoggedIn()) {
            try {
                // if view is selected it means that the product is currently on the saved list and user want to remove it
                if (view.isSelected()) {
                    triggerRemoveFromWishList(mProduct.getSku());
                    TrackerDelegator.trackRemoveFromFavorites(mProduct);

                    // Global Tracker
                    SimpleEventModel sem = new SimpleEventModel(getString(TrackingPage.PDV.getName()),
                            EventActionKeys.REMOVE_FROM_WISHLIST, mProduct.getSku(), (long) mProduct.getPrice());
                    TrackerManager.trackEvent(getContext(), EventConstants.RemoveFromWishList, sem);
                } else {
                    triggerAddToWishList(mProduct.getSku());
                    TrackerDelegator.trackAddToFavorites(mProduct);

                    // Global Tracker
                    addToWishListEventModel = new EmarsysEventModel(getString(TrackingPage.PDV.getName()),
                            EventActionKeys.ADD_TO_WISHLIST, mProduct.getSku(), (long) mProduct.getPrice(), null);
                }
            } catch (NullPointerException e) {
                Log.w(TAG, "NPE ON ADD ITEM TO SAVED", e);
            }
        } else {
            // Save values to end action after login
            final Bundle args = getArguments();
            if (args != null) {
                if (view.isSelected()) {
                    args.putParcelable(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST, mProduct);
                } else {
                    args.putParcelable(AddToWishListHelper.ADD_TO_WISHLIST, mProduct);
                }
            }
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
        if (position < mProduct.getRelatedProducts().size()) { // To avoid click on Placebo
            @TargetLink.Type String target = (String) view.getTag(R.id.target_sku);
            mRelatedRichRelevanceHash = mProduct.getRelatedProducts().get(position).getRichRelevanceClickHash();
            new TargetLink(getWeakBaseActivity(), target)
                    .addAppendListener(this)
                    .retainBackStackEntries()
                    .run();
        }

    }

    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle data) {
        if (TextUtils.isNotEmpty(mRelatedRichRelevanceHash))
            data.putString(ConstantsIntentExtra.RICH_RELEVANCE_HASH, mRelatedRichRelevanceHash);
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
                // Case from sendPurchaseRecommend button
                if (isFromBuyButton) {
                    onClickBuyProduct();
                }
                setProductPriceInfo();
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
    public void onDialogSizeListClickView(int position, CampaignItem item) {

    }

    @Override
    public void onDialogListDismiss() {
        isFromBuyButton = false;
    }

    /*
     * ############## TRIGGERS ##############
     */

    private void triggerLoadProduct(String sku, String richRelevanceHash) {
        mBeginRequestMillis = System.currentTimeMillis();
        if (TextUtils.isNotEmpty(richRelevanceHash))
            richRelevanceHash = TargetLink.getIdFromTargetLink(richRelevanceHash);

        triggerContentEvent(new GetProductHelper(), GetProductHelper.createBundle(sku, richRelevanceHash), this);
        //recommendManager = new RecommendManager();
        //sendRecommend();
    }

    private void triggerGetRegions(String apiCall) {
        DataManager.getInstance().loadData(new GetRegionsHelper(), GetRegionsHelper.createBundle(apiCall), this);
    }

    private void triggerGetDeliveryTime(Integer cityId) {
        if (mProduct != null && mRegions != null) {
            triggerContentEventNoLoading(new GetDeliveryTimeHelper(), GetDeliveryTimeHelper.createBundle(mProduct.getSimples().get(0).getSku()
                    , cityId), this);
        }
    }

    private void triggerGetCities(String apiCall, int region) {
        DataManager.getInstance().loadData(new GetCitiesHelper(), GetCitiesHelper.createBundle(apiCall, region, null), this);
    }

    private void triggerAddItemToCart(String sku) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku), this);
    }

    private void triggerAddToWishList(String sku) {
        triggerContentEventProgress(new AddToWishListHelper(), AddToWishListHelper.createBundle(sku), this);
    }

    private void triggerRemoveFromWishList(String sku) {
        triggerContentEventProgress(new RemoveFromWishListHelper(), RemoveFromWishListHelper.createBundle(sku), this);
    }

    private void triggerRichRelevance(String target) {
        triggerContentEvent(new GetRichRelevanceHelper(), GetRichRelevanceHelper.createBundle(TargetLink.getIdFromTargetLink(target)), this);
    }
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
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                // Track add to cart
                if (addToCartEventModel != null) {
                    PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
                    if (cart != null) {
                        addToCartEventModel.emarsysAttributes = EmarsysEventModel.createAddToCartEventModelAttributes(addToCartEventModel.label,
                                (long) cart.getTotal(), true);
                    } else {
                        addToCartEventModel.emarsysAttributes = EmarsysEventModel.createAddToCartEventModelAttributes(addToCartEventModel.label,
                                0, true);
                    }
                    TrackerManager.trackEvent(getContext(), EventConstants.AddToCart, addToCartEventModel);
                }
                break;
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                // Force wish list reload for next time
                WishListFragment.sForceReloadWishListFromNetwork = true;
                // Update value
                updateWishListValue();
                break;
            case ADD_PRODUCT_TO_WISH_LIST:
                // Force wish list reload for next time
                WishListFragment.sForceReloadWishListFromNetwork = true;
                // Update value
                updateWishListValue();

                // Tracking add to wish list
                if (addToWishListEventModel != null) {
                    if (mProduct != null) {
                        addToWishListEventModel.emarsysAttributes = EmarsysEventModel.createAddToWishListEventModelAttributes(mProduct.getCategoryKey(), true);
                    }
                    TrackerManager.trackEvent(getContext(), EventConstants.AddToWishList, addToWishListEventModel);
                }
                break;
            case GET_PRODUCT_DETAIL:
                ProductComplete product = (ProductComplete) baseResponse.getContentData();
                // Validate product
                if (product == null || product.getName() == null) {
                    showWarningErrorMessage(getString(R.string.product_could_not_retrieved));
                    getBaseActivity().onBackPressed();
                    return;
                }
                // Save product
                mProduct = product;
                // Verify if there's Rich Relevance request to make
                if (product.getRichRelevance() != null && !product.getRichRelevance().isHasData())
                    triggerRichRelevance(mProduct.getRichRelevance().getTarget());

                if (CollectionUtils.isNotEmpty(mProduct.getImageList())) {
                    sSharedSelectedPosition = !ShopSelector.isRtl() ? IntConstants.DEFAULT_POSITION : mProduct.getImageList().size() - 1;
                } else {
                    sSharedSelectedPosition = IntConstants.DEFAULT_POSITION;
                }

                // Show product or update partial
                displayProduct(mProduct);

                // Track Screen Timing
                BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.PDV.getName()), getString(R.string.gaScreen), mProduct.getSku(), getLoadTime());
                TrackerManager.trackScreenTiming(getContext(), screenModel);

                // Tracking
                Bundle params = new Bundle();
                params.putParcelable(AdjustTracker.PRODUCT, mProduct);
                params.putString(AdjustTracker.TREE, categoryTree);

                TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_DETAIL_LOADED, params);
                // Database
                LastViewedTableHelper.insertLastViewedProduct(product);
                BrandsTableHelper.updateBrandCounter(product.getBrandName());
                triggerGetDeliveryTime(selectedCityId);
                break;
            case GET_PRODUCT_BUNDLE:
                BundleList bundleList = (BundleList) baseResponse.getContentData();
                //keep the bundle
                mProduct.setProductBundle(bundleList);
                // build combo section from here
                setCombos();
                break;
            case GET_RICH_RELEVANCE_EVENT:
                RichRelevance productRichRelevance = (RichRelevance) baseResponse.getContentData();
                mProduct.setRichRelevance(productRichRelevance);
                setRelatedItems();
                break;
            case GET_REGIONS_EVENT:
                mRegions = (AddressRegions) baseResponse.getContentData();
                if (CollectionUtils.isNotEmpty(mRegions)) {
                    setRegions(mRegions);
                    triggerGetDeliveryTime(selectedCityId);
                }
                break;
            case GET_CITIES_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                mCities = (GetCitiesHelper.AddressCitiesStruct) baseResponse.getContentData();
                if (CollectionUtils.isNotEmpty(mCities)) {
                    setCities(mCities);
                }
                break;
            case GET_DELIVERY_TIME:
                Print.d(TAG, "RECEIVED DELIVERY_TIME");
                DeliveryTimeCollection deliveryTimeCollection = (DeliveryTimeCollection) baseResponse.getContentData();
                DeliveryTime deliveryTime = deliveryTimeCollection.getDeliveryTimes().get(0);
                String strDeliveryTime;
                if (deliveryTime.getTehranDeliveryTime().isEmpty()) {
                    strDeliveryTime = deliveryTime.getDeliveryMessage();
                } else {
                    strDeliveryTime = String.format(new Locale("fa"), "%s %s\n%s %s", getString(R.string.tehran_delivery_time), deliveryTime.getTehranDeliveryTime(), getString(R.string.other_cities_delivery_time), deliveryTime.getOtherCitiesDeliveryTime());
                }
                rootView.findViewById(R.id.deliveryRow).setVisibility(View.VISIBLE);
                mDeliveryTimeTextView.setText(strDeliveryTime);
                mDeliveryTimeTextView.setVisibility(View.VISIBLE);
                defaultRegionId = deliveryTimeCollection.getRegionId();
                defaultCityId = deliveryTimeCollection.getCityId();
                selectDefaultRegion(mRegions);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Hide dialog progress
        hideActivityProgress();
        // Specific errors
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Validate type
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
        switch (eventType) {
            case GET_PRODUCT_BUNDLE:
                // do nothing
                break;
            case GET_REGIONS_EVENT:
                hideDeliveryTimeBlock();
                break;
            case GET_CITIES_EVENT:
                hideDeliveryTimeBlock();
                break;
            case GET_DELIVERY_TIME:
                hideDeliveryTimeBlock();
                break;
            case GET_PRODUCT_DETAIL:
                showWarningErrorMessage(baseResponse.getErrorMessage(), eventType);
                getBaseActivity().onBackPressed();
                break;
            case GET_RICH_RELEVANCE_EVENT:
                setRelatedItems();
                break;
            default:
                super.handleErrorEvent(baseResponse);
                break;
        }

    }

    private void hideDeliveryTimeBlock() {
        rootView.findViewById(R.id.deliveryRow).setVisibility(View.GONE);
        rootView.findViewById(R.id.tvDeliveryTimeSectionTitle).setVisibility(View.GONE);
        mRegionSpinner.setVisibility(View.GONE);
        mCitySpinner.setVisibility(View.GONE);
    }

    /**
     * Build combo section if has bundles
     * TODO: Improve this component using RecyclerView
     **/
    private void buildComboSection(final BundleList bundleList) {
        // Validate content
        if (bundleList == null || CollectionUtils.isEmpty(bundleList.getProducts())) {
            UIUtils.setVisibility(mComboProductsLayout, false);
            return;
        }
        // Get header
        TextView title = (TextView) mComboProductsLayout.findViewById(R.id.pdv_combo_title);
        title.setText(mProduct.isFashion() ? R.string.buy_the_look : R.string.combos);
        // Get price
        TextView price = (TextView) mComboProductsLayout.findViewById(R.id.pdv_combo_price);
        price.setText(CurrencyFormatter.formatCurrency(bundleList.getPrice()));
        // Get container
        ViewGroup comboGroup = (ViewGroup) mComboProductsLayout.findViewById(R.id.pdv_combo_container);
        comboGroup.removeAllViews();
        // Revert elements if RTL (the original is used for ComboFragment)
        ArrayList<ProductBundle> bundleProducts = new ArrayList<>(bundleList.getProducts());
        if (ShopSelector.isRtl() && DeviceInfoHelper.isPreJellyBeanMR1()) {
            Collections.reverse(bundleProducts);
        }

        LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int count = 0;
        for (int i = 0; i < bundleProducts.size(); i++) {
            ProductBundle item = bundleProducts.get(i);
            ViewGroup comboProductItem = (ViewGroup) inflater.inflate(R.layout.pdv_fragment_combo_group_item, comboGroup, false);
            fillProductBundleInfo(comboProductItem, item);
            if (!item.getSku().equals(mProduct.getSku())) {
                comboProductItem.setOnClickListener(new ComboItemClickListener(comboProductItem, price, bundleList, i));
            } else {
                final CheckBox checkBox = (CheckBox) comboProductItem.findViewById(R.id.item_check);
                checkBox.post(new Runnable() {
                    @Override
                    public void run() {
                        checkBox.setEnabled(false);
                    }
                });
            }
            comboGroup.addView(comboProductItem);
            // Add plus separator
            if (count < bundleProducts.size() - 1) {
                comboGroup.addView(inflater.inflate(R.layout.pdv_fragment_combo_divider, comboGroup, false));
            }
            count++;
        }
        // Slide the horizontal scroll view to the end to show the first element
        if (ShopSelector.isRtl() && DeviceInfoHelper.isPreJellyBeanMR1()) {
            final HorizontalScrollView scroll = (HorizontalScrollView) mComboProductsLayout.findViewById(R.id.pdv_combo_scroll);
            scroll.postDelayed(new Runnable() {
                public void run() {
                    scroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            }, 100L);
        }
        mComboProductsLayout.setOnClickListener(this);
        mComboProductsLayout.setVisibility(View.VISIBLE);
    }


    /**
     * Fill a product bundle info
     *
     * @param view              - combo item view
     * @param productBundleItem - product bundle
     */
    private void fillProductBundleInfo(View view, final ProductBundle productBundleItem) {
        ImageView mImage = (ImageView) view.findViewById(R.id.image_view);
        ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
        final CheckBox mCheck = (CheckBox) view.findViewById(R.id.item_check);
        mCheck.post(new Runnable() {
            @Override
            public void run() {
                mCheck.setChecked(productBundleItem.isChecked());
                mCheck.setEnabled(true);
            }
        });

        //RocketImageLoader.instance.loadImage(productBundleItem.getImageUrl(), mImage, mProgress, R.drawable.no_image_large);
        ImageManager.getInstance().loadImage(productBundleItem.getImageUrl(), mImage, mProgress, R.drawable.no_image_large, false);

        TextView mBrand = (TextView) view.findViewById(R.id.item_brand);
        mBrand.setText(productBundleItem.getBrandName());
        TextView mTitle = (TextView) view.findViewById(R.id.item_title);
        mTitle.setText(productBundleItem.getName());
        TextView mPrice = (TextView) view.findViewById(R.id.item_price);
        if (productBundleItem.hasDiscount()) {
            mPrice.setText(CurrencyFormatter.formatCurrency(productBundleItem.getSpecialPrice()));
        } else {
            mPrice.setText(CurrencyFormatter.formatCurrency(productBundleItem.getPrice()));
        }
    }

    /**
     * functions that verifies if product simple is out of stock
     */
    private boolean verifyOutOfStock() {
        return (mProduct.getSelectedSimple() != null && mProduct.getSelectedSimple().isOutOfStock());
    }

    /**
     * function that dependent of the the stock and if its saved, sets the correct string for the button
     */
    private void setOutOfStockButton() {
        if (verifyOutOfStock()) {
            mSaveForLater.setVisibility(View.VISIBLE);
            mBuyButton.setVisibility(View.GONE);
            if (mProduct.isWishList()) {
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

    private void triggerGetProductBundle(String sku) {
        triggerContentEvent(new GetProductBundleHelper(), GetProductBundleHelper.createBundle(sku), this);

    }


    Spinner.OnItemSelectedListener onAddressItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Object object = parent.getItemAtPosition(position);
            if (object instanceof AddressRegion) {
                // Request the cities for this region id
                int region = ((AddressRegion) object).getValue();
                // Get cities
                if (region != -1) {
                    triggerGetCities(ApiConstants.GET_CITIES_API_PATH, region);
                }

            } else if (object instanceof AddressCity) {
                // Case list
                int city = ((AddressCity) object).getValue();
                if (city != -1) {
                    selectedRegionId = ((AddressRegion) mRegionSpinner.getSelectedItem()).getValue();
                    mDeliveryTimeTextView.setText(R.string.getting_data_from_server);
                    selectedCityId = city;
                    triggerGetDeliveryTime(city);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * Method used to set the regions on the respective form
     */
    private void setRegions(ArrayList<AddressRegion> regions) {
        Print.d(TAG, "SET REGIONS REGIONS: ");

        rootView.findViewById(R.id.tvDeliveryTimeSectionTitle).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.pdv_delivery_address_container).setVisibility(View.VISIBLE);

        // Create adapter
        List<AddressRegion> regionsWithEmptyItem = new ArrayList<>();
        regionsWithEmptyItem.add(new AddressRegion(-1, getString(R.string.select_region)));
        regionsWithEmptyItem.addAll(regions);
        RegionCitySpinnerAdapter adapter = new RegionCitySpinnerAdapter(getBaseActivity(), R.layout.form_spinner_item, regionsWithEmptyItem);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        mRegionSpinner.setAdapter(adapter);
        mRegionSpinner.setOnItemSelectedListener(onAddressItemSelected);
        selectDefaultRegion(regions);
        hideActivityProgress();
        // Show invisible content to trigger spinner listeners
    }

    private void selectDefaultRegion(List<AddressRegion> regions) {
        int selected = 0;
        if (defaultRegionId != 0) {
            selected = defaultRegionId;
        } else if (selectedRegionId != null) {
            selected = selectedRegionId;
        }
        for (int i = 0; i < regions.size(); i++) {
            if (regions.get(i).getValue() == selected) {
                mRegionSpinner.setSelection(i + 1);
            }
        }
    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(ArrayList<AddressCity> cities) {
        mCitySpinner.setVisibility(View.VISIBLE);
        // Create adapter
        List<AddressCity> citiesWithEmptyItem = new ArrayList<>();
        citiesWithEmptyItem.add(new AddressCity(-1, getString(R.string.select_city)));
        citiesWithEmptyItem.addAll(cities);
        RegionCitySpinnerAdapter adapter = new RegionCitySpinnerAdapter(getBaseActivity(), R.layout.form_spinner_item, citiesWithEmptyItem);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        mCitySpinner.setAdapter(adapter);
        selectDefaultCity(cities);
        mCitySpinner.setOnItemSelectedListener(onAddressItemSelected);
        hideActivityProgress();
        // Show invisible content to trigger spinner listeners
    }

    private void selectDefaultCity(List<AddressCity> cities) {
        int selected = 0;
        if (defaultCityId != 0) {
            selected = defaultCityId;
        } else if (selectedCityId != null) {
            selected = selectedCityId;
        }
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).getValue() == selected) {
                mCitySpinner.setSelection(i + 1);
            }
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

    private void sendRecommend() {

        RecommendListCompletionHandler handler = new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {
                LayoutInflater inflater = LayoutInflater.from(getBaseActivity());

                if (recommendationsGridTeaserHolder == null) {
                    recommendationsGridTeaserHolder = new HomeRecommendationsGridTeaserHolder(getBaseActivity(), inflater.inflate(R.layout.recommendation_grid, mRelatedProductsView, false), null);
                }
                if (recommendationsGridTeaserHolder != null) {
                    try {


                        // Set view
                        mRelatedProductsView.removeView(recommendationsGridTeaserHolder.itemView);
                        recommendationsGridTeaserHolder = new HomeRecommendationsGridTeaserHolder(getBaseActivity(), inflater.inflate(R.layout.recommendation_grid, mRelatedProductsView, false), null);

                        recommendationsGridTeaserHolder.onBind(data);
                        // Add to container

                        mRelatedProductsView.addView(recommendationsGridTeaserHolder.itemView, mRelatedProductsView.getChildCount() - 1);
                    } catch (Exception ex) {

                    }
                    // Save
                    //mViewHolders.add(holder);
                    recommendationsTeaserHolderAdded = true;

                }
            }


        };


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseActivity());
        String logic = sharedPref.getString("", "RELATED");
        RecommendManager recommendManager = new RecommendManager();
        Item item = recommendManager.getCartItem(mProduct);
        recommendManager.sendRelatedRecommend(item.getRecommendedItem(),
                null,
                item.getItemID(),
                null,
                handler);



        /*recommendManager.sendHomeRecommend(new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(final String category, final List<RecommendedItem> data) {
                LayoutInflater inflater = LayoutInflater.from(getBaseActivity());

                if (recommendationsGridTeaserHolder == null ) {
                    recommendationsGridTeaserHolder = new HomeRecommendationsGridTeaserHolder(getBaseActivity(), inflater.inflate(R.layout.home_teaser_recommendation_grid, mRelatedProductsView, false), null);
                }
                if (recommendationsGridTeaserHolder != null ) {
                    mRelatedProductsView.removeView(recommendationsGridTeaserHolder.itemView);

                    // Set view
                    recommendationsGridTeaserHolder.onBind(data);
                    // Add to container
                    recommendationsGridTeaserHolder = new HomeRecommendationsGridTeaserHolder(getBaseActivity(), inflater.inflate(R.layout.home_teaser_recommendation_grid, mRelatedProductsView, false), null);
                        mRelatedProductsView.addView(recommendationsGridTeaserHolder.itemView, mRelatedProductsView.getChildCount()-1);

                    // Save
                    //mViewHolders.add(holder);
                    recommendationsTeaserHolderAdded = true;

                }

            }
        });*/
    }

    public static void clearSelectedRegionCityId() {
        selectedCityId = null;
        selectedRegionId = null;
    }

    private class RegionCitySpinnerAdapter extends ArrayAdapter {
        public RegionCitySpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Object item = getItem(position);
            Integer value = null;
            if (item instanceof AddressRegion) {
                AddressRegion region = (AddressRegion) item;
                value = region.getValue();
            } else if (item instanceof AddressCity){
                AddressCity city = (AddressCity) item;
                value = city.getValue();
            }
            if (value != null) {
                convertView = super.getView(position, convertView, parent);
                TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
                // change first item's color to gray
                tv.setTextColor(ContextCompat.getColor(tv.getContext(), value == -1 ? R.color.black_700 : R.color.black_47));
            }
            return super.getView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) convertView;
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), isEnabled(position) ? R.color.black_47 : R.color.black_700));
            return super.getDropDownView(position, convertView, parent);
        }

        @Override
        public boolean isEnabled(int position) {
            Object item = getItem(position);
            Integer value = null;
            if (item instanceof AddressRegion) {
                AddressRegion region = (AddressRegion) item;
                value = region.getValue();
            } else if (item instanceof AddressCity){
                AddressCity city = (AddressCity) item;
                value = city.getValue();
            }
            if (value != null) {
                return value != -1;
            }
            return super.isEnabled(position);
        }
    }

}
