package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.app.JumiaApplication;
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

import de.akquinet.android.androlog.Log;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/25
 */
public class MyAccountEditAddressFragment extends EditAddressFragment {

    private static final String TAG = LogTagHelper.create(MyAccountEditAddressFragment.class);

    /**
     * Get instance
     * @return MyAddressesFragment
     */
    public static MyAccountEditAddressFragment newInstance(Bundle bundle) {
        MyAccountEditAddressFragment myAccountEditAddressFragment = new MyAccountEditAddressFragment();
        myAccountEditAddressFragment.mCurrentAddress = bundle.getParcelable(EditAddressFragment.SELECTED_ADDRESS);
        return myAccountEditAddressFragment;
    }

    public MyAccountEditAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.string.edit_address,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View orderSummaryLayout = view.findViewById(super.ORDER_SUMMARY_CONTAINER);
        if(orderSummaryLayout != null){
            orderSummaryLayout.setVisibility(View.GONE);
        }

        //Validate is service is available
        if(JumiaApplication.mIsBound){
            // Get and show form
            if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
                triggerInitForm();
            } else if(mFormResponse != null && mRegions != null){
                loadEditAddressForm(mFormResponse);
            } else {
                triggerEditAddressForm();
            }
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    protected void onClickRetryButton() {
        //TODO retry error when this method has access to eventType
        triggerInitForm();
    }

    protected void onGetEditAddressFormErrorEvent(Bundle bundle){
        super.onGetEditAddressFormErrorEvent(bundle);
        onErrorOccurred();
    }

    protected void onGetRegionsErrorEvent(Bundle bundle){
        super.onGetRegionsErrorEvent(bundle);
        onErrorOccurred();
    }

    protected void onGetCitiesErrorEvent(Bundle bundle){
        super.onGetCitiesErrorEvent(bundle);
        onErrorOccurred();
    }

    protected void onEditAddressErrorEvent(Bundle bundle){
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            @SuppressWarnings("unchecked")
            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            showErrorDialog(errors);
            showFragmentContentContainer();
        } else {
            Log.w(TAG, "RECEIVED GET_CITIES_EVENT: " + errorCode.name());
            onErrorOccurred();
        }
    }

    private void onErrorOccurred(){
        Toast.makeText(getBaseActivity(), getResources().getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
        getBaseActivity().onBackPressed();
    }
}
