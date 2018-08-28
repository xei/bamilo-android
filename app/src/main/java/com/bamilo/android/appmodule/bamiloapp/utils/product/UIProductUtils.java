package com.bamilo.android.appmodule.bamiloapp.utils.product;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogGenericFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.product.Variation;
import com.bamilo.android.framework.service.objects.product.pojo.ProductBase;
import com.bamilo.android.framework.service.objects.product.pojo.ProductMultiple;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;

public class UIProductUtils {

    /**
     * Method used to set only the price view validating discount.
     */
    public static void setPriceRules(@NonNull ProductBase productBase, @NonNull TextView view) {
        double price = productBase.hasDiscount() ? productBase.getSpecialPrice() : productBase.getPrice();
        view.setText(CurrencyFormatter.formatCurrency(price));
    }

    /**
     * Method used to set special price and price views
     */
    public static void setPriceRules(@NonNull ProductBase productBase, @NonNull TextView price, @NonNull TextView specialPrice) {
        String priceRange = productBase.getPriceRange();
        //If ProductMultiple already has simple
        if (productBase instanceof ProductMultiple && ((ProductMultiple) productBase).getSelectedSimple() != null) {
            //noinspection ConstantConditions
            setPrice(((ProductMultiple) productBase).getSelectedSimple(), price, specialPrice);
            //If hasn't simple but has range
        } else if (TextUtils.isNotEmpty(priceRange)) {
            specialPrice.setText(priceRange);
            price.setVisibility(View.GONE);
        } else {
            setPrice(productBase, price, specialPrice);
        }
        specialPrice.setVisibility(View.VISIBLE);
    }

    public static void setPriceRulesWithAutoAdjust(Context context, @NonNull ProductBase productBase, @NonNull TextView discount, @NonNull TextView price) {
        String priceRange = productBase.getPriceRange();
        price.setVisibility(View.VISIBLE);
        discount.setVisibility(View.VISIBLE);
        //If ProductMultiple already has simple
        if (productBase instanceof ProductMultiple && ((ProductMultiple) productBase).getSelectedSimple() != null) {
            //noinspection ConstantConditions
            setPriceWithAutoAdjust(context, ((ProductMultiple) productBase).getSelectedSimple(), discount, price);
            //If hasn't simple but has range
        } else if (TextUtils.isNotEmpty(priceRange)) {
            discount.setText(priceRange);
            price.setVisibility(View.GONE);
        } else {
            setPriceWithAutoAdjust(context, productBase, discount, price);
        }
    }

    private static void setPrice(@NonNull ProductBase productBase, @NonNull TextView price, @NonNull TextView specialPrice) {
        // Case discount
        if (productBase.hasDiscount()) {
            specialPrice.setText(CurrencyFormatter.formatCurrency(productBase.getSpecialPrice()));
            price.setText(CurrencyFormatter.formatCurrency(productBase.getPrice()));
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            price.setVisibility(View.VISIBLE);
        }
        // Case normal
        else {
            price.setVisibility(View.GONE);
            specialPrice.setText(CurrencyFormatter.formatCurrency(productBase.getPrice()));

        }
    }

