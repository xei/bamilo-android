package com.mobile.view.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.mobile.app.JumiaApplication;
import com.mobile.components.ExpandableGridViewComponent;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.BundleItemsListAdapter.OnItemChecked;
import com.mobile.controllers.BundleItemsListAdapter.OnItemSelected;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.ViewGroupFactory;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetProductBundleHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.objects.product.pojo.ProductSpecification;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.imageloader.RocketImageLoader.ImageHolder;
import com.mobile.utils.imageloader.RocketImageLoader.RocketImageLoaderLoadImagesListener;
import com.mobile.utils.pdv.RelatedProductsAdapter;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class displays the product detail screen
 * </p>
 * <p>
 * Its uses the HorizontalListView to display the variations for that product
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2013 Smart Mobile Factory GmbH - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential.
 * </p>
 *
 * @author Michael Kroez
 * @version 1.00
 * @project WhiteLabelRocket
 * @modified Manuel Silva
 * @modified Alexandra Pires
 * @date 4/1/2013
 * @description This class displays the product detail screen
 */
public class ProductDetailsFragment extends BaseFragment implements IResponseCallback, OnItemChecked, OnItemSelected, RocketImageLoaderLoadImagesListener, AdapterView.OnItemClickListener {

    private final static String TAG = ProductDetailsFragment.class.getSimpleName();

    private ProductComplete mCompleteProduct;

    private String mCompleteProductSku;

    private RatingBar mProductRating;

    private TextView mProductRatingCount;

    private ImageView mWishListButton;

    private long mBeginRequestMillis;

    private String mNavigationPath;

    private String mNavigationSource;

    private String mPhone2Call = "";

    private TextView mSpecialPriceText;

    private TextView mPriceText;

    private TextView mDiscountPercentageText;

    private boolean isRelatedItem = false;

    private static String categoryTree = "";

    private ViewGroupFactory mGalleryViewGroupFactory;

    public static final String SELLER_ID = "sellerId";

    private ViewGroup mTableBundles;

    private ViewGroup mProductPicturesContainer;

    private ImageView mMainImage;

    private ViewGroup sellerView;

    private ViewGroup mDescriptionView;   //description section

    private ViewGroup mSpecificationsView;   //specifications section

    private ViewGroup mOtherVariationsLayout; //other variations section

    private ViewGroup mSizeLayout; //size section

    private ViewGroup mRelatedProductsView; //related products

    private ViewGroup mComboProductsLayout; //comboSection

    private RatingBar mProductFashionRating;

    private ViewGroup mTitleContainer;

    private ViewGroup mTitleFashionContainer;

