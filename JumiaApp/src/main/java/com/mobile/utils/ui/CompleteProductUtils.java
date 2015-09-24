package com.mobile.utils.ui;

import android.graphics.Paint;
import android.view.View;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.campaign.CampaignItem;
import com.mobile.newFramework.objects.campaign.CampaignItemSize;
import com.mobile.newFramework.objects.product.pojo.ProductBase;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;


public class CompleteProductUtils {

    public static void setPriceRules(ProductBase productBase, TextView price, TextView specialPrice){
        String priceRange = productBase.getPriceRange();

        //If ProductMultiple already has simple
        if(productBase instanceof ProductMultiple && ((ProductMultiple) productBase).getSelectedSimple() != null) {

            setPrice(((ProductMultiple) productBase).getSelectedSimple(), price, specialPrice);

        //If Campaign product already has simple
        }else if(productBase instanceof CampaignItem && ((CampaignItem) productBase).getSelectedSize() != null){

            setPrice(((CampaignItem) productBase).getSelectedSize(),price,specialPrice);

        //If hasn't simple but has range
        } else if(TextUtils.isNotEmpty(priceRange)){
            specialPrice.setText(CurrencyFormatter.formatCurrencyRange(priceRange));
            price.setText("");
        } else {
            setPrice(productBase,price,specialPrice);
        }
        specialPrice.setVisibility(View.VISIBLE);
    }

    private static void setPrice(ProductBase productBase, TextView price, TextView specialPrice){
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

    private static void setPrice(CampaignItemSize campaignItemSize, TextView price, TextView specialPrice){
        specialPrice.setText(CurrencyFormatter.formatCurrency(campaignItemSize.price));
        price.setText("");
    }

}
