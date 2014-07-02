package pt.rocket.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import pt.rocket.view.fragments.ShoppingCartFragment;
import pt.rocket.view.fragments.ShoppingCartFragment.CartItemValues;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.actionbarsherlock.view.ActionMode;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

/**
 * This Class implements the Basket Adapter
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Manuel Silva
 * @version 1.01
 * 
 *          2012/06/19
 * 
 */
public class ShoppingBasketFragListAdapter extends BaseAdapter {

    protected final String TAG = LogTagHelper.create(ShoppingBasketFragListAdapter.class);

    public int currentOrientation;

    ActionMode contextMenu;
    List<CartItemValues> itemValuesList;

    int shop_cart_size = 0;
    public boolean isScrolling;

    public boolean multiSelect = false;

    public boolean firstTime = true;

    private LayoutInflater inflater;

    private ShoppingCartFragment activity;

    public ShoppingBasketFragListAdapter(ShoppingCartFragment activity,
            List<CartItemValues> itemValuesList) {

        this.activity = activity;
        // initialize the image loader service
        this.itemValuesList = itemValuesList;
        this.inflater = (LayoutInflater) activity.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return itemValuesList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemValuesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void deleteSelectedProductsFromList() {
        notifyDataSetChanged();
    }

    /**
     * Update Adapter Arrays
     * 
     * @param is_in_wish
     * @param is_chek
     * @param product_n
     * @param price
     * @param product_id
     * @param unities
     * @param images
     */
    public void updateAllArrays(ArrayList<CartItemValues> itemValuesList) {
        this.itemValuesList = itemValuesList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Item prodItem;
        if (view == null) {
            view = inflater.inflate(R.layout.shopping_basket_product_element_container, parent,
                    false);
        }
        if((Item) view.getTag() == null){
            prodItem = new Item();
            prodItem.itemValues = itemValuesList.get(position);
            // Log.d( TAG, "getView: productName = " + itemValues.product_name);

            prodItem.itemName = (TextView) view.findViewById(R.id.item_name);
            prodItem.priceView = (TextView) view.findViewById(R.id.item_regprice);
            prodItem.quantityBtn = (Button) view.findViewById(R.id.changequantity_button);
            
            prodItem.productView = (ImageView) view.findViewById(R.id.image_view);

            prodItem.pBar = view.findViewById(R.id.image_loading_progress);
            prodItem.discountPercentage = (TextView) view.findViewById(R.id.discount_percentage);
            prodItem.priceDisc = (TextView) view.findViewById(R.id.item_discount);
            prodItem.promoImg = (ImageView) view.findViewById(R.id.item_promotion);
            prodItem.variancesContainer = (TextView) view
                    .findViewById(R.id.variances_container);
//            prodItem.stockInfo = (TextView) view.findViewById(R.id.item_stock);
            prodItem.deleteBtn = (Button) view.findViewById(R.id.delete_button);
            view.setTag(prodItem);
        } else {
            prodItem = (Item) view.getTag();
        }
        
        prodItem.itemName.setText(prodItem.itemValues.product_name);
        prodItem.itemName.setSelected(true);

        
        String url = prodItem.itemValues.image;
        AQuery aq = new AQuery(this.activity.getBaseActivity());
        aq.id(prodItem.productView).image(url, true, true, 0, 0, new BitmapAjaxCallback() {

                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                        prodItem.productView.setImageBitmap(bm);
                        prodItem.productView.setVisibility(View.VISIBLE);
                        prodItem.pBar.setVisibility(View.GONE);
                    }
                });

        if (!prodItem.itemValues.price.equals(prodItem.itemValues.price_disc)) {
            prodItem.priceDisc.setText(prodItem.itemValues.price_disc);
            prodItem.priceDisc.setVisibility(View.VISIBLE);

            prodItem.priceView.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(View.VISIBLE);
            prodItem.priceView.setPaintFlags(prodItem.priceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.priceView.setTextColor(activity.getResources().getColor(R.color.grey_middlelight));

            prodItem.discountPercentage.setText("-" + prodItem.itemValues.discount_value.intValue() + "%");
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
            prodItem.promoImg.setVisibility(View.VISIBLE);
        } else {
            prodItem.priceDisc.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(View.INVISIBLE);
            prodItem.promoImg.setVisibility(View.GONE);
            prodItem.discountPercentage.setVisibility(View.GONE);
        }
        prodItem.variancesContainer.setVisibility(View.GONE);
        if (prodItem.itemValues.variation != null) {
            
            Map<String, String> simpleData = prodItem.itemValues.simpleData;

                if (prodItem.itemValues.variation != null && prodItem.itemValues.variation.length() > 0 && !prodItem.itemValues.variation.equalsIgnoreCase(",") && !prodItem.itemValues.variation.equalsIgnoreCase("...") && !prodItem.itemValues.variation.equalsIgnoreCase(".")) {
//                    TextView variances = (TextView) inflater.inflate(
//                            R.layout.shopping_basket_variance_text, prodItem.variancesContainer, false);

//                    variances.setText(prodItem.itemValues.variation);
                    prodItem.variancesContainer.setVisibility(View.VISIBLE);
                    prodItem.variancesContainer.setText(prodItem.itemValues.variation);
                } 
        }

        
//        if (prodItem.itemValues.stock > 0) {
//            prodItem.stockInfo.setText(activity.getString(R.string.shoppingcart_instock));
//            prodItem.stockInfo.setTextColor(activity.getResources().getColor(R.color.green_stock));
//        } else {
//            prodItem.stockInfo.setText(activity.getString(R.string.shoppingcart_notinstock));
//            prodItem.stockInfo.setTextColor(activity.getResources().getColor(R.color.red_basic));
//        }

        
        prodItem.deleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prodItem.itemValues.is_checked = true;
                activity.deleteSelectedElements();
            }
        });

       
        prodItem.quantityBtn.setText("  " + String.valueOf(prodItem.itemValues.quantity) + "  ");
        prodItem.quantityBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prodItem.itemValues.is_checked = true;
                activity.changeQuantityOfItem(position);
            }
        });

        return view;
    }

    /**
     * This function round a number to a specific precision using a predefine rounding mode
     * 
     * @param unrounded
     *            The value to round
     * @param precision
     *            The number of decimal places we want
     * @param roundingMode
     *            The type of rounding we want done. Please refer to the java.math.BigDecimal class
     *            for more info
     * @return The number rounded according to the specifications we established
     */
    public double roundValue(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);

        return rounded.doubleValue();
    }

    /**
     * A representation of each item on the list
     */
    private static class Item {

        public TextView itemName;
        public TextView priceView;
        public Button quantityBtn;
        public ImageView productView;
        public View pBar;
        public TextView discountPercentage;
        public TextView priceDisc;
        public ImageView promoImg;
        public TextView variancesContainer;
//        public TextView stockInfo;
        public Button deleteBtn;
        public CartItemValues itemValues;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#finalize()
         */
        @Override
        protected void finalize() throws Throwable {
            itemValues = null;
            itemName = null;
            priceView = null;
            quantityBtn = null;
            productView = null;
            pBar = null;
            discountPercentage = null;
            priceDisc = null;
            promoImg = null;
            variancesContainer = null;
//            stockInfo = null;
            deleteBtn = null;

            super.finalize();
        }
    }

    
    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946 
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if(observer !=null){
            super.unregisterDataSetObserver(observer);    
        }
    }
}
