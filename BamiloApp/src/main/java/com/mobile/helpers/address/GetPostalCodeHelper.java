package com.mobile.helpers.address;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.addresses.AddressPostalCodes;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

import java.util.Map;

/**
 * Helper used to get the postal codes for a specific city
 */
public class GetPostalCodeHelper extends SuperBaseHelper {
    
    public static String TAG = GetPostalCodeHelper.class.getSimpleName();

    private String customTag;

    @Override
    public EventType getEventType() {
        return EventType.GET_POSTAL_CODE_EVENT;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        customTag = args.getString(TAG);
        args.remove(TAG);
        return super.getRequestData(args);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getPostalCodes);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        AddressPostalCodes postalCodes = (AddressPostalCodes) baseResponse.getContentData();
        AddressPostalCodesStruct addressPostalCodes = new AddressPostalCodesStruct(postalCodes);
        addressPostalCodes.setCustomTag(customTag);
        baseResponse.getMetadata().setData(addressPostalCodes);
    }

    public static Bundle createBundle(String endpoint, int city, String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + String.format(TargetLink.getIdFromTargetLink(endpoint), city));
        bundle.putString(TAG, tag);
        return bundle;
    }

    public class AddressPostalCodesStruct extends AddressPostalCodes{
        private String customTag;
        public AddressPostalCodesStruct(AddressPostalCodes addressPostalCodes){
            super(addressPostalCodes);
        }

        public String getCustomTag() {
            return customTag;
        }

        void setCustomTag(String customTag) {
            this.customTag = customTag;
        }
    }

}
