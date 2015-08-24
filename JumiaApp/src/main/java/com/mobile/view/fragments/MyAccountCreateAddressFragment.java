package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.Constants;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;

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
public class MyAccountCreateAddressFragment extends CreateAddressFragment {

    private static final String TAG = MyAccountCreateAddressFragment.class.getSimpleName();

    protected boolean isFirstUserAddress;

    /**
     * Fragment used to create an address
     * @return MyAccountCreateAddressFragment
     *
     */
    public static MyAccountCreateAddressFragment newInstance(Bundle bundle) {
        MyAccountCreateAddressFragment myAccountCreateAddressFragment = new MyAccountCreateAddressFragment();
        myAccountCreateAddressFragment.setArguments(bundle);
        return myAccountCreateAddressFragment;
    }

    /**
     * Empty constructor
     *
     */
    public MyAccountCreateAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.string.my_addresses,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if(bundle != null) {
            isFirstUserAddress = bundle.getBoolean(TrackerDelegator.LOGIN_KEY, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View orderSummaryLayout = view.findViewById(super.ORDER_SUMMARY_CONTAINER);
        if(orderSummaryLayout != null){
            orderSummaryLayout.setVisibility(View.GONE);
        }

        ((Button)view.findViewById(R.id.checkout_button_enter)).setText(getResources().getString(R.string.save_label));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
            triggerInitForm();
        } else if(mFormResponse != null && regions != null){
            loadCreateAddressForm(mFormResponse);
        } else {
            triggerCreateAddressForm();
        }
    }

    @Override
    protected void onClickRetryButton() {
        //TODO retry error when this method has access to eventType
        triggerInitForm();
    }

    @Override
    protected void onCreateAddressSuccessEvent(Bundle bundle) {
        super.onCreateAddressSuccessEvent(bundle);
        AnalyticsGoogle.get().trackAddressCreation(TrackingEvent.ACCOUNT_CREATE_ADDRESS,
                (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString()+"":""); //replaced getID because doesn't come from api

        if(!mIsSameCheckBox.isChecked() && !oneAddressCreated){
            oneAddressCreated = true;

            if (null != billingFormGenerator) {
                ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                triggerCreateAddress(mBillValues, true);
            }
        } else {
            if(isFirstUserAddress){
                FragmentController.getInstance().popLastEntry(FragmentType.MY_ACCOUNT_CREATE_ADDRESS.toString());
                getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            } else {
                getBaseActivity().onBackPressed();
            }
        }
    }

    @Override
    protected void onGetCreateAddressFormErrorEvent(Bundle bundle) {
        super.onGetCreateAddressFormErrorEvent(bundle);
        onErrorOccurred();
    }

    @Override
    protected void onGetRegionsErrorEvent(Bundle bundle) {
        super.onGetRegionsErrorEvent(bundle);
        onErrorOccurred();
    }

    @Override
    protected void onGetCitiesErrorEvent(Bundle bundle) {
        super.onGetCitiesErrorEvent(bundle);
        onErrorOccurred();

    }

    @Override
    protected void onCreateAddressErrorEvent(Bundle bundle) {
        super.onCreateAddressErrorEvent(bundle);

        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (errorCode == ErrorCode.REQUEST_ERROR) {
//            @SuppressWarnings("unchecked")
//            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
//
//            showErrorDialog(errors, getString(R.string.address_creation_failed));
              showErrorDialog(getString(R.string.address_creation_failed_main), getString(R.string.address_creation_failed_title));
            showFragmentContentContainer();
        } else {
            Log.w(TAG, "RECEIVED CREATE_ADDRESS_EVENT: " + errorCode.name());
            getBaseActivity().onBackPressed();
        }
    }

    private void onErrorOccurred(){
        Toast.makeText(getBaseActivity(),getResources().getString(R.string.error_please_try_again),Toast.LENGTH_SHORT).show();
        getBaseActivity().onBackPressed();
    }

}
