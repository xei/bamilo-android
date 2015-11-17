/**
 * 
 */
package com.mobile.helpers.checkout;

import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.SetBillingAddress;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

/**
 * Helper used to set the shipping address 
 * @author sergiopereira
 */
public class SetBillingAddressHelper extends SuperBaseHelper {
    
    private static String TAG = SetBillingAddressHelper.class.getSimpleName();
    
    public static final String FORM_CONTENT_VALUES = "content_values";

    @Override
    public EventType getEventType() {
        return EventType.SET_BILLING_ADDRESS_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new SetBillingAddress(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setBillingAddress);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        //TODO move to observable
        SetBillingAddress billing = (SetBillingAddress) baseResponse.getMetadata().getData();
//        bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, billing.getOrderSummary());
        Print.i(TAG, "ORDER SUMMARY: " + billing.getOrderSummary().toString());
        // Get and set next step
//        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextFragment(billing.getNextStep()));

        NextStepStruct nextStepStruct = new NextStepStruct(billing);
        baseResponse.getMetadata().setData(nextStepStruct);
    }

}
