package com.mobile.helpers.address;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

/**
 * Helper used to get the form to create an address
 * @author sergiopereira
 *
 */
public class GetFormAddAddressHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_CREATE_ADDRESS_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCreateAddressForm);
    }

}

