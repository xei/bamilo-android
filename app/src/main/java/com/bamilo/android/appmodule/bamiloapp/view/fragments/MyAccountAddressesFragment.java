package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.GetMyAddressesHelper;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.framework.service.objects.addresses.Addresses;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.R;

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
        mGABeginRequestMillis = System.currentTimeMillis();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Hide some checkout views
        // TODO: 8/28/18 farshid
//        UIUtils.showOrHideViews(View.GONE, view.findViewById(R.id.checkout_total_bar), view.findViewById(ORDER_SUMMARY_CONTAINER));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        // Goto edit address
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.ARG_1, addressId);
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        EventTracker.INSTANCE.editAddress();
    }

    /**
     * Process the click on add button.
     */
    @Override
    protected void onClickCreateAddressButton() {
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        EventTracker.INSTANCE.addAddress();
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
            return;
        }
        EventType eventType = baseResponse.getEventType();
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
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate type
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        switch (eventType) {
            case GET_CUSTOMER_ADDRESSES_EVENT:
                showContinueShopping();
                break;
            default:
                break;
        }
    }
}
