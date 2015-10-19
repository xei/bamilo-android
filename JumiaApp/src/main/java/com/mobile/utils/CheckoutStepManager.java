package com.mobile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.mobile.components.customfontviews.AutoResizeTextView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.TextViewUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.R;

import java.util.HashMap;
import java.util.Set;

/**
 * Class used to manage the checkout steps
 * @author sergiopereira
 *
 */
public class CheckoutStepManager {
    
    private static final String TAG = CheckoutStepManager.class.getSimpleName();

    public final static String ADDRESSES_STEP = "createAddress";
    public final static String BILLING_STEP = "addresses";
    public final static String SHIPPING_STEP = "shippingMethod";
    public final static String PAYMENT_STEP = "paymentMethod";
    public final static String ORDER_STEP = "finish";

    public static final int CHECKOUT_TOTAL_MAX_LINES = 2;

    /**
     * Method used to get the Fragment type from a constant 
     * @param nextStep The next step
     * @return {@link FragmentType}
     */
    public static FragmentType getNextFragment(String nextStep) {
        Print.i(TAG, "NEXT STEP STRING: " + nextStep);
        // Default case
        FragmentType fragmentType = FragmentType.UNKNOWN;
        // Create addresses step
        if (nextStep.equalsIgnoreCase(ADDRESSES_STEP)) fragmentType = FragmentType.CREATE_ADDRESS;
        // Billing and shipping address step
        else if (nextStep.equalsIgnoreCase(BILLING_STEP)) fragmentType = FragmentType.MY_ADDRESSES;
        // Shipping method step
        else if (nextStep.equalsIgnoreCase(SHIPPING_STEP)) fragmentType = FragmentType.SHIPPING_METHODS;
        // Payment method step
        else if (nextStep.equalsIgnoreCase(PAYMENT_STEP)) fragmentType = FragmentType.PAYMENT_METHODS;
        // Order step
        else if (nextStep.equalsIgnoreCase(ORDER_STEP)) fragmentType = FragmentType.MY_ORDER;
        // Return next fragment type
        Print.i(TAG, "NEXT STEP FRAGMENT: " + fragmentType.toString());
        return fragmentType;
    }

    /**
     * Return all checkout fragment types
     *
     * @return String[] a list of types
     */
    public static String[] getAllNativeCheckout() {
        return new String[]{
                FragmentType.LOGIN.toString(),
                FragmentType.MY_ADDRESSES.toString(),
                FragmentType.CREATE_ADDRESS.toString(),
                FragmentType.EDIT_ADDRESS.toString(),
                FragmentType.SHIPPING_METHODS.toString(),
                FragmentType.PAYMENT_METHODS.toString(),
                FragmentType.MY_ORDER.toString(),
                FragmentType.CHECKOUT_THANKS.toString()
        };
    }

    /**
     * Method used for showing checkout total at checkout steps.
     *
     * @param view View with TextView (checkout_total_label).
     * @param value Value to show on checkout total
     */
    public static void showCheckoutTotal(View view, String value){

        if(!TextUtils.isEmpty(value) && view != null){
            Resources resources = view.getResources();
            final String title = resources.getString(R.string.order_summary_total_label);
            final String finalValue = CurrencyFormatter.formatCurrency(value).replaceAll("\\s","");
            final int greyColor = resources.getColor(R.color.grey_middledark);
            final int redColor = resources.getColor(R.color.red_cc0000);
            final AutoResizeTextView titleTextView = ((AutoResizeTextView) view.findViewById(R.id.checkout_total_label));
            titleTextView.setMaxLines(CHECKOUT_TOTAL_MAX_LINES);
            titleTextView.setText(TextViewUtils.setSpan(title + " ", finalValue,
                    greyColor, redColor));

            titleTextView.post(new Runnable() {
                @Override
                public void run() {
                    if(titleTextView.getLineCount() >= CHECKOUT_TOTAL_MAX_LINES){
                        titleTextView.setText(TextViewUtils.setSpan(title + "\n", finalValue, greyColor, redColor));
                    }
                }
            });

        }
    }

    /**
     * Method used for showing checkout total at checkout steps.
     *
     * @param viewStub ViewStub or View with TextView (checkout_total_label).
     * @param purchaseEntity OrderSummary to get total
     */
    public static void showCheckoutTotal(View viewStub, PurchaseEntity purchaseEntity){
        String value = "" + purchaseEntity.getTotal();
        if(!TextUtils.isEmpty(value) && viewStub != null){
            if(viewStub instanceof ViewStub){
                viewStub = ((ViewStub) viewStub).inflate();
            }
            showCheckoutTotal(viewStub,value);
        }
    }

    public static void showPriceRules(Context context, ViewGroup priceRulesContainer, HashMap<String, String> priceRules){
        priceRulesContainer.removeAllViews();
        if (priceRules != null && priceRules.size() > 0) {
            priceRulesContainer.setVisibility(View.VISIBLE);
            LayoutInflater mLayoutInflater = LayoutInflater.from(context);
            Set<String> priceRulesKeys = priceRules.keySet();
            for (String key : priceRulesKeys) {
                View priceRuleElement = mLayoutInflater.inflate(R.layout.price_rules_element,priceRulesContainer, false);
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_label)).setText(key);
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_value)).setText("-"+ CurrencyFormatter.formatCurrency(priceRules.get(key)));
                priceRulesContainer.addView(priceRuleElement);
            }
        } else {
            priceRulesContainer.setVisibility(View.GONE);
        }
    }

}
