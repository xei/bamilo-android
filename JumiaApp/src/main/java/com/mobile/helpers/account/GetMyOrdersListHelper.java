/**
 * 
 */
package com.mobile.helpers.account;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper used to retrieve order history
 * 
 * @author Paulo Carvalho
 */
public class GetMyOrdersListHelper extends SuperBaseHelper {

    public static String TAG = GetMyOrdersListHelper.class.getSimpleName();

    public static final String PAGE_NUMBER = "page";

    public static final String PER_PAGE = "per_page";


    @Override
    public EventType getEventType() {
        return EventType.GET_MY_ORDERS_LIST_EVENT;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(PAGE_NUMBER, "" + args.getInt(PAGE_NUMBER));
        data.put(PER_PAGE, "" + args.getInt(PER_PAGE));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getOrdersList);
    }

//    @Override
//    public void postSuccess(BaseResponse baseResponse) {
//        super.postSuccess(baseResponse);
//        MyOrder orders = (MyOrder) baseResponse.getMetadata().getData();
//        // Get order summary from response
//        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, orders.getOrders());
//        bundle.putInt(CURRENT_PAGE, orders.getCurrentPage());
//        bundle.putInt(TOTAL_PAGES, orders.getTotalOrders());
//        bundle.putInt(TOTAL_PAGES, orders.getNumPages());
//    }



}
