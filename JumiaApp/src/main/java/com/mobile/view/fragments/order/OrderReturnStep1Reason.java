package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.view.View;

import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.order.GetReturnReasonFormHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.view.R;

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
            triggerGetReasonForm();
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
    private void loadForm(Form form) {
        // Create form view
        mDynamicForm = FormFactory.getSingleton().create(FormConstants.ORDER_RETURN_RESON_FORM, getContext(), form);
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

    private void triggerGetReasonForm() {
        triggerContentEvent(new GetReturnReasonFormHelper(), null, this);
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    protected void onClickRetryButton(View view) {
        triggerGetReasonForm();
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        // Show form
        Form form = (Form) response.getContentData();
        if (null != form) {
            loadForm(form);
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        showFragmentErrorRetry();
    }

}
