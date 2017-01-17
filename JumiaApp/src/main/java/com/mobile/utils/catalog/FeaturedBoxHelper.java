package com.mobile.utils.catalog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mobile.components._unused_.scrollable.ExpandedListView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.FeaturedItemsAdapter;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.objects.catalog.FeaturedBox;
import com.mobile.newFramework.objects.catalog.FeaturedItem;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.search.NoResSearchAdapter;
import com.mobile.utils.search.SearchModel;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

import static android.R.attr.width;
import static android.support.design.R.attr.height;

/**
 * Class used to show the featured box.
 * @author sergiopereira
 *
 */
public class FeaturedBoxHelper {
    
    private static final String TAG = FeaturedBoxHelper.class.getSimpleName();
    
    private static final int ITEMS_PER_PAGE_PORTRAIT = 3;
    
    private static final int ITEMS_PER_PAGE_LANDSCAPE = 5;


    private String mRichRelevanceHash ="";

    static BaseFragment baseFragment;
    /**
     * Show the featured response.
     * @return true or false - Case NPE returns false
     * @author sergiopereira 
     */
    public static boolean show(BaseFragment fragment, FeaturedBox featuredBox) {
        Log.i(TAG, "ON ERROR SEARCH RESULT");
        baseFragment = fragment;
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

            NoResSearchAdapter adapter = new NoResSearchAdapter(baseFragment.getContext(), searchGenerateData());
            ExpandableHeightListView searchListView = (ExpandableHeightListView) view.findViewById(R.id.search_listview);
            //ExpandedListView searchListView = (ExpandedListView) view.findViewById(R.id.search_listview);
            searchListView.setAdapter(adapter);
            searchListView.setExpanded(true);
            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 1 :
                            Toast.makeText(view.getContext(),"1",Toast.LENGTH_LONG).show();
                            break;
                        case 2 :
                            Toast.makeText(view.getContext(),"2",Toast.LENGTH_LONG).show();
                            break;
                        case 3 :Toast.makeText(view.getContext(),"3",Toast.LENGTH_LONG).show();
                            break;
                        case 4 :Toast.makeText(view.getContext(),"4",Toast.LENGTH_LONG).show();
                            break;
                        case 5 :Toast.makeText(view.getContext(),"5",Toast.LENGTH_LONG).show();
                            break;
                        case 6 :Toast.makeText(view.getContext(),"6",Toast.LENGTH_LONG).show();
                            break;

                    }
                }
            });
            searchListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            return true;
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SHOW FEATURED BOX", e);
            return false;
        }
    }


       private static ArrayList<SearchModel> searchGenerateData(){
        ArrayList<SearchModel> models = new ArrayList<SearchModel>();
        models.add(new SearchModel("مجموعه های منتخب"));
        models.add(new SearchModel(R.drawable.search_hp,"صفحه اصلی"));
        models.add(new SearchModel(R.drawable.search_fashion,"مد و لباس"));
        models.add(new SearchModel(R.drawable.search_health_and_beauty,"زیبایی و سلامت"));
           models.add(new SearchModel(R.drawable.search_mobile_tablet,"موبایل و تبلت"));
           models.add(new SearchModel(R.drawable.searh_elect_acc,"لوازم جانبی الکترونیکی"));
           models.add(new SearchModel(R.drawable.search_home_life_style,"خانه و سبک زندگی"));

        return models;
    }


    /**
     * Show the error message.
     */
    private static void showErrorMessage(View view, FeaturedBox featuredBox) {
        String message = featuredBox.getErrorMessage();
        String guide = "از صحت نگارش عبارت مورد نظر خود اطمینان حاصل نموده و مجدد جستجو نمایید";
        if (!TextUtils.isEmpty(message)) {
            TextView textViewErrorMessage = (TextView) view.findViewById(R.id.no_results_search_error_message);
            TextView textViewErrorMessageGuide = (TextView) view.findViewById(R.id.no_results_search_error_message_guide);

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

    private static void onClickTeaserItem(String Title, String Link, TeaserGroupType mGroupType) {
        Print.i(TAG, "ON CLICK TEASER ITEM");
        // Get title
        String title = Title;
        // Get target link
        @TargetLink.Type String link = Link;

        // Get teaser group type
        TeaserGroupType origin = mGroupType;


        // Parse target link
        new TargetLink(baseFragment.getWeakBaseActivity(), link)
                .addTitle(title)
                .setOrigin(origin)
                .addAppendListener(new TargetLink.OnAppendDataListener() {
                    @Override
                    public void onAppendData(FragmentType next, String title, String id, Bundle data) {

                    }
                })
                .addCampaignListener(new TargetLink.OnCampaignListener() {
                    @NonNull
                    @Override
                    public Bundle onTargetCampaign(String title, String id, TeaserGroupType mOrigin) {
                        return null;
                    }
                })
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
          /*  ((TextView) view.findViewById(R.id.no_results_search_notice_message)).setText(noticeMessage);*/
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

class ExpandableHeightListView extends ListView
{

    boolean expanded = false;

    public ExpandableHeightListView(Context context)
    {
        super(context);
    }

    public ExpandableHeightListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ExpandableHeightListView(Context context, AttributeSet attrs,
                                    int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // HACK! TAKE THAT ANDROID!
        if (isExpanded())
        {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}