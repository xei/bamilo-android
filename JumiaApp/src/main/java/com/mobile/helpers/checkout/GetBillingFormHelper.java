package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.CheckoutFormBilling;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;


/**
 * Helper used to get all customer addresses 
 * @author sergiopereira
 *
 */
public class GetBillingFormHelper extends SuperBaseHelper {
    
    public static String TAG = GetBillingFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_BILLING_FORM_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getBillingAddressForm);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        CheckoutFormBilling billingForm = (CheckoutFormBilling)baseResponse.getMetadata().getData();
        // Create form
        Form form = billingForm.getForm();
        // Create addresses
        Addresses addresses = billingForm.getAddresses();
        // Get order summary
        PurchaseEntity orderSummary = billingForm.getOrderSummary();
        // Create bundle
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, addresses);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, form);
        bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, orderSummary);
    }

}