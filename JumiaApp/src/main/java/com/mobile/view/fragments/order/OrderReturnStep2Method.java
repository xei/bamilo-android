package com.mobile.view.fragments.order;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.order.GetReturnMethodsFormHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.order.ReturnOrderViewHolder;
import com.mobile.view.R;

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

        if (savedInstanceState != null) {
            mFormResponse = (Form) savedInstanceState.getParcelable(ConstantsIntentExtra.DATA);
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
        mContainer.inflate(getBaseActivity(), R.layout._def_order_return_step2_method, mContainer);
        mReturnFormContainer = (ViewGroup) mContainer.findViewById(R.id.form_container);
        mReturnItemsContainer = (ViewGroup) mContainer.findViewById(R.id.items_container);
        // Get button
        TextView button = (TextView) view.findViewById(R.id.order_return_main_button_ok);
        button.setOnClickListener(this);

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && mFormResponse != null){
            loadReturnMethodForm(mFormResponse);
        } else {
            triggerReturnMethodForm();
        }
    }

    /**
     * Load the dynamic form
     */
    protected void loadReturnMethodForm(Form form) {
        Print.i(TAG, "LOAD EDIT ADDRESS FORM: ");
        // Return Method form
        mReturnFormGenerator = FormFactory.getSingleton().create(FormConstants.RETURN_METHOD_FORM, getBaseActivity(), form).addOnClickListener(this);
        mReturnFormGenerator.loadSaveFormState(mFormSavedState);
        mReturnFormContainer.removeAllViews();
        mReturnFormContainer.addView(mReturnFormGenerator.getContainer());
        mReturnFormContainer.refreshDrawableState();
        loadItems();
    }

    /**
     * Load Items list
     */
    private void loadItems(){
        mReturnItemsContainer.removeAllViews();
        ArrayList<OrderTrackerItem> items = getOrderItems();
        for (OrderTrackerItem  orderItem : items) {
            ReturnOrderViewHolder custom = new ReturnOrderViewHolder(getContext(), getOrderNumber(), orderItem);
            mReturnItemsContainer.addView(custom.getView());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the state
        Bundle bundle = new Bundle();
        if(mReturnFormGenerator != null) {
            mReturnFormGenerator.saveFormState(bundle);
        }
        mFormSavedState = bundle;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ConstantsIntentExtra.DATA, mFormResponse);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        // Case next step
        if (view.getId() == R.id.order_return_main_button_ok) {
            if(mReturnFormGenerator.validate()){
                // Get data from forms
                ContentValues values = mReturnFormGenerator.save();
                Print.i("code1save : "+values.toString());
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
                boolean result = new TargetLink(getWeakBaseActivity(), link).addTitle(title).run();
            } else {
                String link = (String) view.getTag(R.id.html_link);
                String title = (String) view.getTag(R.id.target_title);
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.DATA, link);
                bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
                getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
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
        // Show container
        showFragmentContentContainer();
        EventType eventType = response.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        if(isOnStoppingProcess || eventType == null){
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        switch (eventType) {
            case RETURN_METHODS_FORM_EVENT:
                // Form
                Form form = (Form) response.getContentData();
                mFormResponse = form;
                loadReturnMethodForm(mFormResponse);
                break;
        }
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {

    }

}
