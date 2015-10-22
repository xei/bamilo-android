package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by alexandrapires on 10/22/15.
 * simplified adapter for My Orders list
 */
public class OrdersListAdapterNew extends BaseAdapter {

    public final static String TAG = OrdersListAdapterNew.class.getSimpleName();

    private ArrayList<Order> orders;

    private Context context;

    public OrdersListAdapterNew(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }



    @Override
    public int getCount() {

        if(CollectionUtils.isNotEmpty(orders))
            return orders.size();
        return 0;
    }


    @Override
    public Object getItem(int position) {
        if(CollectionUtils.isNotEmpty(orders))
            return orders.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.def_myorders_pending_list_item, parent, false);

        Order order = orders.get(position);

        TextView mOrderPrice = (TextView) convertView.findViewById(R.id.order_price);
        mOrderPrice.setText(order.getmOrderTotal());

        TextView mOrderNumber = (TextView) convertView.findViewById(R.id.order_number_label);
        mOrderNumber.setText(order.getmOrderNumber());

        TextView mOrderDate = (TextView) convertView.findViewById(R.id.order_date);
        mOrderDate.setText(order.getmDate());

        return convertView;
    }



    /**
     * Updates the Orders array list
     *
     * @param orders
     *            The array list containing the orders
     */
    public void updateOrders(ArrayList<Order> orders) {
        this.orders = orders;
        this.notifyDataSetChanged();
    }

    public void clearProducts() {
        orders.clear();
        notifyDataSetChanged();
    }

    public void appendOrders(Collection<? extends Order> newOrders) {
        for (Order order : newOrders) {
            if (!orders.contains(order)) {
                orders.add(order);
            }
        }
        notifyDataSetChanged();
    }
}
