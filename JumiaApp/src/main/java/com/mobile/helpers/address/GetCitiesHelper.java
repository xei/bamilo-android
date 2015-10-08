package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.addresses.AddressCities;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper used to get the address cities
 */
public class GetCitiesHelper extends SuperBaseHelper {
    
    public static String TAG = GetCitiesHelper.class.getSimpleName();
    
    public static String REGION_ID_TAG = "region_id";
    
    public static String CUSTOM_TAG = "custom_tag";

    private String customTag;

    @Override
    public EventType getEventType() {
        return EventType.GET_CITIES_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(Constants.BUNDLE_URL_KEY))).toString();
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        customTag = args.getString(CUSTOM_TAG);
        Map<String, String> data = new HashMap<>();
        data.put(RestConstants.REGION, args.getString(REGION_ID_TAG));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCities);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        AddressCities cities = (AddressCities) baseResponse.getMetadata().getData();
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, cities);
        bundle.putString(CUSTOM_TAG, customTag);
    }

    public static Bundle createBundle(String url, int region, String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url.split("\\?")[0]); // FIXME: API v2.0
        bundle.putString(GetCitiesHelper.REGION_ID_TAG, String.valueOf(region));
        bundle.putString(GetCitiesHelper.CUSTOM_TAG, tag);
        return bundle;
    }

}
