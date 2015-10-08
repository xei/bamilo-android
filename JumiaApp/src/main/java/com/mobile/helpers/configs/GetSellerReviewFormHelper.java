/**
 * 
 */
package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Seller reviews dynamic form helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetSellerReviewFormHelper extends SuperBaseHelper {

    protected static String TAG = GetSellerReviewFormHelper.class.getSimpleName();

    public static final String PRODUCT_URL = "productUrl";

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

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        Form form = (Form) baseResponse.getMetadata().getData();
        //form.sortForm(mEventType);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);
    }

}
