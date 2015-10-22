package com.mobile.helpers.checkout;

import com.mobile.forms.ShippingMethodFormBuilder;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutFormShipping;
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
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        CheckoutFormShipping shippingMethodsForm = (CheckoutFormShipping) baseResponse.getMetadata().getData();

        ShippingMethodFormStruct shippingMethodFormStruct = new ShippingMethodFormStruct(shippingMethodsForm);

        baseResponse.getMetadata().setData(shippingMethodFormStruct);
    }

    public class ShippingMethodFormStruct extends CheckoutFormShipping {
        private ShippingMethodFormBuilder formBuilder;

        public ShippingMethodFormStruct(){}

        ShippingMethodFormStruct(CheckoutFormShipping shippingMethodsForm){
            super(shippingMethodsForm);
            formBuilder = new ShippingMethodFormBuilder();
            formBuilder.shippingMethodFormBuilderHolder = shippingMethodsForm.getForm();
        }


        public ShippingMethodFormBuilder getFormBuilder() {
            return formBuilder;
        }

        public void setFormBuilder(ShippingMethodFormBuilder formBuilder) {
            this.formBuilder = formBuilder;
        }
    }

}
