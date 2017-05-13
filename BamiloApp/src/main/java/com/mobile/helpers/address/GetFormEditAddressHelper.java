package com.mobile.helpers.address;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;

/**
 * Helper used to get the form to edit an address 
 * @author sergiopereira
 */
public class GetFormEditAddressHelper extends SuperBaseHelper {
    
    public static String TAG = GetFormEditAddressHelper.class.getSimpleName();

    public static final String SELECTED_ADDRESS_ID = RestConstants.ID;

    @Override
    public EventType getEventType() {
        return EventType.GET_EDIT_ADDRESS_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getEditAddressForm);
    }

    public static Bundle createBundle(int id) {
        ContentValues values = new ContentValues();
        values.put(GetFormEditAddressHelper.SELECTED_ADDRESS_ID, id);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}

