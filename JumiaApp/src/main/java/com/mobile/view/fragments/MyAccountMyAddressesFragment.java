package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Address;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.address.GetMyAddressesHelper;
import com.mobile.helpers.address.SetDefaultBillingAddress;
import com.mobile.helpers.address.SetDefaultShippingAddress;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;

import de.akquinet.android.androlog.Log;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/24
 */
public class MyAccountMyAddressesFragment extends MyAddressesFragment{

    private static final String TAG = LogTagHelper.create(MyAccountMyAddressesFragment.class);

    private boolean isSetShippingComplete;

    private boolean isSetBillingComplete;

    /**
     * Get instance
     * @return MyAddressesFragment
     */
    public static MyAccountMyAddressesFragment newInstance() {
        return new MyAccountMyAddressesFragment();
    }

    public MyAccountMyAddressesFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.string.my_addresses,
                KeyboardState.ADJUST_CONTENT);

        resetRequests();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "ON VIEW CREATED");
        super.onViewCreated(view, savedInstanceState);
        ((Button)view.findViewById(R.id.checkout_addresses_button_enter)).setText(getResources().getString(R.string.save_label));

        View orderSummaryLayout = view.findViewById(super.ORDER_SUMMARY_CONTAINER);
        if(orderSummaryLayout != null){
            orderSummaryLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onClickRetryButton() {
        triggerGetForm();
    }

    @Override
    protected void onClickCreateAddressButton() {
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    protected void onClickEditAddressButton(View view) {
        // Get tag that contains the address id
        int addressId = Integer.parseInt(view.getTag().toString());
        Log.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
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
            getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Log.i(TAG, "SELECTED ADDRESS ID: " + addressId + " NO MATCH");
        }
    }

    /**
     * Submit the current values using the form
     *
     * @param shippingAddressId
     * @param billingAddressId
     * @param isDifferent
     * @author ricardosoares
     */
    protected void submitForm(String shippingAddressId, String billingAddressId, String isDifferent){
        Log.d(TAG, "SUBMIT ADDRESSES SHIP: " + shippingAddressId + " BIL: " + billingAddressId + " SAME: " + isDifferent);


        //Verification to check if submitted shipping address isn't already the default one
        if(!shippingAddressId.equals(addresses.getShippingAddress().getId() + "")) {
            // Create content values from form
            ContentValues shippingContentValues = new ContentValues();
            shippingContentValues.put(SetDefaultShippingAddress.ID, shippingAddressId);
            triggerSetDefaultShippingAddress(shippingContentValues);
        } else {
            isSetShippingComplete = true;
        }

        //Verification to check if submitted billing address isn't already the default one
        if(!billingAddressId.equals(addresses.getBillingAddress().getId() + "")) {
            // Create content values from form
            ContentValues billingContentValues = new ContentValues();
            billingContentValues.put(SetDefaultBillingAddress.ID, billingAddressId);
            triggerSetDefaultBillingAddress(billingContentValues);
        } else {
            isSetBillingComplete = true;
        }

        // In case of all addresses already are the default ones
        if(isSetBillingComplete && isSetShippingComplete){
            Toast.makeText(getActivity(), R.string.addresses_saved_message,Toast.LENGTH_SHORT).show();
            resetRequests();
        }
    }

    /**
     * ############# REQUESTS #############
     */


    @Override
    protected void triggerGetForm() {
        resetRequests();
        cleanContainers();
        sameAddress = null;
        triggerGetMyAddresses();
    }

    protected void triggerGetMyAddresses(){
        if(JumiaApplication.mIsBound) {
            triggerContentEvent(new GetMyAddressesHelper(), null, this);
        } else {
            showFragmentErrorRetry();
        }
    }

    private void triggerSetDefaultShippingAddress(ContentValues contentValues) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetDefaultShippingAddress.FORM_CONTENT_VALUES, contentValues);
        triggerContentEvent(new SetDefaultShippingAddress(), bundle, this);
    }

    private void triggerSetDefaultBillingAddress(ContentValues contentValues){
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetDefaultBillingAddress.FORM_CONTENT_VALUES, contentValues);
        triggerContentEvent(new SetDefaultBillingAddress(), bundle, this);
    }

    /**
     * ############# RESPONSE #############
     */

    @Override
    protected boolean onErrorEvent(Bundle bundle) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch(eventType){
            case GET_CUSTOMER_ADDRESSES_EVENT:
                showContinueShopping();
                break;
            case SET_DEFAULT_SHIPPING_ADDRESS:
                showFragmentContentContainer();
                Toast.makeText(getBaseActivity(),getResources().getString(R.string.error_please_try_again),Toast.LENGTH_LONG).show();
                resetRequests();
                break;
            case SET_DEFAULT_BILLING_ADDRESS:
                showFragmentContentContainer();
                Toast.makeText(getBaseActivity(),getResources().getString(R.string.error_please_try_again),Toast.LENGTH_LONG).show();
                resetRequests();
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    protected boolean onSuccessEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);


        switch(eventType){
            case GET_CUSTOMER_ADDRESSES_EVENT:
                hiddenForm = bundle.getParcelable(Constants.BUNDLE_FORM_DATA_KEY);
                this.addresses = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);

                if(this.addresses != null){
                    // Show addresses using saved value, if is the same address for Bill and Ship
                    if (!TextUtils.isEmpty(sameAddress)) {
                        showAddresses(Boolean.parseBoolean(sameAddress));
                    } else {
                        showAddresses(addresses.hasDefaultShippingAndBillingAddress());
                    }
                } else {
                    showContinueShopping();
                }

                break;
            case SET_DEFAULT_SHIPPING_ADDRESS:
                isSetShippingComplete = true;
                checkSettingRequestComplete();
                break;
            case SET_DEFAULT_BILLING_ADDRESS:
                isSetBillingComplete = true;
                checkSettingRequestComplete();
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Check if requests are complete. Resets configs in case of success.
     *
     */
    protected void checkSettingRequestComplete(){
        if(isSetBillingComplete && isSetShippingComplete){
            Toast.makeText(getActivity(), R.string.addresses_saved_message,Toast.LENGTH_SHORT).show();
            onClickRetryButton();
        }
    }

    /**
    *   Reset requests configs.
    */
    private void resetRequests(){
        isSetBillingComplete = false;
        isSetShippingComplete = false;
    }
}