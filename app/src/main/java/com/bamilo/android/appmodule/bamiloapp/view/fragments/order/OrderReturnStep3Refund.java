package com.bamilo.android.appmodule.bamiloapp.view.fragments.order;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.appmodule.bamiloapp.helpers.order.GetReturnRefundFormHelper;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.objects.orders.OrderTrackerItem;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.utils.RadioGroupExpandable;
import com.bamilo.android.appmodule.bamiloapp.utils.order.ReturnItemViewHolder;
import com.bamilo.android.R;

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
    }

    @Override
    protected void onCreateInstanceState(@NonNull Bundle bundle) {
        super.onCreateInstanceState(bundle);
        mFormResponse = bundle.getParcelable(ConstantsIntentExtra.DATA);
        mFormSavedState = bundle.getParcelable(ConstantsIntentExtra.ARG_1);
    }


    /*
         * (non-Javadoc)
         * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
         */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View.inflate(getBaseActivity(), R.layout._def_order_return_step2_method, mContainer);
        mReturnRefundFormContainer = mContainer.findViewById(R.id.form_container);
        mReturnRefundItemsContainer = mContainer.findViewById(R.id.items_container);
        // Get button
        TextView button = view.findViewById(R.id.order_return_main_button_ok);
        button.setOnClickListener(this);
        triggerReturnRefundForm();

    }

    /**
     * Load the dynamic form
     */
    protected void loadReturnRefundForm(Form form) {

        if(mReturnRefundFormContainer != null){
            // Return Method form
            mReturnRefundFormGenerator = FormFactory.create(FormConstants.RETURN_METHOD_FORM, getBaseActivity(), form).addOnClickListener(this);
            mReturnRefundFormGenerator.loadSaveFormState(mFormSavedState);
            mReturnRefundFormContainer.removeAllViews();
            mReturnRefundFormContainer.addView(mReturnRefundFormGenerator.getContainer());
            mReturnRefundFormContainer.refreshDrawableState();
            loadItems();
        }

    }

    /**
     * Load Items list
     */
    private void loadItems(){
        mReturnRefundItemsContainer.removeAllViews();
        ArrayList<OrderTrackerItem> items = getOrderItems();
        for (OrderTrackerItem  orderItem : items) {
            ContentValues values = getSubmittedStepValues(OrderReturnStepsMain.REASON);
            String quantity = OrderReturnStep1Reason.getQuantityValue(values, orderItem.getSku());
            ReturnItemViewHolder custom = new ReturnItemViewHolder(getContext(), getOrderNumber(), orderItem).addQuantity(quantity).bind().showQuantityToReturnText();
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
            // hide keyboard
            getBaseActivity().hideKeyboard();
            if(mReturnRefundFormGenerator.validate()){
                // Get data from forms
                ContentValues values = mReturnRefundFormGenerator.save();
                // Hammered to get refund label
                hammeredToGetRefundLabel(values, mReturnRefundFormGenerator);
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

        if(isOnStoppingProcess || eventType == null){
            return;
        }

        switch (eventType) {
            case RETURN_REFUND_FORM_EVENT:
                // Form
                mFormResponse = (Form) response.getContentData();
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


    /**
     * This hammered was added because all return steps are being saved in the app side.<br>
     * And we need save the selected labels to show in the finish step.
     * TODO: NAFAMZ-16058 - Hammered dded in v3.2
     */
    @SuppressWarnings("ConstantConditions")
    private void hammeredToGetRefundLabel(@NonNull ContentValues result, @NonNull DynamicForm form) {
        try {
            // Save readable reason
            View view = form.getItemByKey(RestConstants.METHOD).getDataControl();
            String label = ((RadioGroupExpandable) view).getSelectedLabel();
            result.put(RestConstants.REFUND, label);
        } catch (NullPointerException e) {
        }
    }

    /**
     * This hammered was added to get the label.
     * TODO: NAFAMZ-16058 - Hammered dded in v3.2
     */
    @Nullable
    public static String getRefundLabel(@Nullable ContentValues values) {
        return values != null ? values.getAsString(RestConstants.REFUND) : null;
    }

}
