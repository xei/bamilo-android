package com.bamilo.android.appmodule.bamiloapp.view.fragments.order;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.bamilo.android.framework.service.objects.orders.OrderTrackerItem;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragmentRequester;

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
        // Set title
        ((TextView) view.findViewById(R.id.order_return_main_title)).setText(mTitle);
        // Get container
        mContainer = view.findViewById(R.id.order_return_main_inflate);
        // Set button
        TextView button = view.findViewById(R.id.order_return_main_button_ok);
        button.setText(mButton);
        button.setOnClickListener(this);
    }

    /**
     * Get items from parent
     */
    protected ArrayList<OrderTrackerItem> getOrderItems() {
        return ((OrderReturnStepsMain) getParentFragment()).getReturnItems();
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

    /**
     * Get submitted values
     */
    protected  ContentValues getSubmittedValues(){
        OrderReturnStepsMain parent = (OrderReturnStepsMain) getParentFragment();
        if (parent != null) {
            return parent.getSubmittedValues();
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
            // Hide keyboard
            getBaseActivity().hideKeyboard();
            // Next
            onClickNextStep();
        } else {
            super.onClick(view);
        }
    }

    protected void onClickNextStep() {
        onClickNextStep(mStep + 1);
    }

    protected void onClickNextStep(int step) {
        OrderReturnStepsMain parent = (OrderReturnStepsMain) getParentFragment();
        if (parent != null) {
            parent.onSwitchStep(step);
        }
    }

}
