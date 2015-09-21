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
        AddressCitiesStruct cities = new AddressCitiesStruct( (AddressCities)baseResponse.getMetadata().getData());
        cities.setCustomTag(customTag);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, cities);
    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle bundle) {
//        Log.d(TAG, "REQUEST");
//        // Bundle bundle = new Bundle();
//        customTag = bundle.getString(CUSTOM_TAG);
//        // Get region
//        int region = bundle.getInt(REGION_ID_TAG);
//        // Get action
//        String action = bundle.getString(Constants.BUNDLE_URL_KEY);
//        if(TextUtils.isEmpty(action)) action = EVENT_TYPE.action;
//
//        // Validate action
//        if(action.contains("fk_customer_address_region")) action = action.replace("fk_customer_address_region", "" + region);
//        else action = action.replace("region=\\d+" , "region=" + region);
//
//        Log.d(TAG, "URL: " + action);
//
//        bundle.putString(Constants.BUNDLE_URL_KEY, action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "RESPONSE: " + jsonObject.toString());
//        try {
//            // Regions
//            ArrayList<AddressCity> cities = new ArrayList<>();
//            // For each item
//            JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.JSON_DATA_TAG);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject json = jsonArray.getJSONObject(i);
//                // Save the region
//                cities.add(new AddressCity(json));
//            }
//            // Save regions
//            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, cities);
//            bundle.putString(CUSTOM_TAG, customTag);
//        } catch (JSONException e) {
//            Log.w(TAG, "PARSE EXCEPTION", e);
//            return parseErrorBundle(bundle);
//        }
//        Log.d(TAG, "PARSE WITH SUCCESS");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE RESPONSE BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }

    public class AddressCitiesStruct extends AddressCities {
        private String customTag;

        public AddressCitiesStruct(){}

        public AddressCitiesStruct(AddressCities addressCities){
            super(addressCities);
        }


        public String getCustomTag() {
            return customTag;
        }

        public void setCustomTag(String customTag) {
            this.customTag = customTag;
        }
    }
    
    public static Bundle createBundle(String url, int region, String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url.split("\\?")[0]); // TODO REMOVE
        bundle.putString(GetCitiesHelper.REGION_ID_TAG, String.valueOf(region));
        bundle.putString(GetCitiesHelper.CUSTOM_TAG, tag);
        return bundle;
    }

}
