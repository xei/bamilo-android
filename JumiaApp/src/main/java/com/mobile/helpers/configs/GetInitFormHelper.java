package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.FormsIndex;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper to get forms index Get forms index
 */
public class GetInitFormHelper extends SuperBaseHelper {

    private static String TAG = GetInitFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.INIT_FORMS;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return super.getRequestUrl(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getFormsIndex);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        FormsIndex formsIndex = (FormsIndex) baseResponse.getMetadata().getData();
        //TODO move to observable
        JumiaApplication.INSTANCE.setFormDataRegistry(formsIndex);
    }

}
