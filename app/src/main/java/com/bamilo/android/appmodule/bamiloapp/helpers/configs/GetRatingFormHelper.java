package com.bamilo.android.appmodule.bamiloapp.helpers.configs;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
