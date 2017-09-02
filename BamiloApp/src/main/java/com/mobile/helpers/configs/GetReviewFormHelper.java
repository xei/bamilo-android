package com.mobile.helpers.configs;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

/**
 * Get Reviews dynamic form helper.
 */
public class GetReviewFormHelper extends SuperBaseHelper {

    protected static String TAG = GetReviewFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_FORM_REVIEW_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getReviewForm);
    }

}
