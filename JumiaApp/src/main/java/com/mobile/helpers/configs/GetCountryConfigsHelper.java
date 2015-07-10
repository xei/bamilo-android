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
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
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
        CountryPersistentConfigs.writePreferences(JumiaApplication.INSTANCE.getApplicationContext(), countryConfigs);
    }

//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.i(TAG, "ON REQUEST");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, TYPE.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(TYPE.name()));
//        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
//        return bundle;
//    }

//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE RESPONSE");
//        try {
//            CountryConfigs countryConfigs = new CountryConfigs(jsonObject);
//            countryConfigs.writePreferences(JumiaApplication.INSTANCE.getApplicationContext());
//        } catch (JSONException e) {
//            e.printStackTrace();
//            bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.i(TAG, "ON PARSE ERROR RESPONSE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.i(TAG, "ON ERROR RESPONSE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
