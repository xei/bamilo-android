package com.bamilo.android.appmodule.bamiloapp.utils.catalog;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.controllers.FeaturedItemsAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendManager;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.RecommendationsHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.search.SearchModel;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;
import android.widget.TextView;
import com.bamilo.android.framework.service.objects.catalog.FeaturedBox;
import com.bamilo.android.framework.service.objects.catalog.FeaturedItem;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.output.Print;
import com.emarsys.predict.RecommendedItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Class used to show the featured box.
 *
 * @author sergiopereira
 */
public class FeaturedBoxHelper {

    private static final String TAG = FeaturedBoxHelper.class.getSimpleName();

    private static final int ITEMS_PER_PAGE_PORTRAIT = 3;

    private static final int ITEMS_PER_PAGE_LANDSCAPE = 5;
    static RecommendationsHolder recommendationsTeaserHolder;
    static LinearLayout RecommendationResult;
    private static boolean recommendationsTeaserHolderAdded = false;

    private String mRichRelevanceHash = "";

    static BaseFragment baseFragment;

    /**
     * Show the featured response.
     *
     * @return true or false - Case NPE returns false
     * @author sergiopereira
     */
    public static boolean show(BaseFragment fragment, FeaturedBox featuredBox) {
        baseFragment = fragment;
        try {
            // define how many items will be displayed on the viewPager
            int partialSize = DeviceInfoHelper.isTabletInLandscape(fragment.getBaseActivity())
                    ? ITEMS_PER_PAGE_LANDSCAPE : ITEMS_PER_PAGE_PORTRAIT;
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

            RecommendationResult = view.findViewById(R.id.recommendation_result);

            sendRecommend(fragment);

            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private static void sendRecommend(final BaseFragment fragment) {
        RecommendManager recommendManager = new RecommendManager();
        recommendManager.sendNoResultRecommend(BamiloApplication.INSTANCE.getSearchedTerm(), 15,
                new RecommendListCompletionHandler() {
                    @Override
                    public void onRecommendedRequestComplete(final String category,
                                                             final List<RecommendedItem> data) {
                        if (data == null || data.size() == 0) {
                            //mRelatedProductsView.removeView(recommendationsHolder.itemView);
                            // recommendations.setVisibility(View.GONE);
                            return;
                        }

                        LayoutInflater inflater = LayoutInflater.from(fragment.getBaseActivity());

                        if (recommendationsTeaserHolder == null) {
                            recommendationsTeaserHolder = new RecommendationsHolder(
                                    fragment.getBaseActivity(),
                                    inflater.inflate(R.layout.recommendation, RecommendationResult,
                                            false), null);
                        }
                        if (recommendationsTeaserHolder != null) {

                            RecommendationResult.removeView(recommendationsTeaserHolder.itemView);
                            recommendationsTeaserHolder = new RecommendationsHolder(
                                    fragment.getBaseActivity(),
                                    inflater.inflate(R.layout.recommendation, RecommendationResult,
                                            false), null);

                            recommendationsTeaserHolder.onBind(data);
                            // Add to container
                            RecommendationResult.addView(recommendationsTeaserHolder.itemView,
                                    RecommendationResult.getChildCount() - 1);
                            recommendationsTeaserHolderAdded = true;
                        }
                    }
                });
    }

    private static void onClickBackToHome() {
        // Get user id
        String userId = "";
        if (BamiloApplication.CUSTOMER != null
                && BamiloApplication.CUSTOMER.getIdAsString() != null) {
        }
        // Goto home
        baseFragment.getBaseActivity()
                .onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
                        FragmentController.ADD_TO_BACK_STACK);
    }

    private static ArrayList<SearchModel> searchGenerateData() {
        ArrayList<SearchModel> models = new ArrayList<>();
        models.add(new SearchModel("مجموعه های منتخب"));
        models.add(new SearchModel(R.drawable.search_hp, "صفحه اصلی"));
        models.add(new SearchModel(R.drawable.search_fashion, "مد و لباس"));
        models.add(new SearchModel(R.drawable.search_health_and_beauty, "زیبایی و سلامت"));
        models.add(new SearchModel(R.drawable.search_mobile_tablet, "موبایل و تبلت"));
        models.add(new SearchModel(R.drawable.searh_elect_acc, "لوازم جانبی الکترونیکی"));
        models.add(new SearchModel(R.drawable.search_home_life_style, "خانه و سبک زندگی"));

        return models;
    }


    /**
     * Show the error message.
     */
    private static void showErrorMessage(View view, FeaturedBox featuredBox) {
        String message = featuredBox.getErrorMessage();
        String guide = "از صحت نگارش عبارت مورد نظر خود اطمینان حاصل نموده و مجدد جستجو نمایید";
        if (!TextUtils.isEmpty(message)) {
            TextView textViewErrorMessage = view
                    .findViewById(R.id.no_results_search_error_message);
            TextView textViewErrorMessageGuide = view
                    .findViewById(R.id.no_results_search_error_message_guide);

            textViewErrorMessage.setText(message);
            textViewErrorMessageGuide.setText(guide);
        }
    }

    /**
     * Shoe the tips.
     */
    private static void showTips(View view, FeaturedBox featuredBox) {
        String searchTips = featuredBox.getSearchTips();
        if (!TextUtils.isEmpty(searchTips)) {
            view.findViewById(R.id.no_results_search_tips_layout).setVisibility(View.VISIBLE);
        }
    }

    private static void onClickNoSearchItem(String Title, String Link, TeaserGroupType mGroupType) {
        Print.i(TAG, "ON CLICK TEASER ITEM");
        // Get title
        // Get target link
        @TargetLink.Type String link = Link;

        // Get teaser group type

        // Parse target link
        new TargetLink(baseFragment.getWeakBaseActivity(), link)
                .addTitle(Title)
                .setOrigin(mGroupType)
                .retainBackStackEntries()
                .enableWarningErrorMessage()
                .run();
    }


    private static boolean processDeepLink(String link, TeaserGroupType mGroupType) {
        // Parse target link
        return new TargetLink(baseFragment.getWeakBaseActivity(), link)
                .addTitle("test")
                .setOrigin(mGroupType)
                .retainBackStackEntries()
                .run();
    }


    /**
     * Show view pager with products.
     */
    private static void showFeaturedProductBox(Context context, View view, FeaturedBox featuredBox,
                                               int partialSize) {
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
    private static void showFeaturedBrandBox(Context context, View view, FeaturedBox featuredBox,
                                             int partialSize) {
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
            /*  ((TextView) view.findViewById(R.id.no_results_search_notice_message)).setText(noticeMessage);*/
        }
    }

    /**
     * Fill adapter with featured products
     */
    private static void generateFeaturedProductsLayout(Context context, View view,
                                                       ArrayList<FeaturedItem> featuredProducts, int partialSize) {
        View mLoadingFeaturedProducts = view.findViewById(R.id.loading_featured_products);
        ViewPager mFeaturedProductsViewPager = view
                .findViewById(R.id.featured_products_viewpager);
        // try to use portrait layout if there are less products than what the default layout would present
        if (featuredProducts.size() < partialSize) {
            partialSize = ITEMS_PER_PAGE_PORTRAIT;
        }
        // 
        FeaturedItemsAdapter mFeaturedProductsAdapter = new FeaturedItemsAdapter(context,
                featuredProducts, LayoutInflater.from(context), partialSize);
        mFeaturedProductsViewPager.setAdapter(mFeaturedProductsAdapter);
        mFeaturedProductsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedProducts.setVisibility(View.GONE);
    }

    /**
     * Fill adapter with featured brands
     */
    private static void generateFeaturedBrandsLayout(Context context, View view,
                                                     ArrayList<FeaturedItem> featuredBrandsList, int partialSize) {
        View mLoadingFeaturedBrands = view.findViewById(R.id.loading_featured_brands);
        ViewPager mFeaturedBrandsViewPager = view
                .findViewById(R.id.featured_brands_viewpager);
        // try to use portrait layout if there are less brands than what the default layout would present
        if (featuredBrandsList.size() < partialSize) {
            partialSize = 3;
        }
        // 
        FeaturedItemsAdapter mFeaturedBrandsAdapter = new FeaturedItemsAdapter(context,
                featuredBrandsList, LayoutInflater.from(context), partialSize);
        mFeaturedBrandsViewPager.setAdapter(mFeaturedBrandsAdapter);
        mFeaturedBrandsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedBrands.setVisibility(View.GONE);
    }


}
