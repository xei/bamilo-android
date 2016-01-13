package com.mobile.utils.ui;

import android.content.res.Resources;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.campaign.CampaignItemSize;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.product.pojo.ProductBase;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

public class ProductUtils {

    public static void setPriceRules(@NonNull ProductBase productBase, @NonNull TextView price, @NonNull TextView specialPrice){
        String priceRange = productBase.getPriceRange();
        //If hasn't simple but has range
        if(TextUtils.isNotEmpty(priceRange)){
            specialPrice.setText(priceRange);
            price.setText("");
        //If ProductMultiple already has simple
        }else if(productBase instanceof ProductMultiple && ((ProductMultiple) productBase).getSelectedSimple() != null) {
            setPrice(((ProductMultiple) productBase).getSelectedSimple(), price, specialPrice);
        } else {
            setPrice(productBase, price, specialPrice);
        }
        specialPrice.setVisibility(View.VISIBLE);
    }

    private static void setPrice(@NonNull ProductBase productBase, @NonNull TextView price, @NonNull TextView specialPrice){
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

    @Deprecated
    private static void setPrice(CampaignItemSize campaignItemSize, TextView price, TextView specialPrice){

        if (campaignItemSize.hasDiscount()) {
            specialPrice.setText(CurrencyFormatter.formatCurrency(campaignItemSize.specialPrice));
            price.setText(CurrencyFormatter.formatCurrency(campaignItemSize.price));
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        // Case normal
        else {
            specialPrice.setText(CurrencyFormatter.formatCurrency(campaignItemSize.price));
            price.setText("");
        }
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

    /**
     * Set the variation container
     */
    public static void setVariationContent(@NonNull View view, @NonNull ProductMultiple product){
        // Set simple button
        if(product.hasMultiSimpleVariations()) {
            // Set simple value
            String simpleVariationValue = "...";
            if(product.hasSelectedSimpleVariation()) {
                simpleVariationValue = product.getSimples().get(product.getSelectedSimplePosition()).getVariationValue();
            }
            if(view instanceof TextView) {
                ((TextView) view).setText(simpleVariationValue);
            }
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Validate if it show regular warning or confirmation cart message<br>
     *      - If has cart popup, show configurable confirmation message with cart total price<br>
     *      - Else show regular message add item to cart<br>
     */
    public static void showAddToCartCompleteMessage(@NonNull BaseFragment fragment, @NonNull BaseResponse baseResponse, @NonNull EventType eventType) {
        boolean isToShowCartPopUp = CountryPersistentConfigs.hasCartPopup(fragment.getBaseActivity().getApplicationContext());
        PurchaseEntity cart = JumiaApplication.INSTANCE.getCart();
        if (isToShowCartPopUp && cart != null && cart.getTotal() > 0) {
            fragment.getBaseActivity().mConfirmationCartMessageView.showMessage(cart.getTotal());
        } else {
            fragment.showWarningSuccessMessage(baseResponse.getSuccessMessage(), eventType);
        }
    }

    public static void setShopFirst(@NonNull ProductRegular productBase, @NonNull View badge){
        badge.setVisibility((!productBase.isShopFirst() || ShopSelector.isRtlShop()) ? View.GONE : View.VISIBLE);
    }

}
