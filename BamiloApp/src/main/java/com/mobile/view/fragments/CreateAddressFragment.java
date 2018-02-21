package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.components.absspinner.PromptSpinnerAdapter;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.helpers.address.CreateAddressHelper;
import com.mobile.helpers.address.GetCitiesHelper;
import com.mobile.helpers.address.GetPostalCodeHelper;
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.addresses.AddressCity;
import com.mobile.service.objects.addresses.AddressPostalCode;
import com.mobile.service.objects.addresses.AddressRegion;
import com.mobile.service.objects.addresses.AddressRegions;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.ApiConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential.
 *
 * @author ricardo.soares
 * @version 1.0
 * @date 2015/02/24
 */
public abstract class CreateAddressFragment extends BaseFragment implements IResponseCallback, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = CreateAddressFragment.class.getSimpleName();
    private static final String SHIPPING_SAVED_STATE = "shippingSavedStateBundle";
    private static final String REGION_CITIES_POSITIONS = "regionsCitiesBundle";
    private static final String GENDER_MALE = "male", GENDER_FEMALE = "female";
    private static final int UNKNOWN_POSTAL_CODE = -1;
    protected ArrayList<AddressRegion> regions;
    protected String selectedRegionOnShipping = "";
    protected String selectedCityOnShipping = "";
    protected PurchaseEntity orderSummary;
    Spinner address_spinner, city_spinner, postal_spinner, gender_spinner;
    TextView name_error, family_error, cellphone_error, address_error, address_region_error, address_postal_region_error,
            address_city_error, gender_error, address_postal_code_error;
    EditText name;
    EditText family;
    EditText address;
    EditText postal_code;
    String gender_lable = "";
    EditText cellphone;
    private Button add;
    String getCityApi = ApiConstants.GET_CITIES_API_PATH,
            getPostalApi = ApiConstants.GET_ADDRESS_POST_CODES_API_PATH,
            getRegionApi = ApiConstants.GET_REGIONS_API_PATH;
    String createAddressUrl = ApiConstants.CREATE_ADDRESS_API_PATH;
    int region_Id, city_Id, post_id;
    private EventType errorType;
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

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.ADD_ADDRESS.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        triggerGetRegions(getRegionApi);
        /* region = (Spinner) view.findViewById(R.id.address_region);
        city = (Spinner) view.findViewById(R.id.address_city);*/

        name = (EditText) view.findViewById(R.id.address_name);
        if (BamiloApplication.CUSTOMER.getFirstName() != null) {
            name.setText(BamiloApplication.CUSTOMER.getFirstName().trim());
        }
        family = (EditText) view.findViewById(R.id.address_family);
        if (BamiloApplication.CUSTOMER.getLastName() != null) {
            family.setText(BamiloApplication.CUSTOMER.getLastName().trim());
        }

        gender_spinner = (Spinner) view.findViewById(R.id.address_gender);

        cellphone = (EditText) view.findViewById(R.id.address_cell);
        cellphone.setText(BamiloApplication.CUSTOMER.getPhoneNumber());

        address = (EditText) view.findViewById(R.id.address_direction);
        address_spinner = (Spinner) view.findViewById(R.id.address_state);
        city_spinner = (Spinner) view.findViewById(R.id.address_city);
        postal_spinner = (Spinner) view.findViewById(R.id.address_postal_region);
        postal_code = (EditText) view.findViewById(R.id.address_postal_code);
        add = (Button) view.findViewById(R.id.add_address_btn);

        name_error = (TextView) view.findViewById(R.id.address_name_error);
        family_error = (TextView) view.findViewById(R.id.address_last_name_error);
        cellphone_error = (TextView) view.findViewById(R.id.address_cellphone_error);
        address_region_error = (TextView) view.findViewById(R.id.address_region_error);
        address_postal_region_error = (TextView) view.findViewById(R.id.address_postal_region_error);
        address_city_error = (TextView) view.findViewById(R.id.address_city_error);
        address_error = (TextView) view.findViewById(R.id.address_text_error);
        gender_error = (TextView) view.findViewById(R.id.address_gender_error);
        address_postal_code_error = (TextView) view.findViewById(R.id.address_postal_code_error);
        // Spinner Drop down elements
        /*ArrayList<AddressCity> city = new ArrayList<AddressCity>();
        city.add(new AddressCity(0, getString(R.string.address_city_placeholder)));
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, city);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        city_spinner.setAdapter(adapter);*/
        city_spinner.setVisibility(View.GONE);


        postal_spinner.setVisibility(View.GONE);
        setGender();
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
        // Get order summary
        orderSummary = BamiloApplication.INSTANCE.getCart();
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
            // Validate check
            Bundle shippingSavedStateBundle = new Bundle();
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
    }


    /**
     * Load the dynamic form
     */
    /*protected void loadCreateAddressForm(Form mFormShipping) {
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
    }*/
    protected String setGender() {

        ArrayList<String> gender = new ArrayList<>();
        gender.add(getString(R.string.gender_male));
        gender.add(getString(R.string.gender_female));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, gender);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(getString(R.string.gender));
        gender_spinner.setAdapter(promptAdapter);
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender_error.setVisibility(View.GONE);
                if (position == 1) {
                    gender_lable = GENDER_MALE;
                } else if (position == 2) {
                    gender_lable = GENDER_FEMALE;
                } else {
                    gender_lable = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        String selectedGender = BamiloApplication.CUSTOMER.getGender();
        if (TextUtils.isNotEmpty(selectedGender)) {
            if (selectedGender.equals(GENDER_MALE)) {
                gender_spinner.setSelection(1);
                gender_lable = GENDER_MALE;
                gender_spinner.setVisibility(View.GONE);
            } else if (selectedGender.equals(GENDER_FEMALE)) {
                gender_spinner.setSelection(2);
                gender_lable = GENDER_FEMALE;
                gender_spinner.setVisibility(View.GONE);
            }
        } else {
            gender_spinner.setVisibility(View.VISIBLE);
        }
        return gender_lable;
    }

    /**
     * Validate the current region selection and update the cities
     */
    protected void setCitiesOnSelectedRegion(String requestedRegionAndFields, final ArrayList<AddressCity> cities) {
        city_spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(getString(R.string.address_city_placeholder));
        city_spinner.setAdapter(promptAdapter);
        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                address_city_error.setVisibility(View.GONE);
                if (position == 0) {
                    city_Id = cities.get(position).getValue();
                    triggerGetPostalCodes(getPostalApi, city_Id, String.valueOf(city_Id));
                } else {
                    city_Id = cities.get(position - 1).getValue();
                    triggerGetPostalCodes(getPostalApi, city_Id, String.valueOf(city_Id));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        hideActivityProgress();
        showFragmentContentContainer();


    }

    /**
     * Validate the current city selection and update the postal codes
     */
    protected void setPostalCodesOnSelectedCity(String requestedCityAndFields, final ArrayList<AddressPostalCode> postalCodes) {
        if (postalCodes.size() > 1) {
            postal_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, postalCodes);
            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
            promptAdapter.setPrompt(getString(R.string.address_district_placeholder));
            postal_spinner.setAdapter(promptAdapter);
            postal_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    address_postal_region_error.setVisibility(View.GONE);
                    post_id = postalCodes.get(position).getValue();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            hideActivityProgress();
            showFragmentContentContainer();
        } else {
            if (!CollectionUtils.isEmpty(postalCodes)) {
                post_id = postalCodes.get(0).getValue();
            } else {
                post_id = UNKNOWN_POSTAL_CODE;
            }
            postal_spinner.setVisibility(View.GONE);
            address_postal_region_error.setVisibility(View.GONE);
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
        switch (errorType) {
            case GET_REGIONS_EVENT:
                triggerGetRegions(getRegionApi);
                break;
            case GET_CITIES_EVENT: {
                Object object = address_spinner.getItemAtPosition(address_spinner.getSelectedItemPosition());
                if (object != null && object instanceof AddressRegion
                        && ((AddressRegion) object).getValue() != 0) {
                    // Request the cities for this region id
                    int regionId = ((AddressRegion) object).getValue();
                    // Save the selected region on the respective variable
                    selectedRegionOnShipping = "" + regionId;
                    triggerGetCities(getCityApi, regionId, selectedRegionOnShipping);
                } else {
                    city_spinner.setSelection(0);
                    errorType = EventType.GET_REGIONS_EVENT;
                    onClickRetryButton(view);
                }
                break;
            }
            case GET_POSTAL_CODE_EVENT: {
                Object object = city_spinner.getItemAtPosition(city_spinner.getSelectedItemPosition());
                if (object != null && object instanceof AddressCity
                        && ((AddressCity) object).getValue() != 0) {
                    // Request the cities for this region id
                    int cityId = ((AddressCity) object).getValue();
                    // Save the selected region on the respective variable
                    selectedCityOnShipping = "" + cityId;
                    triggerGetPostalCodes(getPostalApi, cityId, selectedCityOnShipping);
                } else {
                    postal_spinner.setSelection(0);
                    errorType = EventType.GET_CITIES_EVENT;
                    onClickRetryButton(view);
                }
                break;
            }
            case CREATE_ADDRESS_SIGNUP_EVENT:
                onClickRetryButton();
                break;
            case CREATE_ADDRESS_EVENT:
                onClickCreateAddressButton();
                break;
        }
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
        if (!formValidated()) {
            Print.i(TAG, "SAME FORM: INVALID");
        } else {

            ContentValues values = new ContentValues();
            values.put("address_form[id]", "");
            values.put("address_form[first_name]", name.getText().toString());
            values.put("address_form[last_name]", family.getText().toString());
            values.put("address_form[address1]", address.getText().toString());
            values.put("address_form[address2]", postal_code.getText().toString());
            values.put("address_form[region]", region_Id);
            values.put("address_form[city]", city_Id);
            if (post_id != UNKNOWN_POSTAL_CODE) {
                values.put("address_form[postcode]", post_id);
            } else {
                values.put("address_form[postcode]", "");
            }
            values.put("address_form[phone]", cellphone.getText().toString());
            values.put("address_form[is_default_shipping]", 1);
            values.put("address_form[is_default_billing]", 1);

            values.put("address_form[gender]", gender_lable);


            triggerCreateAddress(createAddressUrl, values);
        }
    }


    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onItemSelected(com.mobile.components.absspinner.IcsAdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getItemAtPosition(position);
        if (object instanceof AddressRegion) {
            address_region_error.setVisibility(View.GONE);
            if (((AddressRegion) object).getValue() != 0) {   //if not the place holder option, load cities of selected region
                // Request the cities for this region id
                int regionId = ((AddressRegion) object).getValue();
                // Save the selected region on the respective variable
                selectedRegionOnShipping = "" + regionId;
                triggerGetCities(getCityApi, regionId, selectedRegionOnShipping);
            } else {
                showFragmentContentContainer();
            }
        } else if (object instanceof AddressCity) {
            address_city_error.setVisibility(View.GONE);
            if (((AddressCity) object).getValue() != 0) {
                // Request the postal codes for this city id
                int cityId = ((AddressCity) object).getValue();
                // Save the selected city on the respective variable
                selectedCityOnShipping = "" + cityId;
                triggerGetPostalCodes(getPostalApi, cityId, selectedCityOnShipping);
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
        boolean flag = true;
        name_error.setVisibility(View.GONE);
        family_error.setVisibility(View.GONE);
        address_region_error.setVisibility(View.GONE);
        address_city_error.setVisibility(View.GONE);
        address_error.setVisibility(View.GONE);
        gender_error.setVisibility(View.GONE);
        cellphone_error.setVisibility(View.GONE);
        address_postal_code_error.setVisibility(View.GONE);
        if (address_spinner.getSelectedItem() == null || address_spinner.getSelectedItem().equals(getString(R.string.address_province_placeholder))) {
            address_region_error.setVisibility(View.VISIBLE);
            address_region_error.setText(R.string.error_isrequired);
            flag = false;
        }
        if (city_spinner.getVisibility() == View.VISIBLE && (city_spinner.getSelectedItem() == null || city_spinner.getSelectedItem().equals(getString(R.string.address_city_placeholder)))) {
            address_city_error.setVisibility(View.VISIBLE);
            address_city_error.setText(R.string.error_isrequired);
            flag = false;
        }
        if (postal_spinner.getVisibility() == View.VISIBLE && (postal_spinner.getSelectedItem() == null || postal_spinner.getSelectedItem().equals(getString(R.string.delivery_neighbourhood)))) {
            address_postal_region_error.setVisibility(View.VISIBLE);
            address_postal_region_error.setText(R.string.error_isrequired);
            flag = false;
        }
        if (BamiloApplication.CUSTOMER.getGender().isEmpty()) {
            if (gender_spinner.getSelectedItem() == null || gender_spinner.getSelectedItem().equals(getString(R.string.gender))) {
                gender_error.setVisibility(View.VISIBLE);
                gender_error.setText(R.string.error_isrequired);
                flag = false;
            }
        }
        if (name.getText().length() >= 0) {
          /*  */
            if (name.getText().length() < 2) {
                name_error.setVisibility(View.VISIBLE);
                name_error.setText(R.string.error_isrequired);
                flag = false;
            }

        }
        if (family.getText().length() >= 0) {
          /*  */
            if (family.getText().length() < 2) {
                family_error.setVisibility(View.VISIBLE);
                family_error.setText(R.string.error_isrequired);
                flag = false;
            }

        }
        if (address.getText().length() >= 0) {
          /*  */
            if (address.getText().length() < 2) {
                address_error.setVisibility(View.VISIBLE);
                address_error.setText(R.string.error_isrequired);
                flag = false;
            }

        }
        Pattern pattern = Pattern.compile(getString(R.string.cellphone_regex), Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(cellphone.getText());
        boolean result = matcher.matches();
        if (!result) {
            cellphone.setVisibility(View.VISIBLE);
            cellphone_error.setVisibility(View.VISIBLE);
            cellphone_error.setText(R.string.address_phone_number_invalidity_error);
            flag = false;
        }
        if (cellphone.getText().length() == 0) {
            cellphone.setVisibility(View.VISIBLE);
            cellphone_error.setVisibility(View.VISIBLE);
            cellphone_error.setText(R.string.error_isrequired);
            flag = false;
        }


        if (postal_code.getText().length() != 0 && postal_code.getText().length() != 10) {
            address_postal_code_error.setVisibility(View.VISIBLE);
            flag = false;
        }

        if (gender_lable.length() == 0) {
            gender_error.setVisibility(View.VISIBLE);
            flag = false;
        }
        return flag;
    }

    protected void triggerCreateAddress(String action, ContentValues values) {
        Print.i(TAG, "TRIGGER: CREATE ADDRESS");
        triggerContentEvent(new CreateAddressHelper(), CreateAddressHelper.createBundle(action, values), this);
        // Hide the keyboard
        getBaseActivity().hideKeyboard();
    }

    /**
     * Trigger to get regions
     */
    protected void triggerGetRegions(String url) {
        Print.i(TAG, "TRIGGER: GET REGIONS: " + url);
        triggerContentEvent(new GetRegionsHelper(), GetRegionsHelper.createBundle(url), this);
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
        showFragmentContentContainer();
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        switch (eventType) {
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

    protected void onGetRegionsSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
        regions = (AddressRegions) baseResponse.getContentData();


        // Creating adapter for spinner

        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(getString(R.string.address_province_placeholder));
        address_spinner.setAdapter(promptAdapter);
        address_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                address_region_error.setVisibility(View.GONE);
                if (position != 0) {
                    region_Id = regions.get(position - 1).getValue();
                    triggerGetCities(getCityApi, region_Id, String.valueOf(region_Id));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
//        formValidated();
        setCitiesOnSelectedRegion(requestedRegionAndField, cities);
    }

    protected void onGetPostalCodesSuccessEvent(BaseResponse baseResponse) {
        GetPostalCodeHelper.AddressPostalCodesStruct postalCodesStruct = (GetPostalCodeHelper.AddressPostalCodesStruct) baseResponse.getContentData();
        Print.d(TAG, "RECEIVED GET_POSTAL_CODES_EVENT");
        Print.d(TAG, "REQUESTED CITY FROM FIELD: " + postalCodesStruct.getCustomTag());
        String requestedRegionAndField = postalCodesStruct.getCustomTag();
        setPostalCodesOnSelectedCity(requestedRegionAndField, postalCodesStruct);
//        formValidated();
        showFragmentContentContainer();
    }

    protected void onCreateAddressSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
        if (TextUtils.isEmpty(BamiloApplication.CUSTOMER.getGender())) {
            BamiloApplication.CUSTOMER.setGender(gender_lable);
        }
    }

    /**
     * Filter the error response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        errorType = baseResponse.getEventType();
        // Validate
        if (isOnStoppingProcess || errorType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.i(TAG, "SUPER HANDLE ERROR EVENT");
            return;
        }
        // Validate type
        switch (errorType) {
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
