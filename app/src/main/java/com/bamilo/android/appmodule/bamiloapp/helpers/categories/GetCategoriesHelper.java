package com.bamilo.android.appmodule.bamiloapp.helpers.categories;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
