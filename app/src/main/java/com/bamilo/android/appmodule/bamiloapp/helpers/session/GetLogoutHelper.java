package com.bamilo.android.appmodule.bamiloapp.helpers.session;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetLogoutHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.LOGOUT_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.logoutCustomer);
    }
}
