package com.bamilo.android.appmodule.bamiloapp.helpers.session;

import android.content.Context;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

/**
 * Auto login helper
 */
public class LoginAutoHelper extends LoginHelper {

    public LoginAutoHelper(Context context) {
        super(context);
    }

    @Override
    public EventType getEventType() {
        return EventType.AUTO_LOGIN_EVENT;
    }

    public static Bundle createAutoLoginBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, BamiloApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        return bundle;
    }

}
