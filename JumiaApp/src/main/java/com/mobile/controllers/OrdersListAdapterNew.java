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

    private int selectedPosition = -1;

    public OrdersListAdapterNew(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.setOrders(orders);
    }



    @Override
    public int getCount() {

        if(CollectionUtils.isNotEmpty(getOrders()))
            return getOrders().size();
        return 0;
    }


    @Override
    public Order getItem(int position) {
        if(CollectionUtils.isNotEmpty(getOrders()))
            return getOrders().get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.myorders_pending_list_item, parent, false);

        Order order = getOrders().get(position);

        TextView mOrderPrice = (TextView) convertView.findViewById(R.id.order_price);
        mOrderPrice.setText(order.getmOrderTotal());

        TextView mOrderNumber = (TextView) convertView.findViewById(R.id.order_number);
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
        this.setOrders(orders);
        this.notifyDataSetChanged();
    }



    public void appendOrders(Collection<? extends Order> newOrders) {
        for (Order order : newOrders) {
            if (!getOrders().contains(order)) {
                getOrders().add(order);
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position){
        selectedPosition = position;
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }


    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
}
