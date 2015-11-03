package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.checkout.GetOrderStatusHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.orders.OrderStatus;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class used ...
 * @author spereira
 */
public class OrderStatusFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = OrderStatusFragment.class.getSimpleName();

    private OrderStatus mOrderTracker;

    private String mOrderNumber;

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
        Print.i(TAG, "ON CREATE");
        // Get data
        savedInstanceState = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (savedInstanceState != null) {
            mOrderNumber = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
        }

        mOrderNumber = "300065329";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Validate the sate
        if (TextUtils.isNotEmpty(mOrderNumber)) {
            triggerOrder(mOrderNumber);
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE SATE");
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
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
     * ###### TRIGGERS ######
     */

    private void triggerOrder(String orderNr) {
        triggerContentEvent(new GetOrderStatusHelper(), GetOrderStatusHelper.createBundle(orderNr), this);
    }

    /*
     * ###### RESPONSES ######
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        // Get order status
        mOrderTracker = (OrderStatus) baseResponse.getMetadata().getData();
        // Show order
        // Show container
        showFragmentContentContainer();
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Specific errors
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
        // Validate generic errors
        if (!super.handleErrorEvent(baseResponse)) {
            // Show retry
            showFragmentErrorRetry();
        }
    }
}
