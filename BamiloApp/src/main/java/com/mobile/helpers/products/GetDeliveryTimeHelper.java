package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;

public class GetDeliveryTimeHelper extends SuperBaseHelper {

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getDeliveryTime);

    }

    @Override
    public EventType getEventType() {
        return EventType.GET_DELIVERY_TIME;
    }

    public static Bundle createBundle(String sku, Integer cityId) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKUS, sku);
        if (cityId != null && cityId >= 0) {
            values.put(RestConstants.CITY_ID_REQUEST, cityId);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}
