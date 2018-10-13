package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.helpers.address.GetMyAddressesHelper;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.addresses.Addresses;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * Created by Arash on 1/22/2017.
 */

public class NewMyAccountAddressesFragment extends NewBaseAddressesFragment {

    private static final String TAG = NewMyAccountAddressesFragment.class.getSimpleName();

    private View mCheckoutTotalBar;
    private FloatingActionButton fabNewAddress;
    private int mSelectedAddress;
    private boolean pageTracked = false;

    public NewMyAccountAddressesFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT_MY_ADDRESSES,
                R.layout.new_my_account_addresses,
                R.string.my_addresses,
                ConstantsCheckout.NO_CHECKOUT, false);
    }

    /*
    * ############# LIFE CYCLE #############
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.MY_ADDRESSES.getName()), getString(R.string.gaScreen),
                getString(R.string.gaMyProfileLabel),
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddressView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        mAddressView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAddressView.setLayoutManager(llm);
        //mCheckoutTotalBar = view.findViewById(R.id.address_continue);
        fabNewAddress = (FloatingActionButton) view.findViewById(R.id.fab_new_address);
        fabNewAddress.setOnClickListener(onClickCreateAddressButton);
        getBaseActivity().updateBaseComponents(EnumSet.of(MyMenuItem.UP_BUTTON_BACK), NavigationAction.MY_ACCOUNT_MY_ADDRESSES, R.string.my_addresses, ConstantsCheckout.NO_CHECKOUT);

        mSelectedAddress = -1;

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get addresses
        triggerGetForm();
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


    /**
     * Process the click on add button.
     */
    View.OnClickListener onClickCreateAddressButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    };


    /**
     * ############# REQUESTS #############
     */

    @Override
    protected void triggerGetAddresses() {
        triggerContentEvent(new GetMyAddressesHelper(), null, this);
    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerGetForm();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }*/
        hideActivityProgress();
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_CUSTOMER_ADDRESSES_EVENT:
                super.showAddresses((Addresses) baseResponse.getContentData(), -1);
                fabNewAddress.show();
                if (!pageTracked) {
                    /*TrackerDelegator.trackPage(TrackingPage.MY_ADDRESSES, getLoadTime(), false);*/


                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.MY_ADDRESSES.getName()), getString(R.string.gaScreen),
                            getString(R.string.gaMyProfileLabel),
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }
                break;
            case GET_DELETE_ADDRESS_FORM_EVENT:
                fabNewAddress.show();
                triggerGetAddresses();
                break;
            case SET_DEFAULT_SHIPPING_ADDRESS:
                triggerGetAddresses();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }*/
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
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
