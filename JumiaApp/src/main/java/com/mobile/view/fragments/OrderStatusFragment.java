package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.helpers.checkout.GetTrackOrderHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.orders.OrderTracker;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

/**
 * Created by alexandrapires on 10/26/15.
 */
public class OrderStatusFragment extends BaseFragment implements IResponseCallback {

    public static final String TAG = OrderStatusFragment.class.getSimpleName();

    private String mOrderNumber;

    private OrderTracker mOrderTracker;

    private TextView mtxOrderNumber;

    private TextView mOrderCreationDate;

    private TextView mGrandTotal;

    private TextView mTotalProducts;

    private TextView mOrderDate;



    private TextView mPaymentMethod;

    /**
     * Get instance
     */
    public static OrderStatusFragment getInstance() {
        return new OrderStatusFragment();
    }


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
        super(IS_NESTED_FRAGMENT, R.layout._def_order_status);
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if(arguments != null) {
            mOrderNumber = arguments.getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR);
        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadView(view);
        Print.i(TAG, "ON VIEW CREATED");

    }






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
        triggerGetTrackOrder(mOrderNumber);
        //   setupView();
    }




    @Override
    public void onRequestError(BaseResponse baseResponse) {

        if(super.handleSuccessEvent(baseResponse))
            return;

        Print.d(TAG, "ON ERROR EVENT");
        super.handleErrorEvent(baseResponse);

    }




    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

        hideActivityProgress();

        Print.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if(super.handleSuccessEvent(baseResponse))
            return;

        Print.d(TAG, "ON SUCCESS EVENT");
        mOrderTracker = (OrderTracker) baseResponse.getMetadata().getData();
        fillOrderDetails();


    }


    private void loadView(View view)
    {
        mtxOrderNumber = (TextView) view.findViewById(R.id.order_number);
        mOrderDate = (TextView) view.findViewById(R.id.order_date);
        mPaymentMethod = (TextView) view.findViewById(R.id.payment_method);

        mOrderCreationDate = (TextView) view.findViewById(R.id.creation_date);
        mGrandTotal = (TextView) view.findViewById(R.id.grand_total);
        mTotalProducts = (TextView) view.findViewById(R.id.total_products);
    }


    private void fillOrderDetails()
    {
        Print.w(TAG, "FILLING ORDER STATUS INFO");
        mtxOrderNumber.setText("ORDER NR " + mOrderTracker.getOrder_number());
        mOrderDate.setText("Date: "+mOrderTracker.getDate());
        mPaymentMethod.setText("Payment Method: " + mOrderTracker.getPaymentMethod());
        mOrderCreationDate.setText("Creation date: "+mOrderTracker.getCreation_date());
        mGrandTotal.setText("Grand Total: "+mOrderTracker.getGrand_total());
        mTotalProducts.setText("Total Products: " + mOrderTracker.getTotal_products());
   }




    private void triggerGetTrackOrder(String orderNumber) {
        triggerContentEventProgress(new GetTrackOrderHelper(), GetTrackOrderHelper.createBundle(orderNumber), this);
    }


}
