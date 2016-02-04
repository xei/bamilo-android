package com.mobile.utils.catalog;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.FeaturedItemsAdapter;
import com.mobile.newFramework.objects.catalog.FeaturedBox;
import com.mobile.newFramework.objects.catalog.FeaturedItem;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show the featured box.
 * @author sergiopereira
 *
 */
public class FeaturedBoxHelper {
    
    private static final String TAG = FeaturedBoxHelper.class.getSimpleName();
    
    private static final int ITEMS_PER_PAGE_PORTRAIT = 3;
    
    private static final int ITEMS_PER_PAGE_LANDSCAPE = 5;

    /**
     * Show the featured response.
     * @return true or false - Case NPE returns false
     * @author sergiopereira 
     */
    public static boolean show(BaseFragment fragment, FeaturedBox featuredBox) {
        Log.i(TAG, "ON ERROR SEARCH RESULT");
        try {
            // define how many items will be displayed on the viewPager
            int partialSize = DeviceInfoHelper.isTabletInLandscape(fragment.getBaseActivity()) ? ITEMS_PER_PAGE_LANDSCAPE : ITEMS_PER_PAGE_PORTRAIT;
            // Get view
            View view = fragment.getView();
            // Error message
            showErrorMessage(view, featuredBox);
            // Search tips
            showTips(view, featuredBox);
            // Featured product box
            showFeaturedProductBox(fragment.getBaseActivity(), view, featuredBox, partialSize);
            // Featured brand box
            showFeaturedBrandBox(fragment.getBaseActivity(), view, featuredBox, partialSize);
            // Notice message
            showNoticeMessage(view, featuredBox);
            // Success
            return true;
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SHOW FEATURED BOX", e);
            return false;
        }
    }
    
    /**
     * Show the error message.
     */
    private static void showErrorMessage(View view, FeaturedBox featuredBox) {
        String message = featuredBox.getErrorMessage();
        if (!TextUtils.isEmpty(message)) {
            TextView textViewErrorMessage = (TextView) view.findViewById(R.id.no_results_search_error_message);
            textViewErrorMessage.setText(message);
        }
    }
    
    /**
     * Shoe the tips.
     */
    private static void showTips(View view, FeaturedBox featuredBox) {
        String searchTips = featuredBox.getSearchTips();
        if (!TextUtils.isEmpty(searchTips)) {
            view.findViewById(R.id.no_results_search_tips_layout).setVisibility(View.VISIBLE);
            TextView textViewSearchTips = (TextView) view.findViewById(R.id.no_results_search_tips_text);                
            textViewSearchTips.setVisibility(View.VISIBLE);
            textViewSearchTips.setText(searchTips);
        }
    }

    /**
     * Show view pager with products.
     */
    private static void showFeaturedProductBox(Context context, View view, FeaturedBox featuredBox, int partialSize) {
        // Feature box products: title
        String productsTitle = featuredBox.getProductsTitle();
        if (!TextUtils.isEmpty(productsTitle)) {
            ((TextView) view.findViewById(R.id.featured_products_title)).setText(productsTitle);
        }
        // Feature box products: view pager
        ArrayList<FeaturedItem> featureBoxProducts = featuredBox.getProducts();
        if (CollectionUtils.isNotEmpty(featureBoxProducts)) {
            view.findViewById(R.id.featured_products).setVisibility(View.VISIBLE);
            generateFeaturedProductsLayout(context, view, featureBoxProducts, partialSize);
        }
    }

    /**
     * Show view pager with brands.
     */
    private static void showFeaturedBrandBox(Context context, View view, FeaturedBox featuredBox, int partialSize) {
        // Feature box brands: title
        String brandsTitle = featuredBox.getBrandsTitle();
        if (!TextUtils.isEmpty(brandsTitle)) {
            ((TextView) view.findViewById(R.id.featured_brands_title)).setText(brandsTitle);
        }
        // Feature box brands: view pager
        ArrayList<FeaturedItem> featureBoxBrands = featuredBox.getBrands();
        if (CollectionUtils.isNotEmpty(featureBoxBrands)) {
            view.findViewById(R.id.featured_brands).setVisibility(View.VISIBLE);
            generateFeaturedBrandsLayout(context, view, featureBoxBrands, partialSize);
        }
    }
    
    /**
     * Show the notice message
     */
    private static void showNoticeMessage(View view, FeaturedBox featuredBox) {
        String noticeMessage = featuredBox.getNoticeMessage();
        if (!TextUtils.isEmpty(noticeMessage)) {
            ((TextView) view.findViewById(R.id.no_results_search_notice_message)).setText(noticeMessage);
        }
    }
    
    /**
     * Fill adapter with featured products
     */
    private static void generateFeaturedProductsLayout(Context context, View view, ArrayList<FeaturedItem> featuredProducts, int partialSize) {
        View mLoadingFeaturedProducts = view.findViewById(R.id.loading_featured_products);
        ViewPager mFeaturedProductsViewPager = (ViewPager) view.findViewById(R.id.featured_products_viewpager);
        // try to use portrait layout if there are less products than what the default layout would present
        if (featuredProducts.size() < partialSize) partialSize = ITEMS_PER_PAGE_PORTRAIT;
        // 
        FeaturedItemsAdapter mFeaturedProductsAdapter = new FeaturedItemsAdapter(context, featuredProducts, LayoutInflater.from(context), partialSize);
        mFeaturedProductsViewPager.setAdapter(mFeaturedProductsAdapter);
        mFeaturedProductsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedProducts.setVisibility(View.GONE);
    }
    
    /**
     * Fill adapter with featured brands
     */
    private static void generateFeaturedBrandsLayout(Context context, View view, ArrayList<FeaturedItem> featuredBrandsList, int partialSize) {
        View mLoadingFeaturedBrands = view.findViewById(R.id.loading_featured_brands);
        ViewPager mFeaturedBrandsViewPager = (ViewPager) view.findViewById(R.id.featured_brands_viewpager);
        // try to use portrait layout if there are less brands than what the default layout would present
        if (featuredBrandsList.size() < partialSize) partialSize = 3;
        // 
        FeaturedItemsAdapter mFeaturedBrandsAdapter = new FeaturedItemsAdapter(context, featuredBrandsList, LayoutInflater.from(context), partialSize);
        mFeaturedBrandsViewPager.setAdapter(mFeaturedBrandsAdapter);
        mFeaturedBrandsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedBrands.setVisibility(View.GONE);
    }

}
