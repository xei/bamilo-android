package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.app.BamiloApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.forms.Form;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.errors.ErrorCode;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutEditAddressFragment extends EditAddressFragment {

    private static final String TAG = CheckoutEditAddressFragment.class.getSimpleName();

    /**
     * Empty constructor
     */
    public CheckoutEditAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.string.checkout_label,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        initializeFormData();
    }

    private void initializeFormData() {
        // Get and show form
        if(mFormResponse != null && orderSummary != null && mRegions != null){
            loadEditAddressForm(mFormResponse);
        } else {
            triggerEditAddressForm();
        }
    }

    @Override
    protected void loadEditAddressForm(Form form) {
        super.loadEditAddressForm(form);
        // Show
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
    }

    @Override
    protected void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if (null == BamiloApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            initializeFormData();
        }
    }

    protected void onGetEditAddressFormErrorEvent(BaseResponse baseResponse){
        super.onGetEditAddressFormErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse){
        super.onGetRegionsErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse){
        super.onGetCitiesErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    protected void onEditAddressErrorEvent(BaseResponse baseResponse){
        super.onEditAddressErrorEvent(baseResponse);
        int errorCode = baseResponse.getError().getCode();
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            showFormValidateMessages(mEditFormGenerator, baseResponse, EventType.EDIT_ADDRESS_EVENT);
        } else {
            Print.w(TAG, "RECEIVED GET_CITIES_EVENT: " + errorCode);
            super.showUnexpectedErrorWarning();
        }
        showFragmentContentContainer();
    }
}