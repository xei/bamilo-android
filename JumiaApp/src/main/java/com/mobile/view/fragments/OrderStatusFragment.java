package com.mobile.view.fragments;

import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Created by rsoares on 10/20/15.
 */
public class OrderStatusFragment extends BaseFragment {

    /**
     * Get instance
     */
    public static OrderStatusFragment getInstance() {
        return new OrderStatusFragment();
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



}
