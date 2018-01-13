package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.OrdersAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.account.GetMyOrdersListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.orders.MyOrder;
import com.mobile.service.objects.orders.Order;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.ItemTrackingFragment;

import java.util.ArrayList;
import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * @author Paulo Carvalho
 */
public class MyOrdersFragment extends BaseFragment implements IResponseCallback, AdapterView.OnItemClickListener, OnScrollListener {

    private static final String TAG = MyOrdersFragment.class.getSimpleName();

    //DROID-10
    private long mGABeginRequestMillis;
    private ArrayList<Order> mOrdersList;
    private ListView mOrdersListView;
    private SwipeRefreshLayout srlOrderList;
    private OrdersAdapter mOrdersAdapter;
    private int mPageIndex = IntConstants.FIRST_PAGE;
    boolean isLoadingMore;
    private View mOrderStatusContainer;
    private int mMaxPages;
    private boolean isErrorOnLoadingMore;
    private int mScrollPosition;
    private boolean pageTracked = false;

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
        Print.i(TAG, "ON CREATE");
        mGABeginRequestMillis = System.currentTimeMillis();
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

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get views
        mOrdersListView = (ListView) view.findViewById(R.id.orders_list);
        srlOrderList = (SwipeRefreshLayout) view.findViewById(R.id.srlOrderList);
        srlOrderList.setColorSchemeResources(R.color.appBar);
        srlOrderList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageIndex = IntConstants.FIRST_PAGE;
                triggerGetOrderList(mPageIndex, false);
            }
        });
        // Get container order status
        mOrderStatusContainer = view.findViewById(R.id.my_orders_frame_order_status);
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
        // Validate the state
        onValidateDataState();
        mOrdersListView.setSelection(mScrollPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE STATE");
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
        Print.i(TAG, "ON PAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
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
        Print.i(TAG, "ON VALIDATE DATA STATE");
        // Validate customer is logged in
        if (!BamiloApplication.isCustomerLoggedIn()) {
            onLoginRequired();
        }
        // Case first time
        else if (CollectionUtils.isEmpty(mOrdersList)) {
            triggerGetOrderList(mPageIndex, true);
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
        // Case frame for order status
        if (mOrderStatusContainer != null) {
            mOrdersListView.performItemClick(null, 0, mOrdersAdapter.getItemId(0));
        }
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
        boolean isBottomReached = totalItemCount != 0 && (firstVisibleItem + visibleItemCount + 1) == totalItemCount;
        // Validate
        if (isBottomReached && !isLoadingMore && mPageIndex < mMaxPages) {
            Log.i(TAG, "LOADING MORE DATA");
            isLoadingMore = true;
            triggerGetOrderList(mPageIndex + 1, true);
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
        if (mOrderStatusContainer == null) {
            getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            adapter.notifySelectedData(position);
            FragmentController.getInstance().addChildFragment(this, mOrderStatusContainer.getId(), ItemTrackingFragment.getNestedInstance(bundle), OrderStatusFragment.TAG);
        }
    }

    /*
     * ######### TRIGGERS #########
     */

    private void triggerGetOrderList(int page, boolean showDefaultProgress) {
        if (page == IntConstants.FIRST_PAGE) {
            if (!showDefaultProgress) {
                triggerContentEventNoLoading(new GetMyOrdersListHelper(), GetMyOrdersListHelper.createBundle(page), this);
                srlOrderList.setRefreshing(true);
            } else {
                triggerContentEvent(new GetMyOrdersListHelper(), GetMyOrdersListHelper.createBundle(page), this);
            }
        } else {
            triggerContentEventNoLoading(new GetMyOrdersListHelper(), GetMyOrdersListHelper.createBundle(page), this);
        }
    }

    /*
     * ######### RESPONSES #########
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Hide dialog progress
        hideActivityProgress();
        srlOrderList.setRefreshing(false);
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_MY_ORDERS_LIST_EVENT:
                MyOrder orders = (MyOrder) baseResponse.getContentData();
                ArrayList<Order> orderList = orders.getOrders();
                // Get max pages
                mPageIndex = orders.getCurrentPage();
                mMaxPages = orders.getTotalPages();
                // Validate
                if (CollectionUtils.isEmpty(orderList) && mPageIndex == 1) {
                    showErrorFragment(ErrorLayoutFactory.NO_ORDERS_LAYOUT, this);
                } else if (mPageIndex > 1) {
                    appendToList(orderList);
                    isLoadingMore = false;
                } else
                    showOrders(orderList);


                if (!pageTracked) {
                    /*TrackerDelegator.trackPage(TrackingPage.ORDER_LIST, getLoadTime(), false);*/

                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.MY_ORDERS.getName()), getString(R.string.gaScreen),
                            "",
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.d(TAG, "ON ERROR EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
        }

        if (super.handleErrorEvent(baseResponse)) {
            return;
        }

        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_MY_ORDERS_LIST_EVENT:
                Print.w("ORDER", "ERROR Visible");
                isErrorOnLoadingMore = true;
                showFragmentErrorRetry();
                break;
            default:
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

}