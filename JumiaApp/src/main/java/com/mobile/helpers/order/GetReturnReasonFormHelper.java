package com.mobile.helpers.order;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.ReturnReasonForm;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

import org.json.JSONException;

import java.util.Map;

/**
 * Helper to get the return reason form
 * @author spereira
 */
public class GetReturnReasonFormHelper extends SuperBaseHelper {

    public static String TAG = GetReturnReasonFormHelper.class.getSimpleName();

    private int number;

    @Override
    public EventType getEventType() {
        return EventType.GET_RETURN_REASON_FORM;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getReturnReasonForm);
    }

    @Nullable
    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        // Get number
        number = args.getInt(TAG);
        // Return
        return super.getRequestData(args);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        // Generate forms
        try {
            ((ReturnReasonForm) baseResponse.getContentData()).generateForms(number);
        } catch (JSONException e) {
            postError(baseResponse);
        }
    }

    public static Bundle createBundle(@IntRange int number) {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG, number);
        return bundle;
    }

}