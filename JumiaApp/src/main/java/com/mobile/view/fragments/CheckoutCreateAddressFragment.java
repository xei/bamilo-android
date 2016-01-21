package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 *
 * @author sergiopereira
 *
 */
public class CheckoutCreateAddressFragment extends CreateAddressFragment{

    private static final String TAG = CheckoutCreateAddressFragment.class.getSimpleName();

    private View mCheckoutTotalBar;

    /**
     * Fragment used to create an address
     * @return CheckoutCreateAddressFragment
     * @author sergiopereira
     */
    public static CheckoutCreateAddressFragment getInstance() {
        return new CheckoutCreateAddressFragment();
    }

    /**
     * Empty constructor
     * @author sergiopereira
     */
    public CheckoutCreateAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.string.checkout_label,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_CREATE_ADDRESS);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCheckoutTotalBar = view.findViewById(R.id.checkout_total_bar);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get and show form
        if(mFormShipping != null &&  mFormBilling!= null && orderSummary != null && regions != null){
            loadCreateAddressForm(mFormShipping, mFormBilling);
        } else {
            triggerCreateAddressForm();
        }
    }

    protected void loadCreateAddressForm(Form formShipping,Form formBilling) {
        super.loadCreateAddressForm(formShipping,formBilling);
        // Show order summary
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
        // Set the checkout total bar
        CheckoutStepManager.setTotalBar(mCheckoutTotalBar, orderSummary);
    }

    @Override
    protected void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if(null != JumiaApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    @Override
    protected void onCreateAddressSuccessEvent(BaseResponse baseResponse) {
        super.onCreateAddressSuccessEvent(baseResponse);
        //GTM
        TrackerDelegator.trackAddAddress(true);
        // Waiting for both responses
        if(!mIsSameCheckBox.isChecked() && !oneAddressCreated){
            oneAddressCreated = true;

            if (null != billingFormGenerator) {
                ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                triggerCreateAddress(billingFormGenerator.getForm().getAction(), mBillValues);
            }
            return ;
        }

        // Get next step
        FragmentType nextFragment = ((NextStepStruct)baseResponse.getMetadata().getData()).getFragmentType();
        if(nextFragment == null || nextFragment == FragmentType.UNKNOWN){
            Print.w(TAG, "NEXT STEP IS UNKNOWN OR NULL -> FALL BACK MY_ADDRESSES");
            nextFragment = FragmentType.CHECKOUT_MY_ADDRESSES;
        }

        FragmentController.getInstance().popLastEntry(FragmentType.CHECKOUT_CREATE_ADDRESS.toString());
        getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        showWarningSuccessMessage(baseResponse.getSuccessMessage(), baseResponse.getEventType());
    }

    @Override
    protected void onGetCreateAddressFormErrorEvent(BaseResponse baseResponse) {
        super.onGetCreateAddressFormErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    @Override
    protected void onGetRegionsErrorEvent(BaseResponse baseResponse) {
        super.onGetRegionsErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    @Override
    protected void onGetCitiesErrorEvent(BaseResponse baseResponse) {
        super.onGetCitiesErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    @Override
    protected void onCreateAddressErrorEvent(BaseResponse baseResponse) {
        super.onCreateAddressErrorEvent(baseResponse);
        //GTM
        TrackerDelegator.trackAddAddress(false);
        // Error
        int errorCode = baseResponse.getError().getCode();
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            // Case is same form for both or is the first
            if(mIsSameCheckBox != null && (mIsSameCheckBox.isChecked() || !oneAddressCreated)) {
                showFormValidateMessages(shippingFormGenerator, baseResponse, EventType.CREATE_ADDRESS_EVENT);
            }
            // Case is not the same and is the second
            else {
                showFormValidateMessages(billingFormGenerator, baseResponse, EventType.CREATE_ADDRESS_EVENT);
            }
        } else {
            Print.w(TAG, "RECEIVED CREATE_ADDRESS_EVENT: " + errorCode);
            super.showUnexpectedErrorWarning();
        }
        showFragmentContentContainer();
    }
}