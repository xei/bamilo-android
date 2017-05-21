package com.mobile.helpers.categories;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

/**
 * Class used to get All categories  from API
 * 
 * @author sergiopereira
 * @modified Paulo Carvalho
 * 
 */
public class GetCategoriesHelper extends SuperBaseHelper {
    
    public static String TAG = GetCategoriesHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCategoriesPaginated);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_CATEGORIES_EVENT;
    }
    
}
