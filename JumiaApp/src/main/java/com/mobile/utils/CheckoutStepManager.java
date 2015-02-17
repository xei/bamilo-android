package com.mobile.utils;

import org.json.JSONObject;

import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.rest.RestConstants;

import de.akquinet.android.androlog.Log;

/**
 * Class used to manage the checkout steps
 * @author sergiopereira
 *
 */
public class CheckoutStepManager {
    
    private static final String TAG = CheckoutStepManager.class.getSimpleName();
    
    public final static String POLL_STEP = "checkoutpoll";
    public final static String ADDRESSES_STEP = "createAddress";
    public final static String BILLING_STEP = "billing";
    public final static String SHIPPING_STEP = "shippingMethod";
    public final static String PAYMENT_STEP = "paymentMethod";
    public final static String ORDER_STEP = "finish";
    
    /**
     * Method used to get the next step value from JSON and return a fragment type
     * @param jsonObject
     * @return {@link FragmentType}
     */
    public static FragmentType getNextCheckoutStep(JSONObject jsonObject){
        // Get the next step from json 
        String nextStep = null;
        
        try {
            
            // Validate data
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
                         
            // Validate value
            if(nextStep != null) return getNextFragment(nextStep);
            else return FragmentType.UNKNOWN;
                    
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: JSON OBJECT IS NULL");
            return FragmentType.UNKNOWN;
        }
    }
    
    /**
     * Method used to get the Fragment type from a constant 
     * @param nextStep
     * @return {@link FragmentType}
     */
    public static FragmentType getNextFragment(String nextStep) {
        Log.i(TAG, "NEXT STEP STRING: " + nextStep);
        // Default case
        FragmentType fragmentType = FragmentType.UNKNOWN;
        // Poll answer
        if (nextStep.equalsIgnoreCase(POLL_STEP)) fragmentType = FragmentType.POLL;
        // Create addresses step
        else if (nextStep.equalsIgnoreCase(ADDRESSES_STEP)) fragmentType = FragmentType.CREATE_ADDRESS;
        // Billing and shipping address step
        else if (nextStep.equalsIgnoreCase(BILLING_STEP)) fragmentType = FragmentType.MY_ADDRESSES;
        // Shipping method step
        else if (nextStep.equalsIgnoreCase(SHIPPING_STEP)) fragmentType = FragmentType.SHIPPING_METHODS;
        // Payment method step
        else if (nextStep.equalsIgnoreCase(PAYMENT_STEP)) fragmentType = FragmentType.PAYMENT_METHODS;
        // Order step
        else if (nextStep.equalsIgnoreCase(ORDER_STEP)) fragmentType = FragmentType.MY_ORDER;
        // Return next fragment type
        Log.i(TAG, "NEXT STEP FRAGMENT: " + fragmentType.toString());
        return fragmentType;
    }
}
