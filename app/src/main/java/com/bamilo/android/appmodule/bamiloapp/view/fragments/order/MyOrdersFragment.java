package com.bamilo.android.appmodule.bamiloapp.view.fragments.order;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.OrdersAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;
import com.bamilo.android.core.modules.OrdersListModule;
import com.bamilo.android.core.presentation.OrdersListPresenter;
import com.bamilo.android.core.service.model.OrdersListResponse;
import com.bamilo.android.core.service.model.ServerResponse;
import com.bamilo.android.core.service.model.data.orders.OrderListItem;
import com.bamilo.android.core.view.OrdersListView;
import com.bamilo.android.framework.service.objects.orders.Order;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.NetworkConnectivity;

import java.util.ArrayList;
import java.util.EnumSet;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Paulo Carvalho
 */
public class MyOrdersFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnScrollListener, OrdersListView {

    private static final String TAG = MyOrdersFragment.class.getSimpleName();

    @Named("apiPaginatedItemsCount")
    @Inject
    int itemsPerPage;

    @Inject
    OrdersListPresenter presenter;

    private ArrayList<Order> mOrdersList;
    private ListView mOrdersListView;
    private SwipeRefreshLayout srlOrderList;
    private OrdersAdapter mOrdersAdapter;
    private int mPageIndex = IntConstants.FIRST_PAGE;
    boolean isLoadingMore;
    private int mMaxPages;
    private boolean isErrorOnLoadingMore;
    private int mScrollPosition;
    private boolean pageTracked = false;
    private boolean showDefaultProgress;

