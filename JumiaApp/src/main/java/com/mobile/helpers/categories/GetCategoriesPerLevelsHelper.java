package com.mobile.helpers.categories;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Class used to get categories from API:<br>
 * - All categories (No argmuments)<br>
 * - Root categories (Arguments with paginate=1)<br>
 * - Specific category (Arguments with paginate=1 and category=<url_key>)<br>
 * 
 * @author sergiopereira
 * 
 */
public class GetCategoriesPerLevelsHelper extends SuperBaseHelper {
    
    public static String TAG = GetCategoriesPerLevelsHelper.class.getSimpleName();
    
    public static final String PAGINATE_KEY = "paginate";
    
    public static final String CATEGORY_KEY = "category";
    
    public static final String PAGINATE_ENABLE = "1";

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
//        new GetCategoriesPaginated(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCategoriesPaginated);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_CATEGORIES_EVENT;
    }

}
