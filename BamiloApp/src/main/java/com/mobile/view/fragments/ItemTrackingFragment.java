package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.apicore.di.modules.ItemTrackingModule;
import com.bamilo.apicore.presentation.ItemTrackingPresenter;
import com.bamilo.apicore.service.model.ItemTrackingResponse;
import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.data.itemtracking.CompleteOrder;
import com.bamilo.apicore.service.model.data.itemtracking.PackageItem;
import com.bamilo.apicore.view.ItemTrackingView;
import com.mobile.adapters.ItemTrackingListAdapter;
import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.managers.TrackerManager;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.NetworkConnectivity;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;
import com.mobile.view.fragments.order.MyOrdersFragment;

import java.util.EnumSet;

import javax.inject.Inject;

/**
 * Created on 10/28/2017.
 */

public class ItemTrackingFragment extends BaseFragment implements ItemTrackingView, ItemTrackingListAdapter.OnItemTrackingListClickListener {

    private RecyclerView rvItemsList;
    private SwipeRefreshLayout srlItemTrackingStatus;
    private String orderNumber;
    private ItemTrackingListAdapter mAdapter;
    private boolean pageTracked = false;
    private boolean showDefaultProgress;

    @Inject
    ItemTrackingPresenter presenter;

    /**
     * Constructor as nested fragment, called from {@link MyOrdersFragment#}.
     */
    public static ItemTrackingFragment getNestedInstance(Bundle bundle) {
        ItemTrackingFragment orderStatusFragment = new ItemTrackingFragment();
        orderStatusFragment.setArguments(bundle);
        orderStatusFragment.isNestedFragment = true;
        return orderStatusFragment;
    }

    public ItemTrackingFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ORDERS,
                R.layout.fragment_item_tracking,
                R.string.order_status_label,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BamiloApplication
                .getComponent()
                .plus(new ItemTrackingModule(this))
                .inject(this);

        Bundle args = getArguments();
        orderNumber = args.getString(ConstantsIntentExtra.ORDER_NUMBER);

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.ORDER_DETAIL.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvItemsList = (RecyclerView) view.findViewById(R.id.rvItemsList);
        rvItemsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        srlItemTrackingStatus = (SwipeRefreshLayout) view.findViewById(R.id.srlItemTrackingStatus);
        srlItemTrackingStatus.setColorSchemeResources(R.color.appBar);
        srlItemTrackingStatus.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrderInfo(orderNumber, false);
            }
        });
        if (mAdapter == null) {
            loadOrderInfo(orderNumber, true);
        } else {
            mAdapter.setOnItemTrackingListClickListener(this);
            rvItemsList.setAdapter(mAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadOrderInfo(String orderNumber, boolean showDefaultProgress) {
        this.showDefaultProgress = showDefaultProgress;
        presenter.loadOrderDetails(orderNumber, NetworkConnectivity.isConnected(getContext()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onClickRetryButton(View view) {
        loadOrderInfo(orderNumber, true);
    }

    @Override
    protected void onClickContinueButton() {
        loadOrderInfo(orderNumber, true);
    }

    private void showOrderStatus(CompleteOrder order) {
        mAdapter = new ItemTrackingListAdapter(order);
        mAdapter.setOnItemTrackingListClickListener(this);
        rvItemsList.setAdapter(mAdapter);
    }

    @Override
    public void onReviewButtonClicked(View v, PackageItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, item.getSku());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, String.format("%s %s", item.getBrand(), item.getName()));
        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, false);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, getString(TrackingPage.ORDER_DETAIL.getName()));
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
        bundle.putBoolean(ConstantsIntentExtra.REDIRECT_TO_REVIEWS, true);
        // Goto review fragment
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, false);
    }

    @Override
    public void onProductThumbClickListener(View v, PackageItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, item.getSku());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, String.format("%s %s", item.getBrand(), item.getName()));
        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, getString(TrackingPage.ORDER_DETAIL.getName()));
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
        // Goto PDV
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void performOrderDetails(CompleteOrder order) {
        showOrderStatus(order);
    }

    @Override
    public void showMessage(com.bamilo.apicore.service.model.EventType eventType, String message) {
        showWarningErrorMessage(message);
    }

    @Override
    public void showOfflineMessage(com.bamilo.apicore.service.model.EventType eventType) {
        if (isNestedFragment) {
            showNoNetworkWarning();
        } else {
            showFragmentNoNetworkRetry();
        }
    }

    @Override
    public void showConnectionError(com.bamilo.apicore.service.model.EventType eventType) {
        if (isNestedFragment) {
            showUnexpectedErrorWarning();
        } else {
            showFragmentNetworkErrorRetry();
        }
    }

    @Override
    public void showServerError(com.bamilo.apicore.service.model.EventType eventType, ServerResponse response) {
        if (eventType == com.bamilo.apicore.service.model.EventType.TRACK_ORDER_EVENT &&
                response instanceof ItemTrackingResponse) {
            ItemTrackingResponse itemTrackingResponse = (ItemTrackingResponse)response;
            if (itemTrackingResponse.getMessages() != null && !itemTrackingResponse.getMessages().isEmpty()) {
                showWarningErrorMessage(itemTrackingResponse.getMessages().get(0));
            }
            getBaseActivity().onBackPressed();
        }
    }

    @Override
    public void toggleProgress(com.bamilo.apicore.service.model.EventType eventType, boolean show) {
        if (show) {
            if (!showDefaultProgress) {
                srlItemTrackingStatus.setRefreshing(true);
            } else {
                showFragmentLoading();
            }
        } else {
            showFragmentContentContainer();
            srlItemTrackingStatus.setRefreshing(false);
        }
    }

    @Override
    public void showRetry(com.bamilo.apicore.service.model.EventType eventType) {
        showConnectionError(eventType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
