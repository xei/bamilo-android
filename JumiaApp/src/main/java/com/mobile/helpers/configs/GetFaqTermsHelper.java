package com.mobile.helpers.configs;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.preferences.CountryPersistentConfigs;

public class GetFaqTermsHelper extends SuperBaseHelper{

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getFaqTerms);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_FAQ_TERMS;
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        MobileAbout mobileAbout = (MobileAbout)baseResponse.getMetadata().getData();

        CountryPersistentConfigs.saveMoreInfo(JumiaApplication.INSTANCE.getApplicationContext(), mobileAbout);
    }
}
