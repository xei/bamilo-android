package com.mobile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobile.components.customfontviews.AutoResizeTextViewNew;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.utils.TextViewUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.BaseActivity;
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
     * Method used to set the total bar for MyAccount.
     */
    public static void setTotalBarForMyAccount(@NonNull View view) {
        // Hide total view
        view.findViewById(R.id.checkout_total_label).setVisibility(View.GONE);
        // Set next label
        TextView textView =  (TextView) view.findViewById(R.id.checkout_button_enter);
        textView.setText(view.getContext().getResources().getString(R.string.save_label));
        float weight = ((LinearLayout.LayoutParams) textView.getLayoutParams()).weight;
        // Set the view group with the same text weight
        ((LinearLayout) view.findViewById(R.id.checkout_total_bar)).setWeightSum(weight);
    }

    /**
     * Method used for showing checkout total at checkout steps.
     *
     * @param view ViewStub or View with TextView (checkout_total_label).
     * @param purchaseEntity OrderSummary to get total
     */
    public static void setTotalBar(@NonNull View view, @NonNull PurchaseEntity purchaseEntity){
        double value = purchaseEntity.getTotal();
        if(value > 0){
            Resources resources = view.getResources();
            final String title = resources.getString(R.string.order_summary_total_label);
            final String finalValue = CurrencyFormatter.formatCurrency(value).replaceAll("\\s","");
            final int color1 = resources.getColor(R.color.black);
            final int color2 = resources.getColor(R.color.black_800);
            final AutoResizeTextViewNew titleTextView = ((AutoResizeTextViewNew) view.findViewById(R.id.checkout_total_label));
            titleTextView.setMaxLines(CHECKOUT_TOTAL_MAX_LINES);
            titleTextView.setText(TextViewUtils.setSpan(title + " ", finalValue, color1, color2));
            titleTextView.post(new Runnable() {
                @Override
                public void run() {
                    if (titleTextView.getLineCount() >= CHECKOUT_TOTAL_MAX_LINES) {
                        titleTextView.setText(TextViewUtils.setSpan(title + "\n", finalValue, color1, color2));
                    }
                }
            });
        }
    }

    public static void showPriceRules(Context context, ViewGroup priceRulesContainer, HashMap<String, String> priceRules) {
        priceRulesContainer.removeAllViews();
        if (priceRules != null && priceRules.size() > 0) {
            priceRulesContainer.setVisibility(View.VISIBLE);
            LayoutInflater mLayoutInflater = LayoutInflater.from(context);
            Set<String> priceRulesKeys = priceRules.keySet();
            for (String key : priceRulesKeys) {
                View priceRuleElement = mLayoutInflater.inflate(R.layout.price_rules_element, priceRulesContainer, false);
                // Set label
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_label)).setText(key);
                // Set value
                String text = String.format(context.getString(R.string.format_discount_value), CurrencyFormatter.formatCurrency(priceRules.get(key)));
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_value)).setText(text);
                priceRulesContainer.addView(priceRuleElement);
            }
        } else {
            priceRulesContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Method used to validate the next after log in.
     * @param activity - The activity
     * @param isInCheckoutProcess - Checkout flag
     * @param mParentFragmentType - The parent fragment type
     * @param nextStepFromParent - The next step from parent
     * @param nextStepFromApi - The next step from Api, used case is in checkout process
     * @author spereira
     */
    public static void validateLoggedNextStep(BaseActivity activity, boolean isInCheckoutProcess, FragmentType mParentFragmentType, FragmentType nextStepFromParent, FragmentType nextStepFromApi) {
        // Case next step from api
        if(isInCheckoutProcess) {
            goToCheckoutNextStepFromApi(activity, mParentFragmentType, nextStepFromApi);
        } else {
            goToNextStepFromParent(activity, nextStepFromParent);
        }
    }

    /**
     * Method used to switch the step
     */
    private static void goToNextStepFromParent(BaseActivity activity, FragmentType nextStepFromParent) {
        // Validate the next step
        if (nextStepFromParent != null && nextStepFromParent != FragmentType.UNKNOWN) {
            Print.i(TAG, "NEXT STEP FROM PARENT: " + nextStepFromParent.toString());
            FragmentController.getInstance().popLastEntry(FragmentType.LOGIN.toString());
            Bundle args = new Bundle();
            args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
            activity.onSwitchFragment(nextStepFromParent, args, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Print.i(TAG, "NEXT STEP FROM PARENT: BACK");
            activity.onBackPressed();
        }
    }

    /**
     * Method used to switch the checkout step
     */
    private static void goToCheckoutNextStepFromApi(BaseActivity activity, FragmentType mParentFragmentType, FragmentType nextStepType) {
        // Get next step
        if (nextStepType == null || nextStepType == FragmentType.UNKNOWN) {
            Print.w(TAG, "NEXT STEP FROM API IS NULL");
            //super.showFragmentErrorRetry();
            activity.onBackPressed();
        } else {
            Print.i(TAG, "NEXT STEP FROM API: " + nextStepType.toString());
            // Case comes from MY_ACCOUNT
            if(mParentFragmentType == FragmentType.MY_ACCOUNT) {
                if(nextStepType == FragmentType.CREATE_ADDRESS) nextStepType = FragmentType.MY_ACCOUNT_CREATE_ADDRESS;
                else if(nextStepType == FragmentType.EDIT_ADDRESS) nextStepType = FragmentType.MY_ACCOUNT_EDIT_ADDRESS;
                else if(nextStepType == FragmentType.MY_ADDRESSES) nextStepType = FragmentType.MY_ACCOUNT_MY_ADDRESSES;
            }
            // Clean stack for new native checkout on the back stack (auto login)
            activity.removeAllNativeCheckoutFromBackStack();
            activity.onSwitchFragment(nextStepType, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }


}
