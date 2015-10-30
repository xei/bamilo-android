package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.OrderedProductAdapter;
import com.mobile.helpers.checkout.GetTrackOrderHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.objects.orders.OrderTracker;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by rsoares on 10/20/15.
 */
public class OrderStatusFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = OrderStatusFragment.class.getSimpleName();

    public final static String ORDER = Order.class.getSimpleName();

    private OrderTracker mOrderTracker;

    private TextView mOrderNumberTitle;
    private TextView mTotalProducts;
    private TextView mGrandTotal;
    private TextView mDelivery ;

    //Customer
    private TextView mCustomername;
    private TextView mCustomerPhone;

    //Shipping address
    private TextView mAddress;

    //payment details
    private TextView mpaymentType;
    private TextView mpaymentDetail;

    //products list
    private OrderedProductAdapter mOrderedProductAdapter;
    private RecyclerView recyclerListProducts;


    /**
     * Get instance
     */
    public static OrderStatusFragment getInstance(Bundle bundle) {
        OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
        orderStatusFragment.setArguments(bundle);
        return orderStatusFragment;
    }

    /**
     * Empty constructor
     */
    public OrderStatusFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyOrders,
                R.layout.order_status_fragment,
                R.string.order_status_label,
                KeyboardState.ADJUST_CONTENT);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        Print.d(TAG, "ON CREATE");
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            triggerOrder(getArguments().getString(ORDER));
        } else {

        }
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Print.d(TAG, "ON VIEW CREATED");
        super.onViewCreated(view, savedInstanceState);
        //load header title
        mOrderNumberTitle = (TextView) view.findViewById(R.id.order_number_title);
        mTotalProducts = (TextView) view.findViewById(R.id.total_products);
        mGrandTotal = (TextView) view.findViewById(R.id.grand_total);
        mDelivery = (TextView) view.findViewById(R.id.delivery);

        //Customer
        mCustomername = (TextView) view.findViewById(R.id.customer_name);
        mCustomerPhone = (TextView) view.findViewById(R.id.customer_phone);

        //Shipping address
        mAddress = (TextView) view.findViewById(R.id.shipping_address);

        //payment details
        mpaymentType = (TextView) view.findViewById(R.id.payment_type);
        mpaymentDetail = (TextView) view.findViewById(R.id.payment_detail);

        //products
        recyclerListProducts = (RecyclerView) view.findViewById(R.id.productsList);

        //reverse order of child in linear horizontal layouts for inverting the weight proportions
        if(ShopSelector.isRtl() && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){

            LinearLayout line1 = (LinearLayout) view.findViewById(R.id.line1);
            LinearLayout line2 = (LinearLayout) view.findViewById(R.id.line2);

            reverseLayoutChildsIfRTL(line1);
            reverseLayoutChildsIfRTL(line2);


        }

    }


    /**
     * Allow in aLinearlayout horizontal oriented to put it's children in reversed order; this is needed to invert weigh layouts if is RTL and API > 17
     * */
    private void reverseLayoutChildsIfRTL(ViewGroup view)
    {
        for(int k = view.getChildCount()-1 ; k >= 0 ; k--)
        {
            View item = view.getChildAt(k);
            view.removeViewAt(k);
            view.addView(item);
        }
    }



    private void loadViews()
    {


        mOrderNumberTitle.setText(getResources().getString(R.string.order_number_header, mOrderTracker.getId()));

        if(mOrderTracker.getTotal_products() == 1) {
            mTotalProducts.setText(getResources().getString(R.string.plurals_item, mOrderTracker.getTotal_products()));
        }
        else {
            mTotalProducts.setText(getResources().getString(R.string.plurals_items, mOrderTracker.getTotal_products()));
        }

        mGrandTotal.setText(mOrderTracker.getGrand_total());
      //  mDelivery.setText("");      //delivery ?

        Address shippingAddress = mOrderTracker.getShippingAddress();
        mAddress.setText(shippingAddress.getAddress()+"\n"+shippingAddress.getCity()+"\n"+shippingAddress.getRegion());
        mCustomerPhone.setText(shippingAddress.getPhone());

        mCustomername.setText(shippingAddress.getFirstName()+" "+shippingAddress.getLastName());

        //payment
        mpaymentType.setText(mOrderTracker.getPayment_method());
        mpaymentDetail.setVisibility(View.GONE);    //if doesn't have detail


        if(mOrderedProductAdapter == null) {
            mOrderedProductAdapter = new OrderedProductAdapter((List) mOrderTracker.getOrderTrackerItems());
            mOrderedProductAdapter.setResources(getResources());
        }



        recyclerListProducts.setNestedScrollingEnabled(false);
        recyclerListProducts.setHasFixedSize(true);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mGridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerListProducts.setLayoutManager(mGridLayoutManager);
        recyclerListProducts.setItemAnimator(new DefaultItemAnimator());

        recyclerListProducts.setAdapter(mOrderedProductAdapter);


    }




    private void triggerOrder(String orderNr) {
        triggerContentEvent(new GetTrackOrderHelper(), GetTrackOrderHelper.createBundle(orderNr), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        switch (eventType){
            case TRACK_ORDER_EVENT:
                mOrderTracker = (OrderTracker) baseResponse.getMetadata().getData();
                loadViews();
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");

        // Specific errors
        EventType eventType = baseResponse.getEventType();
        ErrorCode errorCode = baseResponse.getError().getErrorCode();

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Generic errors
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }

        switch (eventType){
            case TRACK_ORDER_EVENT:
                break;
        }
    }
}
