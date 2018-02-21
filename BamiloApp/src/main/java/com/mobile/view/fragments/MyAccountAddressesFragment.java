package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.address.GetMyAddressesHelper;
import com.mobile.service.objects.addresses.Addresses;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class used to represent customer addresses in the account context.
 * @author sergio pereira
 */
public class MyAccountAddressesFragment extends BaseAddressesFragment {

    private static final String TAG = MyAccountAddressesFragment.class.getSimpleName();
    //DROID-10
    private long mGABeginRequestMillis;
    /**
     * Constructor
     */
    public MyAccountAddressesFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT_MY_ADDRESSES,
                R.string.my_addresses,
                ConstantsCheckout.NO_CHECKOUT);
    }

    /*
     * ############# LIFE CYCLE #############
     */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
        mGABeginRequestMillis = System.currentTimeMillis();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Hide some checkout views
        UIUtils.showOrHideViews(View.GONE, view.findViewById(R.id.checkout_total_bar), view.findViewById(ORDER_SUMMARY_CONTAINER));
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

    /*
     * ############# REQUESTS #############
     */

    @Override
    protected void triggerGetAddresses() {
        triggerContentEvent(new GetMyAddressesHelper(), null, this);
    }

    /*
     * ############# CLICK LISTENER #############
     */

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
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on add button.
     */
    @Override
    protected void onClickCreateAddressButton() {
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * ############# RESPONSE #############
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
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
            case GET_CUSTOMER_ADDRESSES_EVENT:
                super.showAddresses((Addresses) baseResponse.getContentData());
                break;
            default:
                break;
        }
    }
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
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
        // Validate type
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        switch (eventType) {
            case GET_CUSTOMER_ADDRESSES_EVENT:
                showContinueShopping();
                break;
            default:
                break;
        }
    }

}
