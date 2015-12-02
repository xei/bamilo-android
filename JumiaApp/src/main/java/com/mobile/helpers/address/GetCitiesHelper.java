package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.addresses.AddressCities;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

import java.util.Map;

/**
 * Helper used to get the address cities
 */
public class GetCitiesHelper extends SuperBaseHelper {
    
    public static String TAG = GetCitiesHelper.class.getSimpleName();

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
        customTag = args.getString(TAG);
        args.remove(TAG);
        return super.getRequestData(args);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCities);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        AddressCitiesStruct cities = new AddressCitiesStruct( (AddressCities)baseResponse.getMetadata().getData());
        cities.setCustomTag(customTag);
        baseResponse.getMetadata().setData(cities);
    }

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
        bundle.putString(Constants.BUNDLE_URL_KEY, String.format(url, region));
        bundle.putString(TAG, tag);
        return bundle;
    }

}
