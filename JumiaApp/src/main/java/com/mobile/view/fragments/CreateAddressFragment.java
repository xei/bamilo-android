package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
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
import com.mobile.newFramework.objects.addresses.FormListItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.ArrayList;
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
public abstract class CreateAddressFragment extends BaseFragment implements IResponseCallback, IcsAdapterView.OnItemSelectedListener {

    private static final String TAG = CreateAddressFragment.class.getSimpleName();
    private static final String SHIPPING_SAVED_STATE = "shippingSavedStateBundle";
    private static final String REGION_CITIES_POSITIONS = "regionsCitiesBundle";
    protected ViewGroup mShippingFormContainer;
    protected DynamicForm shippingFormGenerator;
    protected Form mFormShipping;
    protected ArrayList<AddressRegion> regions;
    protected String selectedRegionOnShipping = "";
    protected String selectedCityOnShipping = "";
    protected TextView mShippingTitle;
    protected boolean isCityIdAnEditText = false;
    protected ScrollView mScrollViewContainer;
    protected PurchaseEntity orderSummary;
    private Bundle mShippingFormSavedState;
    private Bundle mSavedRegionCitiesPositions;

    /*
     * Constructors
     */

    public CreateAddressFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, int titleResId, @KeyboardState int adjust_state, @ConstantsCheckout.CheckoutType int titleCheckout) {
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
            saveRegionsCitiesPositions(listPositions, shippingSavedStateBundle);
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
    private void saveRegionsCitiesPositions(Bundle listPositions, Bundle addressSavedStateBundle) {
        listPositions.putInt(RestConstants.REGION, addressSavedStateBundle.getInt(RestConstants.REGION));
        listPositions.putInt(RestConstants.CITY, addressSavedStateBundle.getInt(RestConstants.CITY));
        listPositions.putInt(RestConstants.POSTCODE, addressSavedStateBundle.getInt(RestConstants.POSTCODE));
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
        if (shippingFormGenerator != null) {
            Bundle shippingSavedStateBundle = new Bundle();
            shippingFormGenerator.saveFormState(shippingSavedStateBundle);
            mShippingFormSavedState = shippingSavedStateBundle;
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
        mFormShipping = null;
    }


    /**
     * Load the dynamic form
     */
    protected void loadCreateAddressForm(Form mFormShipping) {
        Print.i(TAG, "LOAD CREATE ADDRESS FORM");
        // Shipping form
        if (shippingFormGenerator == null) {
            shippingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), mFormShipping);
            mShippingFormContainer.removeAllViews();
            mShippingFormContainer.addView(shippingFormGenerator.getContainer());
            mShippingFormContainer.refreshDrawableState();
        } else if (mShippingFormContainer.getChildCount() == 0) {
            // Have to create set a Dynamic form in order to not have the parent dependencies.
            // this happens when user goes from create address to another screen through the overflow menu, and presses back.
            // Error: The specified child already has a parent. You must call removeView() on the child's parent first.
            shippingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), mFormShipping);
            mShippingFormContainer.addView(shippingFormGenerator.getContainer());
            mShippingFormContainer.refreshDrawableState();
        }
        // Load the saved shipping values
        shippingFormGenerator.loadSaveFormState(mShippingFormSavedState);
        // Define if CITY is a List or Text
        DynamicFormItem item = shippingFormGenerator.getItemByKey(RestConstants.CITY);
        isCityIdAnEditText = item != null && item.getEditControl() instanceof EditText;
        // Validate Regions
        if (regions == null) {
            FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.REGION);
            triggerGetRegions(field.getApiCall());
        } else {
            setRegions(shippingFormGenerator, regions);
        }

    }

    /**
     * Method used to set the regions on the respective form
     */
    protected void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions) {
        // Get region item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.REGION);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        //add place holder by default if value = ""; ignore if added already
        if (TextUtils.isEmpty(v.getEntry().getValue()) && regions.get(0).getValue() != 0) {
            regions.add(0, new AddressRegion(0, v.getEntry().getPlaceHolder()));
        }

        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (mShippingFormSavedState == null) {
            spinner.setSelection(getDefaultPosition(v, regions));
        } else {
            processSpinners(spinner, RestConstants.REGION);
        }

        spinner.setOnItemSelectedListener(this);
        v.setEditControl(spinner);
        group.addView(spinner);
        // Show invisible content to trigger spinner listeners
        showGhostFragmentContentContainer();

    }


    /**
     * Allows to update the spinners (regions/cities/postcodes) correctly with previous values when app goes to background or rotates
     */
    private void processSpinners(IcsSpinner spinner, String restConstantsKey) {
        if (mShippingFormSavedState != null && mShippingFormSavedState.getInt(restConstantsKey) <= spinner.getCount()) {
            spinner.setSelection(mShippingFormSavedState.getInt(restConstantsKey));

        } else if (mShippingFormSavedState != null && mShippingFormSavedState.getInt(restConstantsKey) <= spinner.getCount()) {
            spinner.setSelection(mShippingFormSavedState.getInt(restConstantsKey));
        }
    }


    /**
     * Get the position of the regions
     * @return int the position
     */
    private int getDefaultPosition(DynamicFormItem formItem, ArrayList<? extends FormListItem> regions) {
        try {
            int regionValue = Integer.valueOf(formItem.getEntry().getValue());
            for (int i = 0; i < regions.size(); i++)
                if (regionValue == regions.get(i).getValue()) {
                    return i;
                }
        } catch (NullPointerException | NumberFormatException e) {
            Print.e(TAG, e.getMessage());
        }
        return 0;
    }


    /**
     * Validate the current region selection and update the cities
     */
    protected void setCitiesOnSelectedRegion(String requestedRegionAndFields, ArrayList<AddressCity> cities) {
        if (requestedRegionAndFields.equals(selectedRegionOnShipping)) {
            setCities(shippingFormGenerator, cities);
        }
    }

    /**
     * Validate the current city selection and update the postal codes
     */
    protected void setPostalCodesOnSelectedCity(String requestedCityAndFields, ArrayList<AddressPostalCode> postalCodes) {
        if (requestedCityAndFields.equals(selectedCityOnShipping)) {
            setPostalCodes(shippingFormGenerator, postalCodes);
        }
    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities) {
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.CITY);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        if (TextUtils.isEmpty(v.getEntry().getValue()) && cities.get(IntConstants.DEFAULT_POSITION).getValue() != IntConstants.DEFAULT_POSITION) {
            cities.add(IntConstants.DEFAULT_POSITION, new AddressCity(IntConstants.DEFAULT_POSITION, v.getEntry().getPlaceHolder()));
        }
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setSavedSelectedCityPos(spinner);

        if (mShippingFormSavedState == null) {
            spinner.setSelection(getDefaultPosition(v, cities));
        } else {
            processSpinners(spinner, RestConstants.CITY);
        }

        spinner.setOnItemSelectedListener(this);
        v.setEditControl(spinner);
        group.addView(spinner);
        // Validate if first position is the prompt
        if (cities.get(IntConstants.DEFAULT_POSITION).getValue() == IntConstants.DEFAULT_POSITION) {
            showFragmentContentContainer();
        }
    }

    /**
     * Method used to set the postal Codes on the respective form
     */
    private void setPostalCodes(DynamicForm dynamicForm, ArrayList<AddressPostalCode> postalCodes) {
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.POSTCODE);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        //add place holder from API by default if value = ""; ignore if added already
        if (TextUtils.isEmpty(v.getEntry().getValue()) && postalCodes.get(0).getValue() != 0) {
            postalCodes.add(0, new AddressPostalCode(0, v.getEntry().getPlaceHolder()));
        }
        // Create adapter
        ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, postalCodes);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setSavedSelectedPostalCodePos(spinner);
        if (mShippingFormSavedState == null) {
            spinner.setSelection(getDefaultPosition(v, postalCodes));
        } else {
            processSpinners(spinner, RestConstants.POSTCODE);
        }
        spinner.setOnItemSelectedListener(this);
        v.setEditControl(spinner);
        group.addView(spinner);
    }

    /**
     * Load and set the saved city position one time
     */
    private void setSavedSelectedCityPos(IcsSpinner spinner) {
        int position = IntConstants.INVALID_POSITION;
        if (mSavedRegionCitiesPositions != null) {
            position = mSavedRegionCitiesPositions.getInt(RestConstants.CITY);
        }

        if (position != IntConstants.INVALID_POSITION && spinner.getCount() > 0 && position <= spinner.getCount()) {
            spinner.setSelection(position);
        }
    }

    /**
     * Load and set the postal code position one time
     */
    private void setSavedSelectedPostalCodePos(IcsSpinner spinner) {
        int position = IntConstants.INVALID_POSITION;
        if (mSavedRegionCitiesPositions != null) {
            position = mSavedRegionCitiesPositions.getInt(RestConstants.POSTCODE);
        }

        if (position != IntConstants.INVALID_POSITION && spinner.getCount() > 0 && position <= spinner.getCount()) {
            spinner.setSelection(position);
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
        // Validate
        if (!shippingFormGenerator.validate()) {
            Print.i(TAG, "SAME FORM: INVALID");
            return;
        }
        triggerCreateAddress(shippingFormGenerator.getForm().getAction(), shippingFormGenerator.save());
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
        Object object = parent.getItemAtPosition(position);
        if (object instanceof AddressRegion) {
            if (((AddressRegion) object).getValue() != 0) {   //if not the place holder option, load cities of selected region
                // Get city field
                FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.CITY);
                // Case list
                if (FormInputType.list == field.getInputType()) {
                    // Request the cities for this region id
                    int regionId = ((AddressRegion) object).getValue();
                    // Save the selected region on the respective variable
                        selectedRegionOnShipping = "" + regionId;
                        triggerGetCities(field.getApiCall(), regionId, selectedRegionOnShipping);
                }
                // Case text or other
                else {
                    showFragmentContentContainer();
                }

            } else {
                showFragmentContentContainer();
            }
        } else if (object instanceof AddressCity) {

            if (((AddressCity) object).getValue() != 0) {
                // Get city field
                FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.POSTCODE);
                // Case list
                if (field != null && FormInputType.list == field.getInputType()) {
                    // Request the postal codes for this city id
                    int cityId = ((AddressCity) object).getValue();
                    // Save the selected city on the respective variable
                        selectedCityOnShipping = "" + cityId;
                        triggerGetPostalCodes(field.getApiCall(), cityId, selectedCityOnShipping);
                }
            }
        }
    }

    /*
     * ############# REQUESTS #############
     */

    /**
     * Trigger to create an address
     */
    protected void triggerCreateAddress(String action, ContentValues values) {
        Print.i(TAG, "TRIGGER: CREATE ADDRESS");
        triggerContentEvent(new CreateAddressHelper(), CreateAddressHelper.createBundle(action, values), this);
        // Hide the keyboard
        getBaseActivity().hideKeyboard();
    }

    /**
     * Trigger to get the address form
     */
    protected void triggerCreateAddressForm() {
        Print.i(TAG, "TRIGGER: CREATE ADDRESS FORM");
        triggerContentEvent(new GetFormAddAddressHelper(), null, this);
    }

    /**
     * Trigger to get regions
     */
    protected void triggerGetRegions(String url) {
        Print.i(TAG, "TRIGGER: GET REGIONS: " + url);
        triggerContentEventNoLoading(new GetRegionsHelper(), GetRegionsHelper.createBundle(url), this);
    }

    /**
     * Trigger to get cities
     */
    protected void triggerGetCities(String url, int region, String tag) {
        Print.i(TAG, "TRIGGER: GET CITIES: " + url + " " + tag);
        triggerContentEvent(new GetCitiesHelper(), GetCitiesHelper.createBundle(url, region, tag), this);
    }

    /**
     * Trigger to get postal codes
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
        orderSummary = JumiaApplication.INSTANCE.getCart();
        // Save and load form
        AddressForms form = (AddressForms) baseResponse.getContentData();
        mFormShipping = form.getShippingForm();
        // Load form
        loadCreateAddressForm(mFormShipping);
    }

    protected void onGetRegionsSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
        regions = (AddressRegions) baseResponse.getContentData();
        // Validate response
        if (CollectionUtils.isNotEmpty(regions)) {
            setRegions(shippingFormGenerator, regions);
        } else {
            Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
            super.showFragmentErrorRetry();
        }
    }

    protected void onGetCitiesSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
        ArrayList<AddressCity> citiesArray = (GetCitiesHelper.AddressCitiesStruct) baseResponse.getContentData();
        GetCitiesHelper.AddressCitiesStruct cities = (GetCitiesHelper.AddressCitiesStruct) citiesArray;
        String requestedRegionAndField = cities.getCustomTag();
        Print.d(TAG, "REQUESTED REGION FROM FIELD: " + requestedRegionAndField);
        setCitiesOnSelectedRegion(requestedRegionAndField, cities);
        FormField field = mFormShipping.getFieldKeyMap().get(RestConstants.POSTCODE);
        if (field == null) {
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
        // Validate
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.i(TAG, "SUPER HANDLE ERROR EVENT");
            return;
        }
        // Validate type
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
                onGetPostalCodesErrorEvent();
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

    protected void onGetPostalCodesErrorEvent() {
        Print.w(TAG, "RECEIVED GET_POSTAL_CODES_EVENT");
    }

    protected void onCreateAddressErrorEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
    }
}
