package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.address.EditAddressHelper;
import com.mobile.helpers.address.GetCitiesHelper;
import com.mobile.helpers.address.GetFormEditAddressHelper;
import com.mobile.helpers.address.GetPostalCodeHelper;
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.helpers.configs.GetInitFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.objects.addresses.AddressCity;
import com.mobile.newFramework.objects.addresses.AddressPostalCode;
import com.mobile.newFramework.objects.addresses.AddressPostalCodes;
import com.mobile.newFramework.objects.addresses.AddressRegion;
import com.mobile.newFramework.objects.addresses.AddressRegions;
import com.mobile.newFramework.objects.addresses.FormListItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/25
 */
public abstract class EditAddressFragment extends BaseFragment implements IResponseCallback, IcsAdapterView.OnItemSelectedListener {

    private static final String TAG = EditAddressFragment.class.getSimpleName();

    public static final String SELECTED_ADDRESS = "selected_address";

    public static final int INVALID_ADDRESS_ID = -1;

    protected ViewGroup mEditFormContainer;

    protected DynamicForm mEditFormGenerator;

    protected Form mFormResponse;

    protected ArrayList<AddressRegion> mRegions;

    protected int mAddressId;

    protected PurchaseEntity orderSummary;

    protected boolean isCityIdAnEditText = false;

    private Bundle mFormSavedState;