    private static void setPriceWithAutoAdjust(Context context, @NonNull ProductBase productBase, @NonNull TextView discountView, @NonNull TextView priceView) {
        // Case discount
        if (productBase.hasDiscount()) {
            final String specialPrice = CurrencyFormatter.formatCurrency(productBase.getSpecialPrice());
            final String regularPrice = CurrencyFormatter.formatCurrency(productBase.getPrice());

            priceView.setText(regularPrice);
            discountView.setText(specialPrice);
            priceView.setPaintFlags(discountView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        // Case normal
        else {
            discountView.setText(CurrencyFormatter.formatCurrency(productBase.getPrice()));
        }
    }

    public static void setDiscountRules(@NonNull ProductBase product, @NonNull TextView percentage) {
        if (product.hasDiscount()) {
            Resources resources = percentage.getResources();
            percentage.setText(String.format(resources.getString(R.string.format_discount_percentage), product.getMaxSavingPercentage()));
            percentage.setVisibility(View.VISIBLE);
        } else {
            percentage.setVisibility(View.INVISIBLE);
        }
        percentage.setEnabled(product.hasDiscount());
    }

    /**
     * Set the variation container
     */
    public static void setVariationContent(@NonNull View view, @NonNull ProductMultiple product) {
        // Set simple button
        if (product.hasMultiSimpleVariations()) {
            // Set simple value
            String simpleVariationValue = "...";
            if (product.hasSelectedSimpleVariation()) {
                simpleVariationValue = product.getSimples().get(product.getSelectedSimplePosition()).getVariationValue();
            }
            if (view instanceof TextView) {
                ((TextView) view).setText(simpleVariationValue);
            }
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Validate if it show regular warning or confirmation cart message<br>
     * - If has cart popup, show configurable confirmation message with cart total price<br>
     * - Else show regular message add item to cart<br>
     */
    public static void showAddToCartCompleteMessage(@NonNull BaseFragment fragment, @NonNull BaseResponse baseResponse, @NonNull EventType eventType) {
        boolean isToShowCartPopUp = CountryPersistentConfigs.hasCartPopup(fragment.getBaseActivity().getApplicationContext());
        PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
        if (isToShowCartPopUp && cart != null && cart.getTotal() > 0) {
            fragment.getBaseActivity().mConfirmationCartMessageView.showMessage(cart.getTotal());
        } else {
            fragment.showWarningSuccessMessage(baseResponse.getSuccessMessage(), eventType);
        }
    }

    public static void setShopFirst(@NonNull ProductRegular productBase, @NonNull View badge) {
        //badge.setVisibility((!productBase.isShopFirst() || ShopSelector.isRtlShop()) ? View.GONE : View.VISIBLE);
    }


    public static void setShopFirst(@NonNull Variation productVariation, @NonNull View badge) {
        badge.setVisibility((!productVariation.isShopFirst() || ShopSelector.isRtlShop()) ? View.GONE : View.VISIBLE);
    }


    /**
     * Shows a dialog with shopOverlayInfo content if clicking on a shopFirst badge or shopFirst drawable in a textView
     * - if the shopfirst logo is visible and overlay info is not empty, the dialog show up
     * - Else if overlay Info is empty, the logo is not clickable even visible
     */
    public static void showShopFirstOverlayMessage(final @NonNull BaseFragment fragment, final @NonNull ProductRegular productBase, final @NonNull View shopFirstView) {
        if (shopFirstView.getVisibility() == View.VISIBLE && TextUtils.isNotEmpty(productBase.getShopFirstOverlay())) {
            //If badge is included in a textView: PDV case
            if (shopFirstView instanceof TextView) {
                // Add shop first drawable
                UIUtils.setDrawableRight((TextView) shopFirstView, R.drawable.ic_shop_first_alias);
                // Set listener
                shopFirstView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (UIUtils.drawableClicked(((TextView) v), event)) {
                            DialogGenericFragment.createInfoDialog(null, productBase.getShopFirstOverlay(), fragment.getString(R.string.ok_label)).show(fragment.getActivity().getSupportFragmentManager(), null);
                            return true;
                        }
                        return false;
                    }
                });

            } else { //if is an image
                shopFirstView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogGenericFragment.createInfoDialog(null, productBase.getShopFirstOverlay(), fragment.getString(R.string.ok_label)).show(fragment.getActivity().getSupportFragmentManager(), null);
                    }
                });
            }
        }
    }

    /**
     * Method used to set the free shipping info
     */
    public static void setFreeShippingInfo(@Nullable ProductRegular product, @Nullable View view) {
        if (product != null && view != null) {
            view.setVisibility(product.hasFreeShipping() ? View.VISIBLE : View.GONE);
        }
    }

}
