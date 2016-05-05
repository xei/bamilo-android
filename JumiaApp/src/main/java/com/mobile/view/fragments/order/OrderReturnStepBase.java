package com.mobile.view.fragments.order;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragmentRequester;

import java.util.ArrayList;

/**
 * Fragment used to show the online returns reason.
 *
 * @author spereira
 */
public abstract class OrderReturnStepBase extends BaseFragmentRequester {

    @OrderReturnStepsMain.ReturnStepType
    private final int mStep;
    @StringRes
    private final int mTitle;
    @StringRes
    private final int mButton;

    protected ViewGroup mContainer;

    /**
     * Empty constructor
     */
    public OrderReturnStepBase(@OrderReturnStepsMain.ReturnStepType int step, @StringRes int title, @StringRes int button) {
        super(IS_NESTED_FRAGMENT, R.layout._def_order_return_steps);
        this.mStep = step;
        this.mTitle = title;
        this.mButton = button;
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Set title
        ((TextView) view.findViewById(R.id.order_return_main_title)).setText(mTitle);
        // Get container
        mContainer = (ViewGroup) view.findViewById(R.id.order_return_main_inflate);
        // Set button
        TextView button = (TextView) view.findViewById(R.id.order_return_main_button_ok);
        button.setText(mButton);
        button.setOnClickListener(this);
    }

    /**
     * Get items from parent
     */
    protected ArrayList<OrderTrackerItem> getOrderItems() {
        return ((OrderReturnStepsMain) getParentFragment()).getOrderItems();
    }

    /**
     * Get order number from parent
     */
    protected String getOrderNumber() {
        return ((OrderReturnStepsMain) getParentFragment()).getOrderNumber();
    }

    /*
     * ##### STEP VALUES #####
     */

    /**
     * Save submitted values
     */
    protected void saveSubmittedValues(ContentValues values) {
        OrderReturnStepsMain parent = (OrderReturnStepsMain) getParentFragment();
        if (parent != null) {
            parent.saveSubmittedValuesFromStep(mStep, values);
        }
    }

    /**
     * Validate submitted values
     */
    protected boolean hasSubmittedValuesToFinish() {
        OrderReturnStepsMain parent = (OrderReturnStepsMain) getParentFragment();
        return parent != null && parent.hasSubmittedValuesToFinish();
    }

    /**
     * Get step values
     */
    protected ContentValues getSubmittedStepValues(@OrderReturnStepsMain.ReturnStepType int mStep) {
        OrderReturnStepsMain parent = (OrderReturnStepsMain) getParentFragment();
        if (parent != null) {
            return parent.getSubmittedValuesForStep(mStep);
        }
        return null;
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    public void onClick(View view) {
        // Case next step
        if (view.getId() == R.id.order_return_main_button_ok) {
            onClickNextStep();
        } else {
            super.onClick(view);
        }
    }

    protected void onClickNextStep() {
        OrderReturnStepsMain parent = (OrderReturnStepsMain) getParentFragment();
        if (parent != null) {
            parent.nextStep(mStep + 1);
        }
    }

}
