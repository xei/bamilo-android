package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.addresses.AddressPostalCodes;
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
 * Helper used to get the postal codes for a specific city
 */
public class GetPostalCodeHelper extends SuperBaseHelper {
    
    public static String TAG = GetPostalCodeHelper.class.getSimpleName();
    
    public static String CITY_ID_TAG = "city_id";
    
    public static String CUSTOM_TAG = "custom_tag";

    private String customTag;

    @Override
    public EventType getEventType() {
        return EventType.GET_POSTAL_CODE_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(Constants.BUNDLE_URL_KEY))).toString();
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        customTag = args.getString(CUSTOM_TAG);
        Map<String, String> data = new HashMap<>();
        data.put(RestConstants.CITY_ID, args.getString(CITY_ID_TAG));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getPostalCodes);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        AddressPostalCodes postalCodes = (AddressPostalCodes) baseResponse.getMetadata().getData();
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, postalCodes);
        bundle.putString(CUSTOM_TAG, customTag);
    }

    public static Bundle createBundle(String url, int city, String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url.split("\\?")[0]); // TODO REMOVE
        bundle.putString(GetPostalCodeHelper.CITY_ID_TAG, String.valueOf(city));
        bundle.putString(GetPostalCodeHelper.CUSTOM_TAG, tag);
        return bundle;
    }

}
