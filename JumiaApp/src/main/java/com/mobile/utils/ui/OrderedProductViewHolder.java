package com.mobile.utils.ui;

import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;

/**
 * Class used to represent an order item.
 *
 * @author spereira
 */
public class OrderedProductViewHolder extends ProductListViewHolder {

    public TextView quantity;
    public View reorder;
    public TextView status;
    public TextView delivery;
    public TextView itemReturns;

    /**
     * Constructor
     *
     * @param view -  the view holder
     */
    public OrderedProductViewHolder(View view) {
        super(view);
        quantity = (TextView) view.findViewById(R.id.item_quantity);
        delivery = (TextView) view.findViewById(R.id.item_delivery);
        reorder = view.findViewById(R.id.order_status_item_button_reorder);
        status = (TextView) view.findViewById(R.id.order_status_item_text_delivered);
        itemReturns = (TextView) view.findViewById(R.id.item_returns);
    }
}
