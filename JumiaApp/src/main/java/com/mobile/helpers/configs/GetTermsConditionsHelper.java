package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.statics.StaticTermsConditions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Get Product Information helper
 */
public class GetTermsConditionsHelper extends SuperBaseHelper {

    private static String TAG = GetTermsConditionsHelper.class.getSimpleName();

    public static final String KEY = "key";

    @Override
    public EventType getEventType() {
        return EventType.GET_TERMS_EVENT;
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_PRIORITARY;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(KEY, "terms_mobile");
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new GetTermsConditions(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getTermsAndConditions);
    }
    
}
