package com.mobile.helpers.teasers;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

/**
 * Helper used to get the home page
 * @author sergiopereira
 */
public class GetHomeHelper extends SuperBaseHelper {

    public static String TAG = GetHomeHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getHome);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_HOME_EVENT;
    }

}
