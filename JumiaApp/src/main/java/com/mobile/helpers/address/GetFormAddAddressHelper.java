package com.mobile.helpers.address;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the form to create an address
 * @author sergiopereira
 *
 */
public class GetFormAddAddressHelper extends SuperBaseHelper {

    public static String TAG = GetFormAddAddressHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_CREATE_ADDRESS_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCreateAddressForm);
    }

}

