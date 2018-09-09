package com.bamilo.android.appmodule.bamiloapp.forms;

import android.content.Context;
import android.text.Html;
import android.view.View;

import android.widget.TextView;
import com.bamilo.android.framework.service.objects.checkout.ShippingMethodOption;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.R;

public class ShippingMethod {

    public ShippingMethodOption shippingMethodHolder;

    public ShippingMethod() {
        this.shippingMethodHolder = new ShippingMethodOption();
        this.shippingMethodHolder.value = "";
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
        // Get context
        Context context = view.getContext();
        // Show bottom space
        view.findViewById(R.id.shipping_info_space).setVisibility(View.VISIBLE);
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
