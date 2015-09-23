/**
 * 
 */
package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Seller reviews dynamic form helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetSellerReviewFormHelper extends SuperBaseHelper {

    protected static String TAG = GetSellerReviewFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_FORM_SELLER_REVIEW_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return super.getRequestUrl(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getReviewForm);
    }

}
