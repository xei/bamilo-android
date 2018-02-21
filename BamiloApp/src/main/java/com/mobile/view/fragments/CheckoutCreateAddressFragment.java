package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.mobile.app.BamiloApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.tracking.TrackingEvent;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * @author sergiopereira
 */
public class CheckoutCreateAddressFragment extends CreateAddressFragment {

    private static final String TAG = CheckoutCreateAddressFragment.class.getSimpleName();

    private View mCheckoutTotalBar;

    /**
     * Empty constructor
     */
    public CheckoutCreateAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.string.checkout_add_new_address,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_CREATE_ADDRESS);
        hideActivityProgress();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCheckoutTotalBar = view.findViewById(R.id.checkout_total_bar);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get and show form
       /* if (mFormShipping != null && orderSummary != null && regions != null) {
            loadCreateAddressForm(mFormShipping);
        } else {
            triggerCreateAddressForm();
        }*/
    }

    @Override
    protected void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if (null != BamiloApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    @Override
    protected void onCreateAddressSuccessEvent(BaseResponse baseResponse) {
        super.onCreateAddressSuccessEvent(baseResponse);
        //GTM
        TrackerDelegator.trackAddAddress(true);
        // Get next step
        FragmentType nextFragment = ((NextStepStruct) baseResponse.getMetadata().getData()).getFragmentType();
        if (nextFragment == null || nextFragment == FragmentType.UNKNOWN) {
            Print.w(TAG, "NEXT STEP IS UNKNOWN OR NULL -> FALL BACK MY_ADDRESSES");
            nextFragment = FragmentType.CHECKOUT_MY_ADDRESSES;
        }

        FragmentController.getInstance().popLastEntry(FragmentType.CHECKOUT_CREATE_ADDRESS.toString());
        getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        showWarningSuccessMessage(baseResponse.getSuccessMessage(), baseResponse.getEventType());
    }

    @Override
    protected void onGetCreateAddressFormErrorEvent(BaseResponse baseResponse) {
        super.onGetCreateAddressFormErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    @Override
    protected void onGetRegionsErrorEvent(BaseResponse baseResponse) {
        super.onGetRegionsErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    @Override
    protected void onGetCitiesErrorEvent(BaseResponse baseResponse) {
        super.onGetCitiesErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    @Override
    protected void onCreateAddressErrorEvent(BaseResponse baseResponse) {
        super.onCreateAddressErrorEvent(baseResponse);
        TrackerDelegator.trackAddAddress(false);
        showFragmentContentContainer();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}