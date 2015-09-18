package com.mobile.utils.ui;

import android.graphics.Paint;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.Variation;
import com.mobile.newFramework.objects.product.pojo.ProductBase;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import java.util.ArrayList;

public class CompleteProductUtils {

    public static void setPrice(ProductBase productBase, TextView price, TextView specialPrice){
        String priceRange = productBase.getPriceRange();
        if(TextUtils.isNotEmpty(priceRange)){
            specialPrice.setText(CurrencyFormatter.formatCurrencyRange(priceRange));
            price.setText("");
        } else {
            // Case discount
            if (productBase.hasDiscount()) {
                specialPrice.setText(CurrencyFormatter.formatCurrency(productBase.getSpecialPrice()));
                price.setText(CurrencyFormatter.formatCurrency(productBase.getPrice()));
                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            // Case normal
            else {
                specialPrice.setText(CurrencyFormatter.formatCurrency(productBase.getPrice()));
                price.setText("");
            }
        }
        specialPrice.setVisibility(View.VISIBLE);
    }

    /**
     * ################# RELATED ITEMS #################
     */
    
    /**
     * ################# BUNDLE ITEMS #################
     */

    /**
     * ################# SIMPLE ITEMS #################
     */
    
    
    /**
     * ################# VARIATION ITEMS #################
     */
    
    /**
     * Find the index of selected variation
     * @return int
     * @author manuel
     */
    public static int findIndexOfSelectedVariation(ProductComplete product) {
        ArrayList<Variation> var = product.getVariations();
        int idx;
        for (idx = 0; idx < var.size(); idx++) {
            if (var.get(idx).getSKU().equals(product.getSku()))
                return idx;
        }
        return -1;
    }

}
