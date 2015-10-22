/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.OrdersListAdapter;
import com.mobile.controllers.OrdersListAdapter.OnSelectedOrderChange;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.account.GetMyOrdersListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.orders.MyOrder;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.objects.orders.OrderItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Paulo Carvalho
 * 
 */
public class OrderHistoryFragment extends BaseFragment implements OnSelectedOrderChange{

    private static final String TAG = OrderHistoryFragment.class.getSimpleName();

    private ArrayList<Order> ordersList = new ArrayList<>();
    
    private ListView ordersListView;

    private TextView orderListPaymentTitle;

    private TextView ordersProductDate;
    
    private TextView noOrders;
    
    private LinearLayout productsLandscapeContainer;
    
    private RelativeLayout productsContainer;
    
    OrdersListAdapter ordersAdapter;

    private boolean isVisible = false;
    
    private int selectedProduct = -1;
    
    private static final int NUM_ORDERS = 25;
    
    private int pageIndex = 1;
    
    private int totalPages = 0;
    
    private LinearLayout loadMore;
    
    private boolean showEmptyScreen = false;
    
    private boolean mIsLoadingMore = false;
    
    private boolean mReceivedError = false;
    
    /**
     * Get instance
     */
    public static OrderHistoryFragment getInstance() {
        return new OrderHistoryFragment();
    }

