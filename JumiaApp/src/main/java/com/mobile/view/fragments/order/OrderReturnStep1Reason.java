package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.view.View;

import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.order.GetReturnReasonFormHelper;
import com.mobile.newFramework.forms.ReturnReasonForm;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Fragment used to show the online returns reason.
 *
 * @author spereira
 */
public class OrderReturnStep1Reason extends OrderReturnStepBase {

    private DynamicForm mDynamicForm;

    /**
     * Empty constructor
     */
    public OrderReturnStep1Reason() {
        super(OrderReturnStepsMain.REASON, R.string.order_return_reason_title, R.string.continue_label);
    }

    /*
     * ##### LIFECYCLE #####
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Validate order items
        if (CollectionUtils.isNotEmpty(getOrderItems())) {
            triggerGetReasonForm(getOrderItems().size());
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Case rotation save state
        if (mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
    }

    /*
     * ##### LAYOUT #####
     */

    /**
     * Create and show the form
     */
    private void loadForm(ReturnReasonForm form, ArrayList<OrderTrackerItem> items) {
//        // Create form for each item
//        for (int i = 1; i < items.size(); i++) {
//            forms.add()
//        }
        // Create form view
        mDynamicForm = FormFactory.getSingleton().create(FormConstants.ORDER_RETURN_RESON_FORM, getContext(), form.get(0));
        // Load saved state
        mDynamicForm.loadSaveFormState(this.mSavedState);
        // Remove all views
        if (mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
        }
        // Add form view
        mContainer.addView(mDynamicForm.getContainer());
        // Show
        showFragmentContentContainer();
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerGetReasonForm(int number) {
        triggerContentEvent(new GetReturnReasonFormHelper(), GetReturnReasonFormHelper.createBundle(number), this);
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    protected void onClickRetryButton(View view) {
        // Validate order items
        if (CollectionUtils.isNotEmpty(getOrderItems())) {
            triggerGetReasonForm(getOrderItems().size());
        } else {
            showFragmentErrorRetry();
        }
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        // Get form
        ReturnReasonForm form = (ReturnReasonForm) response.getContentData();
        // Get items
        ArrayList<OrderTrackerItem> items = getOrderItems();
        // Validate data
        if (null != form && CollectionUtils.isNotEmpty(items)) {
            loadForm(form, items);
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        showFragmentErrorRetry();
    }

}
