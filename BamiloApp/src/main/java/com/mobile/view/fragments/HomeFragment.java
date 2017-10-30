package com.mobile.view.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.teasers.GetHomeHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.objects.home.HomePageComponents;
import com.mobile.service.objects.home.TeaserCampaign;
import com.mobile.service.objects.home.model.BaseComponent;
import com.mobile.service.objects.home.type.TeaserGroupType;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.ColorSequenceHolder;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.view.R;
import com.mobile.view.components.BaseViewComponent;
import com.mobile.view.components.CategoriesCarouselViewComponent;
import com.mobile.view.components.DailyDealViewComponent;
import com.mobile.view.components.SliderViewComponent;
import com.mobile.view.components.TileViewComponent;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements SliderViewComponent.OnSlideClickListener, TargetLink.OnCampaignListener, TileViewComponent.OnTileClickListener, DailyDealViewComponent.OnCountDownDealItemClickListener, CategoriesCarouselViewComponent.OnCarouselItemClickListener, IResponseCallback {

    private NestedScrollView mRootScrollView;
    private LinearLayout mContainerLinearLayout;
    private SwipeRefreshLayout srlHomeRoot;
    private ColorSequenceHolder colorSequenceHolder;
    private HomePageComponents mHomePageComponents;
    private List<DailyDealViewComponent> dailyDealViewComponents;
    private boolean isFragmentVisibleToUser = false;

    public HomeFragment() {
        super(true, R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootScrollView = (NestedScrollView) view.findViewById(R.id.nsvHomeContainer);
        mRootScrollView.setClipToPadding(false);
        mRootScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    getBaseActivity().syncSearchBarState(mRootScrollView.getScrollY());
                }
            }
        });
        mContainerLinearLayout = (LinearLayout) view.findViewById(R.id.llHomeContainer);
        getBaseActivity().enableSearchBar(true, mRootScrollView);
        mRootScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isFragmentVisibleToUser) {
                    getBaseActivity().onSearchBarScrolled(scrollY - oldScrollY);
                }
            }
        });
        srlHomeRoot = (SwipeRefreshLayout) view.findViewById(R.id.srlHomeRoot);
        srlHomeRoot.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHomePage();
            }
        });
        srlHomeRoot.setColorSchemeResources(R.color.appBar);
        srlHomeRoot.setProgressViewOffset(false, mRootScrollView.getPaddingTop(), srlHomeRoot.getProgressViewEndOffset());


        initColorSeqHolder();
        if (mHomePageComponents == null ||
                mHomePageComponents.getComponents() == null) {
            loadHomePage();
        } else {
            showComponents(mHomePageComponents);
        }
    }

    @Override
    public void onPause() {
        if (dailyDealViewComponents != null) {
            for (DailyDealViewComponent component : dailyDealViewComponents) {
                component.pause();
            }
        }
        super.onPause();
    }

    private void loadHomePage() {
        srlHomeRoot.setRefreshing(true);
        triggerContentEventNoLoading(new GetHomeHelper(), null, this);
    }

    private void initColorSeqHolder() {
        TypedArray colorsTypedArray = getResources().obtainTypedArray(R.array.tileColorSequence);
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < colorsTypedArray.length(); i++) {
            colors.add(colorsTypedArray.getColor(i, 0));
        }
        colorsTypedArray.recycle();
        colorSequenceHolder = new ColorSequenceHolder(colors);
    }

    @Override
    public void onSlideClicked(View v, int position, SliderViewComponent.Item item) {
        fireTargetLink(null, item.targetLink);
    }

    private void fireTargetLink(String title, String targetLink) {
        new TargetLink(getWeakBaseActivity(), targetLink)
                .addTitle(title)
                .addCampaignListener(this)
                .retainBackStackEntries()
                .enableWarningErrorMessage()
                .run();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (mRootScrollView != null) {
                getBaseActivity().syncSearchBarState(mRootScrollView.getScrollY());
            }
        }
    }

    @NonNull
    @Override
    public Bundle onTargetCampaign(String title, String id, TeaserGroupType mOrigin) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mOrigin);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, createCampaignsData(title, id, mOrigin));
        return bundle;
    }

    @NonNull
    private ArrayList<TeaserCampaign> createCampaignsData(@NonNull String title, @NonNull String id, TeaserGroupType group) {
        ArrayList<TeaserCampaign> campaigns;
        campaigns = TargetLink.createCampaignList(title, id);
        return campaigns;
    }

    @Override
    public void onTileClicked(View v, TileViewComponent.TileItem item) {
        fireTargetLink(null, item.targetLink);
    }

    @Override
    public void onMoreButtonClicked(View v, String targetLink) {
        fireTargetLink(null, targetLink);
    }

    @Override
    public void onProductItemClicked(View v, DailyDealViewComponent.Product product) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, product.sku);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, String.format("%s %s", product.brand, product.name));
        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
        // Goto PDV
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    public void showComponents(HomePageComponents homePageComponents) {
        List<BaseComponent> components = homePageComponents.getComponents();
        int tileComponentCount = 1,
                sliderComponentCount = 1,
                carouselComponentCount = 1,
                dealComponentCount = 1;
        String pageName = "HomePage";
        if (components != null) {
            mContainerLinearLayout.removeAllViews();
            for (BaseComponent component : components) {
                BaseViewComponent viewComponent = BaseViewComponent.createFromBaseComponent(component);
                if (viewComponent != null) {
                    if (viewComponent instanceof TileViewComponent) {
                        ((TileViewComponent) viewComponent).setColorSequenceHolder(colorSequenceHolder);
                        ((TileViewComponent) viewComponent).setOnTileClickListener(this);
                        viewComponent.enableTracking(pageName, tileComponentCount++);
                    } else if (viewComponent instanceof SliderViewComponent) {
                        ((SliderViewComponent) viewComponent).setOnSlideClickListener(this);
                        viewComponent.enableTracking(pageName, sliderComponentCount++);
                    } else if (viewComponent instanceof CategoriesCarouselViewComponent) {
                        ((CategoriesCarouselViewComponent) viewComponent).setOnCarouselItemClickListener(this);
                        viewComponent.enableTracking(pageName, carouselComponentCount++);
                    } else if (viewComponent instanceof DailyDealViewComponent) {
                        ((DailyDealViewComponent) viewComponent).setOnCountDownDealItemClickListener(this);
                        viewComponent.enableTracking(pageName, dealComponentCount++);
                    }
                    mContainerLinearLayout.addView(viewComponent.getView(getContext()));
                }
            }
        }
    }

    @Override
    public void onCarouselClicked(View v, int position, CategoriesCarouselViewComponent.CategoryItem item) {
        if (position == 0) {
            getBaseActivity().onSwitchFragment(FragmentType.CATEGORIES, null, true);
        } else {
            fireTargetLink(item.title, item.targetLink);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        loadHomePage();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        srlHomeRoot.setRefreshing(false);
        showFragmentContentContainer();
        Print.i(TAG, "ON SUCCESS");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_HOME_EVENT: {
                HomePageComponents homePageComponents = (HomePageComponents) baseResponse.getContentData();
                this.mHomePageComponents = homePageComponents;
                showComponents(homePageComponents);
                break;
            }
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        srlHomeRoot.setRefreshing(false);
        Print.i(TAG, "ON ERROR RESPONSE");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Check base errors
        if (super.handleErrorEvent(baseResponse)) return;
        // Check home types
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_HOME_EVENT:
                Print.i(TAG, "ON ERROR RESPONSE: GET_HOME_EVENT");
                showFragmentFallBack();
                break;
        }
    }
}