    /**
     * Empty constructor
     */
    public OrderHistoryFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyOrders,
                R.layout.order_history_main,
                R.string.my_orders_label,
                KeyboardState.ADJUST_CONTENT);
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
            selectedProduct = savedInstanceState.getInt("selectedPos");
            pageIndex = savedInstanceState.getInt("currentPage");
            totalPages = savedInstanceState.getInt("totalPages");
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
        noOrders = (TextView) view.findViewById(R.id.no_orders_title);
        loadMore = (LinearLayout) view.findViewById(R.id.catalog_loading_more);

        if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
            orderListPaymentTitle = (TextView) view.findViewById(R.id.order_list_payment_title);
            ordersProductDate = (TextView) view.findViewById(R.id.order_list_date);
            productsContainer = (RelativeLayout) view.findViewById(R.id.order_products_container);
            productsLandscapeContainer = (LinearLayout) view.findViewById(R.id.orders_products_landscape_list);
        }
        
    }

    
    private void setEmptyScreenState(boolean isToShow){
        showEmptyScreen = isToShow;
        showFragmentContentContainer();
        
        if(isToShow){

            noOrders.setVisibility(View.VISIBLE);
            if(DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
                productsContainer.setVisibility(View.GONE);
                ordersListView.setVisibility(View.GONE);
                productsLandscapeContainer.setVisibility(View.GONE);
            } else {
                ordersListView.setVisibility(View.GONE);
            }
        }else{
            noOrders.setVisibility(View.GONE);
            
            if(DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
                productsContainer.setVisibility(View.VISIBLE);
                ordersListView.setVisibility(View.VISIBLE);
                productsLandscapeContainer.setVisibility(View.VISIBLE);
            } else {
                ordersListView.setVisibility(View.VISIBLE);
            }
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


    IResponseCallback mCallBack = new IResponseCallback() {
        @Override
        public void onRequestError(BaseResponse baseResponse) {
            onErrorEvent(baseResponse);
        }

        @Override
        public void onRequestComplete(BaseResponse baseResponse) {
            onSuccessEvent(baseResponse);
        }
    };
    
    
    protected boolean onSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "ON SUCCESS EVENT");
        mReceivedError = false;
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if(super.handleSuccessEvent(baseResponse))
            return true;
        
            
        EventType eventType = baseResponse.getEventType();

        switch (eventType) {
        case GET_MY_ORDERS_LIST_EVENT:
            MyOrder orders = (MyOrder) baseResponse.getMetadata().getData();

            ArrayList<Order> ordersResponse =  orders.getOrders();
            
            if(CollectionUtils.isEmpty(ordersList) && CollectionUtils.isEmpty(ordersResponse)){
                // show error/empty screen
                setEmptyScreenState(true);
                showProductsLoading(false);
                return false;
            }
            
            if(orders.getCurrentPage() != 0 && orders.getTotalOrders() != 0){
                if(pageIndex == orders.getCurrentPage()){
                    //is already showing the last page of the orders
                    mIsLoadingMore = true;
                    showProductsLoading(false);
                }
                
                pageIndex = orders.getCurrentPage();
                totalPages = orders.getNumPages();

                if(pageIndex <= totalPages){
                    mIsLoadingMore = false;
                    
                    if(!CollectionUtils.isEmpty(ordersList) && ordersAdapter != null){
                        //does nothing because theres no new order
                          ordersList.addAll(ordersResponse);
                          ordersAdapter.updateOrders(ordersList);
                          
                          showFragmentContentContainer();
                          setEmptyScreenState(false);
                          showProductsLoading(false);
                      } else {
                          ordersList = ordersResponse;
                          setEmptyScreenState(false);
                          if(ordersAdapter != null){
                              ordersAdapter.updateOrders(ordersList);
                          } else {
                              setupOrders(ordersList);
                          }
                          showFragmentContentContainer();
                      }
                    
                    if(pageIndex == totalPages){
                        //mark page as last
                        showProductsLoading(false);
                        mIsLoadingMore = true;
                    }
                    showProductsLoading(false);
                } else {
                    showProductsLoading(false);
                    mIsLoadingMore = true;
                    return false;
                }

                if(orders.getTotalOrders() <= NUM_ORDERS){
                    mIsLoadingMore = true;
                }
            }
            
            return true;

        default:
            break;
        }
        return true;
    }

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
            if(isVisible && !errorHandled){
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
                                setEmptyScreenState(true);
                                showProductsLoading(false);
                            }
                        } catch (ClassCastException | NullPointerException e){
                            setEmptyScreenState(true);
                            showProductsLoading(false);
                        }
                    }
            } else {
                Print.w("ORDER", "ERROR notVisible");
                mReceivedError = true;
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
        if(ordersList != null && ordersList.size() > 0){
            if(JumiaApplication.CUSTOMER == null){
                triggerLogin();
                return;
            }
            if(ordersAdapter == null){
                ordersAdapter = new OrdersListAdapter(getActivity().getApplicationContext(), ordersList, this);
                if(selectedProduct == -1 && DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
                    selectedProduct = 0;
                } else {
                    ordersAdapter.setSelectedPosition(selectedProduct);
                }
                ordersListView.setAdapter(ordersAdapter);
                
                if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct != -1) setOrderProducts(ordersList.get(selectedProduct),productsLandscapeContainer,true);
                else if(DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct == -1) setOrderProducts(ordersList.get(0),productsLandscapeContainer,true);
                
                ordersListView.setSelection(selectedProduct);
            } else{
                ordersListView.setAdapter(ordersAdapter);
                ordersAdapter.setSelectedPosition(selectedProduct);
                ordersAdapter.updateOrders(ordersList);
                ordersListView.setSelection(selectedProduct);
                
                if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct != -1) setOrderProducts(ordersList.get(selectedProduct),productsLandscapeContainer,true);
                else if(DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct == -1) setOrderProducts(ordersList.get(0),productsLandscapeContainer,true);
            }
        }
        else{
            bundle.putInt(GetMyOrdersListHelper.PAGE_NUMBER, pageIndex);
            bundle.putInt(GetMyOrdersListHelper.PER_PAGE, NUM_ORDERS);
            triggerContentEvent(new GetMyOrdersListHelper(), bundle, mCallBack);
        }
        
        
    }

    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            isVisible = true;
            TrackerDelegator.trackPage(TrackingPage.MYORDERS_SCREEN, getLoadTime(), false);
            if (isResumed()){
                if(null == JumiaApplication.CUSTOMER){
                    triggerLogin();
                } else {
                    if(mReceivedError){
                        mReceivedError = false;
                        triggerGetOrderList();
                    } else {
                        if(showEmptyScreen){
                            setEmptyScreenState(true);
                            showProductsLoading(false);
                        }  
                    }
                    
                }
            }
        } else {
            isVisible = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
    
    
    /**
     * sets list of orders
     * 
     * @param orders
     */
    private void setupOrders(ArrayList<Order> orders) {
        
        ordersAdapter = new OrdersListAdapter(getActivity().getApplicationContext(), orders, this);
        
        ordersListView.setAdapter(ordersAdapter);

        if(orders.size() > 0 && DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
            if(selectedProduct == -1) selectedProduct = 0;
            
            productsContainer.setVisibility(View.VISIBLE);
            productsLandscapeContainer.setVisibility(View.VISIBLE);
            
            setOrderProducts(orders.get(selectedProduct),productsLandscapeContainer, true);
//            setupProductslandScape(orders.get(selectedProduct));
        } else {
            ordersListView.setVisibility(View.VISIBLE);
            if(selectedProduct != -1) {
                ordersAdapter.setSelectedPosition(selectedProduct);
                ordersAdapter.notifyDataSetChanged();
            }
            
        }
  }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.MY_ORDER_POS, ShopSelector.isRtl() ? 0 : 1);
        getBaseActivity().onSwitchFragment(FragmentType.MY_ORDERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * interface from the adapter to act when the user chooses a order
     */
    @Override
    public void SelectedOrder(Order order, ViewGroup productsContainer, boolean toShowInnerProds, int selectedProd) {
        
        selectedProduct = selectedProd;
        Bundle bundle = new Bundle();
        bundle.putString(OrderStatusFragment.ORDER, order.getmOrderNumber());
        getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle, FragmentController.ADD_TO_BACK_STACK);

//        if (!DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) setOrderProducts(order,productsContainer, toShowInnerProds);
//        else setOrderProducts(order,productsLandscapeContainer,toShowInnerProds);
        
    }
    
    /**
     * inflate order products into a linear layout of the corresponding order
     * 
     * @param order
     * @param productsContainer
     * @param toShowInnerProds
     */
    private void setOrderProducts(Order order, ViewGroup productsContainer, boolean toShowInnerProds) {
        class Item {
            public TextView productName;
            public TextView productQtd;
            public TextView productPrice;
            public View productBottomDivider;
            public View bottomSpace;
        }

        if (ordersAdapter != null) {
            ordersAdapter.setSelectedPosition(selectedProduct);
            ordersAdapter.notifyDataSetChanged();
        }

        if (toShowInnerProds && productsLandscapeContainer != null && DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) {
            if (productsLandscapeContainer.getChildCount() > 0) {
                productsLandscapeContainer.removeAllViews();
            }

            String paymentMethod = TextUtils.htmlEncode(order.getmPayment());
            String paymentMethodLabel = String.format(getString(R.string.payment_method), paymentMethod);
            orderListPaymentTitle.setText(Html.fromHtml(paymentMethodLabel));
            ordersProductDate.setText(order.getmDate());
        }

        if (toShowInnerProds && productsContainer.getChildCount() == 0) {

            ArrayList<OrderItem> orderItems = order.getmOrderProducts();

            for (int i = 0; i < orderItems.size(); i++) {
                View itemView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.order_history_product_item, productsContainer, false);
                Item item = new Item();
                item.productName = (TextView) itemView.findViewById(R.id.order_product_name);
                item.productQtd = (TextView) itemView.findViewById(R.id.order_product_quantity);
                item.productPrice = (TextView) itemView.findViewById(R.id.order_product_price);
                item.productBottomDivider = itemView.findViewById(R.id.order_product_divider);
                item.bottomSpace = itemView.findViewById(R.id.order_product_bottom_space);
                if (!"null".equals(orderItems.get(i).getmProductTotalString())) {
                    item.productPrice.setText(CurrencyFormatter.formatCurrency(orderItems.get(i).getmProductTotalString()));
                } else {
                    item.productPrice.setText(CurrencyFormatter.getCurrencyCode() + " " + orderItems.get(i).getmProductTotal());
                }
                item.productName.setText(orderItems.get(i).getmProductName());
                item.productQtd.setText(getString(R.string.my_order_quantity_label) + " " + orderItems.get(i).getmProductQuantity());
                if (i == (orderItems.size() - 1)) {
                    item.productBottomDivider.setVisibility(View.GONE);
                    item.bottomSpace.setVisibility(View.VISIBLE);
                }
                itemView.setTag(item);
                productsContainer.addView(itemView);
            }
        }
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
        if(ordersAdapter != null){
            outState.putInt("selectedPos", ordersAdapter.getSelectedPosition());
            outState.putInt("currentPage", pageIndex);
            outState.putInt("totalPages", totalPages);
            if(ordersList != null && ordersList.size() > 0)
                outState.putParcelableArrayList("orders",ordersList );
        }
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
            if (totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
                if (!mIsLoadingMore && !mReceivedError && totalPages != pageIndex) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mIsLoadingMore = true;
                            Print.w("ORDER", "LOAD MORE");
                            showProductsLoading(true);
                            getMoreProducts();
                        }
                    });
                }
            } 
            //else {
            //      mReceivedError = false;
            //}

        }
    };

    /**
     * control if the loading more view is shown or not
     * @param isToShow
     */
    private void showProductsLoading(boolean isToShow){
        if(isToShow)
            loadMore.setVisibility(View.VISIBLE);
        else
            loadMore.setVisibility(View.GONE);
    }
    
    /**
     * function that gets more products when the user is scrooling
     */
    private void getMoreProducts(){
        
        Bundle bundle = new Bundle();
        
        bundle.putInt(GetMyOrdersListHelper.PAGE_NUMBER, pageIndex + 1);
        bundle.putInt(GetMyOrdersListHelper.PER_PAGE, NUM_ORDERS);
        triggerContentEventNoLoading(new GetMyOrdersListHelper(), bundle, mCallBack);
        
    }

}