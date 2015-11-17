package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.OrdersAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.account.GetMyOrdersListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.orders.MyOrder;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Paulo Carvalho
 */
public class MyOrdersFragment extends BaseFragment implements IResponseCallback, AdapterView.OnItemClickListener, OnScrollListener {

    private static final String TAG = MyOrdersFragment.class.getSimpleName();

    private ArrayList<Order> mOrdersList;

    private ListView mOrdersListView;

    private OrdersAdapter mOrdersAdapter;

    private int mPageIndex = IntConstants.FIRST_PAGE;

    boolean isLoadingMore;

    private View mOrderStatusContainer;

    private int mMaxPages;

    private boolean isErrorOnLoadingMore;

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

    /**
     * Get instance
     */
    public static MyOrdersFragment getInstance() {
        return new MyOrdersFragment();
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
        if (savedInstanceState != null) {
            mOrdersList = savedInstanceState.getParcelableArrayList(RestConstants.ORDERS);
            mPageIndex = savedInstanceState.getInt(RestConstants.PAGE);
            mMaxPages = savedInstanceState.getInt(RestConstants.TOTAL_PAGES);
        }
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
        // Get container order status
        mOrderStatusContainer = view.findViewById(R.id.my_orders_frame_order_status);
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
        // Validate the state
        onValidateDataState();
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
        if (!JumiaApplication.isCustomerLoggedIn()) {
            // Remove this entry from back stack
            FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.MY_ORDERS.toString());
            // Goto Login and next WishList
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_ORDERS);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case first time
        else if (CollectionUtils.isEmpty(mOrdersList)) {
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
        mOrdersListView.setAdapter(mOrdersAdapter);
        // Case frame for order status
        if(mOrderStatusContainer != null) {
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
        boolean isBottomReached = totalItemCount != 0 && visibleItemCount + 1 == totalItemCount;
        // Validate
        if (isBottomReached && !isLoadingMore && mPageIndex < mMaxPages) {
            Log.i(TAG, "LOADING MORE DATA");
            isLoadingMore = true;
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
        bundle.putString(ConstantsIntentExtra.ARG_1, String.valueOf(selectedOrder.getNumber()));
        bundle.putString(ConstantsIntentExtra.ARG_2, selectedOrder.getDate());
        // Validate if frame order status
        if (mOrderStatusContainer == null) {
            getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            adapter.notifySelectedData(position);
            FragmentController.addChildFragment(this, mOrderStatusContainer.getId(), OrderStatusFragment.getNestedInstance(bundle), OrderStatusFragment.TAG);
        }
    }

    /*
     * ######### TRIGGERS #########
     */

    private void triggerGetOrderList(int page) {
        if (page == IntConstants.FIRST_PAGE) {
            triggerContentEvent(new GetMyOrdersListHelper(), GetMyOrdersListHelper.createBundle(page), this);
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
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_MY_ORDERS_LIST_EVENT:
                MyOrder orders = (MyOrder) baseResponse.getMetadata().getData();
                ArrayList<Order> orderList = orders.getOrders();
                // Get max pages
                mMaxPages = orders.getTotalPages();
                // Validate
                if (CollectionUtils.isEmpty(orderList) && mPageIndex == 1) {
                    showErrorFragment(ErrorLayoutFactory.NO_ORDERS_LAYOUT, this);
                } else {
                    if (mPageIndex > 1) {
                        appendToList(orderList);
                        isLoadingMore = false;
                    } else
                        showOrders(orderList);
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
        int errorCode = baseResponse.getError().getCode();
        switch (eventType) {
            case GET_MY_ORDERS_LIST_EVENT:
                Print.w("ORDER", "ERROR Visible");
                isErrorOnLoadingMore = true;
                //used for when the user session expires on the server side
                try {
                    if (errorCode == ErrorCode.REQUEST_ERROR) {
                        Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();
                        if (errorMessages != null) {
                            if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_CUSTOMER_NOT_LOGGED_IN)) {
                                LogOut.perform(new WeakReference<Activity>(getBaseActivity()));
                                onValidateDataState();
                            }
                        }
                    }
                } catch (ClassCastException | NullPointerException e) {
                    showFragmentErrorRetry();
                }
                break;
            default:
                break;
        }
    }

}