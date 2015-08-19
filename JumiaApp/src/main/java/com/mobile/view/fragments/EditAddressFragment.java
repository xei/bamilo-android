package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.address.GetCitiesHelper;
import com.mobile.helpers.address.GetFormEditAddressHelper;
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.helpers.address.UpdateAddressHelper;
import com.mobile.helpers.configs.GetInitFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.InputType;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.addresses.AddressCity;
import com.mobile.newFramework.objects.addresses.AddressRegion;
import com.mobile.newFramework.objects.orders.OrderSummary;
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
import java.util.HashMap;
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

    public static final String SELECTED_ADDRESS_ID = "id";  //new mobapi 1.8

    protected ViewGroup mEditFormContainer;

    protected DynamicForm mEditFormGenerator;

    protected Form mFormResponse;

    protected ArrayList<AddressRegion> mRegions;

    protected Address mCurrentAddress;

    protected OrderSummary orderSummary;

    protected boolean isCityIdAnEditText = false;

    public EditAddressFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state) {
        super(enabledMenuItems, action, R.layout.checkout_edit_address_main, titleResId, adjust_state);
    }

    public EditAddressFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state, int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_edit_address_main, titleResId, adjust_state, titleCheckout);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
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
        setRetainInstance(true);
        Bundle params = new Bundle();
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_EDIT_ADDRESS);

        TrackerDelegator.trackCheckoutStep(params);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

        // Create address form
        mEditFormContainer = (ViewGroup) view.findViewById(R.id.checkout_edit_form_container);
        // Next button
        view.findViewById(R.id.checkout_edit_button_enter).setOnClickListener(this);
        // Cancel button
        view.findViewById(R.id.checkout_edit_button_cancel).setOnClickListener(this);

        //Validate current address, if null goto back
        if(mCurrentAddress == null)
            showFragmentErrorRetry();

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
        try {
            // Save values on current address
            if(mEditFormGenerator != null && mCurrentAddress != null) {
                // Get data from form
                ContentValues mContentValues = createContentValues(mEditFormGenerator);
                // Save it
                for (Map.Entry<String, Object> entry : mContentValues.valueSet()) {
                    try {
                        if(entry.getKey().contains(RestConstants.JSON_FIRST_NAME_TAG)) mCurrentAddress.setFirstName((String) entry.getValue());
                        else if(entry.getKey().contains(RestConstants.JSON_LAST_NAME_TAG)) mCurrentAddress.setLastName((String) entry.getValue());
                        else if(entry.getKey().contains(RestConstants.JSON_ADDRESS1_TAG)) mCurrentAddress.setAddress((String) entry.getValue());
                        else if(entry.getKey().contains(RestConstants.JSON_ADDRESS2_TAG)) mCurrentAddress.setAddress2((String) entry.getValue());
                        else if(entry.getKey().contains(RestConstants.JSON_PHONE_TAG)) mCurrentAddress.setPhone((String) entry.getValue());
                        // TODO alexandrapires: mobapi 1.8 change
                  //      else if(entry.getKey().contains(RestConstants.JSON_REGION_ID_TAG)) mCurrentAddress.setFkCustomerAddressRegion((Integer) entry.getValue());
                   //     else if(entry.getKey().contains(RestConstants.JSON_CITY_ID_TAG)) mCurrentAddress.setFkCustomerAddressCity(Integer.valueOf((String) entry.getValue()));
                  //      else if(entry.getKey().contains(RestConstants.JSON_REGION)) mCurrentAddress.setFkCustomerAddressRegion((Integer) entry.getValue());
                        else if(entry.getKey().contains(RestConstants.REGION)) mCurrentAddress.setRegion((String) entry.getValue());
                        else if (entry.getKey().contains(RestConstants.CITY)) mCurrentAddress.setCity((String) entry.getValue());
                    } catch (NumberFormatException e) {
                        Print.w(TAG, "INVALID FORMAT FOR REGION OR CITY", e);
                    }
                }
            }
        } catch (ClassCastException e) {
            Print.w(TAG, "INVALID CAST ON CREATE CONTENT VALUES", e);
        } catch (NullPointerException e) {
            Print.w(TAG, "SOME VIEW IS NULL", e);
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
        mEditFormContainer.addView(mEditFormGenerator.getContainer());
        mEditFormContainer.refreshDrawableState();
        // Validate Regions
        if(mRegions == null) {
            FormField field = form.getFieldKeyMap().get(RestConstants.REGION);
            String url = field.getDataCalls().get(RestConstants.API_CALL);
            triggerGetRegions(url);
        } else {
            Print.d(TAG, "REGIONS ISN'T NULL");
            setRegions(mEditFormGenerator, mRegions, mCurrentAddress);
        }
        // Define if CITY is a List or Text
        isCityIdAnEditText = (mEditFormGenerator.getItemByKey(RestConstants.CITY).getEditControl() instanceof EditText);
        // Hide check boxes
        // TODO hideSomeFields(mEditFormGenerator);
        // Show selected address content
        showSelectedAddress(mEditFormGenerator, mCurrentAddress);


        mEditFormContainer.refreshDrawableState();
    }

    /**
     * Show selected address
     */
    private void showSelectedAddress(DynamicForm dynamicForm, Address selectedAddress){
        // First name
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_FIRST_NAME_TAG).getEditControl()).setText(selectedAddress.getFirstName());
        // Last name
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_LAST_NAME_TAG).getEditControl()).setText(selectedAddress.getLastName());
        // Address 1
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_ADDRESS1_TAG).getEditControl()).setText(selectedAddress.getAddress());
        // Address 2
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_ADDRESS2_TAG).getEditControl()).setText(selectedAddress.getAddress2());
        // Phone
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_PHONE_TAG).getEditControl()).setText(selectedAddress.getPhone());
        // Additional phone
        DynamicFormItem additionalPhone = dynamicForm.getItemByKey(RestConstants.JSON_ADDITIONAL_PHONE_TAG);
        if(additionalPhone != null) {
            ((EditText) additionalPhone.getEditControl()).setText(selectedAddress.getAdditionalPhone());
        }
        // Set selected city
        View mControl = dynamicForm.getItemByKey(RestConstants.CITY).getControl();
        View mCityView = ((ViewGroup) mControl).getChildAt(0);
        if (mCityView instanceof RelativeLayout) {
            mCityView = ((RelativeLayout) mCityView).getChildAt(0);
            if (mCityView instanceof EditText) {
                EditText mCityEdit = (EditText) dynamicForm.getItemByKey(RestConstants.CITY).getEditControl();
                mCityEdit.setText(selectedAddress.getCity());
            }
        }
    }

    /**
     * Hide the default check boxes
     */
    private void hideSomeFields(DynamicForm dynamicForm){
        DynamicFormItem item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG);
        item.getEditControl().setVisibility(View.GONE);
        item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_BILLING_TAG);
        item.getEditControl().setVisibility(View.GONE);
        // TODO mobapi 1.8 change: now is always ON_CITY_TAG
        // When CITY_ID is EditText use CITY
 /*       if (isCityIdAnEditText) {
            item = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG);
            if (item != null) item.getControl().setVisibility(View.GONE);
        } else {*/
            // Use CITY_ID
            item = dynamicForm.getItemByKey(RestConstants.CITY);
            item.getControl().setVisibility(View.GONE);
     //   }
    }

    /**
     * Method used to set the regions on the respective form
     */
    private void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions, Address selectedAddress){
        Print.d(TAG, "SET REGIONS REGIONS: ");
        // Get region item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.REGION);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>( getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getRegionPosition(regions, selectedAddress));
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }

    /**
     * Get the position of the address region
     * @return int the position
     */
    private int getRegionPosition(ArrayList<AddressRegion> regions, Address selecedAddress){
        for (int i = 0; i < regions.size(); i++) {
            // TODO alexandrapires: changed mobapi 1.8
         //   if(regions.get(i).getId() == selecedAddress.getFkCustomerAddressRegion()) return i;
            if(String.valueOf(regions.get(i).getValue()) == selecedAddress.getRegion()) return i;
        }
        return 0;
    }

    /**
     * Validate the current region selection and update the cities
     */
    private void setCitiesOnSelectedRegion(ArrayList<AddressCity> cities, Address selectedAddress){
        setCities(mEditFormGenerator, cities, selectedAddress);
    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities, Address selectedAddress){
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.CITY);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>( getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getCityPosition(cities, selectedAddress));
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }

    /**
     * Get the position of the address city
     * @return int the position
     */
    private int getCityPosition(ArrayList<AddressCity> cities, Address selecedAddress){
        for (int i = 0; i < cities.size(); i++) {
            // TODO alexandrapires: changed in mobapi 1.8 //see this behaviour when form loads properly
        //    if(cities.get(i).getId() == selecedAddress.getFkCustomerAddressCity()) return i;
           // if(cities.get(i).getId() == selecedAddress.getCity()) return i;
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

        if (!mEditFormGenerator.validate()) {
            Print.i(TAG, "INVALID FORM");
            return;
        }

        /**
         *
         *
         // Validate mandatory spinner
         ViewGroup mRegionGroup = (ViewGroup) mEditFormGenerator.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
         // Validate if region group is filled
         if(!(mRegionGroup.getChildAt(0) instanceof IcsSpinner)) {
         Log.w(TAG, "REGION SPINNER NOT FILL YET");
         return;
         };
         *
         */

        triggerEditAddress(createContentValues(mEditFormGenerator));
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
//        // Save content values
//        ContentValues mContentValues = dynamicForm.save();
//        // Update default values (unknown keys)
//        for (Map.Entry<String, Object> value : mContentValues.valueSet()) {
//            String key = value.getKey();
//            if (key.contains(RestConstants.JSON_IS_DEFAULT_BILLING_TAG)) {
//                mContentValues.put(key, isDefaultBilling);
//            } else if (key.contains(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG)) {
//                mContentValues.put(key, isDefaultShipping);
//            }
//        }
        // return the new content values
        return dynamicForm.save();
    }

//    /**
//     * Method used to create the content values
//     * @return new content values
//     */
//    private ContentValues createContentValues(DynamicForm dynamicForm){
//        // Save content values
//        ContentValues mContentValues = dynamicForm.save();
//        // Get the region
//        // TODO alexandrapires: mobapi 1.8 change
//    //    ViewGroup mRegionGroup = (ViewGroup) dynamicForm.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
//        ViewGroup mRegionGroup = (ViewGroup) dynamicForm.getItemByKey(RestConstants.REGION).getControl();
//        IcsSpinner mRegionSpinner = (IcsSpinner) mRegionGroup.getChildAt(0);
//        AddressRegion mSelectedRegion = (AddressRegion) mRegionSpinner.getSelectedItem();
//        Print.d(TAG, "SELECTED REGION: " + mSelectedRegion.getLabel() + " " + mSelectedRegion.getValue());
//
//        // Save city
//        String mCityId = "";
//        String mCityName = "";
//        // Get from spinner
//        // TODO alexandrapires: mobapi 1.8
//  //   View mControl = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getControl();
//        View mControl = dynamicForm.getItemByKey(RestConstants.CITY).getControl();
//        View mCityView = ((ViewGroup) mControl).getChildAt(0);
//        if (mCityView instanceof IcsSpinner) {
//            IcsSpinner mCitySpinner = (IcsSpinner) mCityView;
//            // Get selected city
//            AddressCity mSelectedCity = (AddressCity) mCitySpinner.getSelectedItem();
//            Print.d(TAG, "SELECTED CITY: " + mSelectedCity.getLabel() + " " + mSelectedCity.getValue());
//            mCityId = "" + mSelectedCity.getValue();
//            mCityName = mSelectedCity.getLabel();
//        }
//        // Get from edit text
//        else if (mCityView instanceof RelativeLayout) {
//            /*-mCityView = ((RelativeLayout) mCityView).getChildAt(0);
//            if (mCityView instanceof EditText) {
//                EditText mCityEdit = (EditText) dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getEditControl();
//                mCityName = mCityEdit.getText().toString();
//                Log.d(TAG, "SELECTED CITY: " + mCityName);
//            }*/
//        }
//        // Unexpected
//        else {
//        //    Print.w(TAG, "WARNING: THE " + RestConstants.JSON_CITY_ID_TAG + " IS AN UNEXPECTED VIEW");
//            //mobapi 1.8 change
//            Print.w(TAG, "WARNING: THE " + RestConstants.CITY + " IS AN UNEXPECTED VIEW");
//        }
//
//        // Get some values
//        int mAddressId = mCurrentAddress.getId();
//        int mRegionId = mSelectedRegion.getValue();
//        String isDefaultBilling = (mCurrentAddress.isDefaultBilling()) ? "1" : "0";
//        String isDefaultShipping = (mCurrentAddress.isDefaultShipping()) ? "1" : "0";
//        // Put values
//
//        for (Map.Entry<String, Object> entry : mContentValues.valueSet()) {
//            if(entry.getKey().contains(RestConstants.JSON_ADDRESS_ID_TAG)) mContentValues.put(entry.getKey(), mAddressId);
//            else if(entry.getKey().contains(RestConstants.JSON_IS_DEFAULT_BILLING_TAG)) mContentValues.put(entry.getKey(), isDefaultBilling);
//            else if(entry.getKey().contains(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG)) mContentValues.put(entry.getKey(), isDefaultShipping);
//          //  TODO alexandrapires: mobapi 1.8
//         //   else if(entry.getKey().contains(RestConstants.JSON_REGION_ID_TAG)) mContentValues.put(entry.getKey(), mRegionId);
//         //   else if(entry.getKey().contains(RestConstants.JSON_CITY_ID_TAG)) mContentValues.put(entry.getKey(), mCityId);
//            else if(entry.getKey().contains(RestConstants.REGION)) mContentValues.put(entry.getKey(), mRegionId);
//            else if(!isCityIdAnEditText && entry.getKey().contains(RestConstants.CITY)) mContentValues.put(entry.getKey(), mCityName);
//        }
//
//        Print.d(TAG, "CURRENT CONTENT VALUES: " + mContentValues.toString());
//        // return the new content values
//        return mContentValues;
//    }

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
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.CITY);
            if (InputType.list == field.getInputType()) {
                // Get API call
                String url = field.getDataCalls().get(RestConstants.API_CALL);
                // Request the cities for this region id
                int regionId = ((AddressRegion) object).getValue();
                // Get cities
                triggerGetCities(url, regionId);
            }
            // TODO
            else if (InputType.text == field.getInputType()) {
                // Show
                showFragmentContentContainer();
            } else {
                // Show
                showFragmentContentContainer();
                Print.e(TAG, RestConstants.API_CALL + " with an expected inputType");
                super.showUnexpectedErrorWarning();
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
        triggerContentEvent(new UpdateAddressHelper(), bundle, this);
        // Hide the keyboard
        getBaseActivity().hideKeyboard();
    }

    /**
     * Trigger to get the address form
     */
    protected void triggerEditAddressForm(){
        Print.i(TAG, "TRIGGER: EDIT FORM");

        // change mobapi 1.8: pass address id as argument
        ContentValues cv = new ContentValues();
        cv.put(EditAddressFragment.SELECTED_ADDRESS_ID, mCurrentAddress.getId());
        Bundle arg = new Bundle();
        arg.putParcelable(Constants.BUNDLE_DATA_KEY, cv);

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
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
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
                orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
                // Form
                Form form = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                mFormResponse = form;
                // Load form, get regions
                loadEditAddressForm(form);
                break;
            case GET_REGIONS_EVENT:
                Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
                mRegions = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
                if (CollectionUtils.isNotEmpty(mRegions)) {
                    setRegions(mEditFormGenerator, mRegions, mCurrentAddress);
                } else {
                    Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
                    super.showFragmentErrorRetry();
                }
                break;
            case GET_CITIES_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                ArrayList<AddressCity> cities = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
                setCitiesOnSelectedRegion(cities, mCurrentAddress);
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
    protected boolean onErrorEvent(Bundle bundle) {

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        if(isOnStoppingProcess || eventType == null){
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }

        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
            case INIT_FORMS:
                onInitFormsErrorEvent();
                break;
            case GET_EDIT_ADDRESS_FORM_EVENT:
                onGetEditAddressFormErrorEvent(bundle);
                break;
            case GET_REGIONS_EVENT:
                onGetRegionsErrorEvent(bundle);
                break;
            case GET_CITIES_EVENT:
                onGetCitiesErrorEvent(bundle);
                break;
            case EDIT_ADDRESS_EVENT:
                onEditAddressErrorEvent(bundle);
                break;
            default:
                break;
        }

        return false;
    }

    protected void onInitFormsErrorEvent(){
        Print.d(TAG, "RECEIVED INIT_FORMS");
    }

    protected void onGetEditAddressFormErrorEvent(Bundle bundle){
        Print.w(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
    }

    protected void onGetRegionsErrorEvent(Bundle bundle){
        Print.w(TAG, "RECEIVED GET_REGIONS_EVENT");
    }

    protected void onGetCitiesErrorEvent(Bundle bundle){
        Print.w(TAG, "RECEIVED GET_CITIES_EVENT");
    }

    protected void onEditAddressErrorEvent(Bundle bundle){
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
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }

    /**
     * ########### DIALOGS ###########
     */
    /**
     * Dialog used to show an error
     */
    protected void showErrorDialog(HashMap<String, List<String>> errors) {
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

