package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.EventType;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.WarningFactory;
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
                NavigationAction.MY_ACCOUNT,
                R.string.my_addresses,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set total bar
        CheckoutStepManager.setTotalBarForMyAccount(view);
        // Validate order summary
        View orderSummaryLayout = view.findViewById(super.ORDER_SUMMARY_CONTAINER);
        if(orderSummaryLayout != null){
            orderSummaryLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeFormData();
    }

    private void initializeFormData() {
        // Get and show form
        if(mFormShipping != null &&  mFormBilling!= null && regions != null){
            loadCreateAddressForm(mFormShipping,mFormBilling);
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
                (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString()+"":""); //replaced getID because doesn't come from api

        if(!mIsSameCheckBox.isChecked() && !oneAddressCreated){
            oneAddressCreated = true;

            if (null != billingFormGenerator) {
                ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                triggerCreateAddress(billingFormGenerator.getForm().getAction(), mBillValues);
            }
        } else {
            FragmentController.getInstance().popLastEntry(FragmentType.MY_ACCOUNT_CREATE_ADDRESS.toString());
            getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);

            showWarningSuccessMessage(baseResponse.getSuccessMessage(), R.string.create_addresses_success);
        }
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
            if(mIsSameCheckBox != null && (mIsSameCheckBox.isChecked() || !oneAddressCreated)) {
                showFormValidateMessages(shippingFormGenerator, baseResponse, EventType.CREATE_ADDRESS_EVENT);
            }
            // Case is not the same and is the second
            else {
                showFormValidateMessages(billingFormGenerator, baseResponse, EventType.CREATE_ADDRESS_EVENT);
            }
            showFragmentContentContainer();
        } else {
            Log.w(TAG, "RECEIVED CREATE_ADDRESS_EVENT: " + errorCode);
            getBaseActivity().onBackPressed();
        }
    }

    private void onErrorOccurred(){
        getBaseActivity().onBackPressed();
        getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_please_try_again));
    }

}
