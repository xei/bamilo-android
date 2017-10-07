package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.database.CategoriesTableHelper;
import com.mobile.service.objects.home.type.TeaserGroupType;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.view.R;
import com.mobile.view.components.CategoriesCarouselComponent;
import com.mobile.view.components.SliderComponent;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements SliderComponent.OnSlideClickListener, TargetLink.OnAppendDataListener, TargetLink.OnCampaignListener {

    private NestedScrollView mRootScrollView;
    private LinearLayout mContainerLinearLayout;

    public HomeFragment() {
        super(true, R.layout.home_fragment_main);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootScrollView = (NestedScrollView) view.findViewById(R.id.home_page_scroll);
        mRootScrollView.setClipToPadding(false);
        mContainerLinearLayout = (LinearLayout) view.findViewById(R.id.home_page_container);
        getBaseActivity().enableSearchBar(true, mRootScrollView);

        List<SliderComponent.Item> items = new ArrayList<>();
        items.add(new SliderComponent.Item("http://zpvliimg.bamilo.com/cms/info_app/hp_slider/6409_home-appliences_v1.jpg",
                "campaign::selected-sale"));
        items.add(new SliderComponent.Item("http://zpvliimg.bamilo.com/cms/info_app/hp_slider/6404_sportshoes_v1.jpg",
                "shop_in_shop::sports-promotion"));
        items.add(new SliderComponent.Item("http://zpvliimg.bamilo.com/cms/info_app/hp_slider/6400_car-acc_v1.jpg",
                "catalog::VXp2bWp4SmtqYks4L0FNR0ljQ1hRdz09"));
        items.add(new SliderComponent.Item("http://zpvliimg.bamilo.com/cms/info_app/hp_slider/6403_BASTYLE_v1.jpg",
                "catalog::cUQ4WUo5aHFjYWFPOThzZUFLcWgxdUhBdWdHUG05V3lJOExqVlRpMVUrTT0="));
        addSliderComponent(mContainerLinearLayout, items);

        List<CategoriesCarouselComponent.CategoryItem> categoryItems = new ArrayList<>();
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("خانه و سبک زندگی", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_home.png", "catalog_category::home_furniture_lifestyle"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("مد و لباس مردانه", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_fam.png", "catalog_category::fashion_for_men"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("مد و لباس زنانه ", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_faw.png", "catalog_category::fashion_for_women"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("زیبایی و سلامت", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_perfume.png", "catalog_category::health_beauty_personal_care"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("سرگرمی، نوزاد", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_kid.png", "catalog_category::children"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("لوازم جانبی الکترونیکی", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_ea.png", "catalog_category::electronic_accessories"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("فرهنگ و هنر", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_book.png", "catalog_category::books_digitalconent_education"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("ورزش", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_sport.png", "catalog_category::sports-outdoors"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("لوازم برقی", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_homeapp.png", "catalog_category::home_kitchen_appliance"));
        categoryItems.add(new CategoriesCarouselComponent.CategoryItem("ابزار آلات", "http://zpvliimg.bamilo.com/cms/info_app/featured_stores/o_tools.png", "catalog_category::tools-parent"));
        addCategoriesCarouselComponent(mContainerLinearLayout, categoryItems);
    }

    private void addCategoriesCarouselComponent(ViewGroup container, List<CategoriesCarouselComponent.CategoryItem> categoryItems) {
        CategoriesCarouselComponent categoriesCarouselComponent = new CategoriesCarouselComponent(categoryItems);
        container.addView(categoriesCarouselComponent.getView(getContext()));
    }

    private void addSliderComponent(ViewGroup container, List<SliderComponent.Item> items) {
        SliderComponent component = new SliderComponent(items);
        container.addView(component.getView(getContext()));
        component.setOnSlideClickListener(this);
    }

    @Override
    public void onSlideClicked(View v, int position, SliderComponent.Item item) {
        new TargetLink(getWeakBaseActivity(), item.targetLink)
                .addTitle(null)
                .setOrigin(TeaserGroupType.MAIN_TEASERS)
                .addAppendListener(this)
                .addCampaignListener(this)
                .retainBackStackEntries()
                .enableWarningErrorMessage()
                .run();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mRootScrollView != null) {
                getBaseActivity().syncSearchBarState(mRootScrollView.getScrollY());
            }
        }
    }

    /**
     * Append some data
     */
    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle bundle) {
        if (next == FragmentType.PRODUCT_DETAILS) {
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            /*if(TextUtils.isNotEmpty(mRichRelevanceHash)){
                bundle.putString(ConstantsIntentExtra.RICH_RELEVANCE_HASH, mRichRelevanceHash );
            }*/
        } else if (next == FragmentType.CATALOG) {
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
            CategoriesTableHelper.updateCategoryCounter(id, title);
        }
    }

    @NonNull
    @Override
    public Bundle onTargetCampaign(String title, String id, TeaserGroupType mOrigin) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mOrigin);
//        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, createCampaignsData(title, id, mOrigin));
        return bundle;
    }
}
