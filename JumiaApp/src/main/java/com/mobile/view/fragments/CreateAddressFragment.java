package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.absspinner.PromptSpinnerAdapter;
import com.mobile.components.customfontviews.Button;
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
import com.mobile.helpers.address.SetDefaultShippingAddressHelper;
import com.mobile.helpers.session.LoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.AddressForms;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.objects.addresses.AddressCities;
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
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;
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
public abstract class CreateAddressFragment extends BaseFragment implements IResponseCallback, IcsAdapterView.OnItemSelectedListener , View.OnClickListener{

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
    IcsSpinner address_spinner ,city_spinner,postal_spinner,gender_spinner;
    TextView name_error , family_error , national_error,cellphone_error ,address_error,city_error;
    EditText name;
    EditText family;
    EditText address;
    EditText national_id;
    EditText postal_code;

    EditText cellphone;
    private Button add;
    String cityApi="" , postalApi="", regionApi="";
    String action;
    int region_Id , city_Id , post_id;
    /*
     * Constructors
     */

    public CreateAddressFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, int titleResId, @KeyboardState int adjust_state, @ConstantsCheckout.CheckoutType int titleCheckout) {
      /*  super(enabledMenuItems, action, R.layout.checkout_create_address_main, titleResId, adjust_state, titleCheckout);*/
        super(enabledMenuItems, action, R.layout.checkout_create_address_shipping2, titleResId, adjust_state, titleCheckout);
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
        triggerCreateAddressForm();
/*        region = (Spinner) view.findViewById(R.id.address_region);
        city = (Spinner) view.findViewById(R.id.address_city);*/

        name = (EditText) view.findViewById(R.id.address_name);
        name.setText(JumiaApplication.CUSTOMER.getFirstName());
        family = (EditText) view.findViewById(R.id.address_family);
        family.setText(JumiaApplication.CUSTOMER.getLastName());
        national_id = (EditText) view.findViewById(R.id.address_national_id);
        gender_spinner = (IcsSpinner) view.findViewById(R.id.address_gender);
        cellphone = (EditText) view.findViewById(R.id.address_cell);
        address = (EditText) view.findViewById(R.id.address_direction);
        address_spinner = (IcsSpinner) view.findViewById(R.id.address_state);
        city_spinner = (IcsSpinner) view.findViewById(R.id.address_city);
        postal_spinner = (IcsSpinner) view.findViewById(R.id.address_postal_region);
        postal_code = (EditText) view.findViewById(R.id.address_postal_code);
        add = (Button) view.findViewById(R.id.add_address_btn);

        name_error = (TextView) view.findViewById(R.id.address_name_error);
        family_error = (TextView) view.findViewById(R.id.address_last_name_error);
        national_error = (TextView) view.findViewById(R.id.address_national_id_error);
        cellphone_error = (TextView) view.findViewById(R.id.address_cellphone_error);
        address_error = (TextView) view.findViewById(R.id.address_text_error);
        city_error = (TextView) view.findViewById(R.id.address_city_error);
        // Spinner Drop down elements
        ArrayList<AddressCity> city = new ArrayList<AddressCity>();
        city.add(new AddressCity(0,"شهر"));
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item,city);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        city_spinner.setAdapter(adapter);


        postal_spinner.setVisibility(View.GONE);
        if (JumiaApplication.CUSTOMER.getGender().isEmpty())
        {
            gender_spinner.setVisibility(View.VISIBLE);
        }
        add.setOnClickListener(this);
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
            shippingFormGenerator = FormFactory.create(FormConstants.ADDRESS_FORM, getActivity(), mFormShipping);
            mShippingFormContainer.removeAllViews();
            mShippingFormContainer.addView(shippingFormGenerator.getContainer());
            mShippingFormContainer.refreshDrawableState();
        } else if (mShippingFormContainer.getChildCount() == 0) {
            // Have to create set a Dynamic form in order to not have the parent dependencies.
            // this happens when user goes from create address to another screen through the overflow menu, and presses back.
            // Error: The specified child already has a parent. You must call removeView() on the child's parent first.
            shippingFormGenerator = FormFactory.create(FormConstants.ADDRESS_FORM, getActivity(), mFormShipping);
            mShippingFormContainer.addView(shippingFormGenerator.getContainer());
            mShippingFormContainer.refreshDrawableState();
        }
        // Load the saved shipping values
        shippingFormGenerator.loadSaveFormState(mShippingFormSavedState);
        // Define if CITY is a List or Text
        DynamicFormItem item = shippingFormGenerator.getItemByKey(RestConstants.CITY);
        isCityIdAnEditText = item != null && item.getDataControl() instanceof EditText;
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
        // Get spinner
        IcsSpinner spinner = (IcsSpinner) v.getDataControl();
        spinner.setEnabled(true);
        // Create adapter
        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(v.getEntry().getPlaceHolder());
        spinner.setAdapter(promptAdapter);
        // Add listener
        v.setOnItemSelectedListener(this);
        // Case default value
        if (CollectionUtils.isNotEmpty(mShippingFormSavedState)) {
            processSpinners(spinner, RestConstants.REGION);
        }
        // Case saved state
        else if(TextUtils.isNotEmpty(v.getEntry().getValue())) {
            spinner.setSelection(getDefaultPosition(v, regions));
        }
        // Show form (Zero is the prompt)
        if(spinner.getSelectedItemPosition() != IntConstants.DEFAULT_POSITION &&
                mFormShipping.getFieldKeyMap().get(RestConstants.CITY) != null) {
            showGhostFragmentContentContainer();
        } else {
            showFragmentContentContainer();
        }
    }

    /**
     * Allows to update the spinners (regions/cities/postcodes) correctly with previous values when app goes to background or rotates
     */
    private boolean processSpinners(IcsSpinner spinner, String restConstantsKey) {
        if (mShippingFormSavedState != null && mShippingFormSavedState.getInt(restConstantsKey) < spinner.getCount()) {
            spinner.setSelection(mShippingFormSavedState.getInt(restConstantsKey));
            return true;
        }
        return false;
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
                    // Zero position is the prompt
                    return i + 1;
                }
        } catch (NullPointerException | NumberFormatException e) {
            Print.e(TAG, e.getMessage());
        }
        return 0;
    }

    /**
     * Validate the current region selection and update the cities
     */
    protected void setCitiesOnSelectedRegion(String requestedRegionAndFields, final ArrayList<AddressCity> cities) {

        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt("شهر");
        city_spinner.setAdapter(promptAdapter);
        city_spinner.setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    city_Id = cities.get(position).getValue();
                    triggerGetPostalCodes(postalApi,city_Id, String.valueOf(city_Id));
                }
                else {
                    city_Id = cities.get(position-1).getValue();
                    triggerGetPostalCodes(postalApi,city_Id, String.valueOf(city_Id));
                }
            }

            @Override
            public void onNothingSelected(IcsAdapterView<?> parent) {
            }
        });

        hideActivityProgress();
        showFragmentContentContainer();






    }

    /**
     * Validate the current city selection and update the postal codes
     */
    protected void setPostalCodesOnSelectedCity(String requestedCityAndFields, final ArrayList<AddressPostalCode> postalCodes) {
        if (postalCodes.size()>1) {
            postal_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, postalCodes);
            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
            promptAdapter.setPrompt("محله");
            postal_spinner.setAdapter(promptAdapter);
            postal_spinner.setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                    post_id = postalCodes.get(position).getValue();
                }

                @Override
                public void onNothingSelected(IcsAdapterView<?> parent) {
                }
            });
            hideActivityProgress();
            showFragmentContentContainer();
        }
        else
        {
            post_id = postalCodes.get(0).getValue();
            postal_spinner.setVisibility(View.GONE);
        }

    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities) {
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.CITY);
        // Get spinner
        IcsSpinner spinner = (IcsSpinner) v.getDataControl();
        spinner.setEnabled(true);
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(v.getEntry().getPlaceHolder());
        spinner.setAdapter(promptAdapter);

        setSavedSelectedCityPos(spinner);

        if (mShippingFormSavedState == null) {
            spinner.setSelection(getDefaultPosition(v, cities));
        } else {
            processSpinners(spinner, RestConstants.CITY);
        }
        // Add listener
        v.setOnItemSelectedListener(this);
        // Show form (Zero is the prompt)
        if(spinner.getSelectedItemPosition() != IntConstants.DEFAULT_POSITION &&
                mFormShipping.getFieldKeyMap().get(RestConstants.POSTCODE) != null) {
            showGhostFragmentContentContainer();
        } else {
            showFragmentContentContainer();
        }
    }

    /**
     * Method used to set the postal Codes on the respective form
     */
    private void setPostalCodes(DynamicForm dynamicForm, ArrayList<AddressPostalCode> postalCodes) {
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.POSTCODE);
        // Get a spinner
        IcsSpinner spinner = (IcsSpinner) v.getDataControl();
        spinner.setEnabled(true);
        // Create adapter
        ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, postalCodes);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(v.getEntry().getPlaceHolder());
        spinner.setAdapter(promptAdapter);

        setSavedSelectedPostalCodePos(spinner);
        if (mShippingFormSavedState == null) {
            spinner.setSelection(getDefaultPosition(v, postalCodes));
        } else {
            processSpinners(spinner, RestConstants.POSTCODE);
        }
        // Add listener
        v.setOnItemSelectedListener(this);
    }

    /**
     * Load and set the saved city position one time
     */
    private void setSavedSelectedCityPos(IcsSpinner spinner) {
        int position = IntConstants.INVALID_POSITION;
        if (mSavedRegionCitiesPositions != null) {
            position = mSavedRegionCitiesPositions.getInt(RestConstants.CITY);
        }

        if (position != IntConstants.INVALID_POSITION && spinner.getCount() > 0 && position < spinner.getCount()) {
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

        if (position != IntConstants.INVALID_POSITION && spinner.getCount() > 0 && position < spinner.getCount()) {
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
        if (id == R.id.add_address_btn) {
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
        if (formValidated()==false) {
            Print.i(TAG, "SAME FORM: INVALID");

            return;
        }
        else {

            ContentValues values = new ContentValues();
            values.put("address_form[id]", "");
            values.put("address_form[national_id]", national_id.getText().toString());
            values.put("address_form[first_name]", name.getText().toString());
            values.put("address_form[last_name]", family.getText().toString());
            values.put("address_form[address1]", address.getText().toString());
            values.put("address_form[address2]", postal_code.getText().toString());
            values.put("address_form[region]", region_Id);
            values.put("address_form[city]",   city_Id);
            values.put("address_form[postcode]", post_id);
            values.put("address_form[phone]", cellphone.getText().toString());
            values.put("address_form[is_default_shipping]", 1);
            values.put("address_form[is_default_billing]", 1);
            values.put("address_form[gender]", JumiaApplication.CUSTOMER.getGender());

            triggerCreateAddress(action, values);
            /*triggerDefaultAddressForm();*/
        }
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

    private boolean formValidated() {
        boolean flag =true;
        name_error.setVisibility(View.GONE);
        family_error.setVisibility(View.GONE);
        address_error.setVisibility(View.GONE);
        city_error.setVisibility(View.GONE);
        cellphone_error.setVisibility(View.GONE);
        national_error.setVisibility(View.GONE);
        if (address_spinner.getSelectedItem()==null || address_spinner.getSelectedItem().equals("استان")){
            address_error.setVisibility(View.VISIBLE);
            address_error.setText("تکمیل این گزینه الزامی می باشد");
        }
        if ( city_spinner.getSelectedItem()==null || city_spinner.getSelectedItem().equals("شهر")){
            city_error.setVisibility(View.VISIBLE);
            city_error.setText("تکمیل این گزینه الزامی می باشد");
        }
        if (name.getText().length()>=0) {
          /*  */
            if (name.getText().length()<2)
            {
                name_error.setVisibility(View.VISIBLE);
                name_error.setText("تکمیل این گزینه الزامی می باشد");
                flag = false;
            }

        }
        if (family.getText().length()>=0) {
          /*  */
            if (family.getText().length()<2)
            {
                family_error.setVisibility(View.VISIBLE);
                family_error.setText("تکمیل این گزینه الزامی می باشد");
                flag = false;
            }

        }
        if (address.getText().length()>=0) {
          /*  */
            if (address.getText().length()<2)
            {
                address_error.setVisibility(View.VISIBLE);
                address_error.setText("تکمیل این گزینه الزامی می باشد");
                flag = false;
            }

        }

        if (national_id.getText().length()>=0) {
          /*  */
            if (national_id.getText().length() != 10 && national_id.getText().length()!=0 ){
                national_error.setVisibility(View.VISIBLE);
                national_error.setText("تعداد ارقام باید ۱۰ رقم باشد");
                flag = false;
            }
            if (national_id.getText().length()==0)
            {
                national_error.setVisibility(View.VISIBLE);
                national_error.setText("تکمیل این گزینه الزامی می باشد");
                flag = false;
            }

        }

        if (cellphone.getText().length()>=0) {
          /*  */
            if (cellphone.getText().length() != 11 && cellphone.getText().length()!=0 ){
                cellphone.setVisibility(View.VISIBLE);
                cellphone_error.setVisibility(View.VISIBLE);
                cellphone_error.setText("تعداد ارقام بایذ 11 رقم باشد");
                flag = false;
            }
            if (cellphone.getText().length()==0)
            {
                cellphone.setVisibility(View.VISIBLE);
                cellphone_error.setVisibility(View.VISIBLE);
                cellphone_error.setText("تکمیل این گزینه الزامی می باشد");
                flag = false;
            }

        }



      ;
        if (flag==false){
            return false;
        }
        else {
            return true;
        }
    }

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
        triggerContentEventNoLoading(new GetCitiesHelper(), GetCitiesHelper.createBundle(url, region, tag), this);
    }

    /**
     * Trigger to get postal codes
     */
    protected void triggerGetPostalCodes(String action, int city, String tag) {
        Print.i(TAG, "TRIGGER: GET POSTAL CODES: " + city + " " + tag);
        triggerContentEvent(new GetPostalCodeHelper(), GetPostalCodeHelper.createBundle(action, city, tag), this);
    }

    protected void triggerDefaultAddressForm(int mAddressId){
        triggerContentEventProgress(new SetDefaultShippingAddressHelper(), SetDefaultShippingAddressHelper.createBundle(mAddressId), this);
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
                hideActivityProgress();
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


        ///////////////////////////////
        action = ((AddressForms) baseResponse.getContentData()).getBillingForm().getAction();
        List<FormField> fields = ((AddressForms) baseResponse.getContentData()).getShippingForm() .getFields();


        int i =0;
        for ( FormField field :fields)
        {
            i++;
            if (i == 7) {
                cityApi=field.getApiCall();
            }
            if(i==6){

                regionApi = field.getApiCall();
                continue;
            }
            if (i == 8) {
                postalApi=field.getApiCall();
            }
            continue;
        }





        /////////////////////////////////

        triggerGetRegions(regionApi);

        // mFormShipping = form.getShippingForm();
        // Load form
        //loadCreateAddressForm(mFormShipping);
    }

    protected void onGetRegionsSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
        regions = (AddressRegions) baseResponse.getContentData();



        // Creating adapter for spinner

        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt("استان");
        address_spinner.setAdapter(promptAdapter);
        address_spinner.setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                if (position!=0) {
                    region_Id = regions.get(position-1).getValue();
                    triggerGetCities(cityApi, region_Id, String.valueOf(region_Id));
                    }
            }

            @Override
            public void onNothingSelected(IcsAdapterView<?> parent) {
            }
        });
        hideActivityProgress();
        showFragmentContentContainer();

/*
        // Validate response
        /*if (CollectionUtils.isNotEmpty(regions)) {
            setRegions(shippingFormGenerator, regions);
        } else {
            Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
            super.showFragmentErrorRetry();
        }*/
    }

    protected void onGetCitiesSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
        ArrayList<AddressCity> citiesArray = (GetCitiesHelper.AddressCitiesStruct) baseResponse.getContentData();
        GetCitiesHelper.AddressCitiesStruct cities = (GetCitiesHelper.AddressCitiesStruct) citiesArray;
        String requestedRegionAndField = cities.getCustomTag();
        Print.d(TAG, "REQUESTED REGION FROM FIELD: " + requestedRegionAndField);
        setCitiesOnSelectedRegion(requestedRegionAndField, cities);
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
