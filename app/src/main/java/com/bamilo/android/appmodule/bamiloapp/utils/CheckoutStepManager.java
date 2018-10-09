package com.bamilo.android.appmodule.bamiloapp.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bamilo.android.framework.components.customfontviews.AutoResizeTextView;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.R;

import java.util.HashMap;
import java.util.Set;

/**
 * Class used to manage the checkout steps
 * @author sergiopereira
 *
 */
public class CheckoutStepManager {
    
    private static final String TAG = CheckoutStepManager.class.getSimpleName();

    public final static String CREATE_ADDRESS_STEP = "createAddress";
    public final static String ADDRESSES_STEP = "addresses";
    public final static String SHIPPING_STEP = "shipping";
    public final static String PAYMENT_STEP = "payment";
    public final static String ORDER_STEP = "finish";

    public static final int CHECKOUT_TOTAL_MAX_LINES = 2;

    /**
     * Method used to get the Fragment type from a constant 
     * @param nextStep The next step
     * @return {@link FragmentType}
     */
    public static FragmentType getNextFragment(@Nullable String nextStep) {
        // Default case
        FragmentType fragmentType = FragmentType.UNKNOWN;
        // Case not valid next step (null or empty)
        if(TextUtils.isEmpty(nextStep)) return fragmentType;
        // Create addresses step
        else if (nextStep.equalsIgnoreCase(CREATE_ADDRESS_STEP)) fragmentType = FragmentType.CHECKOUT_CREATE_ADDRESS;
        // Billing and shipping address step
        else if (nextStep.equalsIgnoreCase(ADDRESSES_STEP)) fragmentType = FragmentType.CHECKOUT_MY_ADDRESSES;
        // Shipping method step
        else if (nextStep.equalsIgnoreCase(SHIPPING_STEP)) fragmentType = FragmentType.CHECKOUT_SHIPPING;
        // Payment method step
        else if (nextStep.equalsIgnoreCase(PAYMENT_STEP)) fragmentType = FragmentType.CHECKOUT_PAYMENT;
        // Order step
        else if (nextStep.equalsIgnoreCase(ORDER_STEP)) fragmentType = FragmentType.CHECKOUT_FINISH;
        // Return next fragment type
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
                FragmentType.CHECKOUT_MY_ADDRESSES.toString(),
                FragmentType.CHECKOUT_CREATE_ADDRESS.toString(),
                FragmentType.CHECKOUT_EDIT_ADDRESS.toString(),
                FragmentType.CHECKOUT_SHIPPING.toString(),
                FragmentType.CHECKOUT_PAYMENT.toString(),
                FragmentType.CHECKOUT_FINISH.toString(),
                FragmentType.CHECKOUT_THANKS.toString(),
                FragmentType.CHECKOUT_CONFIRMATION.toString()
        };
    }

    public static String[] getAllNativeCheckoutAndCart() {
        return new String[]{
                FragmentType.LOGIN.toString(),
                FragmentType.CHECKOUT_MY_ADDRESSES.toString(),
                FragmentType.CHECKOUT_CREATE_ADDRESS.toString(),
                FragmentType.CHECKOUT_EDIT_ADDRESS.toString(),
                FragmentType.CHECKOUT_SHIPPING.toString(),
                FragmentType.CHECKOUT_PAYMENT.toString(),
                FragmentType.CHECKOUT_FINISH.toString(),
                FragmentType.CHECKOUT_THANKS.toString(),
                FragmentType.CHECKOUT_CONFIRMATION.toString(),
                FragmentType.SHOPPING_CART.toString()
        };
    }

    /**
     * Method used to set the total bar for MyAccount.
     */
    public static void setTotalBarWithFullButton(@NonNull View view) {
        // Hide total view
        view.findViewById(R.id.checkout_total_label).setVisibility(View.GONE);
        // Set next label
        TextView textView = view.findViewById(R.id.checkout_button_enter);
        textView.setText(view.getContext().getResources().getString(R.string.save_label));
        float weight = ((LinearLayout.LayoutParams) textView.getLayoutParams()).weight;
        // Set the view group with the same text weight
        ((LinearLayout) view.findViewById(R.id.checkout_total_bar)).setWeightSum(weight);
    }

    /**
     * Method used for showing checkout total at checkout steps.<br>
     * - Case cart empty (unexpected case) hide the total info
     *
     * @param view           ViewStub or View with TextView (checkout_total_label).
     * @param purchaseEntity OrderSummary to get total
     */
    public static void setTotalBar(@NonNull View view, @Nullable PurchaseEntity purchaseEntity) {
        // Get view
        final AutoResizeTextView titleTextView = view.findViewById(R.id.checkout_total_label);
        // Show full button
        if (purchaseEntity == null) {
            setTotalBarWithFullButton(view);
        }
        // Show info
        else {
            Context context = view.getContext();
            final String title = context.getString(R.string.order_summary_total_label);
            final String finalValue = CurrencyFormatter.formatCurrency(purchaseEntity.getTotal());
            final int color1 = ContextCompat.getColor(context, R.color.black);
            final int color2 = ContextCompat.getColor(context, R.color.black_800);
            titleTextView.setMaxLines(CHECKOUT_TOTAL_MAX_LINES);
            titleTextView.setText(UIUtils.setSpan(title + " ", finalValue, color1, color2));
            titleTextView.post(() -> {
                if (titleTextView.getLineCount() >= CHECKOUT_TOTAL_MAX_LINES) {
                    titleTextView.setText(UIUtils.setSpan(title + "\n", finalValue, color1, color2));
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
                String text = String.format(context.getString(R.string.placeholder_discount), CurrencyFormatter.formatCurrency(priceRules.get(key)));
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
    public static void validateLoggedNextStep(BaseActivity activity, boolean isInCheckoutProcess, FragmentType mParentFragmentType, FragmentType nextStepFromParent, FragmentType nextStepFromApi, Bundle arguments) {
        // Case next step from api
        if(isInCheckoutProcess) {
            goToCheckoutNextStepFromApi(activity, mParentFragmentType, nextStepFromApi);
        } else {
            goToNextStepFromParent(activity, nextStepFromParent, arguments);
        }
    }

    /**
     * Method used to switch the step
     */
    private static void goToNextStepFromParent(BaseActivity activity, FragmentType nextStepFromParent, Bundle arguments) {
        // Validate the next step
        if (nextStepFromParent != null && nextStepFromParent != FragmentType.UNKNOWN
                && nextStepFromParent != FragmentType.WRITE_REVIEW) {
            FragmentController.getInstance().popLastEntry(FragmentType.LOGIN.toString());
            activity.onSwitchFragment(nextStepFromParent,arguments, FragmentController.ADD_TO_BACK_STACK);
        } else {
            activity.onBackPressed();
        }
    }

    /**
     * Method used to switch the checkout step
     */
    private static void goToCheckoutNextStepFromApi(BaseActivity activity, FragmentType mParentFragmentType, FragmentType nextStepType) {
        // Get next step
        if (nextStepType == null || nextStepType == FragmentType.UNKNOWN) {
            //super.showFragmentErrorRetry();
            activity.onBackPressed();
        } else {
            // Case comes from MY_ACCOUNT
            if(mParentFragmentType == FragmentType.MY_ACCOUNT) {
                if(nextStepType == FragmentType.CHECKOUT_CREATE_ADDRESS) nextStepType = FragmentType.MY_ACCOUNT_CREATE_ADDRESS;
                else if(nextStepType == FragmentType.CHECKOUT_EDIT_ADDRESS) nextStepType = FragmentType.MY_ACCOUNT_EDIT_ADDRESS;
                else if(nextStepType == FragmentType.CHECKOUT_MY_ADDRESSES) nextStepType = FragmentType.MY_ACCOUNT_MY_ADDRESSES;
            }
            // Clean stack for new native checkout on the back stack (auto login)
            activity.removeAllNativeCheckoutFromBackStack();
            activity.onSwitchFragment(nextStepType, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }
}
