package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Get a static page
 */
public class GetStaticPageHelper extends SuperBaseHelper {

    public static String TAG = GetStaticPageHelper.class.getSimpleName();

    public static final String TERMS_PAGE = "terms_mobile";

    public static final String INTERNATIONAL_PRODUCT_POLICY_PAGE = "international-product-policy";

    @Override
    public EventType getEventType() {
        return EventType.GET_STATIC_PAGE;
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_PRIORITARY;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getStaticPage);
    }

    public static Bundle createBundle(String staticPage) {
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.PARAM_1, staticPage);
        return bundle;
    }
    
}
