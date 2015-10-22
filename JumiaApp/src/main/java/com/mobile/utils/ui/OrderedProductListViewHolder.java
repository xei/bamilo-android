package com.mobile.utils.ui;

import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;

/**
 * Created by rsoares on 10/22/15.
 */
public class OrderedProductListViewHolder extends ProductListViewHolder {

    public TextView productDeliveredDate;

    /**
     * Constructor
     *
     * @param view -  the view holder
     */
    public OrderedProductListViewHolder(View view) {
        super(view);
        productDeliveredDate = (TextView)view.findViewById(R.id.delivered_date);
    }
}
