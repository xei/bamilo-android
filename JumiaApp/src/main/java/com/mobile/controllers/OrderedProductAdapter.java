package com.mobile.controllers;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.utils.ui.OrderedProductListViewHolder;
import com.mobile.utils.ui.ProductListViewHolder;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.List;

/**
 * Created by rsoares on 10/22/15.
 * Adapter for order tracker items in orderStatus
 */
public class OrderedProductAdapter extends ProductListAdapter {

    public OrderedProductAdapter(@NonNull List<ProductRegular> mDataSet) {
        super(mDataSet);
    }

    @Override
    public OrderedProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderedProductListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ordered_product, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductListViewHolder holder, int position) {

        OrderedProductListViewHolder newHolder = (OrderedProductListViewHolder) holder;
        super.onBindViewHolder(holder, position);

        //check status
        OrderTrackerItem item = (OrderTrackerItem) mDataSet.get(position);
        String status = item.getStatus();

            if(status.contains(resources.getString(R.string.pending))) {   //delivered status
                newHolder.orderStatus.setVisibility(View.GONE);
                newHolder.image.setVisibility(View.VISIBLE);
                newHolder.deliveryInfo.setVisibility(View.VISIBLE);
                newHolder.productDeliveredDate.setText(item.getStatusUpdate().split(" ")[0]);  //just date

            }else{
                //pending status: validate how to distinguish this: info coming from API is not translated yet
                newHolder.orderStatus.setVisibility(View.VISIBLE);
                newHolder.image.setVisibility(View.GONE);
                newHolder.deliveryInfo.setVisibility(View.GONE);
            }

    }



    @Override
    protected void setFavourite(ProductListViewHolder holder, ProductRegular item, int position) {
        UIUtils.showOrHideViews(View.GONE, holder.favourite);
    }

    @Override
    protected void setSpecificViewForListLayout(ProductListViewHolder holder, ProductRegular item) {
        UIUtils.showOrHideViews(View.GONE, holder.ratingContainer);
    }
}
