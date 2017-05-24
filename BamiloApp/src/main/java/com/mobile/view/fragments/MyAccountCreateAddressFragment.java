package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.app.BamiloApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.errors.ErrorCode;
import com.mobile.service.tracking.AnalyticsGoogle;
import com.mobile.service.tracking.TrackingEvent;
import com.mobile.service.utils.EventType;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/24
 */
public class MyAccountCreateAddressFragment extends CreateAddressFragment {

    private static final String TAG = MyAccountCreateAddressFragment.class.getSimpleName();

    /**
     * Empty constructor
     */
    public MyAccountCreateAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.string.my_addresses,
                ADJUST_CONTENT,
                ConstantsCheckout.NO_CHECKOUT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideActivityProgress();
        // Set total bar
        if ( view.findViewById(R.id.checkout_total_label)!=null) {
            CheckoutStepManager.setTotalBarWithFullButton(view);
        }
        // Hide order summary
        UIUtils.showOrHideViews(View.GONE, view.findViewById(super.ORDER_SUMMARY_CONTAINER));
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeFormData();
    }

    private void initializeFormData() {
        // Get and show form
        if (mFormShipping != null && regions != null) {
            loadCreateAddressForm(mFormShipping);
        } else {
            triggerCreateAddressForm();
        }
    }

    @Override
    protected void onClickRetryButton() {
        initializeFormData();
    }

    @Override
    protected void onCreateAddressSuccessEvent(BaseResponse baseResponse) {
        super.onCreateAddressSuccessEvent(baseResponse);
        AnalyticsGoogle.get().trackAddressCreation(TrackingEvent.ACCOUNT_CREATE_ADDRESS,
                (BamiloApplication.CUSTOMER != null) ? BamiloApplication.CUSTOMER.getIdAsString() + "" : ""); //replaced getID because doesn't come from api

        FragmentController.getInstance().popLastEntry(FragmentType.MY_ACCOUNT_CREATE_ADDRESS.toString());
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);

        showWarningSuccessMessage(baseResponse.getSuccessMessage(), R.string.create_addresses_success);
    }

    @Override
    protected void onGetCreateAddressFormErrorEvent(BaseResponse baseResponse) {
        super.onGetCreateAddressFormErrorEvent(baseResponse);
        onErrorOccurred();
    }

    @Override
    protected void onGetRegionsErrorEvent(BaseResponse baseResponse) {
        super.onGetRegionsErrorEvent(baseResponse);
        onErrorOccurred();
    }

    @Override
    protected void onGetCitiesErrorEvent(BaseResponse baseResponse) {
        super.onGetCitiesErrorEvent(baseResponse);
        onErrorOccurred();

    }

    @Override
    protected void onCreateAddressErrorEvent(BaseResponse baseResponse) {
        super.onCreateAddressErrorEvent(baseResponse);
        int errorCode = baseResponse.getError().getCode();
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            // Case is same form for both or is the first
            showFormValidateMessages(shippingFormGenerator, baseResponse, EventType.CREATE_ADDRESS_EVENT);
            showFragmentContentContainer();
        } else {
            Log.w(TAG, "RECEIVED CREATE_ADDRESS_EVENT: " + errorCode);
            getBaseActivity().onBackPressed();
        }
    }

    private void onErrorOccurred() {
        getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_please_try_again));
        getBaseActivity().onBackPressed();
    }

    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) {

    }
}