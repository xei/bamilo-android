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
        if(next == FragmentType.PRODUCT_DETAILS) {
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            /*if(TextUtils.isNotEmpty(mRichRelevanceHash)){
                bundle.putString(ConstantsIntentExtra.RICH_RELEVANCE_HASH, mRichRelevanceHash );
            }*/
        }
        else if(next == FragmentType.CATALOG) {
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
