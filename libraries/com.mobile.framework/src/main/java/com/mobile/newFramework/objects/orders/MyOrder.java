package com.mobile.newFramework.objects.orders;


import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

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
    private int numPages = 0;
    private int totalOrders = 0;
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
            numPages = paginationObject.optInt(RestConstants.TOTAL_PAGES, 0);

            totalOrders = jsonObject.optInt(RestConstants.JSON_ORDER_TOTAL_NUM_TAG, -1);
            Print.d( "ORDERS TOTAL: " + totalOrders);
            orders = new ArrayList<>();
            // Get order history
            JSONArray ordersArray = jsonObject.optJSONArray(RestConstants.JSON_ORDERS_TAG);
            if (null != ordersArray && ordersArray.length() > 0)
                for (int i = 0; i < ordersArray.length(); i++) {
                    Order order = new Order(ordersArray.getJSONObject(i));
                    if (totalOrders > 0)
                        order.setTotalOrdersHistory(totalOrders);
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
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }


    public int getCurrentPage() {
        return currentPage;
    }

    public int getNumPages() {
        return numPages;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
}
