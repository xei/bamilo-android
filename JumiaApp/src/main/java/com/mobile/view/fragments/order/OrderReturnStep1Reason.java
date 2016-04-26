package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.order.GetReturnReasonFormHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.ReturnReasonForm;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.order.UIOrderUtils;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Fragment used to show the online returns reason.
 *
 * @author spereira
 */
public class OrderReturnStep1Reason extends OrderReturnStepBase {

    private ArrayList<OrderTrackerItem> mItems;
    private ArrayList<DynamicForm> mDynamicForms;

    /**
     * Empty constructor
     */
    public OrderReturnStep1Reason() {
        super(OrderReturnStepsMain.REASON, R.string.order_return_reason_title, R.string.continue_label);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get order items
        mItems = getOrderItems();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Validate order items
        if (CollectionUtils.isNotEmpty(mItems)) {
            triggerGetReasonForm(mItems.size());
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Case rotation save state
        if (CollectionUtils.isNotEmpty(mItems) && CollectionUtils.sizeIsSame(mItems, mDynamicForms)) {
            for (int i = 0; i < mDynamicForms.size(); i++) {
                Bundle bundle = new Bundle();
                mDynamicForms.get(i).saveFormState(bundle);
                outState.putBundle(mItems.get(i).getSku(), bundle);
            }
        }
    }

    /*
     * ##### LAYOUT #####
     */

    /**
     * Create and show the form.
     */
    private void loadForm(ReturnReasonForm forms, ArrayList<OrderTrackerItem> items) {

//        // Remove all views
//        if (mContainer.getChildCount() > 0) {
//            mContainer.removeAllViews();
//        }

        //
        ViewGroup group = (ViewGroup) LayoutInflater.from(getBaseActivity()).inflate(R.layout._def_order_return_step_reason, this.mContainer, false);
        //
        mDynamicForms = new ArrayList<>();
        // Create form for each item
        for (int i = 0; i < items.size(); i++) {
            // Get Item
            OrderTrackerItem item = items.get(i);
            // Create and add view
            View viewItem = UIOrderUtils.createOrderItem(getContext(), item, group);
            // Get form
            Form form = forms.get(i);
            // Create form view
            DynamicForm dynamicForm = FormFactory.getSingleton().create(FormConstants.ORDER_RETURN_REASON_FORM, getContext(), form);
            // Save dynamic form
            mDynamicForms.add(dynamicForm);
            // Load saved state
            //dynamicForm.loadSaveFormState(mSavedState.getBundle(item.getSku()));
            // Add item and form view
            group.addView(viewItem);
            group.addView(dynamicForm.getContainer());
        }
        // Add group
        mContainer.addView(group);
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
        ReturnReasonForm forms = (ReturnReasonForm) response.getContentData();
        // Get items
        ArrayList<OrderTrackerItem> items = getOrderItems();
        // Validate data
        if (CollectionUtils.isNotEmpty(items) && CollectionUtils.sizeIsSame(items, forms)) {
            loadForm(forms, items);
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        showFragmentErrorRetry();
    }

}
