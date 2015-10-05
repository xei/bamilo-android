package com.mobile.utils.ui;

import android.content.res.Resources;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.campaign.CampaignItem;
import com.mobile.newFramework.objects.campaign.CampaignItemSize;
import com.mobile.newFramework.objects.product.pojo.ProductBase;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.R;

public class ProductUtils {

    public static void setPriceRules(@NonNull ProductBase productBase, @NonNull TextView price, @NonNull TextView specialPrice){
        String priceRange = productBase.getPriceRange();

        //If ProductMultiple already has simple
        if(productBase instanceof ProductMultiple && ((ProductMultiple) productBase).getSelectedSimple() != null) {

            setPrice(((ProductMultiple) productBase).getSelectedSimple(), price, specialPrice);

        //If hasn't simple but has range
        }else if(TextUtils.isNotEmpty(priceRange)){
            specialPrice.setText(CurrencyFormatter.formatCurrencyPattern(priceRange));
            price.setText("");
        } else {
            setPrice(productBase, price, specialPrice);
        }
        specialPrice    .setVisibility(View.VISIBLE);
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

    public static void setPriceRules(@NonNull CampaignItem campaignItem, @NonNull TextView price, @NonNull TextView specialPrice){
        if(campaignItem.hasSelectedSize()) {
            setPrice(campaignItem.getSizes().get(campaignItem.getSelectedSizePosition()), price, specialPrice);
        } else {
            setPrice(campaignItem, price, specialPrice);
        }
        specialPrice.setVisibility(View.VISIBLE);
    }

    private static void setPrice(CampaignItemSize campaignItemSize, TextView price, TextView specialPrice){
        specialPrice.setText(CurrencyFormatter.formatCurrency(campaignItemSize.price));
        price.setText("");
    }

    public static void setDiscountRules(@NonNull ProductBase productBase, @NonNull TextView percentage){
        if (productBase.hasDiscount()) {
            Resources resources = percentage.getResources();
            percentage.setText(String.format(resources.getString(R.string.format_discount_percentage), productBase.getMaxSavingPercentage()));
            percentage.setVisibility(View.VISIBLE);
        } else {
            percentage.setVisibility(View.INVISIBLE);
        }
    }

}
