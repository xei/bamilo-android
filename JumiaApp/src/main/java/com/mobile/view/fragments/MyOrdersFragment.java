/**
 *
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.OrdersListAdapterNew;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.account.GetMyOrdersListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.orders.MyOrder;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Paulo Carvalho
 *
 */
public class MyOrdersFragment extends BaseFragment implements IResponseCallback, AdapterView.OnItemClickListener {

    private static final String TAG = MyOrdersFragment.class.getSimpleName();

    private ArrayList<Order> ordersList = new ArrayList<>();

    private ListView ordersListView;

    OrdersListAdapterNew ordersAdapter;

    private View emptyOrdersView;

    private static final int NUM_ORDERS = 25;

    private int pageIndex = 1;


    /**
     * Get instance
     */
    public static MyOrdersFragment getInstance() {
        return new MyOrdersFragment();
    }

    /**
     * Empty constructor
     */
    public MyOrdersFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.myorders_fragment_main);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
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
        
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey("orders"))
                ordersList = savedInstanceState.getParcelableArrayList("orders");
            //Print.i("ORDER", "ON LOAD SAVED STATE ordersList size:" + ordersList.size());
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
        ordersListView = (ListView) view.findViewById(R.id.orders_list);
        ordersListView.setOnScrollListener(onScrollListener);
        ordersListView.setOnItemClickListener(this);
        emptyOrdersView = view.findViewById(R.id.empty_orders_layout);
    }


    /**
     * If true, shows the order list, if false shows empty screen
     * */
    private void showListOrders(boolean isToShow)
    {
        if(isToShow)
        {
            ordersListView.setVisibility(View.GONE);
            emptyOrdersView.setVisibility(View.VISIBLE);
        }else
        {
            emptyOrdersView.setVisibility(View.GONE);
            ordersListView.setVisibility(View.VISIBLE);

            if(ordersAdapter == null)
                ordersAdapter = new OrdersListAdapterNew(this.getBaseActivity().getApplicationContext(),ordersList) ;
            else
                ordersAdapter.updateOrders(ordersList);
            ordersListView.setAdapter(ordersAdapter);

        }
    }




    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        triggerGetOrderList();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY");
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        onErrorEvent(baseResponse);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        onSuccessEvent(baseResponse);
    }


    protected void onSuccessEvent(BaseResponse baseResponse) {

        Print.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();

        if(super.handleSuccessEvent(baseResponse))
            return;

        EventType eventType = baseResponse.getEventType();

        switch (eventType) {
            case GET_MY_ORDERS_LIST_EVENT:
                MyOrder orders = (MyOrder) baseResponse.getMetadata().getData();
                ordersList =  orders.getOrders();

                if(CollectionUtils.isEmpty(ordersList)){
                    // show error/empty screen
                    showListOrders(true);
                //    showProductsLoading(false);

                }else {
                    showListOrders(false);
                }
                break;

            default:
                //show empty screen by default
                showListOrders(false);
                break;
        }

    }


    /**
     * sets list of orders
     *
     * @param orders
     */
  /*  private void setupOrders(ArrayList<Order> orders) {

        ordersAdapter = new OrdersListAdapterNew(getActivity().getApplicationContext(), orders);

        ordersListView.setAdapter(ordersAdapter);

        ordersListView.setVisibility(View.VISIBLE);

        }*/


    protected boolean onErrorEvent(BaseResponse baseResponse) {
        Print.d(TAG, "ON ERROR EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        boolean errorHandled = false;
        if(super.handleErrorEvent(baseResponse))
            errorHandled = true;


        EventType eventType = baseResponse.getEventType();
        ErrorCode errorCode = baseResponse.getError().getErrorCode();
        switch (eventType) {
            case GET_MY_ORDERS_LIST_EVENT:
                if(!errorHandled){
                    Print.w("ORDER", "ERROR Visible");
                    if(null == JumiaApplication.CUSTOMER){
                        triggerLogin();
                    } else {
                        Print.w("ORDER", "ERROR Visible");
                        //used for when the user session expires on the server side
                        try{
                            boolean isNotLoggedIn = false;
                            if (errorCode == ErrorCode.REQUEST_ERROR) {
                                Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();
                                if (errorMessages != null) {
                                    if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_CUSTOMER_NOT_LOGGED_IN)) {
                                        triggerLogin();
                                        isNotLoggedIn =true;
                                    }
                                }
                            }
                            if(!isNotLoggedIn){
                                showListOrders(true);
                              //  showProductsLoading(false);
                            }
                        } catch (ClassCastException | NullPointerException e){
                            showListOrders(true);
                         //   showProductsLoading(false);
                        }
                    }
                } else {
                    Print.w("ORDER", "ERROR notVisible");
                }

                return true;

            default:
                break;
        }
        return false;
    }


    /**
     * trigger request for the list order or update d adapter
     */
    private void triggerGetOrderList() {
        Bundle bundle = new Bundle();
        if(ordersList != null && ordersList.size() > 0) {
            if (JumiaApplication.CUSTOMER == null) {
                triggerLogin();
                return;
            }

        } else {
            bundle.putInt(GetMyOrdersListHelper.PAGE_NUMBER, pageIndex);
            bundle.putInt(GetMyOrdersListHelper.PER_PAGE, NUM_ORDERS);
       //     triggerContentEvent(new GetMyOrdersListHelper(), bundle, this);
            triggerContentEventProgress(new GetMyOrdersListHelper(), bundle, this);
        }

    }

/*
    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Next button
//        if(id == R.id.checkout_edit_button_enter) onClickEditAddressButton();
            // Next button
//        else if(id == R.id.checkout_edit_button_cancel) onClickCancelAddressButton();
            // Unknown view
//        else Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }*/






    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        getBaseActivity().onSwitchFragment(FragmentType.MY_ORDERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * re-direct to login view
     */
    private void triggerLogin(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_ORDERS);
        bundle.putInt(ConstantsIntentExtra.MY_ORDER_POS, 1);

        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "onSaveInstanceState");

        if(ordersList != null && ordersList.size() > 0)
            outState.putParcelableArrayList("orders",ordersList );

        super.onSaveInstanceState(outState);

    }

    /**
     * listview listener in order to load more products when last item is visible
     */
    private OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            // Sample calculation to determine if the last item is fully
            // visible.
//            if (totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
//                if (!mIsLoadingMore && !mReceivedError && totalPages != pageIndex) {
//                    new Handler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mIsLoadingMore = true;
//                            Print.w("ORDER", "LOAD MORE");
//                            showProductsLoading(true);
//                            getMoreProducts();
//                        }
//                    });
//                }
//            }
        }
    };

    /**
     * control if the loading more view is shown or not
     * @param isToShow
     */
    private void showProductsLoading(boolean isToShow){
//        if(isToShow)
//            loadMore.setVisibility(View.VISIBLE);
//        else
//            loadMore.setVisibility(View.GONE);
    }

    /**
     * function that gets more products when the user is scrooling
     */
    private void getMoreProducts(){

//        Bundle bundle = new Bundle();
//
//        bundle.putInt(GetMyOrdersListHelper.PAGE_NUMBER, pageIndex + 1);
//        bundle.putInt(GetMyOrdersListHelper.PER_PAGE, NUM_ORDERS);
//        triggerContentEventNoLoading(new GetMyOrdersListHelper(), bundle, mCallBack);

    }



/**
 * Clicking on an list item allows to get the order status
 * */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Order selectedOrder =  ordersList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR,selectedOrder.getmOrderNumber());
        getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle, FragmentController.ADD_TO_BACK_STACK);

    }


}