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
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.order.GetReturnMethodsFormHelper;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.objects.orders.OrderTrackerItem;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.utils.RadioGroupExpandable;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.order.ReturnItemViewHolder;
import com.bamilo.android.R;

import java.util.ArrayList;

/**
 * Fragment used to show the online returns reason.
 * @author spereira
 */
public class OrderReturnStep2Method extends OrderReturnStepBase {

    protected ViewGroup mReturnFormContainer;
    protected ViewGroup mReturnItemsContainer;
    protected DynamicForm mReturnFormGenerator;
    protected Form mFormResponse;

    private Bundle mFormSavedState;

    /**
     * Empty constructor
     */
    public OrderReturnStep2Method() {
        super(OrderReturnStepsMain.METHOD, R.string.order_return_method_title, R.string.continue_label);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View.inflate(getBaseActivity(), R.layout._def_order_return_step2_method, mContainer);
        mReturnFormContainer = mContainer.findViewById(R.id.form_container);
        mReturnItemsContainer = mContainer.findViewById(R.id.items_container);
        // Get button
        TextView button = view.findViewById(R.id.order_return_main_button_ok);
        button.setOnClickListener(this);
        triggerReturnMethodForm();

    }

    /**
     * Load the dynamic form
     */
    protected void loadReturnMethodForm(Form form) {
        if(mReturnFormContainer != null){
            // Return Method form
            mReturnFormGenerator = FormFactory.create(FormConstants.RETURN_METHOD_FORM, getBaseActivity(), form).addOnClickListener(this);
            mReturnFormGenerator.loadSaveFormState(mFormSavedState);
            mReturnFormContainer.removeAllViews();
            mReturnFormContainer.addView(mReturnFormGenerator.getContainer());
            mReturnFormContainer.refreshDrawableState();
            loadItems();
        }

    }

    /**
     * Load Items list
     */
    private void loadItems(){
        mReturnItemsContainer.removeAllViews();
        ArrayList<OrderTrackerItem> items = getOrderItems();
        for (OrderTrackerItem  orderItem : items) {
            ContentValues values = getSubmittedStepValues(OrderReturnStepsMain.REASON);
            String quantity = OrderReturnStep1Reason.getQuantityValue(values, orderItem.getSku());
            ReturnItemViewHolder custom = new ReturnItemViewHolder(getContext(), getOrderNumber(), orderItem).addQuantity(quantity).bind().showQuantityToReturnText();
            mReturnItemsContainer.addView(custom.getView());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the state
        saveState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveState();
        outState.putParcelable(ConstantsIntentExtra.DATA, mFormResponse);
        outState.putParcelable(ConstantsIntentExtra.ARG_1, mFormSavedState);
        super.onSaveInstanceState(outState);
    }

    private void saveState(){
        Bundle bundle = new Bundle();
        if(mReturnFormGenerator != null) {
            mReturnFormGenerator.saveFormState(bundle);
        }
        mFormSavedState = bundle;
    }

    @Override
    public void onClick(View view) {
        // Case next step
        if (view.getId() == R.id.order_return_main_button_ok && mReturnFormGenerator != null) {
            if(mReturnFormGenerator.validate()){
                // Get data from forms
                ContentValues values = mReturnFormGenerator.save();
                // Hammered to get method label
                hammeredToGetMethodLabel(values, mReturnFormGenerator);
                // Save data
                super.saveSubmittedValues(values);
                // Next Step
                super.onClickNextStep();

                Bundle bundle = new Bundle();
                mReturnFormGenerator.saveFormState(bundle);
                mFormSavedState = bundle;
            } else if(mReturnFormGenerator.showGlobalMessage()){
                String message = TextUtils.isNotEmpty(mReturnFormGenerator.getErrorMessage()) ?
                        mReturnFormGenerator.getErrorMessage() : getString(R.string.warning_please_select_one);
                showWarningErrorMessage(message);
            }

        } else if(view.getId() == R.id.radio_expandable_text){
            if(TextUtils.isNotEmpty((String) view.getTag(R.id.target_link))) {
                // Case mob api
                @TargetLink.Type String link = (String) view.getTag(R.id.target_link);
                String title = (String) view.getTag(R.id.target_title);
                new TargetLink(getWeakBaseActivity(), link).addTitle(title).run();
            } else {
                String link = (String) view.getTag(R.id.html_link);
                String title = (String) view.getTag(R.id.target_title);
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.DATA, link);
                super.onSwitchTo(FragmentType.STATIC_WEBVIEW_PAGE)
                        .addTitle(title)
                        .addData(bundle)
                        .noBackStack()
                        .run();
            }
        } else {
            super.onClick(view);
        }
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerReturnMethodForm() {
        triggerContentEvent(new GetReturnMethodsFormHelper(), null, this);
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    protected void onClickRetryButton(View view) {
        triggerReturnMethodForm();
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
            case RETURN_METHODS_FORM_EVENT:
                // Form
                mFormResponse = (Form) response.getContentData();
                loadReturnMethodForm(mFormResponse);
                // Show container
                showFragmentContentContainer();
                break;
        }
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        // Case RETURN_METHODS_FORM_EVENT
        showFragmentErrorRetry();
    }


    /**
     * This hammered was added because all return steps are being saved in the app side.<br>
     * And we need save the selected labels to show in the finish step.
     * TODO: NAFAMZ-16058 - Hammered dded in v3.2
     */
    @SuppressWarnings("ConstantConditions")
    private void hammeredToGetMethodLabel(@NonNull ContentValues result, @NonNull DynamicForm form) {
        try {
            // Save readable reason
            View view = form.getItemByKey(RestConstants.METHOD).getDataControl();
            String label = ((RadioGroupExpandable) view).getSelectedLabel();
            result.put(RestConstants.METHOD, label);
        } catch (NullPointerException e) {
        }
    }

    /**
     * This hammered was added to get the label.
     * TODO: NAFAMZ-16058 - Hammered dded in v3.2
     */
    @Nullable
    public static String getMethodLabel(@Nullable ContentValues values) {
        return values != null ? values.getAsString(RestConstants.METHOD) : null;
    }

}
