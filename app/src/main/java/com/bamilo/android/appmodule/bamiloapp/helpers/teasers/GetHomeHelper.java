package com.bamilo.android.appmodule.bamiloapp.helpers.teasers;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
