package com.bamilo.android.appmodule.bamiloapp.helpers.categories;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;

/**
 * Class used to get All categories  from API
 * 
 * @author sergiopereira
 * @modified Paulo Carvalho
 * 
 */
public class GetSubCategoriesHelper extends SuperBaseHelper {
    
    public static String TAG = GetSubCategoriesHelper.class.getSimpleName();
    public static final String SELECTED_Category_URLKEY = "urlkey";

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getSubCategoriesPaginated);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_SUBCATEGORIES_EVENT;
    }


    public static Bundle createBundle(String name) {
        ContentValues values = new ContentValues();
        values.put(GetSubCategoriesHelper.SELECTED_Category_URLKEY, name);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}
