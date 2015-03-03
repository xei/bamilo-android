package com.mobile.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

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

    private static final String TAG = LogTagHelper.create(MyAccountCreateAddressFragment.class);

    /**
     * Fragment used to create an address
     * @return CheckoutCreateAddressFragment
     * @author sergiopereira
     */
    public static MyAccountCreateAddressFragment newInstance() {
        return new MyAccountCreateAddressFragment();
    }

    /**
     * Empty constructor
     * @author sergiopereira
     */
    public MyAccountCreateAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.string.my_addresses,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Validate is service is available
        if(JumiaApplication.mIsBound){
            // Get and show form
            if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
                triggerInitForm();
            } else if(mFormResponse != null && regions != null){
                loadCreateAddressForm(mFormResponse);
            } else {
                triggerCreateAddressForm();
            }
        } else {
            showFragmentErrorRetry();
        }

        View orderSummaryLayout = view.findViewById(super.ORDER_SUMMARY_CONTAINER);
        if(orderSummaryLayout != null){
            orderSummaryLayout.setVisibility(View.GONE);
        }

        ((Button)view.findViewById(R.id.checkout_address_button_enter)).setText(getResources().getString(R.string.save_label));
    }

    @Override
    protected void onClickRetryButton() {
        //TODO retry error when this method has access to eventType
        triggerInitForm();
    }

    @Override
    protected void onCreateAddressSuccessEvent(Bundle bundle) {
        super.onCreateAddressSuccessEvent(bundle);
        getBaseActivity().onBackPressed();
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
            @SuppressWarnings("unchecked")
            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            showErrorDialog(errors);
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
