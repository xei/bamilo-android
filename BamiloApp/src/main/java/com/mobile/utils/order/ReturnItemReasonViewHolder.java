package com.mobile.utils.order;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.orders.OrderTrackerItem;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;
import com.mobile.view.fragments.order.OrderReturnStepsMain;

/**
 * Class used to represent a return return order item with a section.
 *
 * @author spereira
 */
public class ReturnItemReasonViewHolder extends ReturnItemViewHolder {

    private String mReason;
    private View.OnClickListener mListener;

    public ReturnItemReasonViewHolder(@NonNull Context context, @NonNull String order, @NonNull OrderTrackerItem item) {
        super(context, R.layout._def_order_return_step_item_with_reason, order, item);
    }

    public ReturnItemReasonViewHolder addReason(@Nullable String reason) {
        this.mReason = reason;
        return this;
    }

    public ReturnItemReasonViewHolder addClickListener(@Nullable View.OnClickListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public ReturnItemReasonViewHolder bind() {
        super.bind();
        TextView quantity = (TextView) mItemView.findViewById(R.id.order_return_item_text_quantity);
        if (TextUtils.isNotEmpty(mQuantity)) {
            quantity.setText(mContext.getString(R.string.quantity_placeholder, mQuantity));
        } else {
            UIUtils.setVisibility(quantity, false);
        }
        UIOrderUtils.setReturnSections(OrderReturnStepsMain.REASON, mItemView, R.id.order_return_finish_reason, mReason, mListener);
        return this;
    }

}