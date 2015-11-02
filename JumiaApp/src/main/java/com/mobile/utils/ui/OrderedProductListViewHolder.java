package com.mobile.utils.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;

/**
 * Created by rsoares on 10/22/15.
 */
public class OrderedProductListViewHolder extends ProductListViewHolder {

    public TextView productDeliveredDate;

    public TextView orderStatus;

    public ViewGroup deliveryInfo;

    public ImageView image;

    public TextView orderReorder;

    /**
     * Constructor
     *
     * @param view -  the view holder
     */
    public OrderedProductListViewHolder(View view) {
        super(view);
        productDeliveredDate = (TextView)view.findViewById(R.id.delivered_date);
        orderStatus = (TextView) view.findViewById(R.id.order_status);
        deliveryInfo = (ViewGroup) view.findViewById(R.id.delivery_info);
        image = (ImageView) view.findViewById(R.id.image);
        orderReorder = (TextView) view.findViewById(R.id.reorderButton);
    }
}
