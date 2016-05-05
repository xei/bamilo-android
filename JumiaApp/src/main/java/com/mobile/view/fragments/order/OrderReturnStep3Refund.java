package com.mobile.view.fragments.order;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.order.GetReturnRefundFormHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.order.ReturnOrderViewHolder;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Fragment used to show the online returns reason.
 * @author spereira
 */
public class OrderReturnStep3Refund extends OrderReturnStepBase {

    protected ViewGroup mReturnRefundFormContainer;
    protected ViewGroup mReturnRefundItemsContainer;
    protected DynamicForm mReturnRefundFormGenerator;
    protected Form mFormResponse;

    private Bundle mFormSavedState;

    /**
     * Empty constructor
     */
    public OrderReturnStep3Refund() {
        super(OrderReturnStepsMain.REFUND, R.string.order_return_refund_title, R.string.continue_label);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mFormResponse = savedInstanceState.getParcelable(ConstantsIntentExtra.DATA);
            mFormSavedState = savedInstanceState.getParcelable(ConstantsIntentExtra.ARG_1);
        }
    }

    /*
         * (non-Javadoc)
         * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
         */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        View.inflate(getBaseActivity(), R.layout._def_order_return_step2_method, mContainer);
        mReturnRefundFormContainer = (ViewGroup) mContainer.findViewById(R.id.form_container);
        mReturnRefundItemsContainer = (ViewGroup) mContainer.findViewById(R.id.items_container);
        // Get button
        TextView button = (TextView) view.findViewById(R.id.order_return_main_button_ok);
        button.setOnClickListener(this);

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && mFormResponse != null){
            loadReturnRefundForm(mFormResponse);
        } else {
            triggerReturnRefundForm();
        }
    }

    /**
     * Load the dynamic form
     */
    protected void loadReturnRefundForm(Form form) {

        // Return Method form
        mReturnRefundFormGenerator = FormFactory.create(FormConstants.RETURN_METHOD_FORM, getBaseActivity(), form).addOnClickListener(this);
        if(mFormSavedState != null)
        mReturnRefundFormGenerator.loadSaveFormState(mFormSavedState);
        mReturnRefundFormContainer.removeAllViews();
        mReturnRefundFormContainer.addView(mReturnRefundFormGenerator.getContainer());
        mReturnRefundFormContainer.refreshDrawableState();
        loadItems();
    }

    /**
     * Load Items list
     */
    private void loadItems(){
        mReturnRefundItemsContainer.removeAllViews();
        ArrayList<OrderTrackerItem> items = getOrderItems();
        for (OrderTrackerItem  orderItem : items) {
            ReturnOrderViewHolder custom = new ReturnOrderViewHolder(getContext(), getOrderNumber(), orderItem);
            mReturnRefundItemsContainer.addView(custom.getView());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState(){
        // Save the state
        Bundle bundle = new Bundle();
        if(mReturnRefundFormGenerator != null) {
            mReturnRefundFormGenerator.saveFormState(bundle);
        }
        mFormSavedState = bundle;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveState();
        outState.putParcelable(ConstantsIntentExtra.DATA, mFormResponse);
        outState.putParcelable(ConstantsIntentExtra.ARG_1, mFormSavedState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        // Case next step
        if (view.getId() == R.id.order_return_main_button_ok && mReturnRefundFormGenerator != null) {
            if(mReturnRefundFormGenerator.validate()){
                // Get data from forms
                ContentValues values = mReturnRefundFormGenerator.save();
                // Save data
                super.saveSubmittedValues(values);
                // Next Step
                super.onClickNextStep();

                saveState();
            } // If there is no Child Form, show global message
            else if(mReturnRefundFormGenerator.showGlobalMessage()) {
                String message = TextUtils.isNotEmpty(mReturnRefundFormGenerator.getErrorMessage()) ?
                        mReturnRefundFormGenerator.getErrorMessage() : getString(R.string.warning_please_select_one);
                showWarningErrorMessage(message);
            }

        } else {
            super.onClick(view);
        }
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerReturnRefundForm() {
        triggerContentEvent(new GetReturnRefundFormHelper(), null, this);
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    protected void onClickRetryButton(View view) {
        triggerReturnRefundForm();
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        EventType eventType = response.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        if(isOnStoppingProcess || eventType == null){
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        switch (eventType) {
            case RETURN_REFUND_FORM_EVENT:
                // Form
                Form form = (Form) response.getContentData();
                mFormResponse = form;
                loadReturnRefundForm(mFormResponse);
                // Show container
                showFragmentContentContainer();
                break;
        }
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        // Case RETURN_REFUND_FORM_EVENT
        showFragmentErrorRetry();
    }

}
