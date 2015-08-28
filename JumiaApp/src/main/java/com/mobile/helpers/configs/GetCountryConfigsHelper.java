/**
 * @author Manuel Silva
 */
package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.preferences.CountryPersistentConfigs;

/**
 * Get Countries Configurations helper from {@link EventType#GET_COUNTRY_CONFIGURATIONS}
 *
 * @author Manuel Silva
 * @modified sergiopereira
 */
public class GetCountryConfigsHelper extends SuperBaseHelper {

    private static final String TAG = GetCountryConfigsHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
//        new GetCountryConfigurations(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCountryConfigurations);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_COUNTRY_CONFIGURATIONS;
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);

        //TODO move to observable
        CountryConfigs countryConfigs = (CountryConfigs) baseResponse.getMetadata().getData();
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY,countryConfigs);
        CountryPersistentConfigs.writePreferences(JumiaApplication.INSTANCE.getApplicationContext(), countryConfigs);
    }

}