    public EditAddressFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state) {
        super(enabledMenuItems, action, R.layout.checkout_edit_address_main, titleResId, adjust_state);
    }

    public EditAddressFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state, int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_edit_address_main, titleResId, adjust_state, titleCheckout);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Saved form state
        mFormSavedState = savedInstanceState;
        // Get arguments
        Bundle arguments = getArguments() != null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            mAddressId = arguments.getInt(EditAddressFragment.SELECTED_ADDRESS, INVALID_ADDRESS_ID);
        }
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_EDIT_ADDRESS);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Container
        mEditFormContainer = (ViewGroup) view.findViewById(R.id.checkout_edit_form_container);
        // Next button
        view.findViewById(R.id.checkout_edit_button_enter).setOnClickListener(this);
        // Cancel button
        view.findViewById(R.id.checkout_edit_button_cancel).setOnClickListener(this);
        //Validate current address
        if (mAddressId == INVALID_ADDRESS_ID) {
            showFragmentErrorRetry();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.d(TAG, "ON SAVE SATE");
        outState.putInt(EditAddressFragment.SELECTED_ADDRESS, mAddressId);
        if(mEditFormGenerator != null){
            mEditFormGenerator.saveFormState(outState);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        // Case goes to back stack save the state
        Bundle bundle = new Bundle();
        if(mEditFormGenerator != null) {
            mEditFormGenerator.saveFormState(bundle);
        }
        mFormSavedState = bundle;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
        mRegions = null;
    }


    /**
     * Load the dynamic form
     */
    protected void loadEditAddressForm(Form form) {
        Print.i(TAG, "LOAD EDIT ADDRESS FORM");
        // Edit form
        mEditFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getBaseActivity(), form);
        mEditFormContainer.removeAllViews();
        mEditFormGenerator.loadSaveFormState(mFormSavedState);
        mEditFormContainer.addView(mEditFormGenerator.getContainer());
        mEditFormContainer.refreshDrawableState();
        // Validate Regions
        if(mRegions == null) {
            FormField field = form.getFieldKeyMap().get(RestConstants.REGION);
            String url = field.getDataCalls().get(RestConstants.API_CALL);
            triggerGetRegions(url);
        } else {
            Print.d(TAG, "REGIONS ISN'T NULL");
            setRegions(mEditFormGenerator, mRegions);
        }
        // Define if CITY is a List or Text
        isCityIdAnEditText = (mEditFormGenerator.getItemByKey(RestConstants.CITY).getEditControl() instanceof EditText);
        // Show selected address content
        mEditFormContainer.refreshDrawableState();
    }

    /**
     * Method used to set the regions on the respective form
     */
    private void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions){
        Print.d(TAG, "SET REGIONS REGIONS: ");
        // Get region item
        DynamicFormItem formItem = dynamicForm.getItemByKey(RestConstants.REGION);
        // Clean group
        ViewGroup group = (ViewGroup) formItem.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>( getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getDefaultPosition(formItem, regions));
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
        showFragmentContentContainer(); // Show to trigger
    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities){
        // Get city item
        DynamicFormItem formItem = dynamicForm.getItemByKey(RestConstants.CITY);
        // Clean group
        ViewGroup group = (ViewGroup) formItem.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getDefaultPosition(formItem, cities));
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }

    /**
     * Method used to set the postalCodes on the respective form
     */
    private void setPostalCodes(DynamicForm dynamicForm, ArrayList<AddressPostalCode> postalCodes){
        // Get postal code item
        DynamicFormItem formItem = dynamicForm.getItemByKey(RestConstants.POSTCODE);
        // Clean group
        ViewGroup group = (ViewGroup) formItem.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, postalCodes);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getDefaultPosition(formItem, postalCodes));
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }

    /**
     * Get the position of the address city
     * @return int the position
     */
    private int getDefaultPosition(DynamicFormItem formItem, ArrayList<? extends FormListItem> regions){
        try {
            int position = Integer.valueOf(formItem.getEntry().getValue());
            for(int i = 0; i < regions.size(); i++){
                FormListItem formListItem = regions.get(i);
                if(formListItem.getValue() == position){
                    return i;
                }
            }
        } catch (NullPointerException | NumberFormatException e) {

        }
        return 0;
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
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Next button
        if(id == R.id.checkout_edit_button_enter) onClickEditAddressButton();
        // Next button
        else if(id == R.id.checkout_edit_button_cancel) onClickCancelAddressButton();
        // Unknown view
        else Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickRetryButton();
    }

    /**
     * Process the click on retry button.
     * @author paulo
     */
    protected abstract void onClickRetryButton() ;

    /**
     * Process the click on the next step button
     */
    private void onClickEditAddressButton() {
        Print.i(TAG, "ON CLICK: EDIT");
        if (mEditFormGenerator.validate()) {
            triggerEditAddress(createContentValues(mEditFormGenerator));
        } else {
            Print.i(TAG, "INVALID FORM");
        }
    }

    /**
     * Process the click on the cancel button
     */
    private void onClickCancelAddressButton() {
        Print.i(TAG, "ON CLICK: CANCEL");
        getBaseActivity().onBackPressed();
    }


    /**
     * Method used to create the content values
     *
     * @return new content values
     * @author sergiopereira
     */
    protected ContentValues createContentValues(DynamicForm dynamicForm) {
        // Save content values
        return dynamicForm.save();
    }

    /**
     * ########### ON ITEM SELECTED LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onNothingSelected(com.mobile.components.absspinner.IcsAdapterView)
     */
    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) { }

    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onItemSelected(com.mobile.components.absspinner.IcsAdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        Print.d(TAG, "ON ITEM SELECTED");
        Object object = parent.getItemAtPosition(position);
        if (object instanceof AddressRegion) {
            // Get city field
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.CITY);
            // Case list
            if (FormInputType.list == field.getInputType()) {
                // Get API call
                String url = field.getDataCalls().get(RestConstants.API_CALL);
                // Request the cities for this region id
                int regionId = ((AddressRegion) object).getValue();
                // Get cities
                triggerGetCities(url, regionId);
            }
            // Case text or other
            else {
                showFragmentContentContainer();
            }
        } else if (object instanceof AddressCity){

            // Get city field
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.POSTCODE);
            // Case list
            if (field != null && FormInputType.list == field.getInputType()) {
                // Get url
                String url = field.getDataCalls().get(RestConstants.API_CALL);
                // Request the postal codes for this city id
                int cityId = ((AddressCity) object).getValue();
                // Get postal codes
                triggerGetPostalCodes(url, cityId);

            }
        }
    }

    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to edit an address
     */
    private void triggerEditAddress(ContentValues values) {
        Print.i(TAG, "TRIGGER: EDIT ADDRESS");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new EditAddressHelper(), bundle, this);
        // Hide the keyboard
        getBaseActivity().hideKeyboard();
    }

    /**
     * Trigger to get the address form
     */
    protected void triggerEditAddressForm(){
        Print.i(TAG, "TRIGGER: EDIT FORM");
        ContentValues values = new ContentValues();
        values.put(GetFormEditAddressHelper.SELECTED_ADDRESS_ID, mAddressId);
        Bundle arg = new Bundle();
        arg.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new GetFormEditAddressHelper(), arg, this);
    }

    /**
     * Trigger to initialize forms
     */
    protected void triggerInitForm(){
        Print.i(TAG, "TRIGGER: INIT FORMS");
        triggerContentEvent(new GetInitFormHelper(), null, this);
    }

    /**
     * Trigger to get regions
     */
    private void triggerGetRegions(String apiCall){
        Print.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        triggerContentEventNoLoading(new GetRegionsHelper(), GetRegionsHelper.createBundle(apiCall), this);
    }

    /**
     * Trigger to get cities
     */
    private void triggerGetCities(String apiCall, int region){
        Print.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        triggerContentEvent(new GetCitiesHelper(), GetCitiesHelper.createBundle(apiCall, region, null), this);
    }

    /**
     * Trigger to get postal codes
     */
    private void triggerGetPostalCodes(String apiCall, int city){
        Print.i(TAG, "TRIGGER: GET POSTAL CODES: " + apiCall);
        triggerContentEvent(new GetPostalCodeHelper(), GetPostalCodeHelper.createBundle(apiCall, city, null), this);
    }

    /**
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     * @return boolean
     */
    protected boolean onSuccessEvent(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        if(isOnStoppingProcess || eventType == null){
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        switch (eventType) {
            case INIT_FORMS:
                Print.d(TAG, "RECEIVED INIT_FORMS");
                triggerEditAddressForm();
                break;
            case GET_EDIT_ADDRESS_FORM_EVENT:
                Print.d(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
                // Get order summary
                //orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
                orderSummary = JumiaApplication.INSTANCE.getCart();
                // Form
                Form form = (Form)baseResponse.getMetadata().getData();
                mFormResponse = form;
                // Load form, get regions
                loadEditAddressForm(form);
                break;
            case GET_REGIONS_EVENT:
                Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
                mRegions = (AddressRegions) baseResponse.getMetadata().getData();
                if (CollectionUtils.isNotEmpty(mRegions)) {
                    setRegions(mEditFormGenerator, mRegions);
                } else {
                    Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
                    super.showFragmentErrorRetry();
                }
                break;
            case GET_CITIES_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                ArrayList<AddressCity> cities = (GetCitiesHelper.AddressCitiesStruct)baseResponse.getMetadata().getData();
                setCities(mEditFormGenerator, cities);
                // Show
                showFragmentContentContainer();
                break;
            case GET_POSTAL_CODE_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                ArrayList<AddressPostalCode> postalCodes = (AddressPostalCodes)baseResponse.getMetadata().getData();
                setPostalCodes(mEditFormGenerator, postalCodes);
                // Show
                showFragmentContentContainer();
                break;
            case EDIT_ADDRESS_EVENT:
                Print.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
                Toast.makeText(getBaseActivity(), getString(R.string.edit_address_success), Toast.LENGTH_SHORT).show();
                getBaseActivity().onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Filter the error response
     * @return boolean
     */
    protected boolean onErrorEvent(BaseResponse baseResponse) {

        EventType eventType = baseResponse.getEventType();

        if(isOnStoppingProcess || eventType == null){
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }

        ErrorCode errorCode = baseResponse.getError().getErrorCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
            case INIT_FORMS:
                onInitFormsErrorEvent();
                break;
            case GET_EDIT_ADDRESS_FORM_EVENT:
                onGetEditAddressFormErrorEvent(baseResponse);
                break;
            case GET_REGIONS_EVENT:
                onGetRegionsErrorEvent(baseResponse);
                break;
            case GET_CITIES_EVENT:
                onGetCitiesErrorEvent(baseResponse);
                break;
            case GET_POSTAL_CODE_EVENT:
                onGetPostalCodesErrorEvent(baseResponse);
                break;
            case EDIT_ADDRESS_EVENT:
                onEditAddressErrorEvent(baseResponse);
                break;
            default:
                break;
        }

        return false;
    }

    protected void onInitFormsErrorEvent(){
        Print.d(TAG, "RECEIVED INIT_FORMS");
    }

    protected void onGetEditAddressFormErrorEvent(BaseResponse baseResponse){
        Print.w(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse){
        Print.w(TAG, "RECEIVED GET_REGIONS_EVENT");
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse){
        Print.w(TAG, "RECEIVED GET_CITIES_EVENT");
    }

    protected void onGetPostalCodesErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_POSTAL_CODES_EVENT");
    }

    protected void onEditAddressErrorEvent(BaseResponse baseResponse){
        Print.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
    }

    /**
     * ########### RESPONSE LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        onErrorEvent(baseResponse);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        onSuccessEvent(baseResponse);
    }

    /**
     * ########### DIALOGS ###########
     */
    /**
     * Dialog used to show an error
     */
    protected void showErrorDialog(Map<String, List<String>> errors) {
        Print.d(TAG, "SHOW LOGIN ERROR DIALOG");
        List<String> errorMessages = null;
        if (errors != null) {
            errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
        }
        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            showFragmentContentContainer();
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.error_login_title),
                    errorMessages.get(0),
                    getString(R.string.ok_label),
                    "",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                            }
                        }
                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
        } else {
            Toast.makeText(getBaseActivity(), getString(R.string.register_required_text), Toast.LENGTH_SHORT).show();
        }
    }
}

