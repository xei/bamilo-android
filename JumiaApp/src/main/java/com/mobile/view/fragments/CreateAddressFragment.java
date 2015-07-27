package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
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
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.helpers.configs.GetInitFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.InputType;
import com.mobile.newFramework.objects.addresses.AddressCity;
import com.mobile.newFramework.objects.addresses.AddressRegion;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Iterator;
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

    protected static final String SHIPPING_FORM_TAG = "shipping";

    protected static final String BILLING_FORM_TAG = "billing";

    private static final String SHIPPING_REGION_POS = "save_shipping_rg_position";

    private static final String BILLING_REGION_POS = "save_billing_rg_position";

    private static final String SHIPPING_CITY_POS = "save_shipping_ct_position";

    private static final String BILLING_CITY_POS = "save_billing_ct_position";

    private static final String SHIPPING_STATE = "shipping_values";

    private static final String BILLING_STATE = "billing_values";

    private static final int IS_DEFAULT_SHIPPING_ADDRESS = 1;

    protected static final int IS_DEFAULT_BILLING_ADDRESS = 1;

    protected static final int ISNT_DEFAULT_SHIPPING_ADDRESS = 0;

    private static final int ISNT_DEFAULT_BILLING_ADDRESS = 0;

    private static final long ERROR_DELAY = 5000;

    protected ViewGroup mShippingFormContainer;
    protected DynamicForm shippingFormGenerator;
    protected Form mFormResponse;
    protected ViewGroup mBillingIncludeContainer;
    protected ViewGroup mBillingFormContainer;
    protected DynamicForm billingFormGenerator;
    protected ArrayList<AddressRegion> regions;
    protected String selectedRegionOnShipping = "";
    protected String selectedRegionOnBilling = "";
    protected CheckBox mIsSameCheckBox;
    protected TextView mShippingTitle;

    protected Boolean oneAddressCreated = false;
    protected ContentValues mShippingSavedValues;
    protected ContentValues mBillingSavedValues;
    protected boolean isCityIdAnEditText = false;
    protected ScrollView mScrollViewContainer;


    public CreateAddressFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state) {
        super(enabledMenuItems, action, R.layout.checkout_create_address_main, titleResId, adjust_state);
    }

    public CreateAddressFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state, int titleCheckout) {
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
            mShippingSavedValues = savedInstanceState.getParcelable(SHIPPING_STATE);
            // Get the bill content values
            mBillingSavedValues = savedInstanceState.getParcelable(BILLING_STATE);
            //Log.d(TAG, "SAVED CONTENT VALUES: " + mShippingSavedValues.toString());
            //Log.d(TAG, "SAVED CONTENT VALUES: " + ((mBillingSavedValues!= null) ? mBillingSavedValues.toString() : "IS NULL") );
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
            if (mIsSameCheckBox.isChecked()) {
                ContentValues mContentValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                Print.d(TAG, "CONTENT SHIP VALUES: " + mContentValues.toString());
                outState.putParcelable(SHIPPING_STATE, mContentValues);
            } else {
                ContentValues mShipValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, ISNT_DEFAULT_BILLING_ADDRESS);
                Print.d(TAG, "CONTENT SHIP VALUES: " + mShipValues.toString());
                ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                Print.d(TAG, "CONTENT BILL VALUES: " + mBillValues.toString());

                outState.putParcelable(SHIPPING_STATE, mShipValues);
                outState.putParcelable(BILLING_STATE, mBillValues);
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
        regions = null;
        oneAddressCreated = false;
    }


    /**
     * Load the dynamic form
     *
     * @param form
     * @author sergiopereira
     */
    protected void loadCreateAddressForm(Form form) {
        Print.i(TAG, "LOAD CREATE ADDRESS FORM");
        // Shipping form
        shippingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), form);
        mShippingFormContainer.removeAllViews();
        mShippingFormContainer.addView(shippingFormGenerator.getContainer());
        mShippingFormContainer.refreshDrawableState();
        // Billing form
        billingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), form);
        mBillingFormContainer.removeAllViews();
        mBillingFormContainer.addView(billingFormGenerator.getContainer());
        mBillingFormContainer.refreshDrawableState();
        // Define if CITY is a List or Text
        isCityIdAnEditText = (shippingFormGenerator.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getEditControl() instanceof EditText);
        // Hide unused fields form
        hideSomeFields(shippingFormGenerator, false);
        hideSomeFields(billingFormGenerator, true);
        // Validate Regions
        if (regions == null) {
            FormField field = form.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG);
            String url = field.getDataCalls().get(RestConstants.JSON_API_CALL_TAG);
            triggerGetRegions(url);
        } else {
            setRegions(shippingFormGenerator, regions, SHIPPING_FORM_TAG);
            setRegions(billingFormGenerator, regions, BILLING_FORM_TAG);
        }

        // Load the saved shipping values
        loadSavedValues(mShippingSavedValues, shippingFormGenerator);
        loadSavedValues(mBillingSavedValues, billingFormGenerator);
    }

    /**
     * Load the saved values to the respective form
     *
     * @param savedValues
     * @param dynamicForm
     * @author sergiopereira
     */
    private void loadSavedValues(ContentValues savedValues, DynamicForm dynamicForm) {
        // Validate values
        if (savedValues != null) {
            // Get dynamic form and update
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                try {
                    item.loadState(savedValues);
                    //Log.d(TAG, "CURRENT ITEM: " + item.getControl().getId());
                } catch (NullPointerException e) {
                    Print.w(TAG, "LOAD STATE: NOT CONTAINS KEY " + item.getKey());
                }
            }
        }
    }

    /**
     * Hide the default check boxes
     *
     * @param dynamicForm
     * @author sergiopereira
     */
    private void hideSomeFields(DynamicForm dynamicForm, boolean isBilling) {
        DynamicFormItem item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG);
        if (item != null) {
            item.getEditControl().setVisibility(View.GONE);
        }
        item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_BILLING_TAG);
        if (item != null) {
            item.getEditControl().setVisibility(View.GONE);
        }
        // When CITY_ID is EditText use CITY
        if (isCityIdAnEditText) {
            item = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG);
            if (item != null) {
                item.getControl().setVisibility(View.GONE);
            }
        } else {
            // Use CITY_ID
            item = dynamicForm.getItemByKey(RestConstants.JSON_CITY_TAG);
            item.getControl().setVisibility(View.GONE);
        }
        // Hide the gender field only for billing address
        if (isBilling) {
            try {
                item = dynamicForm.getItemByKey(RestConstants.JSON_GENDER_TAG);
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
     *
     * @param dynamicForm
     * @param regions
     * @param tag
     * @author sergiopereira
     */
    protected void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions, String tag) {
        // Get region item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.JSON_REGION_ID_TAG);
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
        int defaultPosition = 0;
        if (mFormResponse.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG).getValue() != null && Integer.parseInt(mFormResponse.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG).getValue()) > 0) {
            int defaultId = Integer.parseInt(mFormResponse.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG).getValue());
            for (int i = 0; i < regions.size(); i++) {
                if (regions.get(i).getId() == defaultId) {
                    defaultPosition = i;
                    break;
                }
            }
        }

        setSavedSelectedRegionPos(spinner, tag, defaultPosition);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }

    /**
     * Load and set the saved region position
     *
     * @param spinner
     * @param tag
     * @author sergiopereira
     */
    private void setSavedSelectedRegionPos(IcsSpinner spinner, String tag, int defaultPosition) {
        // Get saved value
        if (tag.equals(SHIPPING_FORM_TAG) && mShippingSavedValues != null && mShippingSavedValues.containsKey(SHIPPING_REGION_POS)) {
            int pos = mShippingSavedValues.getAsInteger(SHIPPING_REGION_POS);
            if (pos > 0 && pos < regions.size()) {
                spinner.setSelection(pos);
            } else {
                spinner.setSelection(defaultPosition);
            }
        } else if (tag.equals(BILLING_FORM_TAG) && mBillingSavedValues != null && mBillingSavedValues.containsKey(BILLING_REGION_POS)) {
            int pos = mBillingSavedValues.getAsInteger(BILLING_REGION_POS);
            //Log.d(TAG, "SAVED BILLING REGION VALUE: " + pos);
            if (pos > 0 && pos < regions.size()) {
                spinner.setSelection(pos);
            } else {
                spinner.setSelection(defaultPosition);
            }
        } else {
            spinner.setSelection(defaultPosition);
        }
    }

    /**
     * Validate the current region selection and update the cities
     *
     * @param requestedRegionAndFields
     * @param cities
     * @author sergiopereira
     */
    protected void setCitiesOnSelectedRegion(String requestedRegionAndFields, ArrayList<AddressCity> cities) {
        if (requestedRegionAndFields.equals(selectedRegionOnShipping)) {
            setCities(shippingFormGenerator, cities, SHIPPING_FORM_TAG);
        }
        if (requestedRegionAndFields.equals(selectedRegionOnBilling)) {
            setCities(billingFormGenerator, cities, BILLING_FORM_TAG);
        }
    }

    /**
     * Method used to set the cities on the respective form
     *
     * @param dynamicForm
     * @param cities
     * @param tag
     * @author sergiopereira
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities, String tag) {
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG);
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
        setSavedSelectedCityPos(spinner, cities, tag);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }

    /**
     * Load and set the saved city position one time
     *
     * @param spinner
     * @param tag
     * @author sergiopereira
     */
    private void setSavedSelectedCityPos(IcsSpinner spinner, ArrayList<AddressCity> array, String tag) {
        // Get saved value
        if (tag.equals(SHIPPING_FORM_TAG) && mShippingSavedValues != null && mShippingSavedValues.containsKey(SHIPPING_CITY_POS)) {
            int pos = mShippingSavedValues.getAsInteger(SHIPPING_CITY_POS);
            //Log.d(TAG, "SAVED SHIPPING CITY VALUE: " + pos);
            if (pos > 0 && pos < array.size()) {
                spinner.setSelection(pos);
            }
            // Clean the saved city pos
            mShippingSavedValues.remove(SHIPPING_CITY_POS);
        } else if (tag.equals(BILLING_FORM_TAG) && mBillingSavedValues != null && mBillingSavedValues.containsKey(BILLING_CITY_POS)) {
            int pos = mBillingSavedValues.getAsInteger(BILLING_CITY_POS);
            //Log.d(TAG, "SAVED BILLING CITY VALUE: " + pos);
            if (pos > 0 && pos < array.size()) {
                spinner.setSelection(pos);
            }
            // Clean the saved city pos
            mBillingSavedValues.remove(BILLING_CITY_POS);
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

        /*
        // Validate spinner
        ViewGroup mRegionGroup = (ViewGroup) shippingFormGenerator.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
        // Validate if region group is filled
        if (!(mRegionGroup.getChildAt(0) instanceof IcsSpinner)) {
            Log.w(TAG, "REGION SPINNER NOT FILL YET");
            return;
        }
        */

        // Validate check
        if (mIsSameCheckBox.isChecked()) {
            Print.i(TAG, "CREATE ADDRESS: IS SHIPPING AND IS BILLING TOO");
            ContentValues mContentValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
            Print.d(TAG, "CONTENT VALUES: " + mContentValues.toString());
            triggerCreateAddress(mContentValues, false);
        } else {
            Print.i(TAG, "CREATE ADDRESS: SHIPPING AND BILLING");
            ContentValues mShipValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, ISNT_DEFAULT_BILLING_ADDRESS);
            Print.d(TAG, "CONTENT SHIP VALUES: " + mShipValues.toString());
            triggerCreateAddress(mShipValues, false);
            // only to be fired if the first succeds
//            ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
//            Log.d(TAG, "CONTENT BILL VALUES: " + mBillValues.toString());
//            triggerCreateAddress(mBillValues,true);
        }
    }

    /**
     * method that controls that the addresses have the same gender when creating billing and shipping at the same time
     */
    private void validateSameGender() {
        DynamicFormItem shippingGenderItem = shippingFormGenerator.getItemByKey(RestConstants.JSON_GENDER_TAG);
        DynamicFormItem billingGenderItem = billingFormGenerator.getItemByKey(RestConstants.JSON_GENDER_TAG);
        if (shippingGenderItem != null && billingGenderItem != null) {
            try {
                int genderIndex = -1;
                if (((RadioGroupLayout) shippingGenderItem.getEditControl()).getChildCount() > 0) {
                    // Get selected gender index from the shipping form
                    genderIndex = ((RadioGroupLayout) shippingGenderItem.getEditControl()).getSelectedIndex();
                }
                if (((RadioGroupLayout) billingGenderItem.getEditControl()).getChildCount() > 0 && genderIndex != -1) {
                    // Set the billing gender with the same as the shipping
                    ((RadioGroupLayout) billingGenderItem.getEditControl()).setSelection(genderIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used to create the content values
     *
     * @param dynamicForm
     * @param isDefaultShipping
     * @param isDefaultBilling
     * @return new content values
     * @author sergiopereira
     */
    protected ContentValues createContentValues(DynamicForm dynamicForm, int isDefaultShipping, int isDefaultBilling) {
        // Save content values
        ContentValues mContentValues = dynamicForm.save();
        // Get the region
        ViewGroup mRegionGroup = (ViewGroup) dynamicForm.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
        // Get spinner
        IcsSpinner mRegionSpinner = (IcsSpinner) mRegionGroup.getChildAt(0);
        // Get selected region
        AddressRegion mSelectedRegion = (AddressRegion) mRegionSpinner.getSelectedItem();
        Print.d(TAG, "SELECTED REGION: " + mSelectedRegion.getName() + " " + mSelectedRegion.getId());

        // Used to save state
        int mSelectedPosition = mRegionSpinner.getSelectedItemPosition();
        if (isDefaultShipping == IS_DEFAULT_SHIPPING_ADDRESS) {
            mContentValues.put(SHIPPING_REGION_POS, mSelectedPosition);
        } else if (isDefaultBilling == IS_DEFAULT_BILLING_ADDRESS) {
            mContentValues.put(BILLING_REGION_POS, mSelectedPosition);
        }

        // Save region id
        int mRegionId = mSelectedRegion.getId();
        // Save city
        String mCityId = "";
        String mCityName = "";

        // Get from spinner
        View mControl = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getControl();
        View mCityView = ((ViewGroup) mControl).getChildAt(0);
        if (mCityView instanceof IcsSpinner) {
            IcsSpinner mCitySpinner = (IcsSpinner) mCityView;
            // Get selected city
            AddressCity mSelectedCity = (AddressCity) mCitySpinner.getSelectedItem();
            Print.d(TAG, "SELECTED CITY: " + mSelectedCity.getValue() + " " + mSelectedCity.getId());
            mCityId = "" + mSelectedCity.getId();
            mCityName = mSelectedCity.getValue();

            // Used to save state
            int mCitySelectedPosition = mCitySpinner.getSelectedItemPosition();
            if (isDefaultShipping == IS_DEFAULT_SHIPPING_ADDRESS) {
                mContentValues.put(SHIPPING_CITY_POS, mCitySelectedPosition);
            } else if (isDefaultBilling == IS_DEFAULT_BILLING_ADDRESS) {
                mContentValues.put(BILLING_CITY_POS, mCitySelectedPosition);
            }
        }
        // Get from edit text
        else if (mCityView instanceof RelativeLayout) {
            /*-mCityView = ((RelativeLayout) mCityView).getChildAt(0);
            if (mCityView instanceof EditText) {
                EditText mCityEdit = (EditText) dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getEditControl();
                String city = mCityEdit.getText().toString();
                // use this to load this into EditText after rotations. This value will be submitted to API
                mCityId = city;
                mCityName = city;
                Log.d(TAG, "SELECTED CITY: " + mCityName);
            }*/
        }
        // Unexpected
        else {
            Print.e(TAG, RestConstants.JSON_CITY_ID_TAG + " IS AN UNEXPECTED VIEW: " + mCityView.getClass().getName());
        }

        // Put values
        for (Map.Entry<String, Object> value : mContentValues.valueSet()) {
            String key = value.getKey();
            if (key.contains(RestConstants.JSON_IS_DEFAULT_BILLING_TAG)) {
                mContentValues.put(key, isDefaultBilling);
            } else if (key.contains(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG)) {
                mContentValues.put(key, isDefaultShipping);
            } else if (key.contains(RestConstants.JSON_REGION_ID_TAG)) {
                mContentValues.put(key, mRegionId);
            } else if (key.contains(RestConstants.JSON_CITY_ID_TAG)) {
                mContentValues.put(key, mCityId);
            } else if (!isCityIdAnEditText && key.contains(RestConstants.JSON_CITY_TAG)) {
                mContentValues.put(key, mCityName);
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
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.JSON_CITY_ID_TAG);
//            // Validate API call
//            if (field.getDataCalls() == null) {
//                Log.w(TAG, "GET CITY: API CALL IS NULL");
//                return;
//            }
            if (InputType.list == field.getInputType()) {
                // Get API call
                String url = field.getDataCalls().get(RestConstants.JSON_API_CALL_TAG);
                Print.d(TAG, "API CALL: " + url);
                if (url != null) {
                    // Request the cities for this region id
                    int regionId = ((AddressRegion) object).getId();
                    // Save the selected region on the respective variable
                    String tag = (parent.getTag() != null) ? parent.getTag().toString() : "";
                    if (tag.equals(SHIPPING_FORM_TAG)) {
                        selectedRegionOnShipping = SHIPPING_FORM_TAG + "_" + regionId;
                        triggerGetCities(url, regionId, selectedRegionOnShipping);
                    } else if (tag.equals(BILLING_FORM_TAG)) {
                        selectedRegionOnBilling = BILLING_FORM_TAG + "_" + regionId;
                        triggerGetCities(url, regionId, selectedRegionOnBilling);
                    }
                } else {
                    // Show
                    showFragmentContentContainer();
                    Print.e(TAG, "No " + RestConstants.JSON_API_CALL_TAG + " on " + RestConstants.JSON_CITY_ID_TAG);
                }
            } else if (InputType.text == field.getInputType()) {
                // Show
                showFragmentContentContainer();
                // City
                // loadCityEditText(field.getInputType());
            } else {
                // Show
                showFragmentContentContainer();
                Print.e(TAG, RestConstants.JSON_API_CALL_TAG + " with an expected inputType");
                //alexandra pires: webcheckout disabled for 2.7 version
            //    super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "GET CITIES EVENT: IS EMPTY");    //alexandra pires: webcheckout disabled for 2.7 version
                super.showUnexpectedErrorWarning(); //unexpected error
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
     *
     * @param isSame
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
     *
     * @param values
     * @author sergiopereira
     */
    protected void triggerCreateAddress(ContentValues values, boolean isBilling) {
        Print.i(TAG, "TRIGGER: CREATE ADDRESS");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putBoolean(CreateAddressHelper.IS_BILLING, isBilling);
        triggerContentEvent(new CreateAddressHelper(), bundle, this);
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
     * Trigger to initialize forms
     */
    protected void triggerInitForm() {
        Print.i(TAG, "TRIGGER: INIT FORMS");
        triggerContentEvent(new GetInitFormHelper(), null, this);
    }

    /**
     * Trigger to get regions
     *
     * @param apiCall
     * @author sergiopereira
     */
    protected void triggerGetRegions(String apiCall) {
        Print.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        triggerContentEventNoLoading(new GetRegionsHelper(), bundle, this);
    }

    /**
     * Trigger to get cities
     *
     * @param apiCall
     * @param region      id
     * @param selectedTag
     * @author sergiopereira
     */
    protected void triggerGetCities(String apiCall, int region, String selectedTag) {
        Print.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        bundle.putString(GetCitiesHelper.REGION_ID_TAG, String.valueOf(region));
        bundle.putString(GetCitiesHelper.CUSTOM_TAG, selectedTag);
        triggerContentEvent(new GetCitiesHelper(), bundle, this);
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
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     *
     * @param bundle
     * @return boolean
     * @author sergiopereira
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        Print.i(TAG, "ON SUCCESS EVENT");

        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
            case INIT_FORMS:
                onInitFormSuccessEvent();
                break;
            case GET_CREATE_ADDRESS_FORM_EVENT:
                onGetCreateAddressFormSuccessEvent(bundle);
                break;
            case GET_REGIONS_EVENT:
                onGetRegionsSuccessEvent(bundle);
                break;
            case GET_CITIES_EVENT:
                onGetCitiesSuccessEvent(bundle);
                break;
            case CREATE_ADDRESS_SIGNUP_EVENT:
            case CREATE_ADDRESS_EVENT:
                onCreateAddressSuccessEvent(bundle);
                break;
            default:
                break;
        }

        return true;
    }

    protected void onInitFormSuccessEvent() {
        Print.d(TAG, "RECEIVED INIT_FORMS");
        triggerCreateAddressForm();
    }

    protected void onGetCreateAddressFormSuccessEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
        // Get order summary
        //orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
        // Save and load form
        Form form = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        mFormResponse = form;
        // Load form, get regions
        loadCreateAddressForm(form);
    }

    protected void onGetRegionsSuccessEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
        regions = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        // Validate response
        if (CollectionUtils.isNotEmpty(regions)) {
            setRegions(shippingFormGenerator, regions, SHIPPING_FORM_TAG);
            setRegions(billingFormGenerator, regions, BILLING_FORM_TAG);
        } else {
            Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
            //alexandra pires: webcheckout disabled for 2.7 version
         //   super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "GET REGIONS EVENT: IS EMPTY"); alexandrapires: webcheckout disabled for 2.7 version
            super.showFragmentErrorRetry();
        }
    }

    protected void onGetCitiesSuccessEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
        Print.d(TAG, "REQUESTED REGION FROM FIELD: " + bundle.getString(GetCitiesHelper.CUSTOM_TAG));
        String requestedRegionAndField = bundle.getString(GetCitiesHelper.CUSTOM_TAG);
        ArrayList<AddressCity> cities = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        setCitiesOnSelectedRegion(requestedRegionAndField, cities);
        // Show
        showFragmentContentContainer();
    }

    protected void onCreateAddressSuccessEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
    }

    /**
     * Filter the error response
     *
     * @param bundle
     * @return boolean
     * @author sergiopereira
     */
    protected boolean onErrorEvent(Bundle bundle) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
            case INIT_FORMS:
                onInitFormErrorEvent();
                break;
            case GET_CREATE_ADDRESS_FORM_EVENT:
                onGetCreateAddressFormErrorEvent(bundle);
                break;
            case GET_REGIONS_EVENT:
                onGetRegionsErrorEvent(bundle);
                break;
            case GET_CITIES_EVENT:
                onGetCitiesErrorEvent(bundle);
                break;
            case CREATE_ADDRESS_SIGNUP_EVENT:
            case CREATE_ADDRESS_EVENT:
                onCreateAddressErrorEvent(bundle);
                break;
            default:
                break;
        }

        return false;
    }


    protected void onInitFormErrorEvent() {
        Print.d(TAG, "RECEIVED INIT_FORMS");
    }

    protected void onGetCreateAddressFormErrorEvent(Bundle bundle) {
        Print.w(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
    }

    protected void onGetRegionsErrorEvent(Bundle bundle) {
        Print.w(TAG, "RECEIVED GET_REGIONS_EVENT");
    }

    protected void onGetCitiesErrorEvent(Bundle bundle) {
        Print.w(TAG, "RECEIVED GET_CITIES_EVENT");
    }

    protected void onCreateAddressErrorEvent(Bundle bundle) {
        Print.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
        // Clean flag to wait for both different responses
        oneAddressCreated = false;
    }

    /**
     * ########### DIALOGS ###########
     */
    /**
     * Dialog used to show an error
     *
     * @param errorMessage
     * @author sergiopereira
     */
    protected void showErrorDialog(String errorMessage ,String dialogTitle) {
        Print.d(TAG, "SHOW LOGIN ERROR DIALOG");
        // FIXME to be fixed on a next release, where all form validations are made on our side and delt with the the error messages centrally
//        List<String> errorMessages = null;
//        if (errors != null) {
//            errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
//        }
//        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
        showFragmentContentContainer();
        dialog = DialogGenericFragment.newInstance(true, false,
                dialogTitle,
                errorMessage,
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
//        } else {
//            if (mMsgRequired != null) {
//                mMsgRequired.setVisibility(View.VISIBLE);
//                changeMessageState(mMsgRequired, true, ERROR_DELAY);
//
//            } else {
//                Toast.makeText(getBaseActivity(), getString(R.string.register_required_text), Toast.LENGTH_SHORT).show();
//            }
//        }
    }
}
