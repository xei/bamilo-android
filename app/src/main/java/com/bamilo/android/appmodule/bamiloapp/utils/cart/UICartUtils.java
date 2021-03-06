package com.bamilo.android.appmodule.bamiloapp.utils.cart;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;

import android.widget.TextView;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;

import java.math.BigDecimal;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/03/16
 */
public class UICartUtils {

    /**
     * Set shipping and extra costs by rule on shopping cart object
     */
    public static void setShippingRule(PurchaseEntity purchaseEntity,
                                       View shippingContainer,
                                       TextView shippingValueTextView,
                                       View extraCostsContainer,
                                       TextView extraCostsTextView) {
        if (!purchaseEntity.hasSumCosts()) {
            double extraCosts = purchaseEntity.getExtraCosts();
            double shippingFee = purchaseEntity.getShippingValue();

            if (shippingFee != 0d) {
                shippingContainer.setVisibility(View.VISIBLE);
                shippingValueTextView.setText(CurrencyFormatter.formatCurrency(new BigDecimal(shippingFee).toString()));
            } else {
                shippingContainer.setVisibility(View.GONE);
            }
            if (extraCosts != 0d) {
                extraCostsContainer.setVisibility(View.VISIBLE);
                extraCostsTextView.setText(CurrencyFormatter.formatCurrency(new BigDecimal(extraCosts).toString()));
            } else {
                extraCostsContainer.setVisibility(View.GONE);
            }

        } else {
            double sumCostsValue = purchaseEntity.getSumCostsValue();
            if (sumCostsValue != 0d) {
                shippingContainer.setVisibility(View.VISIBLE);
                shippingValueTextView.setText(CurrencyFormatter.formatCurrency(new BigDecimal(sumCostsValue).toString()));
            } else {
                shippingContainer.setVisibility(View.GONE);
            }
            extraCostsContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Shows purchased entity's VAT info if it comes enabled from API
     */
    public static void showVatInfo(@NonNull PurchaseEntity purchaseEntity, @NonNull TextView vatLabelTextView, @NonNull TextView vatValueTextView) {
        if (purchaseEntity.isVatLabelEnable()) {
            vatLabelTextView.setVisibility(View.VISIBLE);
            vatValueTextView.setVisibility(View.VISIBLE);
            vatValueTextView.setText(CurrencyFormatter.formatCurrency(purchaseEntity.getVatValue()));
            vatLabelTextView.setText(purchaseEntity.getVatLabel());
        } else {
            vatValueTextView.setVisibility(View.GONE);
            vatLabelTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Method used to set only the price view validating discount.
     */
    public static void setSubTotal(@NonNull PurchaseEntity cart, @NonNull TextView view, @NonNull TextView strike){
        // Set sub total
        view.setText(CurrencyFormatter.formatCurrency(cart.getSubTotal()));
        // Set sub total unreduced
        if (cart.hasSubTotalUnDiscounted()) {
            strike.setText(CurrencyFormatter.formatCurrency(cart.getSubTotalUnDiscounted()));
            strike.setPaintFlags(strike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    /**
     * Method used to set the variation
     */
    public static void setVariation(@NonNull PurchaseCartItem item, @NonNull TextView label, @NonNull TextView value) {
        // Variation
        if (TextUtils.isEmpty(item.getVariationValue())) {
            UIUtils.showOrHideViews(View.GONE, label, value);
        } else {
            label.setText(item.getVariationName());
            value.setText(item.getVariationValue());
        }
    }

}
