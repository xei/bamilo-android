package com.mobile.controllers;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.Order;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.view.R;

/**
 * 
 * @author pcarvalho
 * 
 */
public class OrdersListAdapter extends BaseAdapter {
    
    public final static String TAG = LogTagHelper.create(OrdersListAdapter.class);

    public interface OnSelectedOrderChange {
        public void SelectedOrder(Order selectedOrder, ViewGroup productsContainer, boolean toShow, int selectedProd);
    }

    private LayoutInflater inflater;

    int counter = 1;

    private Context context;

    private OnSelectedOrderChange oderSelected;

    ArrayList<Order> orders = new ArrayList<Order>();
    
    private int selectedPosition = -1;

    /**
     * A representation of each item on the list
     */
    static class Item {
        public ViewGroup productsCont;
        public RelativeLayout orderHeader;
        public ViewGroup products;
        public TextView orderDate;
        public TextView orderTotalPrice;
        public TextView orderNumber;
        public TextView productDate;
        public TextView productPaymentMethod;
        public ImageView orderCheck;
        public ImageView orderArrow;
        public View orderLine;
    }

    /**
     * the constructor for this adapter
     * 
     * @param activity
     * @param showList
     *            show list (or grid)
     * @param numColumns
     */
    public OrdersListAdapter(Context context, ArrayList<Order> orders, OnSelectedOrderChange listener) {
        this.context = context.getApplicationContext();
        this.orders = orders;
        this.inflater = LayoutInflater.from(context);
        this.oderSelected = listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return orders == null ? 0 : orders.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        final Item item;
        if (itemView == null) {

            // Inflate a New Item View
            itemView = inflater.inflate(R.layout.order_history_item, parent, false);

            item = new Item();
            // item.textView = (TextView) itemView.findViewById( R.id.text);
            item.productsCont = (ViewGroup) itemView.findViewById(R.id.order_more_info);
            item.products = (ViewGroup) itemView.findViewById(R.id.order_product_list);
            item.orderHeader = (RelativeLayout) itemView.findViewById(R.id.order_header);
            item.orderDate = (TextView) itemView.findViewById(R.id.order_date);
            item.orderTotalPrice = (TextView) itemView.findViewById(R.id.order_price);
            item.orderNumber = (TextView) itemView.findViewById(R.id.order_number);
            item.productDate = (TextView) itemView.findViewById(R.id.order_list_date);
            item.productPaymentMethod = (TextView) itemView.findViewById(R.id.order_list_payment);
            item.orderCheck = (ImageView) itemView.findViewById(R.id.order_check);
            item.orderArrow = (ImageView) itemView.findViewById(R.id.order_arrow);
            item.orderLine = (View) itemView.findViewById(R.id.order_bottom_line);

            itemView.setTag(item);

        } else {

            item = (Item) itemView.getTag();
        }

        item.orderDate.setText(orders.get(position).getmDate());
        item.orderTotalPrice.setText(CurrencyFormatter.formatCurrency(orders.get(position).getmOrderTotal()));
        item.orderNumber.setText(context.getString(R.string.my_order_number_label) + " "
                + orders.get(position).getmOrderNumber());

        if (position == orders.size() - 1)
            item.orderLine.setVisibility(View.GONE);
        else
            item.orderLine.setVisibility(View.VISIBLE);
        
        // Case portrait view
        if (item.productsCont != null) {
            item.productPaymentMethod.setText(orders.get(position).getmPayment());
            item.productDate.setText(orders.get(position).getmDate());

            if(selectedPosition != -1 && position == selectedPosition){
                item.orderArrow.setSelected(true);
                item.productsCont.setVisibility(View.VISIBLE);
                item.products.setVisibility(View.VISIBLE);
                oderSelected.SelectedOrder(orders.get(position), item.products, true, position);
            } else {
                item.orderArrow.setSelected(false);
                item.productsCont.setVisibility(View.GONE);
                item.products.setVisibility(View.GONE);
            }
            
            item.orderHeader.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (item.orderArrow.isSelected()) {
                        item.orderArrow.setSelected(false);
                        item.productsCont.setVisibility(View.GONE);
                        item.products.setVisibility(View.GONE);
                        selectedPosition = -1;
                        oderSelected.SelectedOrder(orders.get(position), item.products, false, -1);
                    } else {
                        item.orderArrow.setSelected(true);
                        item.productsCont.setVisibility(View.VISIBLE);
                        item.products.setVisibility(View.VISIBLE);
                        selectedPosition = position;
                        oderSelected.SelectedOrder(orders.get(position), item.products, true, position);
                    }
                }
            });
        // Case landscape view
        } else {
            if(selectedPosition != -1 && position == selectedPosition){
                item.orderHeader.setActivated(true);
            } else {
                item.orderHeader.setActivated(false);
            }
            
            item.orderHeader.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (selectedPosition == position) {
                        oderSelected.SelectedOrder(orders.get(position), item.products, false, position);
                    } else {
                        selectedPosition = position;
                        oderSelected.SelectedOrder(orders.get(position), item.products, true, position);
                    }
                }
            });

        }
        return itemView;
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

    public ArrayList<Order> getOrdersList() {
        return orders;
    }

    public void setSelectedPosition(int position){
        selectedPosition = position;
    }
    
    public int getSelectedPosition(){
        return selectedPosition;
    }
    
    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     * 
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

}
