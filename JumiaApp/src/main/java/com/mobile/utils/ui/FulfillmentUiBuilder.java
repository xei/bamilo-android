package com.mobile.utils.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.checkout.Fulfillment;
import com.mobile.view.R;

import java.util.List;

/**
 * Created by rsoares on 9/25/15.
 */
public class FulfillmentUiBuilder {

    public static void addToView(Context context, ViewGroup parent, List<Fulfillment> fulfillmentList){
        for(int i = 0; i<fulfillmentList.size();i++){
            Fulfillment fulfillment = fulfillmentList.get(i);
            View view = FulfillmentUiBuilder.getView(context, fulfillment);
            ((TextView)view.findViewById(R.id.fulfillment_title)).setText(context.getString(R.string.shipment, i+1, fulfillmentList.size()));
            parent.addView(view);
        }
    }

    public static View getView(Context context, Fulfillment fulfillment){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup fulfillmentMain = (ViewGroup) inflater.inflate(R.layout.fulfillment_main, null, false);
        TextView fulfillmentWho = (TextView)fulfillmentMain.findViewById(R.id.fulfillment_who);
        TextView fulfillmentShippingTime = (TextView)fulfillmentMain.findViewById(R.id.fulfillment_shipping_time);

        fulfillmentWho.setText(String.format(context.getString(R.string.fulfilled_by), fulfillment.getGlobalSeller()));
        fulfillmentShippingTime.setText(fulfillment.getGlobalSeller().getDeliveryTime());

        ViewGroup fulfillmentProductsLayout = (ViewGroup)fulfillmentMain.findViewById(R.id.fulfillment_products_layout);

        List<PurchaseCartItem> purchaseCartItemList = fulfillment.getProducts();
        for(PurchaseCartItem purchaseCartItem : purchaseCartItemList){
            fulfillmentProductsLayout.addView(getProductView(context, purchaseCartItem));
        }

        return fulfillmentMain;
    }

    public static View getProductView(Context context, PurchaseCartItem purchaseCartItem){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup fulfillmentProduct = (ViewGroup) inflater.inflate(R.layout.fulfillment_product, null, false);

        ((TextView)fulfillmentProduct.findViewById(R.id.fulfillment_product_qty)).setText(String.format(context.getString(R.string.qty_placeholder), purchaseCartItem.getQuantity()));
        ((TextView)fulfillmentProduct.findViewById(R.id.fulfillment_product_name)).setText(purchaseCartItem.getName());

        return fulfillmentProduct;
    }

}
