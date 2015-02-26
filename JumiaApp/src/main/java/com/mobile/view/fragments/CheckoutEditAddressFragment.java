/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.forms.Form;
import com.mobile.forms.FormField;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Address;
import com.mobile.framework.objects.AddressCity;
import com.mobile.framework.objects.AddressRegion;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.tracking.TrackingEvent;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.address.GetCitiesHelper;
import com.mobile.helpers.address.GetFormEditAddressHelper;
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.helpers.address.SetEditedAddressHelper;
import com.mobile.helpers.configs.GetInitFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.InputType;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutEditAddressFragment extends EditAddressFragment {

    private static final String TAG = LogTagHelper.create(CheckoutEditAddressFragment.class);

    protected static CheckoutEditAddressFragment mEditAddressFragment;

    /**
     * 
     * @return
     */
    public static CheckoutEditAddressFragment getInstance(Bundle bundle) {
        mEditAddressFragment = new CheckoutEditAddressFragment();
        mEditAddressFragment.mCurrentAddress = bundle.getParcelable(EditAddressFragment.SELECTED_ADDRESS);
        return mEditAddressFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutEditAddressFragment() {
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
            } else if(mFormResponse != null && orderSummary != null && mRegions != null){
                loadEditAddressForm(mFormResponse);
            } else {
                triggerEditAddressForm();
            }
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    protected void loadEditAddressForm(Form form) {
        super.loadEditAddressForm(form);

        // Show
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
    }

    protected void onGetEditAddressFormErrorEvent(Bundle bundle){
        super.onGetEditAddressFormErrorEvent(bundle);
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
    }

    protected void onGetRegionsErrorEvent(Bundle bundle){
        super.onGetRegionsErrorEvent(bundle);
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_REGIONS_EVENT");
    }

    protected void onGetCitiesErrorEvent(Bundle bundle){
        super.onGetCitiesErrorEvent(bundle);
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CITIES_EVENT");
    }

    protected void onEditAddressErrorEvent(Bundle bundle){
        super.onEditAddressErrorEvent(bundle);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            @SuppressWarnings("unchecked")
            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            showErrorDialog(errors);
            showFragmentContentContainer();
        } else {
            Log.w(TAG, "RECEIVED GET_CITIES_EVENT: " + errorCode.name());
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CITIES_EVENT: " + errorCode.name());
        }
    }
}
