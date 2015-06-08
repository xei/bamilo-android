/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.orders.OrderSummary;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.helpers.checkout.GetBillingFormHelper;
import com.mobile.helpers.checkout.SetBillingAddressHelper;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * Class used to show the my addresses and set the on process checkout the billing and shipping address. 
 * @author sergiopereira
 */
public class CheckoutMyAddressesFragment extends MyAddressesFragment {

    private static final String TAG = LogTagHelper.create(CheckoutMyAddressesFragment.class);

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
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
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
        setRetainInstance(true);
        // Get arguments
        Bundle params = new Bundle();        
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_ADDRESSES);
        // Tracking checkout step
        TrackerDelegator.trackCheckoutStep(params);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        ((Button)view.findViewById(R.id.checkout_addresses_button_enter)).setText(getResources().getString(R.string.next_button));
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
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_BILLING_FORM_EVENT");
    }

    /**
     * Show form.
     * @param bundle
     */
    protected void onGetBillingFormEventSuccessEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED GET_BILLING_FORM_EVENT");
        hiddenForm = bundle.getParcelable(Constants.BUNDLE_FORM_DATA_KEY);
        Addresses addresses = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        this.addresses = addresses;
        // Validate response
        if(!isValidateResponse()){
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_BILLING_FORM_EVENT");
            return;
        }
        // Show addresses using saved value, if is the same address for Bill and Ship
        if(!TextUtils.isEmpty(sameAddress)){
            showAddresses(Boolean.parseBoolean(sameAddress));
        } else {
            showAddresses(addresses.hasDefaultShippingAndBillingAddress());
        }
        // Get order summary
        OrderSummary orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);

    }

    protected void onSetBillingAddressErrorEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
        showFragmentContentContainer();
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            @SuppressWarnings("unchecked")
            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            showErrorDialog(errors, R.string.add_address);
        }else{
            Print.w(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT: " + errorCode.name());
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED SET_BILLING_ADDRESS_EVENT: ");
        }
    }

    protected void onSetBillingAddressSuccessEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
        // Get next step
        FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
        nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.SHIPPING_METHODS;
        getBaseActivity().onSwitchFragment(nextFragment, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * ############# REQUESTS #############
     */

    @Override
    protected void triggerGetForm() {
        sameAddress = "";
        triggerGetBillingForm();
    }

    /**
     * Trigger to set the billing form
     * @param contentValues
     */
    private void triggerSetBilling(ContentValues contentValues) {
        Print.d(TAG, "TRIGGER SET BILLING");
        // Submit values
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, contentValues);
        triggerContentEvent(new SetBillingAddressHelper(), bundle, this);
    }

    /**
     * Trigger to get the billing form
     */
    private void triggerGetBillingForm(){
        Print.i(TAG, "TRIGGER: LOGIN FORM");
        triggerContentEvent(new GetBillingFormHelper(), null, this);
    }

    /**
     * ############# RESPONSE #############
     */

    /**
     * Parse success response
     * @param bundle
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
            case GET_BILLING_FORM_EVENT:
                onGetBillingFormEventSuccessEvent(bundle);
                break;
            case SET_BILLING_ADDRESS_EVENT:
                onSetBillingAddressSuccessEvent(bundle);
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Parse error response
     * @param bundle
     * @return boolean
     */
    protected boolean onErrorEvent(Bundle bundle) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
            case GET_BILLING_FORM_EVENT:
                onGetBillingFormEventErrorEvent();
                break;
            case SET_BILLING_ADDRESS_EVENT:
                onSetBillingAddressErrorEvent(bundle);
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
        if(null != JumiaApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
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
            bundle.putParcelable(EditAddressFragment.SELECTED_ADDRESS, selectedAddress);
            getBaseActivity().onSwitchFragment(FragmentType.EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Print.i(TAG, "SELECTED ADDRESS ID: " + addressId + " NO MATCH");
        }
    }

    @Override
    protected void submitForm(String shippingAddressId, String billingAddressId, String isDifferent) {
        Print.d(TAG, "SUBMIT ADDRESSES SHIP: " + shippingAddressId + " BIL: " + billingAddressId + " SAME: " + isDifferent);
        // Create content values from form
        ContentValues contentValues = new ContentValues();
        for (FormField field : hiddenForm.fields) {
            if (field.getKey().contains(BILLING_ID_TAG)) contentValues.put(field.getName(), billingAddressId);
            else if (field.getKey().contains(SHIPPING_ID_TAG)) contentValues.put(field.getName(), shippingAddressId);
            else if (field.getKey().contains(IS_SAME_TAG)) contentValues.put(field.getName(), isDifferent);
        }
        // Trigger
        triggerSetBilling(contentValues);
    }
}
