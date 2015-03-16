/**
 *
 */
package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.forms.Form;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 *
 * @author sergiopereira
 *
 */
public class CheckoutCreateAddressFragment extends CreateAddressFragment{

    private static final String TAG = LogTagHelper.create(CheckoutCreateAddressFragment.class);

    private OrderSummary orderSummary;
    /**
     * Fragment used to create an address
     * @return CheckoutCreateAddressFragment
     * @author sergiopereira
     */
    public static CheckoutCreateAddressFragment getInstance() {
        return new CheckoutCreateAddressFragment();
    }

    /**
     * Empty constructor
     * @author sergiopereira
     */
    public CheckoutCreateAddressFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Validate is service is available
        if(JumiaApplication.mIsBound){
            // Get and show form
            if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
                triggerInitForm();
            } else if(mFormResponse != null && orderSummary != null && regions != null){
                loadCreateAddressForm(mFormResponse);
            } else {
                triggerCreateAddressForm();
            }
        } else {
            showFragmentErrorRetry();
        }
    }


    protected void loadCreateAddressForm(Form form) {
        super.loadCreateAddressForm(form);
        // Show order summary
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

    @Override
    protected void onCreateAddressSuccessEvent(Bundle bundle) {
        super.onCreateAddressSuccessEvent(bundle);
        //GTM
        TrackerDelegator.trackAddAddress(true);
        // Waiting for both responses
        if(!mIsSameCheckBox.isChecked() && !oneAddressCreated){
            oneAddressCreated = true;

            if (null != billingFormGenerator) {
                ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                triggerCreateAddress(mBillValues, true);
            }
            return ;
        }

        // Get next step
        FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
        if(nextFragment == null || nextFragment == FragmentType.UNKNOWN){
            Log.w(TAG, "NEXT STEP IS UNKNOWN OR NULL -> FALL BACK MY_ADDRESSES");
            nextFragment = FragmentType.MY_ADDRESSES;
        }
        Toast.makeText(getBaseActivity(), getString(R.string.create_addresses_success), Toast.LENGTH_SHORT).show();
        FragmentController.getInstance().popLastEntry(FragmentType.CREATE_ADDRESS.toString());
        getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    protected void onGetCreateAddressFormErrorEvent(Bundle bundle) {
        super.onGetCreateAddressFormErrorEvent(bundle);
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
    }

    @Override
    protected void onGetRegionsErrorEvent(Bundle bundle) {
        super.onGetRegionsErrorEvent(bundle);
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
    }

    @Override
    protected void onGetCitiesErrorEvent(Bundle bundle) {
        super.onGetCitiesErrorEvent(bundle);
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CITIES_EVENT");
    }

    @Override
    protected void onCreateAddressErrorEvent(Bundle bundle) {
        super.onCreateAddressErrorEvent(bundle);
        //GTM
        TrackerDelegator.trackAddAddress(false);

        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (errorCode == ErrorCode.REQUEST_ERROR) {
            @SuppressWarnings("unchecked")
            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            showErrorDialog(errors);
            showFragmentContentContainer();
        } else {
            Log.w(TAG, "RECEIVED CREATE_ADDRESS_EVENT: " + errorCode.name());
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED CREATE_ADDRESS_EVENT" + errorCode.name());
        }
    }
}