/**
 * 
 */
package com.mobile.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.OrdersListAdapter;
import com.mobile.controllers.OrdersListAdapter.OnSelectedOrderChange;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.objects.Order;
import com.mobile.framework.objects.OrderItem;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.DeviceInfoHelper;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.account.GetMyOrdersListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author Paulo Carvalho
 * 
 */
public class OrderHistoryFragment extends BaseFragment implements OnClickListener, OnSelectedOrderChange{

    private static final String TAG = LogTagHelper.create(OrderHistoryFragment.class);

    private static OrderHistoryFragment mOrderHistoryFragment;

    private ArrayList<Order> ordersList = new ArrayList<Order>();
    
    private ListView ordersListView;
        
    private TextView ordersProductsPayment;
    
    private TextView ordersProductDate;
    
    private TextView noOrders;
    
    private LinearLayout productsLanscapeContainer;
    
    private RelativeLayout productsContainer;
    
    OrdersListAdapter ordersAdapter;
    
    
    private boolean isVisible = false;
    
    private MyOrdersFragment parentFragment;
    
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
     * 
     * @return
     */
    public static OrderHistoryFragment getInstance() {
        mOrderHistoryFragment = new OrderHistoryFragment();
        return mOrderHistoryFragment;
    }

    /**
     * Empty constructor
     */
    public OrderHistoryFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyOrders,
                R.layout.order_history_main,
                R.string.my_orders_label,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        parentFragment = (MyOrdersFragment) getBaseActivity().getSupportFragmentManager().findFragmentByTag(FragmentType.MY_ORDERS.toString());
        
