package com.mobile.utils.ui;

import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import java.math.BigDecimal;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
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
     *
     * @param shoppingCart
     * @param shippingContainer
     * @param shippingValueTextView
     * @param extraCostsContainer
     * @param extraCostsTextView
     *
     */
    public static void setShippingRule(ShoppingCart shoppingCart,
                                       View shippingContainer,
                                       TextView shippingValueTextView,
                                       View extraCostsContainer,
                                       TextView extraCostsTextView){
        if (!shoppingCart.hasSumCosts()) {
            double extraCosts = shoppingCart.getExtraCosts();
            double shippingFee = shoppingCart.getShippingValue();

            if(shippingFee != 0d) {
                shippingContainer.setVisibility(View.VISIBLE);
                shippingValueTextView.setText(CurrencyFormatter.formatCurrency(new BigDecimal(shippingFee).toString()));
            } else {
                shippingContainer.setVisibility(View.GONE);
            }
            if(extraCosts != 0d){
                extraCostsContainer.setVisibility(View.VISIBLE);
                extraCostsTextView.setText(CurrencyFormatter.formatCurrency(new BigDecimal(extraCosts).toString()));
            } else {
                extraCostsContainer.setVisibility(View.GONE);
            }

        } else {
            double sumCostsValue = Double.parseDouble(shoppingCart.getSumCostsValue());
            if(sumCostsValue != 0d){
                shippingContainer.setVisibility(View.VISIBLE);
                shippingValueTextView.setText(CurrencyFormatter.formatCurrency(new BigDecimal(sumCostsValue).toString()));
            } else {
                shippingContainer.setVisibility(View.GONE);
            }
            extraCostsContainer.setVisibility(View.GONE);
        }
    }
}
