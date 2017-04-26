package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by alexandrapires on 10/22/15.
 * simplified adapter for My Orders list
 */
public class OrdersAdapter extends BaseAdapter {

    public final static String TAG = OrdersAdapter.class.getSimpleName();

    private ArrayList<Order> orders;

    private Context context;

    private int selectedPosition = IntConstants.INVALID_POSITION;

    public OrdersAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return CollectionUtils.isNotEmpty(orders) ? orders.size() : 0;
    }

    @Override
    public Order getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orders.get(position).getNumber();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout._def_my_orders_list_item2, parent, false);
        }
        Order order = getOrders().get(position);
        // Set data
        ((TextView) convertView.findViewById(R.id.order_item_price)).setText("مبلغ کل سفارش : "+CurrencyFormatter.formatCurrency(order.getTotal()));
        ((TextView) convertView.findViewById(R.id.order_item_number)).setText("شماره سفارش : "+String.valueOf(order.getNumber()));
         //JalaliCalendar.gregorianToJalali()
        // Show item as selected
        convertView.findViewById(R.id.order_item_container).setActivated(position == selectedPosition);
        return convertView;
    }

    /**
     * Updates the Orders array list
     *
     * @param orders The array list containing the orders
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

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    /**
     * Set the selected item to show the selector associated.
     */
    public void notifySelectedData(int selected) {
        this.selectedPosition = selected;
        notifyDataSetChanged();
    }
}
