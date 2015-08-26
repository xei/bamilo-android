package com.mobile.newFramework.objects.orders;

import com.mobile.newFramework.objects.cart.ShoppingCart;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * The OrderSummary class representation.<br/>
 * Contains: Order, Cart, Shipping, Payment and Billing.
 *
 * @author sergiopereira
 */
public class OrderSummary extends ShoppingCart {

    public final static String TAG = OrderSummary.class.getSimpleName();

    /**
     * Empty constructor
     */
    public OrderSummary() {
        super();
    }

    /**
     * Constructor
     *
     * @param jsonObject The json response
     * @throws JSONException
     */
    public OrderSummary(JSONObject jsonObject) throws JSONException {
        super();
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        return true;
    }

}
