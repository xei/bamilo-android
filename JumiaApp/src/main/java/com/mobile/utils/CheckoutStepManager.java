package com.mobile.utils;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewStub;

import com.mobile.components.customfontviews.AutoResizeTextView;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.objects.orders.OrderSummary;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.TextViewUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.R;

import org.json.JSONObject;

/**
 * Class used to manage the checkout steps
 * @author sergiopereira
 *
 */
public class CheckoutStepManager {
    
    private static final String TAG = CheckoutStepManager.class.getSimpleName();

    public final static String ADDRESSES_STEP = "createAddress";
    public final static String BILLING_STEP = "billing";
    public final static String SHIPPING_STEP = "shippingMethod";
    public final static String PAYMENT_STEP = "paymentMethod";
    public final static String ORDER_STEP = "finish";

    public static final int CHECKOUT_TOTAL_MAX_LINES = 2;

    /**
     * Method used to get the next step value from JSON and return a fragment type
     * @param jsonObject The json response to get the next step
     * @return {@link FragmentType}
     */
    public static FragmentType getNextCheckoutFragment(JSONObject jsonObject){
        try {

            // Get the next step from json
            String nextStep = getNextCheckoutStep(jsonObject);

            // Validate value
            if(nextStep != null) return getNextFragment(nextStep);
            else return FragmentType.UNKNOWN;

        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: JSON OBJECT IS NULL");
            return FragmentType.UNKNOWN;
        }
    }

    public static String getNextCheckoutStep(JSONObject jsonObject){
        String nextStep = null;

        if(jsonObject.has(RestConstants.JSON_NATIVE_CHECKOUT_TAG)){
            // From native checkout tag
            JSONObject checkoutJson = jsonObject.optJSONObject(RestConstants.JSON_NATIVE_CHECKOUT_TAG);
            nextStep = checkoutJson.optString(RestConstants.JSON_NEXT_STEP_TAG, null);
        } else if(jsonObject.has(RestConstants.JSON_NEXT_STEP_TAG)){
            // From next step tag
            nextStep = jsonObject.optString(RestConstants.JSON_NEXT_STEP_TAG, null);
        } else if (jsonObject.has(RestConstants.JSON_DATA_TAG) &&
                jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG) != null &&
                jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG).has(RestConstants.JSON_NATIVE_CHECKOUT_TAG)) {
            // From data and native checkout tag
            JSONObject dataJson = jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG);
            JSONObject checkoutJson = dataJson.optJSONObject(RestConstants.JSON_NATIVE_CHECKOUT_TAG);
            nextStep = checkoutJson.optString(RestConstants.JSON_NEXT_STEP_TAG, null);
        }

        return nextStep;
    }

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
                FragmentType.ABOUT_YOU.toString(),
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
     * @param viewStub Viewstub with TextView (checkout_total_label).
     * @param orderSummary OrderSummary to get total
     * @param cart Cart in case orderSummary is null
     */
    public static void showCheckoutTotal(ViewStub viewStub, OrderSummary orderSummary, ShoppingCart cart){
        String value = null;
        if(orderSummary != null){
            value = orderSummary.getTotal();
        } else if(cart != null){
            value = cart.getCartValue();
        }

        if(!TextUtils.isEmpty(value) && viewStub != null){
            View inflatedView = viewStub.inflate();
            Resources resources = inflatedView.getResources();
            final String title = resources.getString(R.string.order_summary_total_label);
            final String finalValue = CurrencyFormatter.formatCurrency(value).replaceAll("\\s","");
            final int greyColor = resources.getColor(R.color.grey_middledark);
            final int redColor = resources.getColor(R.color.red_cc0000);
            final AutoResizeTextView titleTextView = ((AutoResizeTextView) inflatedView.findViewById(R.id.checkout_total_label));
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

}
