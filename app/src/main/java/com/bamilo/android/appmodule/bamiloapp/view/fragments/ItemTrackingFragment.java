package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.adapters.ItemTrackingListAdapter;
import com.bamilo.android.core.modules.ItemTrackingModule;
import com.bamilo.android.core.presentation.ItemTrackingPresenter;
import com.bamilo.android.core.service.model.EventType;
import com.bamilo.android.core.service.model.ItemTrackingResponse;
import com.bamilo.android.core.service.model.ServerResponse;
import com.bamilo.android.core.service.model.data.itemtracking.CompleteOrder;
import com.bamilo.android.core.service.model.data.itemtracking.PackageItem;
import com.bamilo.android.core.view.ItemTrackingView;
import com.google.gson.Gson;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.NetworkConnectivity;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.order.MyOrdersFragment;

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

    @Inject
    Gson mGson;

    private boolean shouldReloadPage = false;

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
        if (shouldReloadPage) {
            loadOrderInfo(orderNumber, true);
            shouldReloadPage = false;
        }
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
        boolean cancellationEnabled = false;
        if (order.getCancellation() != null) {
            cancellationEnabled = order.getCancellation().isCancellationServiceEnabled() &&
                    CollectionUtils.isNotEmpty(order.getCancellation().getReasons());
        }
        mAdapter = new ItemTrackingListAdapter(order, cancellationEnabled);
        mAdapter.setOnItemTrackingListClickListener(this);
        rvItemsList.setAdapter(mAdapter);
    }

    @Override
    public void onCancelItemButtonClicked(View v, PackageItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.COMPLETE_ORDER, mGson.toJson(mAdapter.getCompleteOrder()));
        bundle.putString(ConstantsIntentExtra.CANCELING_ORDER_ITEM_ID, item.getId());
        // Goto Cancellation Fragment
        getBaseActivity().onSwitchFragment(FragmentType.ORDER_CANCELLATION, bundle, false);
        shouldReloadPage = true;
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
    public void showMessage(EventType eventType, String message) {
        showWarningErrorMessage(message);
    }

    @Override
    public void showOfflineMessage(EventType eventType) {
        if (isNestedFragment) {
            showNoNetworkWarning();
        } else {
            showFragmentNoNetworkRetry();
        }
    }

    @Override
    public void showConnectionError(EventType eventType) {
        if (isNestedFragment) {
            showUnexpectedErrorWarning();
        } else {
            showFragmentNetworkErrorRetry();
        }
    }

    @Override
    public void showServerError(EventType eventType, ServerResponse response) {
        if (eventType == EventType.TRACK_ORDER_EVENT &&
                response instanceof ItemTrackingResponse) {
            ItemTrackingResponse itemTrackingResponse = (ItemTrackingResponse)response;
            if (itemTrackingResponse.getMessages() != null && itemTrackingResponse.getMessages().getErrors() != null &&
                    !itemTrackingResponse.getMessages().getErrors().isEmpty()) {
                showWarningErrorMessage(itemTrackingResponse.getMessages().getErrors().get(0).getMessage());
            }
            getBaseActivity().onBackPressed();
        }
    }

    @Override
    public void toggleProgress(EventType eventType, boolean show) {
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
    public void showRetry(EventType eventType) {
        showConnectionError(eventType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
