package com.mobile.forms;

import android.content.Context;
import android.text.Html;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.checkout.ShippingMethodOption;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.R;

public class ShippingMethod {

    public ShippingMethodOption shippingMethodHolder;

    public ShippingMethod() {
        this.shippingMethodHolder = new ShippingMethodOption();
        this.shippingMethodHolder.shippingMethod = "";
        this.shippingMethodHolder.label = "";
        this.shippingMethodHolder.deliveryTime = "";
    }

    public String getLabel() {
        return shippingMethodHolder.label;
    }

    public void setLabel(String label) {
        this.shippingMethodHolder.label = label;
    }


    public View generateForm(View view) {
        Context context = view.getContext();
        // Show shipping time
        if (TextUtils.isNotEmpty(shippingMethodHolder.deliveryTime)) {
            String shippingTime = context.getString(R.string.shipping_time, android.text.TextUtils.htmlEncode(shippingMethodHolder.deliveryTime));
            TextView shippingTimeLabel = (TextView) view.findViewById(R.id.shipping_time_label_and_value);
            shippingTimeLabel.setText(Html.fromHtml(shippingTime));
            shippingTimeLabel.setVisibility(View.VISIBLE);
        }
        // Show shipping fee
        if (shippingMethodHolder.shippingFee > 0) {
            String string = CurrencyFormatter.formatCurrency(String.valueOf(shippingMethodHolder.shippingFee));
            String shippingFee = context.getString(R.string.shipping_fee, android.text.TextUtils.htmlEncode(string));
            TextView shippingFeeView = (TextView) view.findViewById(R.id.shipping_fee_label_and_value);
            shippingFeeView.setText(Html.fromHtml(shippingFee));
            shippingFeeView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.shipping_fee_info).setVisibility(View.VISIBLE);
        }
        return view;
    }


    public View generateForm(final Context context) {
        View view = View.inflate(context, R.layout.checkout_shipping_fee_time, null);
        // Show shipping time
        if (TextUtils.isNotEmpty(shippingMethodHolder.deliveryTime)) {
            String shippingTime = context.getString(R.string.shipping_time, android.text.TextUtils.htmlEncode(shippingMethodHolder.deliveryTime));
            TextView shippingTimeLabel = (TextView) view.findViewById(R.id.shipping_time_label_and_value);
            shippingTimeLabel.setText(Html.fromHtml(shippingTime));
            shippingTimeLabel.setVisibility(View.VISIBLE);
        }
        // Show shipping fee
        if (shippingMethodHolder.shippingFee > 0) {
            String string = CurrencyFormatter.formatCurrency(String.valueOf(shippingMethodHolder.shippingFee));
            String shippingFee = context.getString(R.string.shipping_fee, android.text.TextUtils.htmlEncode(string));
            TextView shippingFeeView = (TextView) view.findViewById(R.id.shipping_fee_label_and_value);
            shippingFeeView.setText(Html.fromHtml(shippingFee));
            shippingFeeView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.shipping_fee_info).setVisibility(View.VISIBLE);
        }
        return view;
    }
}
