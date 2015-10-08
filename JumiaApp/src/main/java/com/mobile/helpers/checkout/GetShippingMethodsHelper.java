package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.forms.ShippingMethodFormBuilder;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutFormShipping;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the shipping methods
 *
 * @author sergiopereira
 */
public class GetShippingMethodsHelper extends SuperBaseHelper {

    public static String TAG = GetShippingMethodsHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_SHIPPING_METHODS_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getShippingMethodsForm);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        CheckoutFormShipping shippingMethodsForm = (CheckoutFormShipping) baseResponse.getMetadata().getData();
        ShippingMethodFormBuilder form = new ShippingMethodFormBuilder();
        form.shippingMethodFormBuilderHolder = shippingMethodsForm.getForm();
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);
        bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, shippingMethodsForm.getOrderSummary());
        bundle.putParcelableArrayList(Constants.BUNDLE_FORM_DATA_KEY, shippingMethodsForm.getFulfillmentList());
    }

}
