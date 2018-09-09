package com.bamilo.android.appmodule.bamiloapp.helpers.checkout;

import com.bamilo.android.appmodule.bamiloapp.forms.ShippingMethodFormBuilder;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.checkout.MultiStepShipping;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
