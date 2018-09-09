package com.bamilo.android.appmodule.bamiloapp.helpers.configs;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.statics.MobileAbout;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;

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
