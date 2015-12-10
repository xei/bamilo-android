package com.mobile.helpers.configs;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Ratings dynamic form helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetRatingFormHelper extends SuperBaseHelper {

    protected static String TAG = GetRatingFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_FORM_RATING_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getRatingForm);
    }

}
