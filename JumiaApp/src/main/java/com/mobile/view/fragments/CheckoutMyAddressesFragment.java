package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.checkout.GetStepAddressesHelper;
import com.mobile.helpers.checkout.SetStepAddressesHelper;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.MultiStepAddresses;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * Class used to show the my addresses and set the on process checkout the billing and shipping address. 
 * @author sergiopereira
 */
public class CheckoutMyAddressesFragment extends MyAddressesFragment {

    private static final String TAG = CheckoutMyAddressesFragment.class.getSimpleName();

    private View mCheckoutTotalBar;

    /**
     * Get instance
     * @return CheckoutMyAddressesFragment
     */
    public static CheckoutMyAddressesFragment getInstance() {
        return new CheckoutMyAddressesFragment();
    }

    /**
     * Empty constructor
     */
    public CheckoutMyAddressesFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.string.checkout_label,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Tracking checkout step
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_ADDRESSES);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        mCheckoutTotalBar = view.findViewById(R.id.checkout_total_bar);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        TrackerDelegator.trackPage(TrackingPage.ADDRESS_SCREEN, getLoadTime(), true);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     * Goto web checkout case received a form error event.
     */
    protected void onGetBillingFormEventErrorEvent() {
        Print.w(TAG, "RECEIVED GET_BILLING_FORM_EVENT");
        super.showFragmentErrorRetry();
    }

    /**
     * Show form.
     */
    protected void onGetBillingFormEventSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_BILLING_FORM_EVENT");
        MultiStepAddresses billingForm = (MultiStepAddresses)baseResponse.getContentData();

        Addresses addresses = billingForm.getAddresses();
        this.addresses = addresses;
        // Validate response
        if(!isValidateResponse()){
            super.showFragmentErrorRetry();
            return;
        }
        // Show addresses using saved value, if is the same address for Bill and Ship
        if(!TextUtils.isEmpty(sameAddress)){
            showAddresses(Boolean.parseBoolean(sameAddress));
        } else {
            showAddresses(addresses.hasDefaultShippingAndBillingAddress());
        }
        // Get order summary
        PurchaseEntity orderSummary = billingForm.getOrderSummary();
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
        // Set the checkout total bar
        CheckoutStepManager.setTotalBar(mCheckoutTotalBar, orderSummary);
    }

    protected void onSetBillingAddressErrorEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
        int errorCode = baseResponse.getError().getCode();
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            showErrorDialog(baseResponse.getValidateMessage(), R.string.add_address);
            setDefaultChecked(Boolean.parseBoolean(sameAddress));
        } else{
            Print.w(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT: " + errorCode);
            super.showUnexpectedErrorWarning();
        }
        showFragmentContentContainer();
    }

    protected void onSetBillingAddressSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
        // Get next step
        NextStepStruct nextStepStruct = (NextStepStruct)baseResponse.getContentData();
        FragmentType nextFragment = nextStepStruct.getFragmentType();
        nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.SHIPPING_METHODS;
        getBaseActivity().onSwitchFragment(nextFragment, null, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * ############# REQUESTS #############
     */

    @Override
    protected void triggerGetForm() {
        sameAddress = "";
        triggerGetMultiStepAddresses();
    }

    /**
     * Trigger to set the billing form
     */
    private void triggerSetMultiStepAddresses(String billing, String shipping) {
        Print.d(TAG, "TRIGGER SET BILLING");
        triggerContentEvent(new SetStepAddressesHelper(), SetStepAddressesHelper.createBundle(billing, shipping), this);
    }

    /**
     * Trigger to get the billing form
     */
    private void triggerGetMultiStepAddresses() {
        Print.i(TAG, "TRIGGER: LOGIN FORM");
        triggerContentEvent(new GetStepAddressesHelper(), null, this);
    }

    /**
     * ############# RESPONSE #############
     */

    /**
     * Parse success response
     */
    protected boolean onSuccessEvent(BaseResponse baseResponse) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
            case GET_MULTI_STEP_ADDRESSES:
                onGetBillingFormEventSuccessEvent(baseResponse);
                break;
            case SET_MULTI_STEP_ADDRESSES:
                onSetBillingAddressSuccessEvent(baseResponse);
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Parse error response
     */
    protected boolean onErrorEvent(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }

        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);

        switch (eventType) {
            case GET_MULTI_STEP_ADDRESSES:
                onGetBillingFormEventErrorEvent();
                break;
            case SET_MULTI_STEP_ADDRESSES:
                onSetBillingAddressErrorEvent(baseResponse);
                break;
            default:
                break;
        }

        return false;
    }

    /*
     * ############# LISTENERS #############
     */

    /**
     * Process the retry button
     */
    @Override
    protected void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if (null == JumiaApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            // Get and show addresses
            triggerGetForm();
        }
    }

    /**
     * Process the click on add button.
     * @author sergiopereira
     */
    protected void onClickCreateAddressButton() {
        Print.i(TAG, "ON CLICK: LOGIN");
        getBaseActivity().onSwitchFragment(FragmentType.CREATE_ADDRESS, null, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    protected void onClickEditAddressButton(View view) {
        // Get tag that contains the address id
        int addressId = Integer.parseInt(view.getTag().toString());
        Print.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
        // Selected address
        Address selectedAddress = null;
        // Get addresses
        Address shippingAddress = addresses.getShippingAddress();
        Address billingAddress = addresses.getBillingAddress();
        HashMap<String, Address> otherAddresses = addresses.getAddresses();
        // Validate
        if (shippingAddress != null && shippingAddress.getId() == addressId) selectedAddress = shippingAddress;
        else if (billingAddress != null && billingAddress.getId() == addressId) selectedAddress = billingAddress;
        else if(otherAddresses != null && otherAddresses.containsKey(""+addressId)) selectedAddress = otherAddresses.get(""+addressId);
        // Validate selected address
        if(selectedAddress != null){
            Bundle bundle = new Bundle();
            bundle.putInt(EditAddressFragment.SELECTED_ADDRESS, selectedAddress.getId());
            getBaseActivity().onSwitchFragment(FragmentType.EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Print.i(TAG, "SELECTED ADDRESS ID: " + addressId + " NO MATCH");
        }
    }

    @Override
    protected void submitForm(String shippingAddressId, String billingAddressId, String isDifferent) {
        Print.d(TAG, "SUBMIT ADDRESSES SHIP: " + shippingAddressId + " BIL: " + billingAddressId + " SAME: " + isDifferent);
        triggerSetMultiStepAddresses(billingAddressId, shippingAddressId);
    }
}
