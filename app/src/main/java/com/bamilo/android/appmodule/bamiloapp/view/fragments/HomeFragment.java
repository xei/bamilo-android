package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.modernbamilo.util.storage.SharedPreferencesHelperKt;
import com.bamilo.android.core.modules.HomeModule;
import com.bamilo.android.core.presentation.HomePresenter;
import com.bamilo.android.core.service.model.EventType;
import com.bamilo.android.core.service.model.ServerResponse;
import com.bamilo.android.core.service.model.data.home.BaseComponent;
import com.bamilo.android.core.view.HomeView;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.home.TeaserCampaign;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.NetworkConnectivity;
import com.bamilo.android.appmodule.bamiloapp.utils.ColorSequenceHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.components.BaseViewComponent;
import com.bamilo.android.appmodule.bamiloapp.view.components.BaseViewComponentFactory;
import com.bamilo.android.appmodule.bamiloapp.view.components.CategoriesCarouselViewComponent;
import com.bamilo.android.appmodule.bamiloapp.view.components.DailyDealViewComponent;
import com.bamilo.android.appmodule.bamiloapp.view.components.SliderViewComponent;
import com.bamilo.android.appmodule.bamiloapp.view.components.TileViewComponent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;

public class HomeFragment extends BaseFragment implements SliderViewComponent.OnSlideClickListener, TargetLink.OnCampaignListener,
        TileViewComponent.OnTileClickListener, DailyDealViewComponent.OnCountDownDealItemClickListener,
        CategoriesCarouselViewComponent.OnCarouselItemClickListener, HomeView {

    @Inject
    HomePresenter homePresenter;

    private NestedScrollView mRootScrollView;
    private LinearLayout mContainerLinearLayout;
    private SwipeRefreshLayout srlHomeRoot;
    private ColorSequenceHolder colorSequenceHolder;
    private List<BaseComponent> mComponents;
    private List<DailyDealViewComponent> dailyDealViewComponents;
    private boolean isFragmentVisibleToUser = false;

    public HomeFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET),
                NavigationAction.HOME,
                R.layout.fragment_home,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Track only the screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.HOME.getName()), getString(R.string.gaScreen), "", getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        injectDependencies();
        mRootScrollView = view.findViewById(R.id.nsvHomeContainer);
        mRootScrollView.setClipToPadding(false);
        mRootScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    getBaseActivity().syncSearchBarState(mRootScrollView.getScrollY());
                }
            }
        });
        mContainerLinearLayout = view.findViewById(R.id.llHomeContainer);
        getBaseActivity().enableSearchBar(true, mRootScrollView);
        mRootScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isFragmentVisibleToUser) {
                    getBaseActivity().onSearchBarScrolled(scrollY - oldScrollY);
                }
            }
        });
        srlHomeRoot = view.findViewById(R.id.srlHomeRoot);
        srlHomeRoot.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHomePage();
            }
        });
        srlHomeRoot.setColorSchemeResources(R.color.appBar);
        srlHomeRoot.setProgressViewOffset(false, mRootScrollView.getPaddingTop(), srlHomeRoot.getProgressViewEndOffset());


        initColorSeqHolder();
        if (mComponents == null ||
                mComponents.isEmpty()) {
            loadHomePage();
        } else {
            showComponents(mComponents);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferencesHelperKt.clearTrackHomepagePurchase(getContext());
    }

    private void injectDependencies() {
        BamiloApplication
                .getComponent()
                .plus(new HomeModule(this))
                .inject(this);
    }

    @Override
    public void onPause() {
        stopDealComponentsTimer();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        homePresenter.destroy();
        super.onDestroyView();
    }

    private void stopDealComponentsTimer() {
        if (dailyDealViewComponents != null) {
            for (DailyDealViewComponent component : dailyDealViewComponents) {
                component.pause();
            }
        }
    }

    private void loadHomePage() {
        stopDealComponentsTimer();
        homePresenter.loadHome(NetworkConnectivity.isConnected(getContext()));
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

    public void showComponents(List<BaseComponent> components) {
        if (dailyDealViewComponents == null) {
            dailyDealViewComponents = new ArrayList<>();
        }
        dailyDealViewComponents.clear();

        int tileComponentCount = 1,
                sliderComponentCount = 1,
                carouselComponentCount = 1,
                dealComponentCount = 1;
        String pageName = "HomePage";
        if (components != null) {
            mContainerLinearLayout.removeAllViews();
            for (BaseComponent component : components) {
                BaseViewComponent viewComponent = BaseViewComponentFactory.createBaseViewComponent(component);
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
                        dailyDealViewComponents.add((DailyDealViewComponent) viewComponent);
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
        loadHomePage();
    }

    @Override
    protected void onClickContinueButton() {
        loadHomePage();
    }

    @Override
    public void showMessage(EventType eventType, String message) {
        showWarningErrorMessage(message);
    }

    @Override
    public void showOfflineMessage(EventType eventType) {
        showFragmentNoNetworkRetry();
    }

    @Override
    public void showConnectionError(EventType eventType) {
        showFragmentNetworkErrorRetry();
    }

    @Override
    public void showServerError(EventType eventType, ServerResponse response) {
        // it isn't gonna happen
    }

    @Override
    public void toggleProgress(EventType eventType, boolean show) {
        srlHomeRoot.setRefreshing(show);
    }

    @Override
    public void showRetry(EventType eventType) {
        showConnectionError(eventType);
    }

    @Override
    public void performHomeComponents(List<BaseComponent> components) {
        showFragmentContentContainer();
        mComponents = components;
        showComponents(mComponents);
    }
}
