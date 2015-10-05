/**
 * 
 */
package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutEditAddressFragment extends EditAddressFragment {

    private static final String TAG = CheckoutEditAddressFragment.class.getSimpleName();

    /**
     * Constructor
     */
    public static CheckoutEditAddressFragment getInstance(Bundle bundle) {
        CheckoutEditAddressFragment fragment = new CheckoutEditAddressFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutEditAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.Checkout,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
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
        // Get and show form
        if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().isEmpty()){
            triggerInitForm();
        } else if(mFormResponse != null && orderSummary != null && mRegions != null){
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
        if(null != JumiaApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    protected void onGetEditAddressFormErrorEvent(Bundle bundle){
        super.onGetEditAddressFormErrorEvent(bundle);
        super.showFragmentErrorRetry();
    }

    protected void onGetRegionsErrorEvent(Bundle bundle){
        super.onGetRegionsErrorEvent(bundle);
        super.showFragmentErrorRetry();
    }

    protected void onGetCitiesErrorEvent(Bundle bundle){
        super.onGetCitiesErrorEvent(bundle);
        super.showFragmentErrorRetry();
    }

    protected void onEditAddressErrorEvent(Bundle bundle){
        super.onEditAddressErrorEvent(bundle);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            @SuppressWarnings("unchecked")
            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            showErrorDialog(errors);
        } else {
            Print.w(TAG, "RECEIVED GET_CITIES_EVENT: " + errorCode);
            super.showUnexpectedErrorWarning();
        }
        showFragmentContentContainer();
    }
}
