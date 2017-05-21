package com.mobile.helpers.account;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;

/**
 * Helper used to retrieve order history
 * 
 * @author Paulo Carvalho
 */
public class GetMyOrdersListHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_MY_ORDERS_LIST_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getOrdersList);
    }

    public static Bundle createBundle(int page) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.PAGE, page);
        values.put(RestConstants.PER_PAGE, IntConstants.MAX_ITEMS_PER_PAGE);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }

}
