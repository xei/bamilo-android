package com.mobile.helpers.address;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.addresses.AddressCities;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

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
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + String.format(TargetLink.getIdFromTargetLink(url), region));
        bundle.putString(TAG, tag);
        return bundle;
    }

}
