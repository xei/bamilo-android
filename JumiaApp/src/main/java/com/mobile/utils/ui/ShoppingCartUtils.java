package com.mobile.utils.ui;

import android.support.annotation.NonNull;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

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
public class ShoppingCartUtils {

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
     *
     * */
    public static void showVATInfo(@NonNull PurchaseEntity purchaseEntity, @NonNull TextView vatLabelTextView, @NonNull TextView vatValueTextView){
        if(purchaseEntity.isVatLabelEnable()) {
            vatLabelTextView.setVisibility(View.VISIBLE);
            vatValueTextView.setVisibility(View.VISIBLE);
            vatValueTextView.setText(CurrencyFormatter.formatCurrency(purchaseEntity.getVatValue()));
            vatLabelTextView.setText(purchaseEntity.getVatLabel());
        } else {
            vatValueTextView.setVisibility(View.GONE);
            vatLabelTextView.setVisibility(View.GONE);
        }
    }
}
