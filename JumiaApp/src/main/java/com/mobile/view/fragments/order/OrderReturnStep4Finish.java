package com.mobile.view.fragments.order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
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

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
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
            UIOrderUtils.setReturnSections(group, R.id.order_return_finish_reason, R.string.return_reason, reason);
        }
    }

    /**
     * Set the method section
     */
    private void setMethodSection(@NonNull View group) {
        String method = OrderReturnStep2Method.getMethodLabel(getSubmittedStepValues(OrderReturnStepsMain.REASON));
        UIOrderUtils.setReturnSections(group, R.id.order_return_finish_method, R.string.return_method, method);
    }

    /**
     * Set the refund section
     */
    private void setRefundSection(@NonNull View group) {
        String refund = OrderReturnStep3Refund.getRefundLabel(getSubmittedStepValues(OrderReturnStepsMain.REFUND));
        UIOrderUtils.setReturnSections(group, R.id.order_return_finish_refund, R.string.return_payment, refund);
    }

    /**
     * Set return items with/without reason view
     */
    private void setReturnItems(@NonNull ViewGroup group, @NonNull ArrayList<OrderTrackerItem> items, boolean showReasonView) {
        for (OrderTrackerItem item : items) {
            ReturnItemViewHolder custom;
            // Create item
            if (showReasonView) {
                String reason = OrderReturnStep1Reason.getReasonLabel(getSubmittedStepValues(OrderReturnStepsMain.REASON), item.getSku());
                custom = new ReturnItemReasonViewHolder(getContext(), mOrder, item).addReason(reason).bind();
            } else {
                custom = new ReturnItemViewHolder(getContext(), mOrder, item).bind();
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
    }

    /*
     * ##### LISTENERS #####
     */

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
        // Finish the process
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        // ...
    }

}
