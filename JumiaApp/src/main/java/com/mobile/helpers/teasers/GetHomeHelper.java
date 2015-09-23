package com.mobile.helpers.teasers;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the home page
 * @author sergiopereira
 */
public class GetHomeHelper extends SuperBaseHelper {

    public static String TAG = GetHomeHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
//        new GetHomePage(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getHome);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_HOME_EVENT;
    }

}