        if(savedInstanceState != null) {
            selectedProduct = savedInstanceState.getInt("selectedPos");
            pageIndex = savedInstanceState.getInt("currentPage");
            totalPages = savedInstanceState.getInt("totalPages");
            if(savedInstanceState.containsKey("orders"))
                ordersList = savedInstanceState.getParcelableArrayList("orders");
            
                Log.i("ORDER", "ON LOAD SAVED STATE ordersList size:"+ordersList.size());
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
        Log.i(TAG, "ON VIEW CREATED");               
        
        if(parentFragment != null)
            MyOrdersFragment.mPositionToStart = 1;
        
        ordersListView = (ListView) view.findViewById(R.id.orders_list);
        ordersListView.setOnScrollListener(onScrollListener);
        noOrders = (TextView) view.findViewById(R.id.no_orders_title);
        loadMore = (LinearLayout) view.findViewById(R.id.loadmore);
        if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
            ordersProductsPayment = (TextView) view.findViewById(R.id.order_list_payment);
            ordersProductDate = (TextView) view.findViewById(R.id.order_list_date);
            productsContainer = (RelativeLayout) view.findViewById(R.id.order_products_container);
            productsLanscapeContainer = (LinearLayout) view.findViewById(R.id.orders_products_landscape_list);
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
                productsLanscapeContainer.setVisibility(View.GONE);
            } else {
                ordersListView.setVisibility(View.GONE);
            }
        }else{
            noOrders.setVisibility(View.GONE);
            
            if(DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
                productsContainer.setVisibility(View.VISIBLE);
                ordersListView.setVisibility(View.VISIBLE);
                productsLanscapeContainer.setVisibility(View.VISIBLE);
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
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        
        if(JumiaApplication.mIsBound){
            triggerGetOrderList();            
        } else {
            showFragmentRetry(this);
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
    }


    IResponseCallback mCallBack = new IResponseCallback() {
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };
    
    
    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");
        mReceivedError = false;
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if(super.handleSuccessEvent(bundle))
            return true;
        
            
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        switch (eventType) {
        case GET_MY_ORDERS_LIST_EVENT:
            
            ArrayList<Order> ordersResponse =  bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            
            if(ordersResponse == null || ordersResponse.size() == 0){
                // show error/empty screen
                setEmptyScreenState(true);
                showProductsLoading(false);
                return false;
            }
            
            if(bundle.getInt(GetMyOrdersListHelper.CURRENT_PAGE) != 0 && bundle.getInt(GetMyOrdersListHelper.TOTAL_PAGES) != 0){
                if(pageIndex == bundle.getInt(GetMyOrdersListHelper.CURRENT_PAGE)){
                    //is already showing the last page of the orders
                    mIsLoadingMore = true;
                    showProductsLoading(false);
                }
                
                pageIndex = bundle.getInt(GetMyOrdersListHelper.CURRENT_PAGE);
                totalPages = bundle.getInt(GetMyOrdersListHelper.TOTAL_PAGES);
                if(pageIndex <= totalPages){
                    mIsLoadingMore = false;
                    
                    if(ordersList != null && ordersList.size() > 0 && ordersAdapter != null){
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
            }
            
            return true;

        default:
            break;
        }
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        Log.d(TAG, "ON ERROR EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        boolean errorHandled = false;
        if(super.handleErrorEvent(bundle))
            errorHandled = true;
        

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        switch (eventType) {
        case GET_MY_ORDERS_LIST_EVENT:

           
            if(isVisible && !errorHandled){
                Log.w("ORDER","ERROR Visible");
                    if(null == JumiaApplication.CUSTOMER){
                        triggerLogin();
                    } else {
                        Log.w("ORDER","ERROR Visible");
                        //used for when the user session expires on the server side
                        if(ordersList != null && ordersList.size() > 0)
                            triggerLogin();
                        else{
                            setEmptyScreenState(true);
                            showProductsLoading(false);
                        }

                    }

                
            } else {
                Log.w("ORDER","ERROR notVisible");
                if(null != JumiaApplication.CUSTOMER){
                    Log.w("ORDER","ERROR CUSTOMER != null");
                    mReceivedError = true;
//                    setEmptyScreenState(true);
//                    showProductsLoading(false);
                }else{
                    Log.w("ORDER","ERROR CUSTOMER == null");
                    mReceivedError = true;
                }
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
                
                if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct != -1) setOrderProducts(ordersList.get(selectedProduct),productsLanscapeContainer,true);
                else if(DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct == -1) setOrderProducts(ordersList.get(0),productsLanscapeContainer,true);
                
                ordersListView.setSelection(selectedProduct);
            } else{
                ordersListView.setAdapter(ordersAdapter);
                ordersAdapter.setSelectedPosition(selectedProduct);
                ordersAdapter.updateOrders(ordersList);
                ordersListView.setSelection(selectedProduct);
                
                if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct != -1) setOrderProducts(ordersList.get(selectedProduct),productsLanscapeContainer,true);
                else if(DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && selectedProduct == -1) setOrderProducts(ordersList.get(0),productsLanscapeContainer,true);
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
            productsLanscapeContainer.setVisibility(View.VISIBLE);
            
            setOrderProducts(orders.get(selectedProduct),productsLanscapeContainer, true);
//            setupProductslandScape(orders.get(selectedProduct));
        } else {
            ordersListView.setVisibility(View.VISIBLE);
            if(selectedProduct != -1) {
                ordersAdapter.setSelectedPosition(selectedProduct);
                ordersAdapter.notifyDataSetChanged();
            }
            
        }
            
        
  }
    
//    /**
//     * setup products on landscape view
//     * 
//     * @param order
//     */
//    private void setupProductslandScape(Order order){
//     
////        ordersProductsPayment.setText(order.getmPayment());
////        ordersProductDate.setText(order.getmDate());
////        if (BaseActivity.isTabletInLandscape(getBaseActivity())) setOrderProducts(order,productsLanscapeContainer, true);
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fragment_root_retry_button) {
            Bundle bundle = new Bundle();
            getBaseActivity().onSwitchFragment(FragmentType.MY_ORDERS, bundle, FragmentController.ADD_TO_BACK_STACK);

        }
    }

    /**
     * interface from the adapter to act when the user chooses a order
     */
    @Override
    public void SelectedOrder(Order order, ViewGroup productsContainer, boolean toShowInnerProds, int selectedProd) {
        
        selectedProduct = selectedProd;
        
        if (!DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) setOrderProducts(order,productsContainer, toShowInnerProds);
        else setOrderProducts(order,productsLanscapeContainer,toShowInnerProds);
        
    }
    
    /**
     * inflate order products into a linear layout of the corresponding order
     * 
     * @param order
     * @param productsContainer
     * @param toShowInnerProds
     */
    private void setOrderProducts(Order order, ViewGroup productsContainer, boolean toShowInnerProds){
        class Item {
            public TextView productName;
            public TextView productQtd;
            public TextView productPrice;
            public View productBottomDivider;
            public View bottomSpace;
       }
        if(ordersAdapter != null)
            ordersAdapter.setSelectedPosition(selectedProduct);
        
        ordersAdapter.notifyDataSetChanged();
        
        if (toShowInnerProds && DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
            if(productsLanscapeContainer.getChildCount() > 0)
                productsLanscapeContainer.removeAllViews();
            
            ordersProductsPayment.setText(order.getmPayment());
            ordersProductDate.setText(order.getmDate());
        }

        
       if(toShowInnerProds && productsContainer.getChildCount() == 0) {
           
           ArrayList<OrderItem> orderItems = order.getmOrderProducts();
           
           for (int i = 0; i < orderItems.size(); i++) {
               View itemView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.order_history_product_item, productsContainer, false);

               Item item = new Item();
               
               item.productName = (TextView) itemView.findViewById(R.id.order_product_name);
               item.productQtd = (TextView) itemView.findViewById(R.id.order_product_quantity);
               item.productPrice = (TextView) itemView.findViewById(R.id.order_product_price);
               item.productBottomDivider = (View) itemView.findViewById(R.id.order_product_divider);
               item.bottomSpace = (View) itemView.findViewById(R.id.order_product_bottom_space);
               
               if(!"null".equals(orderItems.get(i).getmProductTotalString()))
                   item.productPrice.setText(CurrencyFormatter.formatCurrency(orderItems.get(i).getmProductTotalString()));
               else
                   item.productPrice.setText(CurrencyFormatter.getCurrencyCode()+" "+orderItems.get(i).getmProductTotal());
               
               item.productName.setText(orderItems.get(i).getmProductName()+" - ");
               item.productQtd.setText(getString(R.string.my_order_quantity_label)+" "+orderItems.get(i).getmProductQuantity());
               if(i == (orderItems.size() - 1)){
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
      //set pager position to the original
      if(null != parentFragment)
          parentFragment.setPagerPosition(0);
      
      getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
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
                            Log.w("ORDER","LOAD MORE");
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
        triggerContentEventWithNoLoading(new GetMyOrdersListHelper(), bundle, mCallBack);
        
    }

}