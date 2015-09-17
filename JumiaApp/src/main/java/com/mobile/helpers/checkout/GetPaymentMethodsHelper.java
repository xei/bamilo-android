package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutFormPayment;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the payment methods
 */
public class GetPaymentMethodsHelper extends SuperBaseHelper {

    public static String TAG = GetPaymentMethodsHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_PAYMENT_METHODS_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getPaymentMethodsForm);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);

        // Create bundle
        CheckoutFormPayment responseData = (CheckoutFormPayment) baseResponse.getMetadata().getData();
        bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, responseData.getOrderSummary());
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, responseData.getForm());

        //TODO move to observable
        JumiaApplication.INSTANCE.setPaymentMethodForm(null);
        JumiaApplication.setPaymentsInfoList(responseData.getForm().getFieldKeyMap().get("payment_method").getPaymentInfoList());
    }

}
