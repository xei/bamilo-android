package com.mobile.view.fragments.order;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.order.ReturnFinishHelper;
import com.mobile.newFramework.objects.orders.OrderStatus;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.order.ReturnItemReasonViewHolder;
import com.mobile.utils.order.ReturnItemViewHolder;
import com.mobile.utils.order.UIOrderUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Fragment used to show the online returns reason.
 *
 * @author spereira
 */

public class OrderReturnStep4Finish extends OrderReturnStepBase {

    private String mOrder;
    private ArrayList<OrderTrackerItem> mItems;

    /**
     * Empty constructor
     */
    public OrderReturnStep4Finish() {
        super(OrderReturnStepsMain.FINISH, R.string.order_return_finish_title, R.string.send_label);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get order number
        mOrder = getOrderNumber();
        // Get order items
        mItems = getOrderItems();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Validate state
        if (hasSubmittedValuesToFinish()) {
            // Load and show
            loadSubmittedValues(mContainer);
        } else {
            // Warning user to restart the process
            showFragmentErrorRetry();
        }
    }

    /*
     * ##### LAYOUT #####
     */

    /**
     * Load submitted values
     */
    private void loadSubmittedValues(@NonNull ViewGroup container) {
        // Remove all views from container
        if (container.getChildCount() > 0) {
            container.removeAllViews();
        }
        // Validate items to set reason section
        boolean showItemsWithReason = CollectionUtils.size(mItems) > 1;
        // View group to add each form
        ViewGroup group = (ViewGroup) LayoutInflater.from(getBaseActivity()).inflate(R.layout._def_order_return_step_finish, this.mContainer, false);
        // Reason
        setReasonSection(group, showItemsWithReason);
        // Method
        setMethodSection(group);
        // refund
        setRefundSection(group);
        // Items
        setReturnItems((ViewGroup) group.findViewById(R.id.order_return_finish_items), mItems, showItemsWithReason);
        // Add
        container.addView(group);
    }

    /**
     * Set the reason section
     */
    private void setReasonSection(@NonNull View group, boolean showItemWithReason) {
        if(showItemWithReason) {
            UIUtils.setVisibility(group.findViewById(R.id.order_return_finish_reason), false);
        } else {
            String sku = getOrderItems().get(IntConstants.DEFAULT_POSITION).getSku();
            String reason = OrderReturnStep1Reason.getReasonLabel(getSubmittedStepValues(OrderReturnStepsMain.REASON), sku);
            UIOrderUtils.setReturnSections(OrderReturnStepsMain.REASON, group, R.id.order_return_finish_reason, reason, this);
        }
    }

    /**
     * Set the method section
     */
    private void setMethodSection(@NonNull View group) {
        String method = OrderReturnStep2Method.getMethodLabel(getSubmittedStepValues(OrderReturnStepsMain.METHOD));
        UIOrderUtils.setReturnSections(OrderReturnStepsMain.METHOD, group, R.id.order_return_finish_method, method, this);
    }

    /**
     * Set the refund section
     */
    private void setRefundSection(@NonNull View group) {
        String refund = OrderReturnStep3Refund.getRefundLabel(getSubmittedStepValues(OrderReturnStepsMain.REFUND));
        UIOrderUtils.setReturnSections(OrderReturnStepsMain.REFUND, group, R.id.order_return_finish_refund, refund, this);
    }

    /**
     * Set return items with/without reason view
     */
    private void setReturnItems(@NonNull ViewGroup group, @NonNull ArrayList<OrderTrackerItem> items, boolean showReasonView) {
        for (OrderTrackerItem item : items) {
            ReturnItemViewHolder custom;
            // Create item
            if (showReasonView) {
                ContentValues values = getSubmittedStepValues(OrderReturnStepsMain.REASON);
                String reason = OrderReturnStep1Reason.getReasonLabel(values, item.getSku());
                String quantity = OrderReturnStep1Reason.getQuantityValue(values, item.getSku());
                custom = new ReturnItemReasonViewHolder(getContext(), mOrder, item)
                        .addReason(reason)
                        .addClickListener(this)
                        .addQuantity(quantity)
                        .bind().showQuantityToReturnText();
            } else {
                custom = new ReturnItemViewHolder(getContext(), mOrder, item).bind().showQuantityToReturnText();
            }
            // Add view
            group.addView(custom.getView());
        }
    }

    /*
     * ##### TRIGGERS #####
     */

    private void triggerFinishReturnProcess() {

        // Submit values
        triggerContentEvent(new ReturnFinishHelper(), ReturnFinishHelper.createBundle(getSubmittedValues()), this);
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case edit
        if (id == R.id.section_item_button) {
            // Get step
            @OrderReturnStepsMain.ReturnStepType
            int step = (int) view.getTag(R.id.target_type);
            onClickStep(step);
        } else if(id == R.id.order_return_main_button_ok){
            if(hasSubmittedValuesToFinish()){
                triggerFinishReturnProcess();
            }
        } else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        // Validate state
        if (!hasSubmittedValuesToFinish()) {
            // Warning user
            showUnexpectedErrorWarning();
            // Restart process
            getBaseActivity().onBackPressed();
        }
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        EventType eventType = response.getEventType();

        switch (eventType) {
            case RETURN_FINISH_EVENT:
                // Finish the process
                showWarningSuccessMessage(response.getSuccessMessage());
                OrderStatus order = (OrderStatus) response.getMetadata().getData();
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.ARG_1, String.valueOf(order.getId()));
                bundle.putString(ConstantsIntentExtra.ARG_2, order.getDate());
                // Validate if frame order status
                getBaseActivity().popBackStackUntilTag(FragmentType.ORDER_STATUS.toString());
                getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle, FragmentController.ADD_TO_BACK_STACK);

                break;
        }

    }

    @Override
    protected void onErrorResponse(BaseResponse response) {

        EventType eventType = response.getEventType();

        switch (eventType) {
            case RETURN_FINISH_EVENT:
                // Case error, stay at this screen.
                showWarningErrorMessage(response.getErrorMessage());

                break;
        }
    }

}
