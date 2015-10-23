package com.mobile.utils.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.checkout.Fulfillment;
import com.mobile.newFramework.objects.checkout.GlobalSeller;
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
        TextView fulfillmentWho = (TextView)fulfillmentMain.findViewById(R.id.fulfillment_seller_name);

        GlobalSeller seller = fulfillment.getGlobalSeller();

        fulfillmentWho.setText(String.format(context.getString(R.string.fulfilled_by), seller.getName()));
        if(seller.isGlobal()){
            TextView fulfillmentShippingInfo = (TextView)fulfillmentMain.findViewById(R.id.fulfillment_shipping_info);
            fulfillmentShippingInfo.setText(seller.getShippingInfo());
            fulfillmentShippingInfo.setVisibility(View.VISIBLE);

            TextView fulfillmentDeliverInfo = (TextView)fulfillmentMain.findViewById(R.id.fulfillment_deliver_info);
            fulfillmentDeliverInfo.setText(seller.getInfo());
            fulfillmentDeliverInfo.setVisibility(View.VISIBLE);

        } else {
            TextView fulfillmentShippingTime = (TextView)fulfillmentMain.findViewById(R.id.fulfillment_shipping_time);
            fulfillmentShippingTime.setText(seller.getDeliveryTime());
            fulfillmentShippingTime.setVisibility(View.VISIBLE);
        }


        ViewGroup fulfillmentProductsLayout = (ViewGroup) fulfillmentMain.findViewById(R.id.fulfillment_products_layout);

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