    /**
     * Empty constructor
     */
    public MyOrdersFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ORDERS,
                R.layout.my_orders_fragment_main,
                R.string.my_orders_label,
                NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        showDefaultProgress = true;
        if (savedInstanceState != null) {
            mOrdersList = savedInstanceState.getParcelableArrayList(RestConstants.ORDERS);
            mPageIndex = savedInstanceState.getInt(RestConstants.PAGE);
            mMaxPages = savedInstanceState.getInt(RestConstants.TOTAL_PAGES);
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.ORDER_LIST.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    private void injectDependencies() {
        BamiloApplication
                .getComponent()
                .plus(new OrdersListModule(this))
                .inject(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get views
        mOrdersListView = view.findViewById(R.id.orders_list);
        srlOrderList = view.findViewById(R.id.srlOrderList);
        srlOrderList.setColorSchemeResources(R.color.appBar);
        srlOrderList.setOnRefreshListener(() -> {
            mPageIndex = IntConstants.FIRST_PAGE;
            showDefaultProgress = false;
            triggerGetOrderList(mPageIndex);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Validate the state
        onValidateDataState();
        mOrdersListView.setSelection(mScrollPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state
        outState.putParcelableArrayList(RestConstants.ORDERS, mOrdersList);
        outState.putInt(RestConstants.PAGE, mPageIndex);
        outState.putInt(RestConstants.TOTAL_PAGES, mMaxPages);
    }

    @Override
    public void onPause() {
        if (mOrdersListView != null) {
            mScrollPosition = mOrdersListView.getFirstVisiblePosition();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * ######### LAYOUT #########
     */

    /**
     * Method used to validate the current state.<br>
     * - Case logged in or not<br>
     * - Case first page<br>
     * - Case restore saved state<br>
     */
    private void onValidateDataState() {
        // Validate customer is logged in
        if (!BamiloApplication.isCustomerLoggedIn()) {
            onLoginRequired();
        }
        // Case first time
        else if (CollectionUtils.isEmpty(mOrdersList)) {
            showDefaultProgress = true;
            triggerGetOrderList(mPageIndex);
        }
        // Case recover saved state
        else {
            showOrders(mOrdersList);
        }
    }

    /**
     * If true, shows the order list
     */
    private void showOrders(ArrayList<Order> mOrdersList) {
        mOrdersListView.setOnScrollListener(this);
        mOrdersListView.setOnItemClickListener(this);
        if (mOrdersAdapter == null) {
            mOrdersAdapter = new OrdersAdapter(this.getBaseActivity().getApplicationContext(), mOrdersList);
        } else {
            mOrdersAdapter.updateOrders(mOrdersList);
        }
        this.mOrdersList = mOrdersAdapter.getOrders();
        mOrdersAdapter.setLoadMoreProgressEnabled(mPageIndex < mMaxPages);
        mOrdersListView.setAdapter(mOrdersAdapter);
        // Show container
        showFragmentContentContainer();
    }

    /**
     * appends an array list to the adapter when scrooling
     */
    private void appendToList(ArrayList<Order> orders) {
        if (mOrdersAdapter != null) {
            mOrdersAdapter.appendOrders(orders);
            mOrdersAdapter.setLoadMoreProgressEnabled(mPageIndex < mMaxPages);
            mOrdersList = mOrdersAdapter.getOrders();
        }
    }

    /*
     * ######### LISTENERS #########
     */

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        getBaseActivity().onSwitchFragment(FragmentType.MY_ORDERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // ...
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Force scroll to up until one row to disable error flag
        boolean isScrollingUp = totalItemCount != 0 && firstVisibleItem + visibleItemCount <= totalItemCount;
        if (isErrorOnLoadingMore && isScrollingUp) {
            isErrorOnLoadingMore = false;
        }
        // Bottom reached
        boolean isBottomReached = totalItemCount != 0 && (firstVisibleItem + visibleItemCount) == totalItemCount;
        // Validate
        if (isBottomReached && !isLoadingMore && mPageIndex < mMaxPages) {
            Log.i(TAG, "LOADING MORE DATA");
            isLoadingMore = true;
            showDefaultProgress = false;
            triggerGetOrderList(mPageIndex + 1);
        }
    }

    /**
     * Clicking on an list item allows to get the order status
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Create bundle with info
        OrdersAdapter adapter = (OrdersAdapter) parent.getAdapter();
        Order selectedOrder = adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.ORDER_NUMBER, String.valueOf(selectedOrder.getNumber()));
        // Validate if frame order status
        getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * ######### TRIGGERS #########
     */

    private void triggerGetOrderList(int page) {
        presenter.loadOrdersList(itemsPerPage, page, NetworkConnectivity.isConnected(getContext()));
    }

    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
        }

        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_MY_ORDERS_LIST_EVENT:
                isErrorOnLoadingMore = true;
                if (isLoadingMore) {
                    // to stop scrolling
                    mOrdersListView.smoothScrollBy(0, 0);
                    mOrdersListView.smoothScrollBy(-getResources().getDimensionPixelSize(R.dimen.catalog_footer_height), 0);
                    isLoadingMore = false;
                    baseResponse.setEventTask(EventTask.ACTION_TASK);
                }
                if (!super.handleErrorEvent(baseResponse)) {
                    showFragmentNetworkErrorRetry();
                }
                break;
            default:
                if (!super.handleErrorEvent(baseResponse)) {
                    showFragmentNetworkErrorRetry();
                }
                break;
        }
    }

    @Override
    protected void onLoginRequired() {
        // Remove this entry from back stack
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.MY_ORDERS.toString());
        // Goto Login and next WishList
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_ORDERS);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void showMessage(com.bamilo.android.core.service.model.EventType eventType, String message) {
        showWarningErrorMessage(message);
    }

    @Override
    public void showOfflineMessage(com.bamilo.android.core.service.model.EventType eventType) {
        if (isLoadingMore) {
            isErrorOnLoadingMore = true;
            // to stop scrolling
            mOrdersListView.smoothScrollBy(0, 0);
            mOrdersListView.smoothScrollBy(-getResources().getDimensionPixelSize(R.dimen.catalog_footer_height), 0);
            isLoadingMore = false;
            showNoNetworkWarning();
        } else {
            showFragmentNoNetworkRetry();
        }
    }

    @Override
    public void showConnectionError(com.bamilo.android.core.service.model.EventType eventType) {
        if (isLoadingMore) {
            isErrorOnLoadingMore = true;
            // to stop scrolling
            mOrdersListView.smoothScrollBy(0, 0);
            mOrdersListView.smoothScrollBy(-getResources().getDimensionPixelSize(R.dimen.catalog_footer_height), 0);
            isLoadingMore = false;
            showUnexpectedErrorWarning();
        } else {
            showFragmentNetworkErrorRetry();
        }
    }

    @Override
    public void showServerError(com.bamilo.android.core.service.model.EventType eventType, ServerResponse response) {

    }

    @Override
    public void toggleProgress(com.bamilo.android.core.service.model.EventType eventType, boolean show) {
        if (showDefaultProgress) {
            if (show) {
                showFragmentLoading();
            } else {
                showFragmentContentContainer();
                showDefaultProgress = false;
            }
        } else {
            srlOrderList.setRefreshing(show);
        }
    }

    @Override
    public void showRetry(com.bamilo.android.core.service.model.EventType eventType) {
        if (isLoadingMore) {
            isErrorOnLoadingMore = true;
            // to stop scrolling
            mOrdersListView.smoothScrollBy(0, 0);
            mOrdersListView.smoothScrollBy(-getResources().getDimensionPixelSize(R.dimen.catalog_footer_height), 0);
            isLoadingMore = false;
            showUnexpectedErrorWarning();
        } else {
            showConnectionError(eventType);
        }
    }

    @Override
    public void performOrdersList(OrdersListResponse ordersListResponse) {
        if (ordersListResponse != null && ordersListResponse.isSuccess()) {
            showFragmentContentContainer();
            ArrayList<Order> orderList = new ArrayList<>();

            for (OrderListItem item : ordersListResponse.getOrderListItems()) {
                Order order = new Order();
                order.setNumber(Integer.valueOf(item.getOrderNumber()));
                order.setDate(item.getOrderDate());
                order.setTotal(Double.valueOf(item.getTotalPrice()));
                orderList.add(order);
            }

            mPageIndex = ordersListResponse.getPagination().getCurrentPage();
            mMaxPages = ordersListResponse.getPagination().getTotalPages();

            if (CollectionUtils.isEmpty(orderList) && mPageIndex == 1) {
                showErrorFragment(ErrorLayoutFactory.NO_ORDERS_LAYOUT, this);
            } else if (mPageIndex > 1) {
                appendToList(orderList);
                isLoadingMore = false;
            } else {
                showOrders(orderList);
            }
        } else {
            onLoginRequired();
        }
    }
}