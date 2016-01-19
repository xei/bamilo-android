package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.checkout.GetStepAddressesHelper;
import com.mobile.helpers.checkout.SetStepAddressesHelper;
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
public class MyAddressesFragmentNew_2 extends MyAddressesFragmentNew {

    private static final String TAG = MyAddressesFragmentNew_2.class.getSimpleName();

    private View mCheckoutTotalBar;

    /**
     * Get instance
     *
     * @return MyAddressesFragment
     */
    public static MyAddressesFragmentNew_2 newInstance() {
        return new MyAddressesFragmentNew_2();
    }

    public MyAddressesFragmentNew_2() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT_MY_ADDRESSES,
                R.layout._def_checkout_my_addresses_new,
                R.string.my_addresses,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Tracking checkout step
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_ADDRESSES);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Total bar
        mCheckoutTotalBar = view.findViewById(R.id.checkout_total_bar);
        view.findViewById(R.id.checkout_button_enter).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        TrackerDelegator.trackPage(TrackingPage.ADDRESS_SCREEN, getLoadTime(), true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     * ############# CLICK LISTENER #############
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Submit
        if (id == R.id.checkout_button_enter) onClickSubmitAddressesButton();
        // Unknown view
        else super.onClick(view);
    }

    /**
     * Process the retry button
     */
    @Override
    protected void onClickRetryButton(View view) {
        Bundle bundle = new Bundle();
        if (JumiaApplication.CUSTOMER == null) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            super.onClickRetryButton(view);
        }
    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag.
     */
    @Override
    protected void onClickEditAddressButton(View view) {
        // Get tag that contains the address id
        int addressId = (int) view.getTag();
        Print.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
        // Goto edit address
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.ARG_1, addressId);
        getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on add button.
     */
    @Override
    protected void onClickCreateAddressButton() {
        getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on submit button.</br>
     */
    protected void onClickSubmitAddressesButton() {
        Print.i(TAG, "ON CLICK: SUBMIT");
        // Validate the is same check box
        triggerSetMultiStepAddresses(mAddresses.getBillingAddress().getId(), mAddresses.getShippingAddress().getId());
    }

    /**
     * ############# REQUESTS #############
     */

    @Override
    protected void triggerGetAddresses() {
        triggerContentEvent(new GetStepAddressesHelper(), null, this);
    }

    /**
     * Trigger to set the billing form
     */
    private void triggerSetMultiStepAddresses(int billing, int shipping) {
        Print.d(TAG, "TRIGGER SET BILLING");
        // TODO
        triggerContentEvent(new SetStepAddressesHelper(), SetStepAddressesHelper.createBundle("" + billing, "" + shipping), this);
    }

    /**
     * ############# RESPONSE #############
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case GET_MULTI_STEP_ADDRESSES:
                // Get form and show order
                MultiStepAddresses multiStepAddresses = (MultiStepAddresses) baseResponse.getContentData();
                CheckoutStepManager.setTotalBar(mCheckoutTotalBar, multiStepAddresses.getOrderSummary());
                super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, multiStepAddresses.getOrderSummary());
                super.showAddresses(multiStepAddresses.getAddresses());
                break;
            case SET_MULTI_STEP_ADDRESSES:
                // Get next step
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                FragmentType nextFragment = nextStepStruct.getFragmentType();
                nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.CHECKOUT_SHIPPING;
                getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        switch (eventType) {
            case GET_MULTI_STEP_ADDRESSES:
                // Show retry
                super.showFragmentErrorRetry();
                break;
            case SET_MULTI_STEP_ADDRESSES:
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    showWarningErrorMessage(baseResponse.getValidateMessage());
                } else {
                    super.showUnexpectedErrorWarning();
                }
                showFragmentContentContainer();
                break;
            default:
                break;
        }
    }


}