    /**
     * Empty constructor
     */
    public ProductDetailsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.pdv_fragment_main,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /**
     *
     */
    public static ProductDetailsFragment getInstance(Bundle bundle) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
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
            if (arguments.containsKey(ConstantsIntentExtra.CATEGORY_TREE_NAME)) {
                categoryTree = arguments.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME) + ",PDV";
            } else {
                categoryTree = "";
            }
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
        // Title TODO: IMPROVE
        mTitleContainer = (ViewGroup) view.findViewById(R.id.pdv_title_container);
        mTitleFashionContainer = (ViewGroup) view.findViewById(R.id.pdv_title_fashion_container);
        // Wish list
        mWishListButton = (ImageView) view.findViewById(R.id.product_detail_image_is_favourite);
        // Big Image
        mGalleryViewGroupFactory = new ViewGroupFactory((ViewGroup) view.findViewById(R.id.product_image_layout));
        mMainImage = (ImageView) (view.findViewById(R.id.imageGallery)).findViewById(R.id.image_place_holder);
        // Gallery
        mProductPicturesContainer = (ViewGroup) view.findViewById(R.id.productImages);
        // Prices
        mSpecialPriceText = (TextView) view.findViewById(R.id.product_special_price);
        mPriceText = (TextView) view.findViewById(R.id.product_price);
        mDiscountPercentageText = (TextView) view.findViewById(R.id.product_detail_discount_percentage);
        // Rating
        view.findViewById(R.id.RatingSection).setOnClickListener(this);
        mProductRating = (RatingBar) view.findViewById(R.id.product_detail_product_rating);
        mProductFashionRating = (RatingBar) view.findViewById(R.id.product_detail_product_rating_fashion);
        mProductRatingCount = (TextView) view.findViewById(R.id.product_detail_product_rating_count);
        // Specifications
        mSpecificationsView = (ViewGroup) view.findViewById(R.id.SpecificationsSection);
        // Variations
        mOtherVariationsLayout = (ViewGroup) view.findViewById(R.id.OtherVariationsSection);
        mSizeLayout = (ViewGroup) view.findViewById(R.id.SizeSection);
        // TODO: Other Offers
        // Seller
        sellerView = (ViewGroup) view.findViewById(R.id.SellerSection);
        // Product Description
        mDescriptionView = (ViewGroup) view.findViewById(R.id.DescriptionSection);
        // Product Combos
        mTableBundles = (ViewGroup) view.findViewById(R.id.tableBundles);
        TextView mComboTitle = (TextView) view.findViewById(R.id.ComboHeaderSection);
        mComboTitle.setText(R.string.combo);
        mComboProductsLayout = (ViewGroup) view.findViewById(R.id.CombosSection);
        mComboProductsLayout.setVisibility(View.GONE);
        // Related Products
        mRelatedProductsView = (ViewGroup) view.findViewById(R.id.RelatedSection);
        // Bottom Buy Bar
        view.findViewById(R.id.imBtShare).setOnClickListener(this);
        view.findViewById(R.id.imBtCall).setOnClickListener(this);
        view.findViewById(R.id.btBuy).setOnClickListener(this);
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
        // Validate product
        if (mCompleteProduct == null) {
            init();
        } else {
            displayProduct(mCompleteProduct);
        }
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
        Bundle bundle = getArguments();
        // Validate deep link arguments
        if (hasArgumentsFromDeepLink(bundle)) {
            return;
        }
        // Validate url and load product
        if (TextUtils.isEmpty(mCompleteProductSku)) {
            getBaseActivity().onBackPressed();
        } else {
            // Url and parameters
            triggerLoadProduct(mCompleteProductSku);
        }
    }

    private void restoreParams(Bundle bundle) {
        // Get source and path
        mNavigationSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcatalog));
        mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
        // Determine if related items should be shown
        isRelatedItem = bundle.getBoolean(ConstantsIntentExtra.IS_RELATED_ITEM);
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
            mNavigationSource = getString(bundle.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix));
            mNavigationPath = bundle.getString(ConstantsIntentExtra.NAVIGATION_PATH);
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
        mCompleteProduct = product;
        mCompleteProductSku = product.getSku();
        // Tracking
        TrackerDelegator.trackProduct(createBundleProduct());
        // Swt layout
        setTitle();
        setWishListButton();
        setProductImages();
        setProductPriceInfo();
        setRatingInfo();
        setSpecifications();
        setProductSize();
        setProductVariations();
        setSellerInfo();
        setDescription();
        setCombos();
        setRelatedItems();
        // Show container
        showFragmentContentContainer();
    }

    /**
     * Set product price info
     */
    private void setProductPriceInfo() {
        Print.d(TAG, "SHOW PRICE INFO: " + mCompleteProduct.getPrice() + " " + mCompleteProduct.getSpecialPrice());
        if (mCompleteProduct.hasDiscount()) {
            mSpecialPriceText.setText(CurrencyFormatter.formatCurrency(mCompleteProduct.getSpecialPrice()));
            mPriceText.setText(CurrencyFormatter.formatCurrency(mCompleteProduct.getPrice()));
            mPriceText.setPaintFlags(mPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mPriceText.setVisibility(View.VISIBLE);
            String discount = String.format(getString(R.string.format_discount_percentage), mCompleteProduct.getMaxSavingPercentage());
            mDiscountPercentageText.setText(discount);
            mDiscountPercentageText.setVisibility(View.VISIBLE);
        } else {
            mSpecialPriceText.setText(CurrencyFormatter.formatCurrency(mCompleteProduct.getPrice()));
            mPriceText.setVisibility(View.GONE);
            mDiscountPercentageText.setVisibility(View.GONE);
        }
    }

    /**
     * TODO: Use Placeholders
     * Set rating stars and info
     */
    private void setRatingInfo() {
        Integer ratingCount = mCompleteProduct.getTotalRatings();
        Integer reviewsCount = mCompleteProduct.getTotalReviews();

        if (ratingCount == 0 && reviewsCount == 0) {
            mProductRatingCount.setText(getResources().getString(R.string.be_first_rate));    //be the first to rate if hasn't
        } else {
            //changeFashion: rating style is changed if vertical is fashion
            if (mCompleteProduct.isFashion()) {
                mProductFashionRating.setRating((float) mCompleteProduct.getAvgRating());
            } else {
                mProductRating.setRating((float) mCompleteProduct.getAvgRating());
            }
            String rating = getResources().getQuantityString(R.plurals.numberOfRatings, ratingCount, ratingCount);
            mProductRatingCount.setText(rating);

//            String rating = getString(R.string.string_ratings).toLowerCase();
//            if (ratingCount == 1)
//                rating = getString(R.string.string_rating).toLowerCase();
//            String review = getString(R.string.reviews).toLowerCase();
//            if (reviewsCount == 1)
//                review = getString(R.string.review).toLowerCase();
//            mProductRatingCount.setText("( " + String.valueOf(ratingCount) + " " + rating + " / " + String.valueOf(reviewsCount) + " " + review + ")");
//            //    loadingRating.setVisibility(View.GONE);
        }

        mProductRating.setVisibility(View.VISIBLE);
    }

    /**
     * function responsible for showing the seller info
     */
    public void setSellerInfo() {

        if (mCompleteProduct.hasSeller()) {

            sellerView.setVisibility(View.VISIBLE);
            //added: load headers:
            TextView txhTitle = (TextView) (sellerView.findViewById(R.id.pdv_specs_title));//.findViewById(R.id.gen_header_text);
            txhTitle.setText(getResources().getString(R.string.seller_info));

            TextView mSellerName = (TextView) sellerView.findViewById(R.id.txSellerName);
            mSellerName.setText(mCompleteProduct.getSeller().getName());
            String rating = getString(R.string.string_ratings).toLowerCase();
            if (mCompleteProduct.getSeller().getRatingCount() == 1)
                rating = getString(R.string.string_rating).toLowerCase();

            TextView mSellerRatingValue = (TextView) sellerView.findViewById(R.id.seller_rating_bar_rating_count);
            mSellerRatingValue.setText(mCompleteProduct.getSeller().getRatingCount() + " " + rating);

            RatingBar mSellerRating = (RatingBar) sellerView.findViewById(R.id.seller_rating_bar);
            mSellerRating.setRating(mCompleteProduct.getSeller().getRatingValue());
            //
            //    int visibility = View.GONE;
            // TODO placeholder
            if (CollectionUtils.isNotEmpty(mCompleteProduct.getSimples()) &&
                    mCompleteProduct.getSimples().get(0).getMinDeliveryTime() > 0 &&
                    mCompleteProduct.getSimples().get(0).getMaxDeliveryTime() > 0) {
                //
                String min = "" + mCompleteProduct.getSimples().get(0).getMinDeliveryTime();
                String max = "" + mCompleteProduct.getSimples().get(0).getMaxDeliveryTime();

                //get delivery time section and change content
                ViewGroup delliverySection = (ViewGroup) sellerView.findViewById(R.id.deliverSection);
                TextView txDeliverTime = (TextView) delliverySection.findViewById(R.id.txDeliver);
                txDeliverTime.setText(getResources().getString(R.string.delivery_time1) + ":");

                TextView mSellerDeliveryTime = (TextView) delliverySection.findViewById(R.id.txDeliverTimeContent);
                mSellerDeliveryTime.setText(min + " - " + max + " " + getResources().getString(R.string.product_delivery_days));

                delliverySection.setVisibility(View.VISIBLE);

                //    visibility = View.VISIBLE;
            }
            //     mSellerDeliveryContainer.setVisibility(visibility);
        } else {
            if (sellerView != null)
                sellerView.setVisibility(View.GONE);
        }
    }

    /**
     * Change and put the title in the correct position within the layout if it's fashion or not
     */
    private void setTitle() {
        if (mCompleteProduct.isFashion()) {
            mTitleContainer.setVisibility(View.GONE);
            ((TextView) mTitleFashionContainer.findViewById(R.id.pdv_product_title)).setText(mCompleteProduct.getBrand());
            ((TextView) mTitleFashionContainer.findViewById(R.id.pdv_product_subtitle)).setText(mCompleteProduct.getName());
        } else {
            mTitleFashionContainer.setVisibility(View.GONE);
            ((TextView) mTitleContainer.findViewById(R.id.pdv_product_title)).setText(mCompleteProduct.getBrand());
            ((TextView) mTitleContainer.findViewById(R.id.pdv_product_subtitle)).setText(mCompleteProduct.getName());
        }
    }


    /**
     * Get and fills the product description section if has description
     */
    private void setDescription() {
        // Validate description
        String description = mCompleteProduct.getDescription();
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
        // TODO: Move to onClick
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowDescription();
            }
        });
    }

    /**
     *
     */
    private void setWishListButton() {
        mWishListButton.setSelected(mCompleteProduct.isWishList());
    }


    /**
     * Load specifications button if has specifications
     */
    private void setSpecifications() {
        ArrayList<ProductSpecification> arrSpecs = mCompleteProduct.getProductSpecifications();
        // Hide view if is fashion too
        if (mCompleteProduct.isFashion() || CollectionUtils.isEmpty(arrSpecs)) {
            mSpecificationsView.setVisibility(View.GONE);
            return;
        }

        //load content
        HashMap<String, String> mSpecs;
        String specs = "";
        for (int i = 0; i < arrSpecs.size(); i++) {
            mSpecs = arrSpecs.get(i).getSpecifications();
            for (Map.Entry<String, String> pair : mSpecs.entrySet()) {
                specs += "&#149; " + pair.getKey() + " " + pair.getValue() + "<br>";
            }
        }
        // Title
        ((TextView) mSpecificationsView.findViewById(R.id.pdv_specs_title)).setText(getString(R.string.specifications));
        // Multi line
        ((TextView) mSpecificationsView.findViewById(R.id.pdv_specs_multi_line)).setText(Html.fromHtml(specs));
        // Button
        TextView button = (TextView) mSpecificationsView.findViewById(R.id.pdv_specs_button);
        button.setText(getString(R.string.more_specifications));
        // TODO: Move to onClick
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowDescription();
            }
        });
    }

    /**
     * Set the gallery
     */
    private void setProductGallery(ProductComplete completeProduct) {
        // Show loading
        mGalleryViewGroupFactory.setViewVisible(R.id.image_loading_progress);
        // Case product without images
        if (CollectionUtils.isEmpty(completeProduct.getImageList())) {
            mGalleryViewGroupFactory.setViewVisible(R.id.image_place_holder);
        }
        // Case product with images
        else RocketImageLoader.getInstance().loadImages(completeProduct.getImageList(), this);
    }

    /**
     *
     */
    private void setCombos() {
        if (mCompleteProduct.getProductBundle() != null && mCompleteProduct.getProductBundle().getBundleProducts().size() > 0) {
            buildComboSection(mCompleteProduct.getProductBundle());
        } else {
            mComboProductsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Set the variation container
     */
    private void setProductVariations() {
        Print.i(TAG, "ON DISPLAY VARIATIONS");
        if (mCompleteProduct.hasVariations()) {
            // Title
            String title = getResources().getString(mCompleteProduct.isFashion() ? R.string.see_other_colors : R.string.see_other_variations);
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
        if (mCompleteProduct.hasMultiSimpleVariations()) {
            // TODO: API return string like this: 38, 40, 42
            // Simple variation name
            String text = mCompleteProduct.getVariationName() + ": ";
            // Simples without OOS
            int size = mCompleteProduct.getSimples().size();
            for (int i = 0; i < size; i++) {
                ProductSimple simple = mCompleteProduct.getSimples().get(i);
                if (!simple.isOutOfStock()) {
                    text += simple.getVariationValue() + (i != size - 1 ? ", " : "");
                }
            }
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
    private void setProductImages() {
        if (mCompleteProduct == null) {
            Print.i(TAG, "mCompleteProduct is null -- verify and fix!!!");
            return;
        }


        if (mProductPicturesContainer != null && mProductPicturesContainer.getChildCount() == 0) {
            LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ArrayList<String> imagesUrls = mCompleteProduct.getImageList();

            for (int i = 0; i < imagesUrls.size(); i++) {
                ViewGroup layoutImage = (ViewGroup) getImageLoaded(imagesUrls.get(i), inflater);
                mProductPicturesContainer.addView(layoutImage);

            }

            // Show container
            if (imagesUrls.size() > 0) {
                mProductPicturesContainer.setVisibility(View.VISIBLE);
            }

            //fill image gallery
            // Match the cached image list with the current image list order
            ArrayList<String> orderCachedImageList = mCompleteProduct.getImageList();//(ArrayList<String>) CollectionUtils.retainAll(mCompleteProduct.getImageList(), urls);
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
        }

    }


    /**
     * Loads and image and inflates into it's section
     *
     * @param imageUrl - product image's url
     * @param inflater -  LayoutInflater
     */
    private View getImageLoaded(String imageUrl, LayoutInflater inflater) {
        View holder = inflater.inflate(R.layout.pdv_images_item, null);
        ProgressBar prog = (ProgressBar) holder.findViewById(R.id.loading_progress);
        ImageView im = (ImageView) holder.findViewById(R.id.image);

        RocketImageLoader.instance.loadImage(imageUrl, im, prog, R.drawable.no_image_small);
        setOnClick(holder, imageUrl);

        return holder;

    }


    /**
     * Allows to change the main image on clicking in the specific view (that holds the small image)
     *
     * @param url  - product image's url to change to
     * @param view -  View that holds the image
     */
    private void setOnClick(final View view, final String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressBar mProgress = (ProgressBar) v.findViewById(R.id.image_loading_progress);
                RocketImageLoader.instance.loadImage(url, mMainImage, mProgress, R.drawable.no_image_small);

            }
        });
    }

    /**
     * ################# RELATED ITEMS #################
     */

    /**
     * Method used to create the view
     */
    private void setRelatedItems() {
        if (CollectionUtils.isNotEmpty(mCompleteProduct.getRelatedProducts())) {
            ExpandableGridViewComponent relatedGridView = (ExpandableGridViewComponent) mRelatedProductsView.findViewById(R.id.pdv_related_grid_view);
            relatedGridView.setExpanded(true);
            relatedGridView.setAdapter(new RelatedProductsAdapter(getBaseActivity(), R.layout.pdv_related_product_item, mCompleteProduct.getRelatedProducts()));
            relatedGridView.setOnItemClickListener(this);
        } else {
            mRelatedProductsView.setVisibility(View.GONE);
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
    private Bundle createBundleProduct() {
        Bundle bundle = new Bundle();
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

    private void executeAddToShoppingCartCompleted(boolean isBundle) {

        if (!isBundle) {
            getBaseActivity().warningFactory.showWarning(WarningFactory.ADDED_ITEM_TO_CART);
        } else {
            getBaseActivity().warningFactory.showWarning(WarningFactory.ADDED_ITEMS_TO_CART);
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
        if (id == R.id.RatingSection) onClickRating();
            // Case description
        else if (id == R.id.product_detail_specifications || id == R.id.product_detail_name)
            onClickShowDescription();
            // Case variation button
        else if (id == R.id.OtherVariationsSection) onClickVariationButton();
            // Case favourite
        else if (id == R.id.product_detail_image_is_favourite) onClickWishListButton();
            // Case size guide
        else if (id == R.id.dialog_list_size_guide_button) onClickSizeGuide(view);
            // seller link
        else if (id == R.id.seller_name_container) goToSellerCatalog();
            // seller rating
        else if (id == R.id.product_detail_product_seller_rating_container) goToSellerRating();
            // product offers
        else if (id == R.id.offers_container || id == R.id.product_detail_product_offers_container)
            goToProductOffers();
            // Case share
        else if (id == R.id.imBtShare) onClickShare(mCompleteProduct);
            // Case call to order
        else if (id == R.id.imBtCall) onClickCallToOrder();
            // Case buy button
        else if (id == R.id.btBuy) onClickShopProduct();

    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Log.i(TAG, "ON CLICK RETRY BUTTON");
        onResume();
    }

    /**
     * function that sends the user to the product offers view
     */
    private void goToProductOffers() {
        Log.i(TAG, "ON CLICK OFFERS");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_NAME, mCompleteProduct.getName());
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProduct.getSku());
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_OFFERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     *
     */
    private void onClickRating() {
        Log.i(TAG, "ON CLICK RATING");
        JumiaApplication.cleanRatingReviewValues();
        JumiaApplication.cleanSellerReviewValues();
        JumiaApplication.INSTANCE.setFormReviewValues(null);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProduct.getSku());
        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mCompleteProduct);
        bundle.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, true);
        getBaseActivity().onSwitchFragment(FragmentType.POPULARITY, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Show the product description
     */
    private void onClickShowDescription() {
        Log.i(TAG, "ON CLICK TO SHOW DESCRIPTION");
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantsIntentExtra.PRODUCT, mCompleteProduct);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_INFO, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     *
     */
    private void onClickVariationButton() {
        Log.i(TAG, "ON CLICK TO SHOW OTHER VARIATIONS");
        // TODO: Call the new fragment
    }

    /**
     *
     */
    private void onClickShopProduct() {
        Log.i(TAG, "ON CLICK BUY BUTTON");
//        // Get selected simple
//        ProductSimple simple = getSelectedSimple();
//        // Validate simple
//        if (simple == null) {
//            showVariantsDialog();
//            return;
//        }
//        // Validate quantity
//        if (simple.getQuantity() == 0) {
//            Toast.makeText(getBaseActivity(), R.string.product_outof_stock, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        // Validate simple sku
//        String simpleSku = simple.getSku();
//        // Add one unity to cart
//        triggerAddItemToCart(mCompleteProduct.getSku(), simpleSku);
//        // Tracking
//        Bundle bundle = new Bundle();
//        bundle.putString(TrackerDelegator.SKU_KEY, simpleSku);
//        bundle.putDouble(TrackerDelegator.PRICE_KEY, mCompleteProduct.getPriceForTracking());
//        bundle.putString(TrackerDelegator.NAME_KEY, mCompleteProduct.getName());
//        bundle.putString(TrackerDelegator.BRAND_KEY, mCompleteProduct.getBrand());
//        bundle.putDouble(TrackerDelegator.RATING_KEY, mCompleteProduct.getAvgRating());
//        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, mCompleteProduct.getMaxSavingPercentage());
//        bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories());
//        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
//        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
//        TrackerDelegator.trackProductAddedToCart(bundle);
    }

    /**
     *
     */
    private void onClickCallToOrder() {
        Log.i(TAG, "ON CLICK TO CALL");
        TrackerDelegator.trackCall(getBaseActivity());
        // Make a call
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone2Call));
        if (intent.resolveActivity(getBaseActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     *
     */
    private void onClickWishListButton() {
        Log.i(TAG, "ON CLICK WISH LIST BUTTON");
        try {
            // Get item
            if (mCompleteProduct.isWishList()) {
                triggerRemoveFromWishList(mCompleteProduct.getSku());
            } else {
                triggerAddToWishList(mCompleteProduct.getSku());
            }
            // Update value
            mWishListButton.setSelected(!mCompleteProduct.isWishList());
            mCompleteProduct.setIsWishList(!mCompleteProduct.isWishList());
        } catch (NullPointerException e) {
            Log.w(TAG, "NPE ON ADD ITEM TO WISH LIST", e);
        }
    }

    /**
     * Process the click on share.
     */
    private void onClickShare(ProductComplete completeProduct) {
        Log.i(TAG, "ON CLICK TO SHARE ITEM");
        try {
            String extraSubject = getString(R.string.share_subject, getString(R.string.app_name_placeholder));
            String extraMsg = getString(R.string.share_checkout_this_product) + "\n" + mCompleteProduct.getShareUrl();
            Intent shareIntent = getBaseActivity().createShareIntent(extraSubject, extraMsg);
            shareIntent.putExtra(RestConstants.SKU, mCompleteProduct.getSku());
            startActivity(shareIntent);
            // Track share
            TrackerDelegator.trackItemShared(shareIntent, completeProduct.getCategories());
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SHARE");
        }
    }

    /**
     * Process click on size guide.
     *
     * @author sergiopereira
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
            } else
                Print.w(TAG, "WARNING: SIZE GUIDE URL IS EMPTY");
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
        }
    }

    /**
     *
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String sku = (String) view.getTag(R.id.target_sku);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, sku);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * ############## TRIGGERS ##############
     */

    private void triggerLoadProduct(String sku) {
        mBeginRequestMillis = System.currentTimeMillis();
        triggerContentEvent(new GetProductHelper(), GetProductHelper.createBundle(sku), this);
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

    /*
     * ############## RESPONSE ##############
     */

    @Override
    public void onRequestComplete(Bundle bundle) {
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
                break;
            case GET_PRODUCT_DETAIL:
                ProductComplete product = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                // Validate product
                if (product == null || product.getName() == null) {
                    // TODO: Use ToastManager
                    Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                    getBaseActivity().onBackPressed();
                    return;
                }
                // Save product
                mCompleteProduct = product;
                // Show product or update partial
                ProductImageGalleryFragment.sSharedSelectedPosition = 0;
                // Show product or update partial
                displayProduct(mCompleteProduct);
                // Tracking
                Bundle params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gproductdetail);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                TrackerDelegator.trackLoadTiming(params);
                // Tracking
                params = new Bundle();
                params.putParcelable(AdjustTracker.PRODUCT, mCompleteProduct);
                params.putString(AdjustTracker.TREE, categoryTree);
                TrackerDelegator.trackPage(TrackingPage.PRODUCT_DETAIL_LOADED, getLoadTime(), false);
                TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_DETAIL_LOADED, params);

                // Waiting for the fragment communication
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFragmentContentContainer();
                    }
                }, 300);

                // TODO: create a method
                if (mCompleteProduct.hasBundle()) {
                    Bundle arg = new Bundle();
                    arg.putString(GetProductBundleHelper.PRODUCT_SKU, mCompleteProduct.getSku());
                    triggerContentEventNoLoading(new GetProductBundleHelper(), arg, this);
                }
                break;
//            case GET_PRODUCT_BUNDLE:
//                mProductBundle = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
//                if (mProductBundle != null)
//                    displayBundle(mProductBundle);
//                else
//                    hideBundle();
//                break;
            case ADD_PRODUCT_BUNDLE:
                getBaseActivity().updateCartInfo();
//                mBundleButton.setEnabled(true);
//                mAddToCartButton.setEnabled(true);
                executeAddToShoppingCartCompleted(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(Bundle bundle) {
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

        // Generic errors
        if (super.handleErrorEvent(bundle)) {
            //mBundleButton.setEnabled(true);
            return;
        }

        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
            case ADD_PRODUCT_BUNDLE:
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                //mBundleButton.setEnabled(true);
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
            case GET_PRODUCT_DETAIL:
                showContinueShopping();
            default:
                break;
        }
    }


    //added apires: build combo section if has bundles

    /**
     * Build combo section if has bundles
     **/
    private void buildComboSection(BundleList bundleList) {
        if (bundleList == null) {
            if (mComboProductsLayout != null) {
                mComboProductsLayout.setVisibility(View.GONE);
                return;
            }
        }

        //load header
        TextView comboHeaderTitle = (TextView) mComboProductsLayout.findViewById(R.id.ComboHeaderSection);
        //TextView comboHeaderTitle = (TextView) mComboProductsLayout.findViewById(R.id.gen_header_text);
        //changeFashion: change title if is fashion
        String titleCombo = getResources().getString(R.string.combo);
        if (mCompleteProduct.isFashion())
            titleCombo = getResources().getString(R.string.buy_the_look);

        comboHeaderTitle.setText(titleCombo);

        ArrayList<ProductBundle> bundleProducts = bundleList.getBundleProducts();
        LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int count = 0;

        for (ProductBundle item : bundleProducts) {

            ViewGroup comboProductItem = (ViewGroup) inflater.inflate(R.layout.pdp_product_item_bundle, null);
            FillProductBundleInfo(comboProductItem, item);
            mTableBundles.addView(comboProductItem);

            if (count < bundleProducts.size() - 1)   //add plus separator
            {
                //separator
                ViewGroup imSep = (ViewGroup) inflater.inflate(R.layout.pdp_plus_bundle, null);
                mTableBundles.addView(imSep);
            }

            count++;

        }

        mTableBundles.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens bundle page here
            }
        });
        mComboProductsLayout.setVisibility(View.VISIBLE);


    }


    /**
     * Fill a product bundle info
     *
     * @param view - combo item view
     * @param p    - product bundle
     */
    private void FillProductBundleInfo(View view, ProductBundle p) {
        ImageView mImage = (ImageView) view.findViewById(R.id.image_view);
        ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
        CheckBox mCheck = (CheckBox) view.findViewById(R.id.item_check);
        mCheck.setChecked(p.isChecked());

        RocketImageLoader.instance.loadImage(p.getImageUrl(), mImage, mProgress, R.drawable.no_image_large);

        com.mobile.components.customfontviews.TextView mBrand = (com.mobile.components.customfontviews.TextView) view.findViewById(R.id.item_brand);
        mBrand.setText(p.getBrand());
        com.mobile.components.customfontviews.TextView mTitle = (com.mobile.components.customfontviews.TextView) view.findViewById(R.id.item_title);
        mTitle.setText(p.getName());

        com.mobile.components.customfontviews.TextView mPrice = (com.mobile.components.customfontviews.TextView) view.findViewById(R.id.item_price);
        mPrice.setText(CurrencyFormatter.formatCurrency(p.getPrice()));
    }


    /**
     * function responsible for handling the item click of the bundle
     */
    @Override
    public void SelectedItem() {
        Print.d("BUNDLE", "GO TO PDV");
    }

    /**
     * function responsible for handling on item of the bundle
     */
    @Override
    public void checkItem(ProductBundle selectedProduct, boolean isChecked, int pos) {
//        // if isChecked is false then item was deselected
//        double priceChange = selectedProduct.getSpecialPrice();
//        if (priceChange == 0) {
//            priceChange = selectedProduct.getPrice();
//        }
//        double totalPrice = (Double) mBundleTextTotal.getTag();
//
//        if (!isChecked) {
//            totalPrice = totalPrice - priceChange;
//            // CurrencyFormatter.formatCurrency(String.valueOf(totalPrice));
//            mBundleTextTotal.setTag(totalPrice);
//            mBundleTextTotal.setText(CurrencyFormatter.formatCurrency(String.valueOf(totalPrice)));
//            if (mProductBundle != null)
//                mProductBundle.getBundleProducts().get(pos).setChecked(false);
//
//            validateBundleButton();
//
//        } else {
//            totalPrice = totalPrice + priceChange;
//            // CurrencyFormatter.formatCurrency(String.valueOf(totalPrice));
//            mBundleTextTotal.setTag(totalPrice);
//            mBundleTextTotal.setText(CurrencyFormatter.formatCurrency(String.valueOf(totalPrice)));
//            if (mProductBundle != null)
//                mProductBundle.getBundleProducts().get(pos).setChecked(true);
//
//            mBundleButton.setEnabled(true);
//        }

    }


    /**
     * function responsible for calling the catalog with the products from a specific seller
     */
    private void goToSellerCatalog() {
        Print.d("SELLER", "GO TO CATALOG");
        if (mCompleteProduct.hasSeller()) {
            Bundle bundle = new Bundle();
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

        Bundle bundle = new Bundle();
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
        for (ImageHolder imageHolder : successUrls) {
            urls.add(imageHolder.url);
        }

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
