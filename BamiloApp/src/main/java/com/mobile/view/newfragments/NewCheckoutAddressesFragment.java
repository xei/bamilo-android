package com.mobile.view.newfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobile.adapters.AddressAdapter;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.address.SetDefaultShippingAddressHelper;
import com.mobile.helpers.checkout.GetStepAddressesHelper;
import com.mobile.helpers.checkout.SetStepAddressesHelper;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.checkout.MultiStepAddresses;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.errors.ErrorCode;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

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
                ConstantsCheckout.CHECKOUT_BILLING,true);

        //getBaseActivity().mAppBarLayout.setExpanded(true, true);
    }

    /*
    * ############# LIFE CYCLE #############
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
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
        Print.i(TAG, "ON VIEW CREATED");
        mAddressView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        mAddressView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAddressView.setLayoutManager(llm);
        mCheckoutTotalBar = view.findViewById(R.id.address_continue);
        fabNewAddress = (FloatingActionButton) view.findViewById(R.id.fab_new_address);

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
        Print.i(TAG, "ON START");
        getBaseActivity().setActionBarTitle(R.string.checkout_choose_address_step);
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Get addresses
        if(mAddresses == null) {
            triggerGetForm();
        } else {
            showAddresses(mAddresses, mSelectedAddress);
        }
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


    View.OnClickListener onClickSubmitAddressesButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Print.i(TAG, "ON CLICK: SUBMIT");
            // Validate the is same check box
            int selectedId = ((AddressAdapter) mAddressView.getAdapter()).getSelectedId();
            Print.i(TAG, "ON CLICK: SUBMIT " + selectedId);
            triggerSetMultiStepAddresses(selectedId, selectedId);
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
        Print.d(TAG, "TRIGGER SET BILLING");
        triggerContentEventProgress(new SetStepAddressesHelper(), SetStepAddressesHelper.createBundle(billing, shipping), this);
    }

    private void triggerSetDefaultAddress(int selectedAddress){
        triggerContentEventNoLoading(new SetDefaultShippingAddressHelper(), SetDefaultShippingAddressHelper.createBundle(selectedAddress), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        /*if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }*/
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case GET_MULTI_STEP_ADDRESSES:
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
        hideActivityProgress();
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
