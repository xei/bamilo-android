package com.mobile.helpers.session;

import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Auto login helper
 */
public class LoginAutoHelper extends LoginHelper {

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
