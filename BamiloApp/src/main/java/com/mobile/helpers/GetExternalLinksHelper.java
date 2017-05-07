package com.mobile.helpers;

import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Class used to get External Links section  from API
 */
public class GetExternalLinksHelper extends SuperBaseHelper {
    
    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getExternalLinks);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_EXTERNAL_LINKS;
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_NOT_PRIORITARY;
    }
    
}
