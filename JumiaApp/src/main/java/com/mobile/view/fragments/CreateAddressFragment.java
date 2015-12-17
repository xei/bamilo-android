package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ScrollView;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.address.CreateAddressHelper;
import com.mobile.helpers.address.GetCitiesHelper;
import com.mobile.helpers.address.GetFormAddAddressHelper;
import com.mobile.helpers.address.GetPostalCodeHelper;
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.AddressForms;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.objects.addresses.AddressCity;
import com.mobile.newFramework.objects.addresses.AddressPostalCode;
import com.mobile.newFramework.objects.addresses.AddressRegion;
import com.mobile.newFramework.objects.addresses.AddressRegions;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential.
 *
 * @author ricardo.soares
 * @version 1.0
 * @date 2015/02/24
 */
public abstract class CreateAddressFragment extends BaseFragment implements IResponseCallback, IcsAdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = CreateAddressFragment.class.getSimpleName();

    private static final int IS_DEFAULT_SHIPPING_ADDRESS = 1;

    protected static final int IS_DEFAULT_BILLING_ADDRESS = 1;

    protected static final int ISNT_DEFAULT_SHIPPING_ADDRESS = 0;

    private static final int ISNT_DEFAULT_BILLING_ADDRESS = 0;

    protected ViewGroup mShippingFormContainer;
    protected DynamicForm shippingFormGenerator;
    protected Form mFormShipping;
    protected Form mFormBilling;
    protected ViewGroup mBillingIncludeContainer;
    protected ViewGroup mBillingFormContainer;
    protected DynamicForm billingFormGenerator;
    protected ArrayList<AddressRegion> regions;
    protected String selectedRegionOnShipping = "";
    protected String selectedRegionOnBilling = "";
    protected String selectedCityOnShipping = "";
    protected String selectedCityOnBilling = "";
    protected CheckBox mIsSameCheckBox;
    protected TextView mShippingTitle;

    protected Boolean oneAddressCreated = false;
    protected boolean isCityIdAnEditText = false;
    protected ScrollView mScrollViewContainer;

    protected PurchaseEntity orderSummary;

    private Bundle mBillingFormSavedState;

    private Bundle mShippingFormSavedState;

    private static final String SHIPPING_SAVED_STATE = "shippingSavedStateBundle";

    private static final String BILLING_SAVED_STATE = "billingSavedStateBundle";

    private Bundle mSavedRegionCitiesPositions;

    private static final String REGION_CITIES_POSITIONS = "regionsCitiesBundle";

    private static final String BILLING_TAG = "billing";

    private static final String SHIPPING_TAG = "shipping";

    public CreateAddressFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, int titleResId, @KeyboardState int adjust_state) {
        super(enabledMenuItems, action, R.layout.checkout_create_address_main, titleResId, adjust_state);
    }

    public CreateAddressFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, int titleResId, @KeyboardState int adjust_state, int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_create_address_main, titleResId, adjust_state, titleCheckout);
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
        // Validate the saved values
        if (savedInstanceState != null) {
            // Get the ship content values
            mShippingFormSavedState = savedInstanceState.getParcelable(SHIPPING_SAVED_STATE);
            // Get the bill content values
            mBillingFormSavedState = savedInstanceState.getParcelable(BILLING_SAVED_STATE);
            // Get region and Cities positions
            mSavedRegionCitiesPositions = savedInstanceState.getBundle(REGION_CITIES_POSITIONS);
        } else {
            Print.i(TAG, "SAVED CONTENT VALUES IS NULL");
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Scroll view
        mScrollViewContainer = (ScrollView) view.findViewById(R.id.checkout_address_form_scroll);
        // Shipping title
        mShippingTitle = (TextView) view.findViewById(R.id.checkout_address_form_shipping_title);
        // Shipping form
        mShippingFormContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_shipping_container);
        // Billing container
        mBillingIncludeContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_include_billing);
        // Billing form
        mBillingFormContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_billing_container);
        // Billing check box
        mIsSameCheckBox = (CheckBox) view.findViewById(R.id.checkout_address_billing_checkbox);
        mIsSameCheckBox.setOnCheckedChangeListener(this);
        mIsSameCheckBox.setChecked(true);
        // Next button
        view.findViewById(R.id.checkout_button_enter).setOnClickListener(this);
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
        TrackerDelegator.trackPage(TrackingPage.NEW_ADDRESS, getLoadTime(), true);
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
            // Validate check
            Bundle shippingSavedStateBundle = new Bundle();
            shippingFormGenerator.saveFormState(shippingSavedStateBundle);
            outState.putParcelable(SHIPPING_SAVED_STATE, shippingSavedStateBundle);
            Bundle listPositions = new Bundle();
            saveRegionsCitiesPositions(SHIPPING_TAG, listPositions, shippingSavedStateBundle);
            if (!mIsSameCheckBox.isChecked()) {
                Bundle billingSavedStateBundle = new Bundle();
                billingFormGenerator.saveFormState(billingSavedStateBundle);
                outState.putParcelable(BILLING_SAVED_STATE, billingSavedStateBundle);
                saveRegionsCitiesPositions(BILLING_TAG, listPositions, billingSavedStateBundle);
            }
            outState.putBundle(REGION_CITIES_POSITIONS, listPositions);
        } catch (ClassCastException e) {
            Print.w(TAG, "INVALID CAST ON CREATE CONTENT VALUES", e);
        } catch (NullPointerException e) {
            Print.w(TAG, "SOME VIEW IS NULL", e);
        }

    }

    /**
     * Method that saves the selected positions of the regions/cities/postalCode
     */
    private void saveRegionsCitiesPositions(String tag, Bundle listPositions, Bundle addressSavedStateBundle) {
        listPositions.putInt(tag + RestConstants.REGION, addressSavedStateBundle.getInt(RestConstants.REGION));
        listPositions.putInt(tag + RestConstants.CITY, addressSavedStateBundle.getInt(RestConstants.CITY));
        listPositions.putInt(tag + RestConstants.POSTCODE, addressSavedStateBundle.getInt(RestConstants.POSTCODE));
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
        if(shippingFormGenerator != null) {
            Bundle shippingSavedStateBundle = new Bundle();
            shippingFormGenerator.saveFormState(shippingSavedStateBundle);
            mShippingFormSavedState = shippingSavedStateBundle;
        }
        if (!mIsSameCheckBox.isChecked()) {
            if(billingFormGenerator != null) {
                Bundle billingSavedStateBundle = new Bundle();
                billingFormGenerator.saveFormState(billingSavedStateBundle);
                mBillingFormSavedState = billingSavedStateBundle;
            }
        }

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
        regions = null;
        oneAddressCreated = false;
        mFormShipping = null;
        mFormBilling = null;
    }


    /**
     * Load the dynamic form
     */
    protected void loadCreateAddressForm(Form mFormShipping, Form mFormBilling) {
        Print.i(TAG, "LOAD CREATE ADDRESS FORM");
        // Shipping form
        if(shippingFormGenerator == null){
            Print.i(TAG,"null");
            shippingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), mFormShipping);
            mShippingFormContainer.removeAllViews();
            mShippingFormContainer.addView(shippingFormGenerator.getContainer());
            mShippingFormContainer.refreshDrawableState();
        } else {
            if(mShippingFormContainer.getChildCount() == 0){
                // Have to create set a Dynamic form in order to not have the parent dependencies.
                // this happens when user goes from create address to another screen through the overflow menu, and presses back.
                // Error: The specified child already has a parent. You must call removeView() on the child's parent first.
                shippingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), mFormShipping);
                mShippingFormContainer.addView(shippingFormGenerator.getContainer());
                mShippingFormContainer.refreshDrawableState();
            }
        }

        // Billing form
        if(billingFormGenerator == null){
            billingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), mFormBilling);
            mBillingFormContainer.removeAllViews();
            mBillingFormContainer.addView(billingFormGenerator.getContainer());
            mBillingFormContainer.refreshDrawableState();
        } else {
            if(mBillingFormContainer.getChildCount() == 0){
                // Have to create set a Dynamic form in order to not have the parent dependencies
                // this happens when user goes from create address to another screen through the overflow menu, and presses back.
                // Error: The specified child already has a parent. You must call removeView() on the child's parent first.
                billingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), mFormBilling);
                mBillingFormContainer.addView(billingFormGenerator.getContainer());
                mBillingFormContainer.refreshDrawableState();
            }
        }

        // Define if CITY is a List or Text
        isCityIdAnEditText = (shippingFormGenerator.getItemByKey(RestConstants.CITY).getEditControl() instanceof EditText);
        // Hide unused fields form
        hideSomeFields(shippingFormGenerator, false);
        hideSomeFields(billingFormGenerator, true);
        // Validate Regions
        if (regions == null) {
            FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.REGION);
            triggerGetRegions(field.getApiCall());
        } else {
            setRegions(shippingFormGenerator, regions, SHIPPING_TAG);
            setRegions(billingFormGenerator, regions, BILLING_TAG);
        }
        // Load the saved shipping values
        shippingFormGenerator.loadSaveFormState(mShippingFormSavedState);
        billingFormGenerator.loadSaveFormState(mBillingFormSavedState);
    }


    /**
     * Hide the default check boxes
     */
    private void hideSomeFields(DynamicForm dynamicForm, boolean isBilling) {
        DynamicFormItem item = dynamicForm.getItemByKey(RestConstants.IS_DEFAULT_SHIPPING);
        if (item != null) {
            item.getEditControl().setVisibility(View.GONE);
        }
        item = dynamicForm.getItemByKey(RestConstants.IS_DEFAULT_BILLING);
        if (item != null) {
            item.getEditControl().setVisibility(View.GONE);
        }

        // Hide the gender field only for billing address
        if (isBilling) {
            try {
                item = dynamicForm.getItemByKey(RestConstants.GENDER);
                if (item != null) {
                    item.getMandatoryControl().setVisibility(View.GONE);
                    item.getEditControl().setVisibility(View.GONE);
                    item.getControl().setVisibility(View.GONE);
                }
            } catch (NullPointerException e) {
                Print.w(TAG, "WARNING: NPE ON TRY HIDE THE GENDER IN BILLING ADDRESS");
            }
        }
    }

    /**
     * Method used to set the regions on the respective form
     */
    protected void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions, String tag) {
        // Get region item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.REGION);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setSavedSelectedRegionPos(spinner, tag);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        v.setEditControl(spinner);
        group.addView(spinner);
        // Show invisible content to trigger spinner listeners
        showGhostFragmentContentContainer();
    }

    /**
     * Load and set the saved region position
     */
    private void setSavedSelectedRegionPos(IcsSpinner spinner, String tag) {
        // Get saved value
        int position = IntConstants.INVALID_POSITION;
        if (mSavedRegionCitiesPositions != null) {
            position = mSavedRegionCitiesPositions.getInt(tag + RestConstants.REGION);
        }

        if (position != IntConstants.INVALID_POSITION && spinner.getCount() > 0 && position <= spinner.getCount()) {
            if (tag.equals(SHIPPING_TAG)) {
                spinner.setSelection(position);
            } else {
                spinner.setSelection(position);
            }
        }
    }

    /**
     * Validate the current region selection and update the cities
     */
    protected void setCitiesOnSelectedRegion(String requestedRegionAndFields, ArrayList<AddressCity> cities) {
        if (requestedRegionAndFields.equals(selectedRegionOnShipping)) {
            setCities(shippingFormGenerator, cities, SHIPPING_TAG);
        }
        if (requestedRegionAndFields.equals(selectedRegionOnBilling)) {
            setCities(billingFormGenerator, cities, BILLING_TAG);
        }
    }

    /**
     * Validate the current city selection and update the postal codes
     */
    protected void setPostalCodesOnSelectedCity(String requestedCityAndFields, ArrayList<AddressPostalCode> postalCodes) {
        if (requestedCityAndFields.equals(selectedCityOnShipping)) {
            setPostalCodes(shippingFormGenerator, postalCodes, SHIPPING_TAG);
        }
        if (requestedCityAndFields.equals(selectedCityOnBilling)) {
            setPostalCodes(billingFormGenerator, postalCodes, BILLING_TAG);
        }
    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities, String tag) {
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.CITY);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setSavedSelectedCityPos(spinner, tag);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        v.setEditControl(spinner);
        group.addView(spinner);
    }

    /**
     * Method used to set the postal Codes on the respective form
     */
    private void setPostalCodes(DynamicForm dynamicForm, ArrayList<AddressPostalCode> postalCodes, String tag) {
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.POSTCODE);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, postalCodes);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setSavedSelectedPostalCodePos(spinner, tag);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        v.setEditControl(spinner);
        group.addView(spinner);
    }

    /**
     * Load and set the saved city position one time
     */
    private void setSavedSelectedCityPos(IcsSpinner spinner, String tag) {
        int position = IntConstants.INVALID_POSITION;
        if (mSavedRegionCitiesPositions != null) {
            position = mSavedRegionCitiesPositions.getInt(tag + RestConstants.CITY);
        }

        if (position != IntConstants.INVALID_POSITION && spinner.getCount() > 0 && position <= spinner.getCount()) {
            if (tag.equals(SHIPPING_TAG)) {
                spinner.setSelection(position);
            } else {
                spinner.setSelection(position);
            }
        }
    }

    /**
     * Load and set the postal code position one time
     *
     */
    private void setSavedSelectedPostalCodePos(IcsSpinner spinner, String tag) {
        int position = IntConstants.INVALID_POSITION;
        if (mSavedRegionCitiesPositions != null) {
            position = mSavedRegionCitiesPositions.getInt(tag + RestConstants.POSTCODE);
        }

        if (position != IntConstants.INVALID_POSITION && spinner.getCount() > 0 && position <= spinner.getCount()) {
            if (tag.equals(SHIPPING_TAG)) {
                spinner.setSelection(position);
            } else {
                spinner.setSelection(position);
            }
        }

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
        if (id == R.id.checkout_button_enter) {
            onClickCreateAddressButton();
        }
        // Unknown view
        else {
            Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickRetryButton();
    }

    /**
     * Process the click on retry button.
     *
     * @author paulo
     */
    protected abstract void onClickRetryButton();

    /**
     * Process the click on the next step button
     *
     * @author sergiopereira
     */
    private void onClickCreateAddressButton() {
        Print.i(TAG, "ON CLICK: CREATE");

        // Clean the flag for each click
        oneAddressCreated = false;

        if(mIsSameCheckBox.isChecked()){
            if(!shippingFormGenerator.validate()){
                Print.i(TAG, "SAME FORM: INVALID");
                return;
            }
        } else {
            validateSameGender();
            if (!shippingFormGenerator.validate() | !billingFormGenerator.validate()) {
                Print.i(TAG, "SHIP OR BILL FORM: INVALID");
                return;
            }
        }

        // Validate check
        ContentValues mContentValues;
        int isBilling = mIsSameCheckBox.isChecked() ? IS_DEFAULT_BILLING_ADDRESS : ISNT_DEFAULT_BILLING_ADDRESS;
        mContentValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, isBilling);
        Print.d(TAG, "CONTENT VALUES: " + mContentValues);
        triggerCreateAddress(shippingFormGenerator.getForm().getAction(), mContentValues);
    }

    /**
     * method that controls that the addresses have the same gender when creating billing and shipping at the same time
     */
    private void validateSameGender() {
        DynamicFormItem shippingGenderItem = shippingFormGenerator.getItemByKey(RestConstants.GENDER);
        DynamicFormItem billingGenderItem = billingFormGenerator.getItemByKey(RestConstants.GENDER);
        if (shippingGenderItem != null && billingGenderItem != null) {
            try {
                int genderIndex = -1;
                RadioGroupLayout group1 = (RadioGroupLayout) shippingGenderItem.getEditControl().findViewById(R.id.radio_group_container);
                if (group1.getChildCount() > 0) {
                    // Get selected gender index from the shipping form
                    genderIndex = group1.getSelectedIndex();
                }
                RadioGroupLayout group2 = (RadioGroupLayout) billingGenderItem.getEditControl().findViewById(R.id.radio_group_container);
                if (group2.getChildCount() > 0 && genderIndex != -1) {
                    // Set the billing gender with the same as the shipping
                    group2.setSelection(genderIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used to create the content values
     *
     * @return new content values
     * @author sergiopereira
     */
    protected ContentValues createContentValues(DynamicForm dynamicForm, int isDefaultShipping, int isDefaultBilling) {
        // Save content values
        ContentValues mContentValues = dynamicForm.save();
        // Update default values (unknown keys)
        for (Map.Entry<String, Object> value : mContentValues.valueSet()) {
            String key = value.getKey();
            if (key.contains(RestConstants.IS_DEFAULT_BILLING)) {
                mContentValues.put(key, isDefaultBilling);
            } else if (key.contains(RestConstants.IS_DEFAULT_SHIPPING)) {
                mContentValues.put(key, isDefaultShipping);
            }
        }
        // return the new content values
        return mContentValues;
    }

    /**
     * ########### ON ITEM SELECTED LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onNothingSelected(com.mobile.components.absspinner.IcsAdapterView)
     */
    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) {
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onItemSelected(com.mobile.components.absspinner.IcsAdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        Print.d(TAG, "CURRENT TAG: " + parent.getTag());
        Object object = parent.getItemAtPosition(position);
        if (object instanceof AddressRegion) {
            // Get city field
            FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.CITY);
            // Case list
            if (FormInputType.list == field.getInputType()) {
                // Request the cities for this region id
                int regionId = ((AddressRegion) object).getValue();
                // Save the selected region on the respective variable
                String tag = (parent.getTag() != null) ? parent.getTag().toString() : "";
                if (tag.equals(SHIPPING_TAG)) {
                    selectedRegionOnShipping = SHIPPING_TAG + "_" + regionId;
                    triggerGetCities(field.getApiCall(), regionId, selectedRegionOnShipping);
                } else if (tag.equals(BILLING_TAG)) {
                    selectedRegionOnBilling = BILLING_TAG + "_" + regionId;
                    triggerGetCities(field.getApiCall(), regionId, selectedRegionOnBilling);
                }
            }
            // Case text or other
            else {
                showFragmentContentContainer();
            }
        } else if (object instanceof AddressCity){

            // Get city field
            FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.POSTCODE);
            // Case list
            if (field != null && FormInputType.list == field.getInputType()) {
                // Request the postal codes for this city id
                int cityId = ((AddressCity) object).getValue();
                // Save the selected city on the respective variable
                String tag = (parent.getTag() != null) ? parent.getTag().toString() : "";
                if (tag.equals(SHIPPING_TAG)) {
                    selectedCityOnShipping = SHIPPING_TAG + "_" + cityId;
                    triggerGetPostalCodes(field.getApiCall(), cityId, selectedCityOnShipping);
                } else if (tag.equals(BILLING_TAG)) {
                    selectedCityOnBilling = BILLING_TAG + "_" + cityId;
                    triggerGetPostalCodes(field.getApiCall(), cityId, selectedCityOnBilling);
                }
            }
        }
    }

    /**
     * ########### ON CHECK CHANGE LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Validate if is billing address
        updateContainers(isChecked);
    }

    /**
     * Update the container according with check box
     * @author sergiopereira
     */
    private void updateContainers(Boolean isSame) {
        // Validate if is billing address
        if (isSame) {
            // Set title
            mShippingTitle.setText(getString(R.string.action_label_add_address));
            // Hide billing container
            if (mBillingIncludeContainer != null) {
                mBillingIncludeContainer.setVisibility(View.GONE);
            }
        } else {
            try {
                // make the scroll move downward 200px just to let the user see the new billing address form
                mScrollViewContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollViewContainer.smoothScrollBy(0, 200);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Set title
            mShippingTitle.setText(getString(R.string.billing_shipping_label));
            // Hide billing container
            mBillingIncludeContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to create an address
     * @author sergiopereira
     */
    protected void triggerCreateAddress(String action, ContentValues values) {
        Print.i(TAG, "TRIGGER: CREATE ADDRESS");
        triggerContentEvent(new CreateAddressHelper(), CreateAddressHelper.createBundle(action, values), this);
        // Hide the keyboard
        getBaseActivity().hideKeyboard();
    }

    /**
     * Trigger to get the address form
     *
     * @author sergiopereira
     */
    protected void triggerCreateAddressForm() {
        Print.i(TAG, "TRIGGER: CREATE ADDRESS FORM");
        triggerContentEvent(new GetFormAddAddressHelper(), null, this);
    }

    /**
     * Trigger to get regions
     * @author sergiopereira
     */
    protected void triggerGetRegions(String url) {
        Print.i(TAG, "TRIGGER: GET REGIONS: " + url);
        triggerContentEventNoLoading(new GetRegionsHelper(), GetRegionsHelper.createBundle(url), this);
    }

    /**
     * Trigger to get cities
     * @author sergiopereira
     */
    protected void triggerGetCities(String url, int region, String tag) {
        Print.i(TAG, "TRIGGER: GET CITIES: " + url + " " + tag);
        triggerContentEvent(new GetCitiesHelper(), GetCitiesHelper.createBundle(url, region, tag), this);
    }

    /**
     * Trigger to get postal codes
     *
     */
    protected void triggerGetPostalCodes(String action, int city, String tag) {
        Print.i(TAG, "TRIGGER: GET POSTAL CODES: " + city + " " + tag);
        triggerContentEvent(new GetPostalCodeHelper(), GetPostalCodeHelper.createBundle(action, city, tag), this);
    }

    /**
     * ############# RESPONSE #############
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        switch (eventType) {
            case GET_CREATE_ADDRESS_FORM_EVENT:
                onGetCreateAddressFormSuccessEvent(baseResponse);
                break;
            case GET_REGIONS_EVENT:
                onGetRegionsSuccessEvent(baseResponse);
                break;
            case GET_CITIES_EVENT:
                onGetCitiesSuccessEvent(baseResponse);
                break;
            case GET_POSTAL_CODE_EVENT:
                onGetPostalCodesSuccessEvent(baseResponse);
                break;
            case CREATE_ADDRESS_SIGNUP_EVENT:
            case CREATE_ADDRESS_EVENT:
                onCreateAddressSuccessEvent(baseResponse);
                break;
            default:
                break;
        }
    }

    protected void onGetCreateAddressFormSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
        // Get order summary
        //orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
        orderSummary = JumiaApplication.INSTANCE.getCart();
        // Save and load form
        AddressForms form = (AddressForms) baseResponse.getContentData();
        mFormShipping = form.getShippingForm();
        mFormBilling = form.getBillingForm();
        // Load form, get regions
        loadCreateAddressForm(mFormShipping, mFormBilling);
    }

    protected void onGetRegionsSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
        regions = (AddressRegions) baseResponse.getContentData();
        // Validate response
        if (CollectionUtils.isNotEmpty(regions)) {
            setRegions(shippingFormGenerator, regions, SHIPPING_TAG);
            setRegions(billingFormGenerator, regions, BILLING_TAG);
        } else {
            Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
            super.showFragmentErrorRetry();
        }
    }

    protected void onGetCitiesSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
        ArrayList<AddressCity> citiesArray = (GetCitiesHelper.AddressCitiesStruct)baseResponse.getContentData();
        GetCitiesHelper.AddressCitiesStruct cities= (GetCitiesHelper.AddressCitiesStruct)citiesArray;
        String requestedRegionAndField = cities.getCustomTag();
        Print.d(TAG, "REQUESTED REGION FROM FIELD: " + requestedRegionAndField);
        setCitiesOnSelectedRegion(requestedRegionAndField, cities);
        FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.POSTCODE);
        if(field == null){
            showFragmentContentContainer();
            Print.i(TAG, "DOES NOT HAVE POSTAL CODE");
        }
    }

    protected void onGetPostalCodesSuccessEvent(BaseResponse baseResponse) {
        GetPostalCodeHelper.AddressPostalCodesStruct postalCodesStruct = (GetPostalCodeHelper.AddressPostalCodesStruct) baseResponse.getContentData();
        Print.d(TAG, "RECEIVED GET_POSTAL_CODES_EVENT");
        Print.d(TAG, "REQUESTED CITY FROM FIELD: " + postalCodesStruct.getCustomTag());
        String requestedRegionAndField = postalCodesStruct.getCustomTag();
        setPostalCodesOnSelectedCity(requestedRegionAndField, postalCodesStruct);
        showFragmentContentContainer();
    }

    protected void onCreateAddressSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
    }

    /**
     * Filter the error response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();

        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }

        switch (eventType) {
            case GET_CREATE_ADDRESS_FORM_EVENT:
                onGetCreateAddressFormErrorEvent(baseResponse);
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
            case CREATE_ADDRESS_SIGNUP_EVENT:
            case CREATE_ADDRESS_EVENT:
                onCreateAddressErrorEvent(baseResponse);
                break;
            default:
                break;
        }
    }

    protected void onGetCreateAddressFormErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_REGIONS_EVENT");
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_CITIES_EVENT");
    }

    protected void onGetPostalCodesErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_POSTAL_CODES_EVENT");
    }

    protected void onCreateAddressErrorEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
        // Clean flag to wait for both different responses
        oneAddressCreated = false;
    }
}
