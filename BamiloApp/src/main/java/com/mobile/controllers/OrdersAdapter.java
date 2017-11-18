package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.orders.Order;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.PersianDateTime;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

/**
 * Created by alexandrapires on 10/22/15.
 * simplified adapter for My Orders list
 */
public class OrdersAdapter extends BaseAdapter {

    public final static String TAG = OrdersAdapter.class.getSimpleName();
    private final static String TAG_ORDER_ITEM = "order_item";

    private ArrayList<Order> orders;

    private Context context;
    private boolean loadMoreProgressEnabled;

    private int selectedPosition = IntConstants.INVALID_POSITION;

    public OrdersAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        int count = CollectionUtils.isNotEmpty(orders) ? orders.size() : 0;
        if (count != 0 && loadMoreProgressEnabled) {
            count++;
        }
        return count;
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
        if (loadMoreProgressEnabled && position == getCount() - 1) {
            if (convertView != null && convertView.getTag() != null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.catalog_fragment_footer, parent, false);
            }
            return convertView;
        }
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout._def_my_orders_list_item2, parent, false);
        }
        convertView.setTag(TAG_ORDER_ITEM);
        Order order = getOrders().get(position);
        String date = order.getDate();
        int Year = Integer.parseInt(date.substring(0, 4));
        int Month = Integer.parseInt(date.substring(5, 7));
        int Day = Integer.parseInt(date.substring(8, 10));


        PersianDateTime pd = PersianDateTime.valueOf(new GregorianCalendar(Year, Month, Day, 0, 0, 0));

        TextView tvPrice = (TextView) convertView.findViewById(R.id.order_item_price);
        tvPrice.setText(String.format(context.getResources().getString(R.string.order_total), CurrencyFormatter.formatCurrency(order.getTotal())));
        tvPrice.setFontStyle(tvPrice.getFontFamily(), HoloFontLoader.TEXT_STYLE_BOLD);
        ((TextView) convertView.findViewById(R.id.order_item_number)).setText(String.format(context.getResources().getString(R.string.order_number), String.valueOf(order.getNumber())));
        ((TextView) convertView.findViewById(R.id.order_item_date)).setText(String.format(context.getResources().getString(R.string.order_date), pd.toString()));// JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(order.getDate()) ).toString()));
        // Show item as selected
        //convertView.findViewById(R.id.order_item_container).setActivated(position == selectedPosition);
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

    public boolean isLoadMoreProgressEnabled() {
        return loadMoreProgressEnabled;
    }

    public void setLoadMoreProgressEnabled(boolean loadMoreProgressEnabled) {
        this.loadMoreProgressEnabled = loadMoreProgressEnabled;
    }
}
