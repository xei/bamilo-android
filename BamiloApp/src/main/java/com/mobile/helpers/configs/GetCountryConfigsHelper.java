package com.mobile.helpers.configs;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.preferences.CountryPersistentConfigs;

/**
 * Get Countries Configurations helper from {@link EventType#GET_COUNTRY_CONFIGURATIONS}
 *
 * @author Manuel Silva
 * @modified sergiopereira
 */
public class GetCountryConfigsHelper extends SuperBaseHelper {

    public static final String TAG = GetCountryConfigsHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCountryConfigurations);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_COUNTRY_CONFIGURATIONS;
    }

    /**
     * TODO move to observable
     */
    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        // Validate and save new configurations
        CountryPersistentConfigs.newConfigurations((CountryConfigs) baseResponse.getContentData());
    }

}
