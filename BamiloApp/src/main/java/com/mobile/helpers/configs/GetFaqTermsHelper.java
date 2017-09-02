package com.mobile.helpers.configs;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.statics.MobileAbout;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
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
        MobileAbout mobileAbout = (MobileAbout) baseResponse.getContentData();
        CountryPersistentConfigs.saveMoreInfo(BamiloApplication.INSTANCE.getApplicationContext(), mobileAbout);
    }
}
