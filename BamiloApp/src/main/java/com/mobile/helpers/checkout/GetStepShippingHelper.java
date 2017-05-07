package com.mobile.helpers.checkout;

import com.mobile.forms.ShippingMethodFormBuilder;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.MultiStepShipping;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the shipping methods
 *
 * @author sergiopereira
 */
public class GetStepShippingHelper extends SuperBaseHelper {

    public static String TAG = GetStepShippingHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_MULTI_STEP_SHIPPING;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getMultiStepShipping);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        MultiStepShipping step = (MultiStepShipping) baseResponse.getContentData();
        ShippingMethodFormStruct shippingMethodFormStruct = new ShippingMethodFormStruct(step);
        baseResponse.getMetadata().setData(shippingMethodFormStruct);
    }


    public class ShippingMethodFormStruct extends MultiStepShipping {

        private ShippingMethodFormBuilder formBuilder;

        public ShippingMethodFormStruct() {
            super();
        }

        ShippingMethodFormStruct(MultiStepShipping shippingMethodsForm) {
            super(shippingMethodsForm);
            formBuilder = new ShippingMethodFormBuilder();
            formBuilder.action = getForm().action;
            formBuilder.shippingMethodFormBuilderHolder = shippingMethodsForm.getForm();
        }

        public ShippingMethodFormBuilder getFormBuilder() {
            return formBuilder;
        }
    }

}
