package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.adapters.AddressAdapter;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.SetDefaultShippingAddressHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetStepAddressesHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.SetStepAddressesHelper;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.utils.Toast;
import com.bamilo.android.framework.service.objects.addresses.Addresses;
import com.bamilo.android.framework.service.objects.checkout.MultiStepAddresses;
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

public class NewCheckoutAddressesFragment extends NewBaseAddressesFragment {

    private static final String TAG = NewCheckoutAddressesFragment.class.getSimpleName();

    private View mCheckoutTotalBar;
    private FloatingActionButton fabNewAddress;
    private int mSelectedAddress;
    private FragmentType checkoutPaymentFragment;
    private boolean pageTracked = false;

    public NewCheckoutAddressesFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.new_checkout_my_addresses,
                R.string.checkout_choose_address_step,
                ConstantsCheckout.CHECKOUT_BILLING, true);

        //getBaseActivity().mAppBarLayout.setExpanded(true, true);
    }

    /*
    * ############# LIFE CYCLE #############
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Toolbar toolbar = (Toolbar) getBaseActivity().findViewById(R.id.toolbar);  // or however you need to do it for your code
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);*/


        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.MY_ADDRESSES.getName()), getString(R.string.gaScreen),
                getString(R.string.gaCheckoutLabel),
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((savedInstanceState != null)
                && (savedInstanceState.getInt("mSelectedAddress", -1) != -1)) {
            mSelectedAddress = savedInstanceState.getInt("mSelectedAddress", -1);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddressView = view.findViewById(R.id.address_recycler_view);
        mAddressView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAddressView.setLayoutManager(llm);
        mCheckoutTotalBar = view.findViewById(R.id.address_continue);
        fabNewAddress = view.findViewById(R.id.fab_new_address);

        getBaseActivity().updateBaseComponents(EnumSet.of(MyMenuItem.UP_BUTTON_BACK), NavigationAction.CHECKOUT, R.string.checkout_label, ConstantsCheckout.CHECKOUT_BILLING);
        mCheckoutTotalBar.setOnClickListener(onClickSubmitAddressesButton);
        fabNewAddress.setOnClickListener(onClickCreateAddressButton);

        mSelectedAddress = -1;

        if ((savedInstanceState != null)
                && (savedInstanceState.getInt("mSelectedAddress", -1) != -1)) {
            mSelectedAddress = savedInstanceState.getInt("mSelectedAddress", -1);
        }

        super.setCheckoutStep(view, 1);
    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerGetForm();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mSelectedAddress", mSelectedAddress);
    }

    @Override
    public void onStart() {
        super.onStart();
        getBaseActivity().setActionBarTitle(R.string.checkout_choose_address_step);
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


    View.OnClickListener onClickSubmitAddressesButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Validate the is same check box
            try {
                int selectedId = ((AddressAdapter) mAddressView.getAdapter()).getSelectedId();
                triggerSetMultiStepAddresses(selectedId, selectedId);
            } catch (Exception e) {
                Toast.makeText(getContext(), "آدرس ارسال را انتخاب کنید", Toast.LENGTH_SHORT).show();
            }

        }


    };

    /**
     * Process the click on add button.
     */
    View.OnClickListener onClickCreateAddressButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    };


    /**
     * ############# REQUESTS #############
     */

    @Override
    protected void triggerGetAddresses() {
        triggerContentEvent(new GetStepAddressesHelper(), null, this);
    }

    /*
     * Trigger to set the billing form
     */

    private void triggerSetMultiStepAddresses(int billing, int shipping) {
        triggerContentEventProgress(new SetStepAddressesHelper(), SetStepAddressesHelper.createBundle(billing, shipping), this);
    }

    private void triggerSetDefaultAddress(int selectedAddress) {
        triggerContentEventNoLoading(new SetDefaultShippingAddressHelper(), SetDefaultShippingAddressHelper.createBundle(selectedAddress), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }*/
        fun(baseResponse);
    }

    private void fun(BaseResponse baseResponse) {

        if (baseResponse == null) {
            fabNewAddress.show();
            super.showAddresses(null, mSelectedAddress);
            hideActivityProgress();
            return;
        }

        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_MULTI_STEP_ADDRESSES:
                fabNewAddress.show();
                // Get form and show order
                MultiStepAddresses multiStepAddresses = (MultiStepAddresses) baseResponse.getContentData();
                //CheckoutStepManager.setTotalBar(mCheckoutTotalBar, multiStepAddresses.getOrderSummary());
                //super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, multiStepAddresses.getOrderSummary());
                try {
                    mSelectedAddress = multiStepAddresses.getOrderSummary().getShippingAddress().getId();
                } catch (Exception ex) {
                    mSelectedAddress = -1;
                }
                super.showAddresses(multiStepAddresses.getAddresses(), mSelectedAddress);
                hideActivityProgress();

                if (!pageTracked) {
                    /*TrackerDelegator.trackPage(TrackingPage.CHECKOUT_ADDRESSES, getLoadTime(), false);*/

                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.MY_ADDRESSES.getName()), getString(R.string.gaScreen),
                            getString(R.string.gaCheckoutLabel),
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }
                break;
            case SET_MULTI_STEP_ADDRESSES:
                int selectedId = ((AddressAdapter) mAddressView.getAdapter()).getSelectedId();
                triggerSetDefaultAddress(selectedId);
                // Get next step
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                checkoutPaymentFragment = nextStepStruct.getFragmentType();
                checkoutPaymentFragment = FragmentType.CHECKOUT_CONFIRMATION;
                checkoutPaymentFragment = (checkoutPaymentFragment != FragmentType.UNKNOWN) ? checkoutPaymentFragment : FragmentType.CHECKOUT_PAYMENT;
                break;
            case SET_DEFAULT_SHIPPING_ADDRESS:
                getBaseActivity().onSwitchFragment(checkoutPaymentFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

        try {
            if (baseResponse.getError().getMessage().contains("JSONException")) {
                fun(null);
                return;
            }
        } catch (Exception e) {
            fun(null);












































            return;
        }

        hideActivityProgress();
        if (isOnStoppingProcess) {
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
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
