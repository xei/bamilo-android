package com.bamilo.android.appmodule.bamiloapp.helpers;

import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
