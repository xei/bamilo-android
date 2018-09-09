package com.bamilo.android.framework.service.objects.orders;


import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to save the newsletter subscription
 * @author sergiopereira
 */
public class MyOrder implements IJSONSerializable {

    public static final String TAG = MyOrder.class.getSimpleName();

    private int currentPage = 0;
    private int totalPages = 0;
    private ArrayList<Order> orders;


    /**
     * Empty constructor
     */
    public MyOrder() {
        // ...
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        try {
            JSONObject paginationObject = jsonObject.optJSONObject(RestConstants.PAGINATION);
            currentPage = paginationObject.optInt(RestConstants.CURRENT_PAGE, 0);
            totalPages = paginationObject.optInt(RestConstants.TOTAL_PAGES, 0);

            int totalOrders = jsonObject.optInt(RestConstants.TOTAL_ORDERS, -1);
            Print.d( "ORDERS TOTAL: " + totalOrders);
            orders = new ArrayList<>();
            // Get order history
            JSONArray ordersArray = jsonObject.optJSONArray(RestConstants.ORDERS);
            if (null != ordersArray && ordersArray.length() > 0)
                for (int i = 0; i < ordersArray.length(); i++) {
                    Order order = new Order(ordersArray.getJSONObject(i));
                    orders.add(order);
                }

        } catch (JSONException e) {
            Print.d("ERROR ON PARSE: " + e.getMessage());

        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
}
