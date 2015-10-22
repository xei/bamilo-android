package com.mobile.view.fragments;

import android.os.Bundle;

import com.mobile.helpers.checkout.GetTrackOrderHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.objects.orders.OrderTracker;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Created by rsoares on 10/20/15.
 */
public class OrderStatusFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = OrderStatusFragment.class.getSimpleName();

    public final static String ORDER = Order.class.getSimpleName();

    private OrderTracker mOrderTracker;

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
                R.layout._def_order_status_fragment,
                R.string.order_status_label,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            triggerOrder(getArguments().getString(ORDER));
        } else {

        }
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
