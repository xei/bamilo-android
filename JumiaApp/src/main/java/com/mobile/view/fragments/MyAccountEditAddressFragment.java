package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;

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

    private static final String TAG = MyAccountEditAddressFragment.class.getSimpleName();

    /**
     * Get instance
     * @return MyAddressesFragment
     */
    public static MyAccountEditAddressFragment newInstance(Bundle bundle) {
        MyAccountEditAddressFragment fragment = new MyAccountEditAddressFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public MyAccountEditAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.string.edit_address,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View orderSummaryLayout = view.findViewById(super.ORDER_SUMMARY_CONTAINER);
        if(orderSummaryLayout != null){
            orderSummaryLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initializeFormData();
    }

    private void initializeFormData() {
        // Get and show form
        if(mFormResponse != null && mRegions != null){
            loadEditAddressForm(mFormResponse);
        } else {
            triggerEditAddressForm();
        }
    }

    @Override
    protected void onClickRetryButton() {
        initializeFormData();
    }

    protected void onGetEditAddressFormErrorEvent(BaseResponse baseResponse){
        super.onGetEditAddressFormErrorEvent(baseResponse);
        onErrorOccurred();
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse){
        super.onGetRegionsErrorEvent(baseResponse);
        onErrorOccurred();
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse){
        super.onGetCitiesErrorEvent(baseResponse);
        onErrorOccurred();
    }

    protected void onEditAddressErrorEvent(BaseResponse baseResponse){
        int errorCode = baseResponse.getError().getCode();
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            showFormValidateMessages(mEditFormGenerator, baseResponse, EventType.EDIT_ADDRESS_EVENT);
            showFragmentContentContainer();
        } else {
            Print.w(TAG, "RECEIVED GET_CITIES_EVENT: " + errorCode);
            onErrorOccurred();
        }
    }

    private void onErrorOccurred(){
        getBaseActivity().onBackPressed();
        getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_please_try_again));
    }
}
