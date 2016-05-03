package com.mobile.utils.order;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.view.R;

/**
 * Class used to represent a return return order item with a section.
 *
 * @author spereira
 */
public class ReturnItemReasonViewHolder extends ReturnOrderViewHolder {

    private String mReason;

    public ReturnItemReasonViewHolder(@NonNull Context context, @NonNull String order, @NonNull OrderTrackerItem item) {
        super(context, R.layout._def_order_return_step_item_with_reason, order, item);
    }

    public ReturnItemReasonViewHolder addReason(@Nullable String reason) {
        this.mReason = reason;
        return this;
    }

    @Override
    public ReturnItemReasonViewHolder onBind() {
        super.onBind();
        UIOrderUtils.setReturnSections(mItemView, R.id.order_return_finish_reason, R.string.return_reason, mReason);
        return this;
    }

}